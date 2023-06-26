package de.symeda.sormas.ui.utils;

import java.util.List;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.DeletableFacade;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolRuntimeException;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.HtmlHelper;

public class DeleteRestoreController<F extends DeletableFacade> {

	public void restoreSelectedItems(List<String> entityUuids, F entityFacade, CoreEntityRestoreMessages messages, Runnable callback) {

		//TODO: extract to checkIfSelectionExists
		if (entityUuids.isEmpty()) {
			displayNothingSelectedToBeRestored(messages);
			return;
		}

		Label restoreConfirmationMessage = new Label();
		restoreConfirmationMessage.setValue(
			String.format(
				I18nProperties.getString(Strings.confirmationRestoreEntities),
				entityUuids.size(),
				I18nProperties.getString(messages.getEntities()).toLowerCase()));

		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.headingRestoreConfirmation),
			restoreConfirmationMessage,
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			500,
			confirmed -> {
				if (Boolean.TRUE.equals(confirmed)) {
					performRestoreSelectedItems(entityUuids, entityFacade, messages, callback);
				}
			});
	}

	public void deleteAllSelectedItems(
		List<String> entityUuids,
		F entityFacade,
		CoreEntityDeleteMessages messages,
		boolean allItemsEligibleForDeletion,
		Runnable callback) {

		if (entityUuids.isEmpty()) {
			displayNothingSelectedToBeDeleted(messages);
			return;
		}

		String deleteWithReasonConfirmationMessage = String.format(
			I18nProperties.getString(Strings.confirmationDeleteEntities),
			entityUuids.size(),
			I18nProperties.getString(messages.getEntities()).toLowerCase()) + getDeleteConfirmationDetails(messages.getEntities(), entityUuids);

		DeletableUtils.showDeleteWithReasonPopup(
			deleteWithReasonConfirmationMessage,
			(deleteDetails) -> performDeleteSelectedItems(entityUuids, entityFacade, messages, deleteDetails, allItemsEligibleForDeletion, callback));

	}

	private void performRestoreSelectedItems(List<String> entityUuids, F entityFacade, CoreEntityRestoreMessages messages, Runnable callback) {

		int unrestoredEntityCount = 0;
		StringBuilder unrestoredEntitiesSb = new StringBuilder();

		for (String selectedRow : entityUuids) {
			try {
				entityFacade.restore(selectedRow);
			} catch (Exception e) {
				unrestoredEntityCount++;
				unrestoredEntitiesSb.append(selectedRow, 0, 6).append(", ");
			}
		}

		if (unrestoredEntitiesSb.length() > 0) {
			unrestoredEntitiesSb = new StringBuilder(" " + unrestoredEntitiesSb.substring(0, unrestoredEntitiesSb.length() - 2) + ". ");
		}

		callback.run();
		handleRestoreResult(unrestoredEntityCount, messages, unrestoredEntitiesSb.toString());
	}

	private void performDeleteSelectedItems(
		List<String> entityUuids,
		F entityFacade,
		CoreEntityDeleteMessages messages,
		DeletionDetails deleteDetails,
		boolean allItemsAreEligibleForDeletion,
		Runnable callback) {

		if (allItemsAreEligibleForDeletion) {
			int undeletedEntityCount = 0;
			int undeletedEntityExternalReasonCount = 0;
			StringBuilder unDeletedEntitiesSb = new StringBuilder();
			StringBuilder unDeletedEntitiesExternalReasonSb = new StringBuilder();
			for (String selectedRow : entityUuids) {
				try {
					entityFacade.delete(selectedRow, deleteDetails);
				} catch (ExternalSurveillanceToolRuntimeException e) {
					undeletedEntityExternalReasonCount++;
					unDeletedEntitiesExternalReasonSb.append(selectedRow, 0, 6).append(", ");
				} catch (Exception e) {
					undeletedEntityCount++;
					unDeletedEntitiesSb.append(selectedRow, 0, 6).append(", ");
				}
			}

			if (unDeletedEntitiesSb.length() > 0) {
				unDeletedEntitiesSb = new StringBuilder(" " + unDeletedEntitiesSb.substring(0, unDeletedEntitiesSb.length() - 2) + ". ");
			}

			if (unDeletedEntitiesExternalReasonSb.length() > 0) {
				unDeletedEntitiesExternalReasonSb =
					new StringBuilder(" " + unDeletedEntitiesExternalReasonSb.substring(0, unDeletedEntitiesExternalReasonSb.length() - 2) + ". ");
			}

			callback.run();
			handleDeleteItemsResult(
				undeletedEntityCount,
				undeletedEntityExternalReasonCount,
				0,
				messages,
				unDeletedEntitiesSb.toString(),
				unDeletedEntitiesExternalReasonSb.toString(),
				null);

		} else {
			//for now only events with event participants will not be eligible for deletion
			performDeleteSelectedItemsWithIneligibleItems(entityUuids, entityFacade, messages, deleteDetails, callback);
		}
	}

	private void performDeleteSelectedItemsWithIneligibleItems(
		List<String> entityUuids,
		F entityFacade,
		CoreEntityDeleteMessages messages,
		DeletionDetails deleteDetails,
		Runnable callback) {

		int undeletedEntityCount = 0;
		int undeletedEntityWithLinkedEntitiesCount = 0;
		int undeletedEntityExternalReasonCount = 0;
		StringBuilder unDeletedEntitiesSb = new StringBuilder();
		StringBuilder unDeletedEntitiesWithLinkedEntitiesSb = new StringBuilder();
		StringBuilder unDeletedEntitiesExternalReasonSb = new StringBuilder();

		for (String selectedRow : entityUuids) {
			if (existEventParticipantsLinkedToEvent(selectedRow)) {
				undeletedEntityWithLinkedEntitiesCount++;
				unDeletedEntitiesWithLinkedEntitiesSb.append(selectedRow, 0, 6).append(", ");
			} else {
				try {
					entityFacade.delete(selectedRow, deleteDetails);
				} catch (ExternalSurveillanceToolRuntimeException e) {
					undeletedEntityExternalReasonCount++;
					unDeletedEntitiesExternalReasonSb.append(selectedRow, 0, 6).append(", ");
				} catch (Exception e) {
					undeletedEntityCount++;
					unDeletedEntitiesSb.append(selectedRow, 0, 6).append(", ");
				}
			}
		}

		if (unDeletedEntitiesSb.length() > 0) {
			unDeletedEntitiesSb = new StringBuilder(" " + unDeletedEntitiesSb.substring(0, unDeletedEntitiesSb.length() - 2) + ". ");
		}

		if (unDeletedEntitiesWithLinkedEntitiesSb.length() > 0) {
			unDeletedEntitiesWithLinkedEntitiesSb = new StringBuilder(
				" " + unDeletedEntitiesWithLinkedEntitiesSb.substring(0, unDeletedEntitiesWithLinkedEntitiesSb.length() - 2) + ". ");
		}

		if (unDeletedEntitiesExternalReasonSb.length() > 0) {
			unDeletedEntitiesExternalReasonSb =
				new StringBuilder(" " + unDeletedEntitiesExternalReasonSb.substring(0, unDeletedEntitiesExternalReasonSb.length() - 2) + ". ");
		}

		callback.run();

		handleDeleteItemsResult(
			undeletedEntityCount,
			undeletedEntityExternalReasonCount,
			undeletedEntityWithLinkedEntitiesCount,
			messages,
			unDeletedEntitiesSb.toString(),
			unDeletedEntitiesExternalReasonSb.toString(),
			unDeletedEntitiesWithLinkedEntitiesSb.toString());
	}

	private void handleRestoreResult(int unrestoredEntityCount, CoreEntityRestoreMessages messages, String unrestoredEntitiesString) {

		if (unrestoredEntityCount == 0) {
			displaySuccessNotification(messages.getHeadingEntitiesRestored(), messages.getMessageEntitiesRestored());
		} else {
			//TODO: add messages for all the entities, add missing strings too
			// 	I18nProperties.getString(Strings.messageCasesNotRestored)),
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
		CoreEntityDeleteMessages messages,
		String undeletedEntitiesString,
		String undeletedEntitiesExternalReasonString,
		String undeletedEntitiesWithReasonString) {

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
							undeletedEntitiesWithReasonString,
							messages.getMessageEntitiesNotDeletedLinkedEntitiesReason()))
					.append("<br/> <br/>");
			}

			if (undeletedEntityExternalReasonCount > 0) {
				description
					.append(
						getDetails(
							messages.getMessageCountEntitiesNotDeleted(),
							undeletedEntityExternalReasonCount,
							undeletedEntitiesWithReasonString,
							messages.getMessageEntitiesNotDeletedExternalReason()))
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

	private Boolean existEventParticipantsLinkedToEvent(String uuid) {
		List<EventParticipantDto> eventParticipantList = FacadeProvider.getEventParticipantFacade().getAllActiveEventParticipantsByEvent(uuid);

		return !eventParticipantList.isEmpty();
	}

	private void displayNothingSelectedToBeDeleted(CoreEntityDeleteMessages messages) {
		new Notification(
			I18nProperties.getString(messages.getHeadingNoSelection()),
			I18nProperties.getString(messages.getMessageNoSelection()),
			Notification.Type.WARNING_MESSAGE,
			false).show(Page.getCurrent());
	}

	private void displayNothingSelectedToBeRestored(CoreEntityRestoreMessages messages) {
		new Notification(
			I18nProperties.getString(messages.getHeadingNoSelection()),
			I18nProperties.getString(messages.getMessageNoSelection()),
			Notification.Type.WARNING_MESSAGE,
			false).show(Page.getCurrent());
	}

	public String getDeleteConfirmationDetails(String entity, List<String> entityUuids) {
		boolean hasPendingRequest = false;

		switch (entity) {
		case "entityCases":
			hasPendingRequest = FacadeProvider.getSormasToSormasCaseFacade().hasPendingRequest(entityUuids);
			break;
		case "entityContacts":
			hasPendingRequest = FacadeProvider.getSormasToSormasContactFacade().hasPendingRequest(entityUuids);
			break;
		case "entityEvents":
			hasPendingRequest = FacadeProvider.getSormasToSormasEventFacade().hasPendingRequest(entityUuids);
			break;
		default:
			break;
		}
		return hasPendingRequest ? "<br/> <br/>" + I18nProperties.getString(Strings.messageDeleteWithPendingShareRequest) + "<br/>" : "";
	}

}
