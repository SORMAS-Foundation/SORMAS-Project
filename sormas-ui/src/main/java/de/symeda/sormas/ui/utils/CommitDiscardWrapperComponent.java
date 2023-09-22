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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.utils;

import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.naming.CannotProceedException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

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
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Buffered;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.v7.ui.AbstractLegacyComponent;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.RichTextArea;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.DeletableFacade;
import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.events.EventDataForm;
import de.symeda.sormas.ui.location.AccessibleTextField;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.person.PersonEditForm;

public class CommitDiscardWrapperComponent<C extends Component> extends VerticalLayout implements DirtyStateComponent, Buffered {

	private static final long serialVersionUID = 1L;
	private Set<String> activeButtons = new HashSet<>();

	public static final String DELETE_RESTORE = "deleteRestore";

	public static interface PreCommitListener {

		void onPreCommit(Runnable successCallback);
	}

	public static interface CommitListener {

		void onCommit() throws CannotProceedException;
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

	public static interface DeleteWithDetailsListener {

		void onDelete(DeletionDetails deletionDetails);
	}

	private transient PreCommitListener preCommitListener;
	private transient Runnable postCommitListener;
	private transient List<CommitListener> commitListeners = new ArrayList<>();
	private transient List<DiscardListener> discardListeners = new ArrayList<>();
	private transient List<DoneListener> doneListeners = new ArrayList<>();
	private transient List<DeleteListener> deleteListeners = new ArrayList<>();
	private transient List<DeleteWithDetailsListener> deleteWithDetailsListeners = new ArrayList<>();
	// only to check if it's set
	private transient CommitListener primaryCommitListener;

	private Panel contentPanel;

	private C wrappedComponent;
	private ArrayList<FieldGroup> fieldGroups;

	private HorizontalLayout buttonsPanel;
	private Button commitButton;
	private Button discardButton;

	private Button deleteButton;

	private boolean commited = false;
	private boolean dirty = false;

	private boolean shortcutsEnabled = false;
	protected transient List<ClickShortcut> actions;

	protected CommitDiscardWrapperComponent() {

	}

	public CommitDiscardWrapperComponent(C component, FieldGroup... fieldGroups) {
		this(component, null, fieldGroups);
	}

	public CommitDiscardWrapperComponent(C component, Boolean isEditingAllowed, FieldGroup... fieldGroups) {
		setWrappedComponent(component, fieldGroups);
		if (isEditingAllowed != null) {
			setEnabled(isEditingAllowed);
		}
	}

	protected void setWrappedComponent(C component, FieldGroup... fieldGroups) {

		this.wrappedComponent = component;
		this.fieldGroups = new ArrayList(Arrays.asList(fieldGroups));

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
			for (FieldGroup fieldGroup : fieldGroups) {
				if (!fieldGroup.isReadOnly()) {
					allReadOnly = false;
					break;
				}
			}
			if (allReadOnly) {
				setReadOnly(true);
			}
		} else if (wrappedComponent != null) {
			if (wrappedComponent instanceof AbstractLegacyComponent && ((AbstractLegacyComponent) wrappedComponent).isReadOnly()) {
				setReadOnly(true);
			}
		}

		dirty = false;
		addDirtyHandler(fieldGroups);
	}

	public void addFieldGroups(FieldGroup... fieldGroups) {

		if (this.fieldGroups == null) {
			this.fieldGroups = new ArrayList(Arrays.asList(fieldGroups));
		} else {
			this.fieldGroups.addAll(Arrays.asList(fieldGroups));
		}
		addDirtyHandler(fieldGroups);
	}

	public void removeFieldGroups(FieldGroup... fieldGroups) {
		if (fieldGroups == null || this.fieldGroups == null) {
			return;
		}
		this.fieldGroups.removeAll(Arrays.asList(fieldGroups));

	}

