/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import com.vaadin.event.Action.Notifier;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Buffered;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.v7.ui.AbstractLegacyComponent;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.RichTextArea;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;


public class CommitDiscardWrapperComponent<C extends Component> extends VerticalLayout implements Buffered {

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
	private FieldGroup[] fieldGroups;

	private HorizontalLayout buttonsPanel;
	private Button commitButton;
	private Button discardButton;

	private Button deleteButton;

	private boolean commited = false;

	private boolean shortcutsEnabled = false;
	protected transient List<ClickShortcut> actions;

	protected CommitDiscardWrapperComponent() {

	}

	public CommitDiscardWrapperComponent(C component, FieldGroup ...fieldGroups) {
		this(component, null, fieldGroups);
	}

	public CommitDiscardWrapperComponent(C component, Boolean isEditingAllowed, FieldGroup ...fieldGroups) {
		setWrappedComponent(component, fieldGroups);
		if (isEditingAllowed != null) {
			setEnabled(isEditingAllowed);
		}
	}

	protected void setWrappedComponent(C component, FieldGroup ...fieldGroups) {

		this.wrappedComponent = component;
		this.fieldGroups = fieldGroups;

		if (contentPanel != null) {
			contentPanel.setContent(wrappedComponent);
			return;
		}

		setSpacing(false);
		setMargin(true);
		setSizeUndefined();

		contentPanel = new Panel(component);
		updateInternalWidth();
		updateInternalHeight();
		addComponent(contentPanel);
		setExpandRatio(contentPanel, 1);

		buttonsPanel = new HorizontalLayout();
		buttonsPanel.setMargin(false);
		buttonsPanel.setSpacing(true);
		buttonsPanel.setWidth(100, Unit.PERCENTAGE);

		Button discardButton = getDiscardButton();
		buttonsPanel.addComponent(discardButton);
		buttonsPanel.setComponentAlignment(discardButton, Alignment.BOTTOM_RIGHT);
		buttonsPanel.setExpandRatio(discardButton, 1);

		Button commitButton = getCommitButton();
		buttonsPanel.addComponent(commitButton);
		buttonsPanel.setComponentAlignment(commitButton, Alignment.BOTTOM_RIGHT);
		buttonsPanel.setExpandRatio(commitButton, 0);

		addComponent(buttonsPanel);
		setComponentAlignment(buttonsPanel, Alignment.BOTTOM_RIGHT);

		setShortcutsEnabled(shortcutsEnabled);

		if (fieldGroups != null && fieldGroups.length > 0) {
			// convention: set wrapper to read-only when all wrapped field groups are read-only
			boolean allReadOnly = true;
			for (FieldGroup fieldGroup : fieldGroups)  {
				if (!fieldGroup.isReadOnly()) {
					allReadOnly = false;
					break;
				}
			}
			if (allReadOnly) {
				setReadOnly(true);
			}
		} else if (wrappedComponent != null) {
			if (wrappedComponent instanceof AbstractLegacyComponent 
					&& ((AbstractLegacyComponent)wrappedComponent).isReadOnly()) {
				setReadOnly(true);
			}
		}
	}

	protected Stream<Field<?>> getFieldsStream() {

		if (fieldGroups != null) {
			return Arrays.stream(fieldGroups)
					.map(FieldGroup::getFields)
					.flatMap(Collection::stream);
		} else {
			return Stream.empty();
		}
	}

