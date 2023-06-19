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
				I18nProperties.getString(messages.getEntities())));

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

	public void deleteAllSelectedItems(List<String> entityUuids, F entityFacade, CoreEntityDeleteMessages messages, Runnable callback) {
		if (entityUuids.isEmpty()) {
			displayNothingSelectedToBeDeleted(messages);
			return;
		}

		String deleteWithReasonConfirmationMessage = String.format(
			I18nProperties.getString(Strings.confirmationDeleteEntities),
			entityUuids.size(),
			I18nProperties.getString(messages.getEntities())) + getDeleteConfirmationDetails(messages.getEntities(), entityUuids);

		DeletableUtils.showDeleteWithReasonPopup(
			deleteWithReasonConfirmationMessage,
			(deleteDetails) -> performDeleteSelectedItems(entityUuids, entityFacade, messages, deleteDetails, callback));

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
		Runnable callback) {

		int undeletedEntityCount = 0;
		StringBuilder unDeletedEntitiesSb = new StringBuilder();

		for (String selectedRow : entityUuids) {
			try {
				entityFacade.delete(selectedRow, deleteDetails);
			} catch (Exception e) {
				undeletedEntityCount++;
				unDeletedEntitiesSb.append(selectedRow, 0, 6).append(", ");
			}
		}

		if (unDeletedEntitiesSb.length() > 0) {
			unDeletedEntitiesSb = new StringBuilder(" " + unDeletedEntitiesSb.substring(0, unDeletedEntitiesSb.length() - 2) + ". ");
		}

		callback.run();
		handleDeleteResult(undeletedEntityCount, messages, unDeletedEntitiesSb.toString());
	}

	private void handleRestoreResult(int unrestoredEntityCount, CoreEntityRestoreMessages messages, String unrestoredEntitiesString) {

		if (unrestoredEntityCount == 0) {
			new Notification(
				I18nProperties.getString(messages.getHeadingEntitiesRestored()),
				I18nProperties.getString(messages.getMessageEntitiesRestored()),
				Notification.Type.TRAY_NOTIFICATION,
				false).show(Page.getCurrent());
		} else {
			Window response = VaadinUiUtil.showSimplePopupWindow(
				I18nProperties.getString(messages.getHeadingSomeEntitiesNotRestored()),
				String.format(
					"%1s <br/> <br/> %2s",
					String.format(
						I18nProperties.getString(messages.getMessageCountEntitiesNotRestored()),
						String.format("<b>%s</b>", unrestoredEntityCount),
						String.format("<b>%s</b>", HtmlHelper.cleanHtml(unrestoredEntitiesString))),
					//TODO: add messages for all the entities, add missing strings too
					//	I18nProperties.getString(Strings.messageCasesNotRestored)),
					I18nProperties.getString(messages.getMessageEntitiesNotRestored())),
				ContentMode.HTML);
			response.setWidth(600, Sizeable.Unit.PIXELS);
		}
	}

	private void handleDeleteResult(int undeletedEntityCount, CoreEntityDeleteMessages messages, String undeletedEntitiesString) {

		if (undeletedEntityCount == 0) {
			new Notification(
				I18nProperties.getString(messages.getHeadingEntitiesDeleted()),
				I18nProperties.getString(messages.getMessageEntitiesDeleted()),
				Notification.Type.TRAY_NOTIFICATION,
				false).show(Page.getCurrent());
		} else {
			Window response = VaadinUiUtil.showSimplePopupWindow(
				I18nProperties.getString(messages.getHeadingSomeEntitiesNotDeleted()),
				String.format(
					"%1s <br/> <br/> %2s",
					String.format(
						I18nProperties.getString(messages.getMessageCountEntitiesNotDeleted()),
						String.format("<b>%s</b>", undeletedEntityCount),
						String.format("<b>%s</b>", HtmlHelper.cleanHtml(undeletedEntitiesString))),
					//TODO: change for events -> will be 2 kind of messages based on the exception type
					I18nProperties.getString(messages.getMessageEntitiesNotDeleted())),
				ContentMode.HTML);
			response.setWidth(600, Sizeable.Unit.PIXELS);
		}
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
