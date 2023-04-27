package de.symeda.sormas.ui.utils;

import java.util.List;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.DeletableFacade;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.HtmlHelper;

public class DeleteRestoreController<F extends DeletableFacade> {

	public void restoreSelectedItems(List<String> entityUuids, F entityFacade, CoreEntityRestoreMessages messages, Runnable callback) {

		if (entityUuids.isEmpty()) {
			new Notification(
				I18nProperties.getString(messages.getHeadingNoSelection()),
				I18nProperties.getString(messages.getMessageNoSelection()),
				Notification.Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
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
					I18nProperties.getString(Strings.messageCasesNotRestored)),
				ContentMode.HTML);
			response.setWidth(600, Sizeable.Unit.PIXELS);
		}
	}

}
