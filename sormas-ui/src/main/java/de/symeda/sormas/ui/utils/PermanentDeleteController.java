package de.symeda.sormas.ui.utils;

import java.util.List;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.PermanentlyDeletableFacade;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.HtmlHelper;

public class PermanentDeleteController<F extends PermanentlyDeletableFacade> {

	public void deleteAllSelectedItems(
		List<String> entityUuids,
		F entityFacade,
		DeleteRestoreMessages messages,
		boolean isEligibleForDeletion,
		Runnable callback) {
		if (entityUuids.isEmpty()) {
			displayNothingSelectedToBeDeleted(messages);
			return;
		}

		if (!isEligibleForDeletion) {
			displayErrorMessage(messages);
			return;
		}

		//TODO: test the permanent delete for case or event
		String deleteConfirmationMessage = String.format(
			I18nProperties.getString(Strings.confirmationDeleteEntities),
			entityUuids.size(),
			I18nProperties.getString(messages.getEntities()).toLowerCase());

		VaadinUiUtil
			.showDeleteConfirmationWindow(deleteConfirmationMessage, () -> performDeleteSelectedItems(entityUuids, entityFacade, messages, callback));
	}

	private void performDeleteSelectedItems(List<String> entityUuids, F entityFacade, DeleteRestoreMessages messages, Runnable callback) {

		int undeletedEntityCount = 0;
		StringBuilder unDeletedEntitiesSb = new StringBuilder();

		for (String selectedRow : entityUuids) {
			try {
				entityFacade.delete(selectedRow);
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

	private void handleDeleteResult(int undeletedEntityCount, DeleteRestoreMessages messages, String undeletedEntitiesString) {

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
					I18nProperties.getString(messages.getMessageEntitiesNotDeleted())),
				ContentMode.HTML);
			response.setWidth(600, Sizeable.Unit.PIXELS);
		}
	}

	private void displayNothingSelectedToBeDeleted(DeleteRestoreMessages messages) {
		new Notification(
			I18nProperties.getString(messages.getHeadingNoSelection()),
			I18nProperties.getString(messages.getMessageNoSelection()),
			Notification.Type.WARNING_MESSAGE,
			false).show(Page.getCurrent());
	}

	private void displayErrorMessage(DeleteRestoreMessages messages) {
		new Notification(
			I18nProperties.getString(messages.getHeadingEntitiesEligibleForDeletion()),
			I18nProperties.getString(messages.getMessageEntitiesEligibleForDeletion()),
			Notification.Type.ERROR_MESSAGE,
			false).show(Page.getCurrent());
	}

}
