package de.symeda.sormas.ui.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.PermanentlyDeletableFacade;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.HtmlHelper;
import de.symeda.sormas.api.uuid.HasUuid;

public class PermanentDeleteController<F extends PermanentlyDeletableFacade> {

	public <T extends HasUuid> void deleteAllSelectedItems(
		Collection<T> entities,
		IPermanentDeleteHandler<?> deleteHandler,
		boolean allItemsAreEligibleForDeletion,
		Consumer<List<T>> batchCallback) {

		if (entities.isEmpty()) {
			displayNothingSelectedToBeDeleted(deleteHandler.getDeleteRestoreMessages());
			return;
		}

		if (!allItemsAreEligibleForDeletion) {
			displayErrorMessage(deleteHandler.getDeleteRestoreMessages());
			return;
		}

		//TODO: test the permanent delete for case or event
		String deleteConfirmationMessage = String.format(
			I18nProperties.getString(Strings.confirmationDeleteEntities),
			entities.size(),
			I18nProperties.getString(deleteHandler.getDeleteRestoreMessages().getEntities()).toLowerCase());

		/*
		 * VaadinUiUtil
		 * .showDeleteConfirmationWindow(deleteConfirmationMessage, () -> performDeleteSelectedItems(entityUuids, entityFacade, messages,
		 * callback));
		 */
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setMargin(false);

		Label contentLabel = new Label(deleteConfirmationMessage, ContentMode.HTML);
		contentLabel.addStyleName(CssStyles.LABEL_WHITE_SPACE_NORMAL);
		contentLabel.setWidthFull();
		verticalLayout.addComponent(contentLabel);

		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.headingDeleteConfirmation),
			verticalLayout,
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			640,
			confirmed -> {
				if (Boolean.TRUE.equals(confirmed)) {

					List<T> selectedEntitiesCpy = new ArrayList<>(entities);
					this.<T> createBulkOperationHandler(deleteHandler)
						.doBulkOperation(
							selectedEntries -> deleteHandler.delete(selectedEntries.stream().map(HasUuid::getUuid).collect(Collectors.toList())),
							selectedEntitiesCpy,
							batchCallback);
				}

				return true;
			});

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

	private <T extends HasUuid> BulkOperationHandler<T> createBulkOperationHandler(IPermanentDeleteHandler<?> deleteHandler) {
		DeleteRestoreMessages deleteRestoreMessages = deleteHandler.getDeleteRestoreMessages();
		return new BulkOperationHandler<>(deleteRestoreMessages.getMessageEntitiesDeleted(), deleteRestoreMessages.getMessageEntitiesNotDeleted());
	}

	public interface IPermanentDeleteHandler<T extends HasUuid> {

		int delete(List<String> uuids);

		DeleteRestoreMessages getDeleteRestoreMessages();
	}

}