	@SuppressWarnings("deprecation")
	protected void addDirtyHandler(FieldGroup[] fieldGroups) {
		if (fieldGroups != null) {
			Stream.of(fieldGroups).forEach(fg -> fg.getFields().forEach(f -> f.addValueChangeListener(ev -> {
				final Object source = ((Field.ValueChangeEvent) ev).getSource();
				// Note by @MateStrysewske: It seems like the duplicate code here is necessary; for some reason,
				// moving it to a separate method breaks the logic at least on my dev system
				if (source instanceof PersonEditForm) {
					final PersonEditForm personEditForm = (PersonEditForm) source;
					final LocationEditForm locationEditForm = personEditForm.getField(PersonDto.ADDRESS);
					if (atLeastOneFieldModified(
						locationEditForm.getField(LocationDto.LATITUDE),
						locationEditForm.getField(LocationDto.LONGITUDE),
						locationEditForm.getField(LocationDto.LAT_LON_ACCURACY))) {
						dirty = true;
					} else if (locationEditForm.getFieldGroup()
						.getFields()
						.stream()
						.filter(lf -> !(lf instanceof AccessibleTextField))
						.anyMatch(Buffered::isModified)) {
						dirty = true;
					} else if (personEditForm.getFieldGroup()
						.getFields()
						.stream()
						.filter(lf -> !(lf instanceof AccessibleTextField))
						.anyMatch(Buffered::isModified)) {
						dirty = true;
					}
				} else if (source instanceof EventDataForm) {
					final EventDataForm eventDataForm = (EventDataForm) source;
					final LocationEditForm locationEditForm = eventDataForm.getField(EventDto.EVENT_LOCATION);
					if (atLeastOneFieldModified(
						locationEditForm.getField(LocationDto.LATITUDE),
						locationEditForm.getField(LocationDto.LONGITUDE),
						locationEditForm.getField(LocationDto.LAT_LON_ACCURACY))) {
						dirty = true;
					} else if (locationEditForm.getFieldGroup()
						.getFields()
						.stream()
						.filter(lf -> !(lf instanceof AccessibleTextField))
						.anyMatch(Buffered::isModified)) {
						dirty = true;
					} else if (eventDataForm.getFieldGroup()
						.getFields()
						.stream()
						.filter(lf -> !(lf instanceof AccessibleTextField))
						.anyMatch(Buffered::isModified)) {
						dirty = true;
					}
				} else if (source instanceof LocationEditForm) {
					final LocationEditForm locationEditForm = (LocationEditForm) source;
					if (atLeastOneFieldModified(
						locationEditForm.getField(LocationDto.LATITUDE),
						locationEditForm.getField(LocationDto.LONGITUDE),
						locationEditForm.getField(LocationDto.LAT_LON_ACCURACY))) {
						dirty = true;
					} else if (locationEditForm.getFieldGroup()
						.getFields()
						.stream()
						.filter(lf -> !(lf instanceof AccessibleTextField))
						.anyMatch(Buffered::isModified)) {
						dirty = true;
					}
				} else if (source instanceof AccessibleTextField) {
					final AccessibleTextField accessibleTextField = (AccessibleTextField) source;
					if (accessibleTextField.isModified()) {
						dirty = true;
					}
				} else {
					dirty = true;
				}
			})));
		}
	}

	private boolean atLeastOneFieldModified(AccessibleTextField... fields) {
		for (AccessibleTextField field : fields) {
			if (field.getState().modified) {
				return true;
			}
		}
		return false;
	}

