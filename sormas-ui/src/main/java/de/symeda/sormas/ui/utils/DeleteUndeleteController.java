package de.symeda.sormas.ui.utils;

import java.util.List;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.DeletableFacade;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.HtmlHelper;

public class DeleteUndeleteController<F extends DeletableFacade> {

	public void undeleteSelectedItems(List<String> entityUuids, F entityFacade, CoreEntityUndeleteMessages messages, Runnable callback) {
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
					entityFacade.undelete(selectedRow);
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
					Notification.Type.HUMANIZED_MESSAGE,
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
