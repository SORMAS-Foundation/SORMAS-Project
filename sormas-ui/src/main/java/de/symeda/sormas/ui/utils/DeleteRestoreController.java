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

	public void performRestoreSelectedItems(List<String> entityUuids, F entityFacade, CoreEntityRestoreMessages messages, Runnable callback) {
		if (entityUuids.isEmpty()) {
			new Notification(
				I18nProperties.getString(messages.getHeadingNoSelection()),
				I18nProperties.getString(messages.getMessageNoSelection()),
				Notification.Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			int countNotRestoredEntities = 0;
			StringBuilder notRestoredEntities = new StringBuilder();
			for (String selectedRow : entityUuids) {
				try {
					entityFacade.restore(selectedRow);
				} catch (Exception e) {
					countNotRestoredEntities++;
					notRestoredEntities.append(selectedRow, 0, 6).append(", ");
				}
			}
			if (notRestoredEntities.length() > 0) {
				notRestoredEntities = new StringBuilder(" " + notRestoredEntities.substring(0, notRestoredEntities.length() - 2) + ". ");
			}
			callback.run();
			if (countNotRestoredEntities == 0) {
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
							String.format("<b>%s</b>", countNotRestoredEntities),
							String.format("<b>%s</b>", HtmlHelper.cleanHtml(notRestoredEntities.toString()))),
						I18nProperties.getString(Strings.messageCasesNotRestored)),
					ContentMode.HTML);
				response.setWidth(600, Sizeable.Unit.PIXELS);
			}
		}
	}

}
