package de.symeda.sormas.ui.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.DeletableFacade;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.HtmlHelper;
import de.symeda.sormas.api.uuid.HasUuid;

public class DeleteRestoreController<F extends DeletableFacade> {

	public <T extends HasUuid> void restoreSelectedItems(
		Collection<T> entities,
		IDeleteRestoreHandler<?> deleteHandler,
		Consumer<List<T>> batchCallback) {

		if (entities.isEmpty()) {
			displayNothingSelectedToBeRestored(deleteHandler.getDeleteRestoreMessages());
			return;
		}

		Label restoreConfirmationMessage = new Label();
		restoreConfirmationMessage.setValue(
			String.format(
				I18nProperties.getString(Strings.confirmationRestoreEntities),
				entities.size(),
				I18nProperties.getString(deleteHandler.getDeleteRestoreMessages().getEntities()).toLowerCase()));

		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.headingRestoreConfirmation),
			restoreConfirmationMessage,
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			500,
			confirmed -> {
				if (Boolean.TRUE.equals(confirmed)) {
					List<T> selectedEntitiesCpy = new ArrayList<>(entities);
					List<T> selectedEligibleEntitiesCpy = new ArrayList<>(entities);
					this.<T> createBulkOperationHandler(deleteHandler, false)
						.doBulkOperation(
							selectedEntries -> deleteHandler.restore(selectedEntries.stream().map(HasUuid::getUuid).collect(Collectors.toList())),
							selectedEntitiesCpy,
							selectedEligibleEntitiesCpy,
							null,
							batchCallback);
				}
			});
	}

	public <T extends HasUuid> void deleteAllSelectedItems(
		Collection<T> entities,
		Collection<T> eligibleEntities,
		Collection<T> ineligibleEntities,
		IDeleteRestoreHandler<?> deleteHandler,
		Consumer<List<T>> batchCallback) {

		if (entities.isEmpty()) {
			displayNothingSelectedToBeDeleted(deleteHandler.getDeleteRestoreMessages());
			return;
		}

		String deleteWithReasonConfirmationMessage = String.format(
			I18nProperties.getString(Strings.confirmationDeleteEntities),
			entities.size(),
			I18nProperties.getString(deleteHandler.getDeleteRestoreMessages().getEntities()).toLowerCase())
			+ getDeleteConfirmationDetails(deleteHandler.getDeleteRestoreMessages().getEntities(), entities);

		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setMargin(false);

		Label contentLabel = new Label(deleteWithReasonConfirmationMessage, ContentMode.HTML);
		contentLabel.addStyleName(CssStyles.LABEL_WHITE_SPACE_NORMAL);
		contentLabel.setWidthFull();
		verticalLayout.addComponent(contentLabel);

		deleteHandler.addAdditionalDeleteReasonField(verticalLayout);

		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.headingDeleteConfirmation),
			verticalLayout,
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			640,
			confirmed -> {
				if (Boolean.TRUE.equals(confirmed)) {
					if (!deleteHandler.validateAdditionalDeleteReasonFields()) {
						return false;
					}
					deleteHandler.clearOtherReason();

					List<T> selectedEntitiesCpy = new ArrayList<>(entities);
					List<T> selectedEligibleEntitiesCpy = eligibleEntities != null ? new ArrayList<>(eligibleEntities) : new ArrayList<>(entities);
					List<T> selectedIneligibleEntitiesCpy = ineligibleEntities != null ? new ArrayList<>(ineligibleEntities) : null;
					this.<T> createBulkOperationHandler(deleteHandler, true)
						.doBulkOperation(
							selectedEntries -> deleteHandler.delete(
								selectedEntries.stream().map(HasUuid::getUuid).collect(Collectors.toList()),
								new DeletionDetails(
									deleteHandler.getDeleteReasonComboBox().getValue(),
									deleteHandler.getOtherDeletionReason().getValue())),
							selectedEntitiesCpy,
							selectedEligibleEntitiesCpy,
							selectedIneligibleEntitiesCpy,
							batchCallback);
				}

				return true;
			});
	}

	private <T extends HasUuid> BulkOperationHandler<T> createBulkOperationHandler(IDeleteRestoreHandler<?> deleteHandler, boolean forDelete) {
		DeleteRestoreMessages deleteRestoreMessages = deleteHandler.getDeleteRestoreMessages();
		return new BulkOperationHandler<>(

			forDelete ? deleteRestoreMessages.getMessageEntitiesDeleted() : deleteRestoreMessages.getMessageEntitiesRestored(),
			//TODO: change the label name to notEligibleItems
			forDelete ? deleteRestoreMessages.getMessageEntitiesNotDeletedLinkedEntitiesReason() : null,
			forDelete ? deleteRestoreMessages.getHeadingSomeEntitiesNotDeleted() : deleteRestoreMessages.getHeadingSomeEntitiesNotRestored(),
			forDelete ? deleteRestoreMessages.getMessageCountEntitiesNotDeleted() : deleteRestoreMessages.getMessageCountEntitiesNotRestored(),
			forDelete ? deleteRestoreMessages.getMessageEntitiesNotDeleted() : deleteRestoreMessages.getMessageEntitiesNotRestored(),
			forDelete ? deleteRestoreMessages.getMessageNoEligibleEntitySelected() : null);
	}

	private void handleRestoreResult(int unrestoredEntityCount, DeleteRestoreMessages messages, String unrestoredEntitiesString) {
		if (unrestoredEntityCount == 0) {
			displaySuccessNotification(messages.getHeadingEntitiesRestored(), messages.getMessageEntitiesRestored());
		} else {
			//TODO: add messages for all the entities, add missing strings too
			// I18nProperties.getString(Strings.messageCasesNotRestored)),
			showSimplePopUp(
				messages.getHeadingSomeEntitiesNotRestored(),
				getDetails(
					messages.getMessageCountEntitiesNotRestored(),
					unrestoredEntityCount,
					unrestoredEntitiesString,
					messages.getMessageEntitiesNotRestored()));
		}
	}

	private void handleDeleteItemsResult(
		int undeletedEntityCount,
		int undeletedEntityExternalReasonCount,
		int undeletedEntityLinkedEntitiesReasonCount,
		DeleteRestoreMessages messages,
		String undeletedEntitiesString,
		String undeletedEntitiesExternalReasonString,
		String undeletedEntitiesLinkedEntitiesReasonString) {
		//TODO: test this condition
		if (undeletedEntityCount == 0 && undeletedEntityExternalReasonCount == 0 && undeletedEntityLinkedEntitiesReasonCount == 0) {
			displaySuccessNotification(messages.getHeadingEntitiesDeleted(), messages.getMessageEntitiesDeleted());
		} else {
			StringBuilder description = new StringBuilder();
			if (undeletedEntityLinkedEntitiesReasonCount > 0) {
				description
					.append(
						getDetails(
							messages.getMessageCountEntitiesNotDeleted(),
							undeletedEntityLinkedEntitiesReasonCount,
							undeletedEntitiesLinkedEntitiesReasonString,
							""))
					//messages.getMessageEntitiesNotDeletedLinkedEntitiesReason()""))
					.append("<br/> <br/>");
			}
			if (undeletedEntityExternalReasonCount > 0) {
				description
					.append(
						getDetails(
							messages.getMessageCountEntitiesNotDeleted(),
							undeletedEntityExternalReasonCount,
							undeletedEntitiesExternalReasonString,
							""))
					//messages.getMessageEntitiesNotDeletedExternalReason()))
					.append("<br/> <br/>");
			}
			if (undeletedEntityCount > 0) {
				description.append(
					getDetails(
						messages.getMessageCountEntitiesNotDeleted(),
						undeletedEntityCount,
						undeletedEntitiesString,
						messages.getMessageEntitiesNotDeleted()));
			}
			showSimplePopUp(messages.getHeadingSomeEntitiesNotDeleted(), description.toString());
		}
	}

	private Window showSimplePopUp(String heading, String message) {
		Window window = VaadinUiUtil.showSimplePopupWindow(I18nProperties.getString(heading), message, ContentMode.HTML);
		window.setWidth(600, Sizeable.Unit.PIXELS);
		return window;
	}

	private String getDetails(
		String messageCountEntitiesNotDeleted,
		int undeletedEntityCount,
		String undeletedEntitiesString,
		String messageEntitiesNotDeleted) {
		return String.format(
			"%1s <br/> <br/> %2s",
			String.format(
				I18nProperties.getString(messageCountEntitiesNotDeleted),
				String.format("<b>%s</b>", undeletedEntityCount),
				String.format("<b>%s</b>", HtmlHelper.cleanHtml(undeletedEntitiesString))),
			I18nProperties.getString(messageEntitiesNotDeleted));
	}

	private void displaySuccessNotification(String heading, String message) {
		new Notification(I18nProperties.getString(heading), I18nProperties.getString(message), Notification.Type.TRAY_NOTIFICATION, false)
			.show(Page.getCurrent());
	}

	private void displayNothingSelectedToBeDeleted(DeleteRestoreMessages messages) {
		new Notification(
			I18nProperties.getString(messages.getHeadingNoSelection()),
			I18nProperties.getString(messages.getMessageNoSelection()),
			Notification.Type.WARNING_MESSAGE,
			false).show(Page.getCurrent());
	}

	private void displayNothingSelectedToBeRestored(DeleteRestoreMessages messages) {
		new Notification(
			I18nProperties.getString(messages.getHeadingNoSelection()),
			I18nProperties.getString(messages.getMessageNoSelection()),
			Notification.Type.WARNING_MESSAGE,
			false).show(Page.getCurrent());
	}

	public <T extends HasUuid> String getDeleteConfirmationDetails(String entity, Collection<T> entities) {

		List<String> entitiesList = entities.stream().map(T::getUuid).collect(Collectors.toList());
		boolean hasPendingRequest = false;
		switch (entity) {
		case "entityCases":
			hasPendingRequest = FacadeProvider.getSormasToSormasCaseFacade().hasPendingRequest(entitiesList);
			break;
		case "entityContacts":
			hasPendingRequest = FacadeProvider.getSormasToSormasContactFacade().hasPendingRequest(entitiesList);
			break;
		case "entityEvents":
			hasPendingRequest = FacadeProvider.getSormasToSormasEventFacade().hasPendingRequest(entitiesList);
			break;
		default:
			break;
		}
		return hasPendingRequest ? "<br/> <br/>" + I18nProperties.getString(Strings.messageDeleteWithPendingShareRequest) + "<br/>" : "";
	}

	public interface IDeleteRestoreHandler<T extends HasUuid> {

		void delete(String uuid, DeletionDetails deletionDetails);

		int delete(List<String> uuids, DeletionDetails deletionDetails);

		int restore(List<String> uuids);

		DeleteRestoreMessages getDeleteRestoreMessages();

		void addAdditionalDeleteReasonField(VerticalLayout verticalLayout);

		boolean validateAdditionalDeleteReasonFields();

		ComboBox<DeletionReason> getDeleteReasonComboBox();

		TextArea getOtherDeletionReason();

		void clearOtherReason();

	}

}