	/**
	 * Whether the buttons can be operated by ENTER and ESC
	 * 
	 * @param shortcutsEnabled
	 */
	public void setShortcutsEnabled(boolean shortcutsEnabled) {

		if (shortcutsEnabled == this.shortcutsEnabled) {
			if (!shortcutsEnabled || actions != null)
				return;
		}

		this.shortcutsEnabled = shortcutsEnabled;

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
			commitButton = ButtonHelper.createButtonWithCaption("commit", I18nProperties.getCaption(Captions.actionSave), new ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick(ClickEvent event) {
					commitAndHandle();
				}
			}, ValoTheme.BUTTON_PRIMARY);
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
			discardButton = ButtonHelper.createButtonWithCaption("discard", I18nProperties.getCaption(Captions.actionDiscard), new ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick(ClickEvent event) {
					discard();
				}
			});
		}

		return discardButton;
	}

	public Button getDeleteButton(String entityName) {
		if (deleteButton == null) {
			deleteButton = ButtonHelper.createButtonWithCaption("delete", I18nProperties.getCaption(Captions.actionDelete), new ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick(ClickEvent event) {
					VaadinUiUtil.showDeleteConfirmationWindow(String.format(I18nProperties.getString(Strings.confirmationDeleteEntity), entityName), new Runnable() {
						public void run() {
							onDelete();
						}
					});
				}
			}, ValoTheme.BUTTON_DANGER, CssStyles.BUTTON_BORDER_NEUTRAL);
		}

		return deleteButton;
	}

	@Override
	public boolean isModified() {
		if (fieldGroups != null) {
			for (FieldGroup fieldGroup : fieldGroups) {
				if (fieldGroup.isModified()) {
					return true;
				}
			}
		} else if (wrappedComponent instanceof Buffered) {
			return ((Buffered)wrappedComponent).isModified(); 
		}
		return false;
	}

	public boolean isCommited() {
		return commited;
	}

	@Override
	public void commit() throws InvalidValueException, SourceException, CommitRuntimeException {

		if (fieldGroups != null)
		{
			if (fieldGroups.length > 1) {
				// validate all fields first, so commit will likely work for all fieldGroups
				// this is basically only needed when we have multiple field groups
				// FIXME this leads to problem #537 for AbstractEditForm with hideValidationUntilNextCommit 
				// can hopefully be fixed easier with Vaadin 8 architecture change
				getFieldsStream().forEach(field -> {
					if (!field.isInvalidCommitted()) {
						field.validate();
					}
				});
			}

			try {
				for (FieldGroup fieldGroup : fieldGroups) {
					fieldGroup.commit();
				}
			} 
			catch (CommitException e) {
				if (e.getCause() instanceof InvalidValueException)
					throw (InvalidValueException)e.getCause();
				else if (e.getCause() instanceof SourceException)
					throw (SourceException)e.getCause();
				else
					throw new CommitRuntimeException(e);
			}
		} else if (wrappedComponent instanceof Buffered) {
			((Buffered)wrappedComponent).commit(); 
		} else {
			// NOOP
		}

		onCommit();
		commited = true;

		onDone();
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
			ex.printStackTrace();
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
								htmlMsg.append("<li style=\"color: #FFF;\">").append(findHtmlMessage(causes[i])).append("</li>");
							}
						}
						htmlMsg.append("</ul>");
					} else if (firstCause != null) {
						htmlMsg.append(findHtmlMessage(firstCause));
					}

				}
			}

			new Notification(I18nProperties.getString(Strings.messageCheckInputData), htmlMsg.toString(), Type.ERROR_MESSAGE, true).show(Page.getCurrent());
		} 
	}

	@Override
	public void discard() {
		if (fieldGroups != null) {
			for (FieldGroup fieldGroup : fieldGroups) {
				fieldGroup.discard();
			}
		} else if (wrappedComponent instanceof Buffered) {
			((Buffered)wrappedComponent).discard(); 
		} else {
			// NOOP
		}
		onDiscard();
		onDone();
	}

	@Override
	public void setBuffered(boolean buffered) {
		if (fieldGroups != null) {
			for (FieldGroup fieldGroup : fieldGroups) {
				fieldGroup.setBuffered(buffered);
			}
		} else if (wrappedComponent instanceof Buffered) {
			((Buffered)wrappedComponent).setBuffered(buffered);
		} else {
			// NOOP
		}
	}

	@Override
	public boolean isBuffered() {
		if (fieldGroups != null) {
			Boolean buffered = null;
			for (FieldGroup fieldGroup : fieldGroups) {
				if (buffered != null && buffered.booleanValue() != fieldGroup.isBuffered())
					throw new IllegalStateException("FieldGroups have different isBuffered states");
				buffered = fieldGroup.isBuffered();
			}
			return Boolean.TRUE.equals(buffered);
		} else if (wrappedComponent instanceof Buffered) {
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

	public void addDeleteListener(DeleteListener listener, String entityName) {
		if (deleteListeners.isEmpty())
			buttonsPanel.addComponent(getDeleteButton(entityName), 0);
		if (!deleteListeners.contains(listener))
			deleteListeners.add(listener);
	}

	public boolean hasDeleteListener() {
		return !deleteListeners.isEmpty();
	}

	private void onDelete() {
		for (DeleteListener listener : deleteListeners)
			listener.onDelete();
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		try {
			super.setReadOnly(readOnly);
		} catch (IllegalStateException e) {
			super.setEnabled(readOnly);
		}
		//
		//		getWrappedComponent().setReadOnly(readOnly);
		//		if (fieldGroups != null) {
		//			for (FieldGroup fieldGroup : fieldGroups) {
		//				fieldGroup.setReadOnly(readOnly);
		//			}
		//		}
		//
		//		buttonsPanel.setVisible(!readOnly);
	}

	//	@Override
	//	public boolean isReadOnly() {
	//		return getWrappedComponent().isReadOnly();
	//	}

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

	public static class CommitRuntimeException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public CommitRuntimeException(CommitException e) {
			super(e.getMessage(), e);
		}
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