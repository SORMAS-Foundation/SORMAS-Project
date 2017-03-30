package de.symeda.sormas.ui.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.vaadin.data.Buffered;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.Action.Notifier;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.FocusNotifier;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;


public class CommitDiscardWrapperComponent<C extends Component> extends
		VerticalLayout implements Buffered {

	private static final long serialVersionUID = 1L;

	public static interface CommitListener {
		void onCommit();
	}

	public static interface DiscardListener {
		void onDiscard();
	}

	public static interface DoneListener {
		void onDone();
	}

	public static interface DeleteListener {
		void onDelete();
	}

	private transient List<CommitListener> commitListeners = new ArrayList<>();
	private transient List<DiscardListener> discardListeners = new ArrayList<>();
	private transient List<DoneListener> doneListeners = new ArrayList<>();
	private transient List<DeleteListener> deleteListeners = new ArrayList<>();
	// only to check if it's set
	private transient CommitListener primaryCommitListener;

	private Panel contentPanel;

	private C wrappedComponent;
	private FieldGroup fieldGroup;

	private HorizontalLayout buttonsPanel;
	private Button commitButton;
	private Button discardButton;

	private Button deleteButton;
	private ConfirmationComponent deleteConfirmationComponent;

	private boolean commited = false;

	private boolean shortcutsEnabled = false;
	protected transient List<ClickShortcut> actions;

	private boolean autoFocusing = false;

	private boolean autoDisablingButtons = false;
	private final ValueChangeListener autoHideValueChangeListener = new ValueChangeListener() {
		private static final long serialVersionUID = 1L;
		@Override
		public void valueChange(Property.ValueChangeEvent event) {
			boolean modified = isModified();
			getCommitButton().setEnabled(modified);
			getDiscardButton().setEnabled(modified);
		}
	};
	private final FocusListener autoHideFocusListener = new FocusListener() {
		private static final long serialVersionUID = 1L;
		@Override
		public void focus(FieldEvents.FocusEvent event) {
			getCommitButton().setEnabled(true);
			getDiscardButton().setEnabled(true);
		}
	};

	protected CommitDiscardWrapperComponent() {

	}

	public CommitDiscardWrapperComponent(C component, FieldGroup fieldGroup) {
		setWrappedComponent(component, fieldGroup);
	}

	protected void setWrappedComponent(C component, FieldGroup fieldGroup) {

		this.wrappedComponent = component;
		this.fieldGroup = fieldGroup;
		
		if (contentPanel != null) {
			contentPanel.setContent(wrappedComponent);
			applyAutoFocusing();
			applyAutoDisabling();
			return;
		}

		setSpacing(false);
		setSizeUndefined();
		
		contentPanel = new Panel(component);
		updateInternalWidth();
		updateInternalHeight();
		addComponent(contentPanel);
		setExpandRatio(contentPanel, 1);

		buttonsPanel = new HorizontalLayout();
		buttonsPanel.setMargin(false);
		buttonsPanel.setWidth(100, Unit.PERCENTAGE);

		Button discardButton = getDiscardButton();
		buttonsPanel.addComponent(discardButton);
		buttonsPanel.setComponentAlignment(discardButton,
				Alignment.BOTTOM_RIGHT);
		buttonsPanel.setExpandRatio(discardButton, 1);

		Button commitButton = getCommitButton();
		buttonsPanel.addComponent(commitButton);
		buttonsPanel
				.setComponentAlignment(commitButton, Alignment.BOTTOM_RIGHT);
		buttonsPanel.setExpandRatio(commitButton, 0);

		addComponent(buttonsPanel);
		setComponentAlignment(buttonsPanel, Alignment.BOTTOM_RIGHT);

		setShortcutsEnabled(shortcutsEnabled);

		applyAutoDisabling();
	}

	public void setAutoFocusing(boolean autoFocusing) {
		this.autoFocusing = autoFocusing;
		applyAutoFocusing();
	}

	public boolean isAutoFocusing() {
		return autoFocusing;
	}

	protected void applyAutoFocusing() {
		if (!autoFocusing)
			return;

		Collection<Field<?>> fields;
		if (fieldGroup == null) {
			if (wrappedComponent instanceof Field) {
				Field<?> field = (Field<?>) wrappedComponent;
				fields = Collections.<Field<?>> singleton(field);
			} else {
				return;
			}
		} else {
			fields = fieldGroup.getFields();
		}

		for (Field<?> field : fields) {
			if (field.isEnabled() && field.isVisible() && field.getTabIndex() >= 0) {

				FocusNotifier fn;
				if (autoDisablingButtons && field instanceof FocusNotifier) {
					fn = (FocusNotifier) field;
					fn.removeFocusListener(autoHideFocusListener);
				} else {
					fn = null;
				}
				field.focus();
				if (fn != null) {
					fn.addFocusListener(autoHideFocusListener);
				}
				break;
			}
		}
	}

	public void setAutoDisablingButtons(boolean autoDisablingButtons) {
		if (this.autoDisablingButtons == autoDisablingButtons)
			return;
		this.autoDisablingButtons = autoDisablingButtons;
		applyAutoDisabling();
	}

	public boolean isAutoDisablingButtons() {
		return autoDisablingButtons;
	}

	protected void applyAutoDisabling() {

		boolean modified = isModified();
		getCommitButton().setEnabled(!autoDisablingButtons || modified);
		getDiscardButton().setEnabled(!autoDisablingButtons || modified);

		Collection<Field<?>> fields;
		if (fieldGroup == null) {
			if (wrappedComponent instanceof Field) {
				Field<?> field = (Field<?>) wrappedComponent;
				fields = Collections.<Field<?>> singleton(field);
			} else {
				return;
			}
		} else {
			fields = fieldGroup.getFields();
		}

		for (Field<?> field : fields) {

			field.removeValueChangeListener(autoHideValueChangeListener);
			if (autoDisablingButtons)
				field.addValueChangeListener(autoHideValueChangeListener);

			if (field instanceof FocusNotifier) {
				((FocusNotifier) field).removeFocusListener(autoHideFocusListener);
				if (autoDisablingButtons)
					((FocusNotifier) field).addFocusListener(autoHideFocusListener);
			}
		}
	}

	/**
	 * Ob die Buttons per ENTER und ESC bedient werden können
	 * 
	 * @param shortcutsEnabled
	 */
	public void setShortcutsEnabled(boolean shortcutsEnabled) {

		if (shortcutsEnabled == this.shortcutsEnabled) {
			if (!shortcutsEnabled || actions != null)
				return;
		}

		this.shortcutsEnabled = shortcutsEnabled;

		if (fieldGroup == null) {
			return;
		}

		if (actions == null) {
			actions = new ArrayList<>();
		}

		Collection<Notifier> notifiers = Arrays.asList((Notifier) contentPanel);

		if (shortcutsEnabled) {
			for (Notifier notifier : notifiers) {
				registerActions(notifier);
			}

		} else {
			for (ClickShortcut action : actions) {
				action.remove();
			}
			actions.clear();
		}
	}

	protected void registerActions(Notifier notifier) {
		actions.add(new ClickShortcut(notifier, commitButton,
				KeyCode.ENTER));
		actions.add(new ClickShortcut(notifier, discardButton,
				KeyCode.ESCAPE));
	}

	public C getWrappedComponent() {
		return wrappedComponent;
	}

	public FieldGroup getFieldGroup() {
		return fieldGroup;
	}

	public HorizontalLayout getButtonsPanel() {
		return buttonsPanel;
	}


	/**
	 * Durch das Aufrufen dieser Methode wird ein Button zum Speichern erzeugt, aber nicht eingefügt.
	 * Das passiert in setWrappedComponent().
	 * @return
	 */
	public Button getCommitButton() {
		if (commitButton == null) {
			commitButton = new Button("save");
			commitButton.addStyleName(ValoTheme.BUTTON_PRIMARY);

			commitButton.addClickListener(new ClickListener() { 
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick(ClickEvent event) {
					commitAndHandle();
				}
			});
		}
		return commitButton;
	}


	/**
	 * Durch das Aufrufen dieser Methode wird ein Button zum Verwerfen erzeugt aber nicht eingefügt.
	 * Das passiert in setWrappedComponent().
	 * @return
	 */
	public Button getDiscardButton() {
		if (discardButton == null) {
			discardButton = new Button("discard");
			discardButton.addStyleName(ValoTheme.BUTTON_LINK);

			discardButton.addClickListener(new ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick(ClickEvent event) {
					discard();
				}
			});
		}
		return discardButton;
	}

	/**
	 * Durch das Aufrufen dieser Methode wird ein Button zum Löschen erstellt, aber nicht angezeigt.
	 * Dazu muss addDeleteListener() aufgerufen werden. 
	 * @return
	 */
	public Button getDeleteButton() {
		if (deleteButton == null) {
			deleteButton = new Button("delete");
			deleteButton.addStyleName(ValoTheme.BUTTON_LINK);
			deleteButton.addClickListener(new ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick(ClickEvent event) {
					buttonsPanel.replaceComponent(deleteButton, getDeleteConfirmationComponent());
					getDiscardButton().setVisible(false);
					getCommitButton().setVisible(false);
				}
			});
		}

		return deleteButton;
	}

	/**
	 * Durch das Aufrufen dieser Methode wird beim Löschen eine Bestätigung angefordert.
	 * Die Details können dann an der zurückgegebenen ConfirmationComponent konfiguriert werden.
	 * Der Button zum Löschen wird damit aber nicht eingefügt.
	 * @return
	 */
	public ConfirmationComponent getDeleteConfirmationComponent() {
		if (deleteConfirmationComponent == null) {
			deleteConfirmationComponent = new ConfirmationComponent(false) {
				private static final long serialVersionUID = 1L;
				@Override
				protected void onConfirm() {
					try {
						onDelete();
						onDone();
					} catch (SilentCommitException ex) {
						// NOOP
					}
					buttonsPanel.replaceComponent(this, getDeleteButton());
				}

				@Override
				protected void onCancel() {
					buttonsPanel.replaceComponent(this, getDeleteButton());
					getDiscardButton().setVisible(true);
					getCommitButton().setVisible(true);
				}
			};
			deleteConfirmationComponent.getConfirmButton().setCaption(
					"Really delete?");
		}
		return deleteConfirmationComponent;
	}

	@Override
	public boolean isModified() {

		if (fieldGroup != null)
			return fieldGroup.isModified();
		else if (wrappedComponent instanceof Buffered) {
			return ((Buffered) wrappedComponent).isModified();
		} else {
			return false;
		}
	}

	public boolean isCommited() {
		return commited;
	}

	@Override
	public void commit() {

		if (fieldGroup != null)
		{
			try {
				fieldGroup.commit();
			} 
			catch (CommitException e) {
				if (e.getCause() instanceof InvalidValueException)
					throw (InvalidValueException)e.getCause();
				else if (e.getCause() instanceof SourceException)
					throw (SourceException)e.getCause();
				else
					throw new CommitRuntimeException(e);
			}
		}
		else if (wrappedComponent instanceof Buffered) {
			((Buffered) wrappedComponent).commit();
		} else {
			// NOOP
		}

		onCommit();
		commited = true;

		onDone();

		applyAutoDisabling();
	}
	
	private String findHtmlMessage(InvalidValueException exception)
	{
		if (!(exception.getMessage() == null || exception.getMessage().isEmpty()))
			return exception.getHtmlMessage();
		
		for (InvalidValueException cause : exception.getCauses()) {
			String message = findHtmlMessage(cause);
			if (message != null)
				return message;
		}
		
		return null;
	}
	
	public void commitAndHandle() {
		try {
			commit();
		} catch (InvalidValueException ex) {
			
			StringBuilder htmlMsg = new StringBuilder();
			String message = ex.getMessage();
            if (message != null && !message.isEmpty()) {
            	htmlMsg.append(ex.getHtmlMessage());
            } else {
            	
	            InvalidValueException[] causes = ex.getCauses();
	            if (causes != null) {
	            	
	            	InvalidValueException firstCause = null;
	            	boolean multipleCausesFound = false;
	                for (int i = 0; i < causes.length; i++) {
	                    if (!causes[i].isInvisible()) {
	                    	if (firstCause == null) {
	                    		firstCause = causes[i];
	                    	} else {
	                    		multipleCausesFound = true;
	                    		break;
	                    	}
	                    }
	                }
	                if (multipleCausesFound) {
	                	htmlMsg.append("<ul>");
	                	// Alle nochmal
		                for (int i = 0; i < causes.length; i++) {
		                    if (!causes[i].isInvisible()) {
			                	htmlMsg.append("<li>").append(findHtmlMessage(causes[i])).append("</li>");
		                    }
		                }
	                	htmlMsg.append("</ul>");
	                } else if (firstCause != null) {
	                	htmlMsg.append(findHtmlMessage(firstCause));
	                }
	                
	            }
            }
				
            new Notification("Please check the input data", htmlMsg.toString(), Type.ERROR_MESSAGE, true).show(Page.getCurrent());
		} 
	}

	@Override
	public void discard() {

		if (fieldGroup != null) {
			fieldGroup.discard();
		} else if (wrappedComponent instanceof Buffered) {
			((Buffered) wrappedComponent).discard();
		} else {
			// NOOP
		}
		onDiscard();
		onDone();

		applyAutoDisabling();
	}

	@Override
	public void setBuffered(boolean buffered) {
		if (fieldGroup != null)
			fieldGroup.setBuffered(buffered);
		else if (wrappedComponent instanceof Buffered) {
			((Buffered) wrappedComponent).setBuffered(buffered);
		} else {
			// NOOP
		}
	}

	@Override
	public boolean isBuffered() {
		if (fieldGroup != null)
			return fieldGroup.isBuffered();
		else if (wrappedComponent instanceof Buffered) {
			return ((Buffered) wrappedComponent).isBuffered();
		} else {
			return false;
		}
	}

	public void addCommitListener(CommitListener listener) {
		if (!commitListeners.contains(listener))
			commitListeners.add(listener);
	}

	public void setPrimaryCommitListener(CommitListener listener) {
		if (primaryCommitListener != null)
			throw new UnsupportedOperationException("primary listener already set");
		if (!commitListeners.contains(listener))
			commitListeners.add(0, listener);
		primaryCommitListener = null;
	}

	public void removeCommitListener(CommitListener listener) {
		commitListeners.remove(listener);
		if (primaryCommitListener != null && primaryCommitListener.equals(listener))
			primaryCommitListener = null;
	}

	private void onCommit() {

		for (CommitListener listener : commitListeners)
			listener.onCommit();
	}


	/**
	 * Fügt einen Listener zum Abbrechen hinzu.
	 * Blendet den Abbrechen-Button aber nicht ein.
	 * @param listener
	 */
	public void addDiscardListener(DiscardListener listener) {
		if (!discardListeners.contains(listener))
			discardListeners.add(listener);
	}

	public void removeDiscardListener(DiscardListener listener) {
		discardListeners.remove(listener);
	}

	private void onDiscard() {
		for (DiscardListener listener : discardListeners)
			listener.onDiscard();
	}

	public void addDoneListener(DoneListener listener) {
		if (!doneListeners.contains(listener))
			doneListeners.add(listener);
	}

	public void removeDoneListener(DoneListener listener) {
		doneListeners.remove(listener);
	}

	private void onDone() {
		for (DoneListener listener : doneListeners)
			listener.onDone();
	}

	/**
	 * Fügt einen Listener zum Löschen hinzu.
	 * Blendet ggf. den Lösch-Button ein.
	 * @param listener
	 */
	public void addDeleteListener(DeleteListener listener) {
		if (deleteListeners.isEmpty())
			buttonsPanel.addComponent(getDeleteButton(), 0);
		if (!deleteListeners.contains(listener))
			deleteListeners.add(listener);
	}
	
	public boolean hasDeleteListener() {
		return !deleteListeners.isEmpty();
	}

	public void removeDeleteListener(DeleteListener listener) {
		deleteListeners.remove(listener);
		if (deleteListeners.isEmpty())
			buttonsPanel.removeComponent(getDeleteButton());
	}

	private void onDelete() {
		for (DeleteListener listener : deleteListeners)
			listener.onDelete();
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		getWrappedComponent().setReadOnly(readOnly);
		fieldGroup.setReadOnly(readOnly);

		buttonsPanel.setVisible(!readOnly);
	}

	@Override
	public boolean isReadOnly() {
		return getWrappedComponent().isReadOnly();
	}

	protected static class ClickShortcut extends Button.ClickShortcut {
		private static final long serialVersionUID = 1L;

		private final Notifier notifier;

		public ClickShortcut(Notifier notifier, Button button, int keyCode) {
			super(button, keyCode, null);
			this.notifier = notifier;
			notifier.addAction(this);
		}

		public void remove() {
			notifier.removeAction(this);
		}

		@Override
		public void handleAction(Object sender, Object target) {

			if (target instanceof TextArea || target instanceof RichTextArea) {
				// NOOP
			} else {
				super.handleAction(sender, target);
			}
		}

		@Override
		public String toString() {
			return notifier + "[" + getKeyCode() + "] =>" + button.getCaption();
		}
	}

	/**
	 * Wenn das Commit nicht erfolgreich war, aber keine Notification angezeigt werden soll
	 * @deprecated Wird nicht mehr beachtet @see {@link CommitDiscardWrapperComponent#commit()}
	 */
	@Deprecated
	public static class SilentCommitException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public SilentCommitException() {
			super();
		}

		public SilentCommitException(String message) {
			super(message);
		}

		public SilentCommitException(String message, Throwable cause) {
			super(message, cause);
		}

		public SilentCommitException(Throwable cause) {
			super(cause);
		}
	}
	
	public static class CommitRuntimeException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public CommitRuntimeException(CommitException e) {
			super(e.getMessage(), e);
		}
	}

	/**
	 * Dirty Hack zum Schließen des CommitDiscardWrapper
	 * 
	 * @param wrappedComponent
	 */
	public static void dirtyDiscardHack(Component wrappedComponent) {
		CommitDiscardWrapperComponent<?> cdw = (CommitDiscardWrapperComponent<?>) wrappedComponent.getParent().getParent();
		cdw.discard();
	}

	@Override
	public void setWidth(float width, Unit unit) {
		super.setWidth(width, unit);
		updateInternalWidth();
	}

	@Override
	public void setHeight(float height, Unit unit) {
		super.setHeight(height, unit);
		updateInternalHeight();
	}

	private void updateInternalWidth() {
		if (contentPanel == null) {
			return;
		}
		if (getWidth() < 0) {
			contentPanel.setWidth(-1, Unit.PIXELS);
		} else {
			contentPanel.setWidth(100, Unit.PERCENTAGE);
		}
	}

	private void updateInternalHeight() {
		if (contentPanel == null) {
			return;
		}
		if (getHeight() < 0) {
			contentPanel.setHeight(-1, Unit.PIXELS);
		} else {
			contentPanel.setHeight(100, Unit.PERCENTAGE);
		}
	}
}