	protected Stream<Field<?>> getFieldsStream() {

		if (fieldGroups != null) {
			return fieldGroups.stream().map(FieldGroup::getFields).flatMap(Collection::stream);
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
		actions.add(new ClickShortcut(notifier, commitButton, KeyCode.ENTER));
		actions.add(new ClickShortcut(notifier, discardButton, KeyCode.ESCAPE));
	}

	public C getWrappedComponent() {
		return wrappedComponent;
	}

	public HorizontalLayout getButtonsPanel() {
		return buttonsPanel;
	}

	/**
	 * 
	 * Calling this method creates a save button. The button will not be added, though.
	 * This happens in setWrappedComponent().
	 * 
	 * @return
	 */
	public Button getCommitButton() {
		if (commitButton == null) {
			commitButton = ButtonHelper.createButton("commit", I18nProperties.getCaption(Captions.actionSave), new ClickListener() {

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
	 * 
	 * Calling this method creates a discard button. The button will not be added, though.
	 * This happens in setWrappedComponent().
	 * 
	 * @return
	 */
	public Button getDiscardButton() {
		if (discardButton == null) {
			discardButton = ButtonHelper.createButton("discard", I18nProperties.getCaption(Captions.actionDiscard), new ClickListener() {

				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
					discard();
				}
			});
			discardButton.setDescription(I18nProperties.getDescription(Descriptions.discardDescription));
		}

		return discardButton;
	}

	public Button getDeleteButton(String entityName, Supplier<String> confirmationMessageSupplier) {
		if (deleteButton == null) {
			deleteButton = buildDeleteButton(() -> {
				String confirmationMessage = confirmationMessageSupplier == null ? null : confirmationMessageSupplier.get();

				VaadinUiUtil.showDeleteConfirmationWindow(
					StringUtils.isBlank(confirmationMessage)
						? String.format(I18nProperties.getString(Strings.confirmationDeleteEntity), entityName)
						: confirmationMessage,
					this::onDelete);
			}, false);
		}

		return deleteButton;
	}

	public Button getDeleteWithReasonOrRestoreButton(String entityName, boolean deleted, String details) {

		if (deleteButton == null) {
			deleteButton = buildDeleteButton(() -> {
				if (!deleted) {
					DeletableUtils.showDeleteWithReasonPopup(
						String.format(
							I18nProperties.getString(Strings.confirmationDeleteEntityWithDetails),
							entityName,
							details != null ? details : ""),
						this::onDeleteWithReason);
				} else {
					onDeleteWithReason(null);
				}
			}, deleted);
		}

		return deleteButton;
	}

	private Button buildDeleteButton(Runnable deletePopupCallback, boolean deleted) {

		Button deleteButton = ButtonHelper.createButton(
			DELETE_RESTORE,
			deleted ? I18nProperties.getCaption(Captions.actionRestore) : I18nProperties.getCaption(Captions.actionDelete),
			e -> {
				if (isDirty()) {
					DirtyCheckPopup.show(this, () -> deletePopupCallback.run());
				} else {
					deletePopupCallback.run();
				}
			},
			ValoTheme.BUTTON_DANGER,
			CssStyles.BUTTON_BORDER_NEUTRAL);

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
			return ((Buffered) wrappedComponent).isModified();
		}
		return false;
	}

	public boolean isCommited() {
		return commited;
	}

	@Override
	public void commit() throws InvalidValueException, SourceException, CommitRuntimeException {

		if (preCommitListener != null) {
			preCommitListener.onPreCommit(this::doCommit);
		} else {
			doCommit();
		}

		if (postCommitListener != null) {
			postCommitListener.run();
		}

	}

	private void doCommit() throws InvalidValueException, SourceException, CommitRuntimeException {
		if (fieldGroups != null) {
			if (fieldGroups.size() > 1) {
				List<InvalidValueException> invalidValueExceptions =
					fieldGroups.stream().filter(fieldGroup -> !fieldGroup.isValid()).map(fieldGroup -> {
						try {
							// all invalid fieldGroups are committed to fetch the CommitExceptions
							fieldGroup.commit();
						} catch (CommitException e) {
							return e;
						}
						// when the fieldGroup did not throw a CommitException, it is invalid and committed
						throw new IllegalStateException();
					}).map(e -> {
						// keep invalid value exceptions, throw the rest
						Throwable c = e.getCause();
						if (c instanceof InvalidValueException) {
							return (InvalidValueException) c;
						} else if (c instanceof SourceException) {
							throw (SourceException) c;
						} else {
							throw new CommitRuntimeException(e);
						}
					}).collect(Collectors.toList());

				if (invalidValueExceptions.isEmpty()) {
					//NOOP
				} else if (invalidValueExceptions.size() == 1) {
					throw invalidValueExceptions.get(0);
				} else {
					throw new InvalidValueException(
						null,
						invalidValueExceptions.stream()
							.map(InvalidValueException::getCauses)
							.flatMap(Arrays::stream)
							.toArray(InvalidValueException[]::new));
				}
			}

			try {
				for (FieldGroup fieldGroup : fieldGroups) {
					fieldGroup.commit();
				}
			} catch (CommitException e) {
				Throwable c = e.getCause();
				if (c instanceof InvalidValueException) {
					throw (InvalidValueException) c;
				} else if (c instanceof SourceException) {
					throw (SourceException) c;
				} else {
					throw new CommitRuntimeException(e);
				}
			}
		} else if (wrappedComponent instanceof Buffered) {
			((Buffered) wrappedComponent).commit();
		} else {
			// NOOP
		}
		dirty = false;

		onCommit();
		commited = true;
		onDone();
	}

	private String findHtmlMessage(InvalidValueException exception) {
		if (!(exception.getMessage() == null || exception.getMessage().isEmpty()))
			return exception.getHtmlMessage();

		for (InvalidValueException cause : exception.getCauses()) {
			String message = findHtmlMessage(cause);
			if (message != null)
				return message;
		}

		return null;
	}

	private String findHtmlMessageDetails(InvalidValueException exception) {
		for (InvalidValueException cause : exception.getCauses()) {
			if (!cause.getMessage().equalsIgnoreCase(exception.getMessage())) {
				String message = findHtmlMessage(cause);
				if (message != null && !message.equalsIgnoreCase(exception.getMessage()))
					return message;
			}
		}

		return null;
	}

	@Override
	public boolean commitAndHandle() {
		try {
			commit();
			return true;
		} catch (InvalidValueException ex) {
			StringBuilder htmlMsg = new StringBuilder();
			String message = ex.getMessage();
			if (message != null && !message.isEmpty() && ex.getCauses().length == 0) {
				htmlMsg.append(ex.getHtmlMessage());
			} else {

				List<InvalidValueException> causes = extractCauses(ex);

				if (causes != null) {

					if (causes.size() > 1) {
						htmlMsg.append("<ul>");
						// All again
						for (InvalidValueException cause : causes) {
							htmlMsg.append("<li style=\"color: #FFF;\">").append(findHtmlMessage(cause)).append("</li>");
						}
						htmlMsg.append("</ul>");
					} else if (causes.size() > 0) {
						htmlMsg.append("<ul>");
						InvalidValueException firstCause = causes.get(0);
						String info = findHtmlMessage(firstCause);
						boolean validInfo = nonNull(info) && !info.isEmpty() && !info.equalsIgnoreCase("null");
						if (validInfo) {
							htmlMsg.append("<li style=\"color: #FFF;\">").append(info).append("</li>");
							htmlMsg.append("</ul>");
						}
						String additionalInfo = findHtmlMessageDetails(firstCause);
						if (nonNull(additionalInfo) && !additionalInfo.isEmpty()) {
							if (validInfo) {
								htmlMsg.append(" : ");
							}
							htmlMsg.append(findHtmlMessageDetails(firstCause));
						}
					}
				}
			}

			new Notification(I18nProperties.getString(Strings.messageCheckInputData), htmlMsg.toString(), Type.ERROR_MESSAGE, true)
				.show(Page.getCurrent());

			return false;
		}
	}

	public List<InvalidValueException> extractCauses(InvalidValueException cause) {
		List<Validator.InvalidValueException> tempCauses = new ArrayList<>();

		if (cause.isInvisible()) {
			return tempCauses;
		}

		if (cause.getCauses().length == 0) {
			tempCauses.add(cause);
		}

		for (InvalidValueException childCause : cause.getCauses()) {
			tempCauses.addAll(extractCauses(childCause));
		}

		return tempCauses;
	}

	@Override
	public void discard() {
		if (fieldGroups != null) {
			for (FieldGroup fieldGroup : fieldGroups) {
				fieldGroup.discard();
			}
		} else if (wrappedComponent instanceof Buffered) {
			((Buffered) wrappedComponent).discard();
		} else {
			// NOOP
		}
		dirty = false;

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
			((Buffered) wrappedComponent).setBuffered(buffered);
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

	public void setPreCommitListener(PreCommitListener listener) {
		this.preCommitListener = listener;
	}

	public void setPostCommitListener(Runnable postCommitListener) {
		this.postCommitListener = postCommitListener;
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

	protected void onCommit() {

		for (CommitListener listener : commitListeners) {
			try {
				listener.onCommit();
			} catch (CannotProceedException e) {
				break;
			}
		}
	}

	/**
	 * 
	 * Adds a discard listener , but does not show it.
	 * 
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
			buttonsPanel.addComponent(getDeleteButton(entityName, null), 0);
		if (!deleteListeners.contains(listener))
			deleteListeners.add(listener);
	}

	public void addDeleteListener(DeleteListener listener, String entityName, Supplier<String> confirmationMessageSupplier) {
		if (deleteListeners.isEmpty())
			buttonsPanel.addComponent(getDeleteButton(entityName, confirmationMessageSupplier), 0);
		if (!deleteListeners.contains(listener))
			deleteListeners.add(listener);
	}

	public void addDeleteWithReasonOrRestoreListener(DeleteWithDetailsListener listener, String entityName) {

		addDeleteWithReasonListener(listener, entityName, null);
	}

	public void addDeleteWithReasonListener(DeleteWithDetailsListener listener, String entityName, String details) {

		if (deleteWithDetailsListeners.isEmpty()) {
			buttonsPanel.addComponent(getDeleteWithReasonOrRestoreButton(entityName, false, null), 0);
		}
		if (!deleteWithDetailsListeners.contains(listener)) {
			deleteWithDetailsListeners.add(listener);
		}
	}

	public void addDeleteWithReasonOrRestoreListener(
		DeleteWithDetailsListener deleteListener,
		String details,
		DeleteWithDetailsListener restoreListener,
		String entityName,
		String entityUuid,
		CoreFacade coreFacade) {

		final boolean deleted = coreFacade.isDeleted(entityUuid);

		if (deleteWithDetailsListeners.isEmpty()) {
			buttonsPanel.addComponent(getDeleteWithReasonOrRestoreButton(entityName, deleted, details), 0);
		}

		if (!deleted) {
			deleteWithDetailsListeners.add(deleteListener);
		} else {
			deleteWithDetailsListeners.add(restoreListener);
		}
	}

	public void addDeleteWithReasonOrRestoreListener(
		DeleteWithDetailsListener deleteListener,
		DeleteWithDetailsListener restoreListener,
		String entityName,
		boolean isDeleted) {

		if (deleteWithDetailsListeners.isEmpty()) {
			buttonsPanel.addComponent(getDeleteWithReasonOrRestoreButton(entityName, isDeleted, null), 0);
		}

		if (!isDeleted) {
			deleteWithDetailsListeners.add(deleteListener);
		} else {
			deleteWithDetailsListeners.add(restoreListener);
		}
	}

	public void addDeleteWithReasonOrRestoreListener(
		String viewName,
		String details,
		String entityName,
		String entityUuid,
		DeletableFacade deletableFacade) {
		addDeleteWithReasonOrRestoreListener(viewName, details, entityName, entityUuid, coreFacade, null, null);
	}

	public void addDeleteWithReasonOrRestoreListener(
		String viewName,
		String details,
		String entityName,
		String entityUuid,
		DeletableFacade deletableFacade,
		String restoreInvalidMessage,
		Function<String, Boolean> checkRestoreValidity) {

		final boolean deleted = deletableFacade.isDeleted(entityUuid);

		if (deleteWithDetailsListeners.isEmpty()) {
			buttonsPanel.addComponent(getDeleteWithReasonOrRestoreButton(entityName, deleted, details), 0);
		}

		if (!deleted) {
			deleteWithDetailsListeners.add((deleteDetails) -> deletableFacade.delete(entityUuid, deleteDetails));
		} else {
			deleteWithDetailsListeners.add((deleteDetails) -> {
				if (checkRestoreValidity == null || checkRestoreValidity.apply(entityUuid)) {
					deletableFacade.restore(entityUuid);
				} else {
					NotificationHelper.showNotification(restoreInvalidMessage, Type.ERROR_MESSAGE, -1);
				}
			});
		}
		deleteWithDetailsListeners.add((deleteDetails) -> UI.getCurrent().getNavigator().navigateTo(viewName));

	}

	private void onDelete() {
		for (DeleteListener listener : deleteListeners)
			listener.onDelete();
	}

	private void onDeleteWithReason(DeletionDetails deletionDetails) {
		for (DeleteWithDetailsListener listener : deleteWithDetailsListeners) {
			listener.onDelete(deletionDetails);
		}
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		try {
			super.setReadOnly(readOnly);
		} catch (IllegalStateException e) {
			super.setEnabled(readOnly);
		}
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

	@Override
	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	//In case of having delete right without edit right the delete button should remain enabled
	public void restrictEditableComponentsOnEditView(
		UserRight editParentRight,
		UserRight editChildRight,
		UserRight deleteEntityRight,
		EditPermissionType editPermissionType,
		boolean isInJurisdiction) {

		boolean isEditAllowed = isEditAllowed(editParentRight, editChildRight, editPermissionType);

		if (!isEditAllowed) {
			if (isInJurisdiction && isUserRightAllowed(deleteEntityRight)) {
				addToActiveButtonsList(CommitDiscardWrapperComponent.DELETE_RESTORE);
			}

			this.setNonEditable();
		}
	}

	public void restrictEditableComponentsOnEditView(
		UserRight editParentRight,
		UserRight editChildRight,
		UserRight deleteEntityRight,
		UserRight archiveEntityRight,
		EditPermissionType editPermissionType,
		boolean isInJurisdiction) {

		boolean isEditAllowed = isEditAllowed(editParentRight, editChildRight, editPermissionType);

		if (!isEditAllowed) {
			if (isInJurisdiction && isUserRightAllowed(deleteEntityRight)) {
				addToActiveButtonsList(CommitDiscardWrapperComponent.DELETE_RESTORE);
			}
			if (isInJurisdiction && archiveEntityRight != null && isUserRightAllowed(archiveEntityRight)) {
				addToActiveButtonsList(ArchivingController.ARCHIVE_DEARCHIVE_BUTTON_ID);
			}

			this.setNonEditable();
		}
	}

	public void setNonEditable() {
		this.setEditable(false, activeButtons.stream().toArray(String[]::new));
	}

	public boolean isUserRightAllowed(UserRight userRight) {
		return UserProvider.getCurrent().hasUserRight(userRight);
	}

	public boolean isEditAllowed(UserRight editParentRight, UserRight editChildRight, EditPermissionType editPermissionType) {
		if (editChildRight != null) {
			return UserProvider.getCurrent().hasUserRight(editParentRight) && UserProvider.getCurrent().hasUserRight(editChildRight);
		} else {
			return UserProvider.getCurrent().hasUserRight(editParentRight)
				&& (editPermissionType == null || editPermissionType == EditPermissionType.ALLOWED);
		}
	}

	//excludedButtons: contains the buttons attached to the CommitDiscardWrapperComponent which we intend to
	// exclude from applying a new editable status
	public void setEditable(boolean editable, String... excludedButtons) {
		wrappedComponent.setEnabled(editable);

		for (int i = 0; i < buttonsPanel.getComponentCount(); i++) {
			Component button = buttonsPanel.getComponent(i);
			if (!ArrayUtils.contains(excludedButtons, button.getId())) {
				button.setEnabled(editable);
			}
		}
	}

	public void addToActiveButtonsList(String button) {
		activeButtons.add(button);
	}

	public void setButtonsVisible(boolean visible) {
		buttonsPanel.setVisible(visible);
	}
}
