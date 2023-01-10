package de.symeda.sormas.ui.utils;

import java.util.List;
import java.util.function.Function;

import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.BulkOperationResults;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.uuid.HasUuid;

public class BulkOperationHelper {

	private BulkOperationHelper() {

	}

	public static void doBulkOperation(
		Function<List<? extends HasUuid>, BulkOperationResults> bulkOperationFunction,
		List<? extends HasUuid> selectedEntries,
		int initialEntryCount) {

		BulkOperationResults bulkOperationResults = bulkOperationFunction.apply(selectedEntries);
		BulkOperationHelper.handleBulkOperationResult(bulkOperationResults, bulkOperationFunction, selectedEntries, initialEntryCount);
	}

	public static void handleBulkOperationResult(
		BulkOperationResults bulkOperationResults,
		Function<List<? extends HasUuid>, BulkOperationResults> bulkOperationFunction,
		List<? extends HasUuid> selectedEntries,
		int initialEntryCount) {

		if (bulkOperationResults.getRemainingEntries().isEmpty()) {
			Notification.show(I18nProperties.getString(Strings.messageCasesEdited), Notification.Type.HUMANIZED_MESSAGE);
		} else if (bulkOperationResults.hasExceededEntryLimit()) {
			VaadinUiUtil.showChooseOptionPopup(
				null,
				new Label(
					String.format(
						I18nProperties.getString(Strings.messageBulkOperationEntryLimitReached),
						DataHelper.BULK_EDIT_ENTRY_LIMIT,
						bulkOperationResults.getRemainingEntries().size())),
				I18nProperties.getCaption(Captions.actionYes),
				I18nProperties.getCaption(Captions.actionNo),
				640,
				confirmed -> {
					if (Boolean.TRUE.equals(confirmed)) {
						selectedEntries.removeIf(e -> !bulkOperationResults.getRemainingEntries().contains(e.getUuid()));
						doBulkOperation(bulkOperationFunction, selectedEntries, initialEntryCount);
					}
				});
		} else if (bulkOperationResults.hasExceededTimeLimit()) {
			VaadinUiUtil.showChooseOptionPopup(
				null,
				new Label(
					String.format(
						I18nProperties.getString(Strings.messageBulkOperationTimeLimitReached),
						DataHelper.BULK_EDIT_TIME_LIMIT / 1000,
						initialEntryCount - bulkOperationResults.getRemainingEntries().size(),
						bulkOperationResults.getRemainingEntries().size())),
				I18nProperties.getCaption(Captions.actionYes),
				I18nProperties.getCaption(Captions.actionNo),
				640,
				confirmed -> {
					if (Boolean.TRUE.equals(confirmed)) {
						selectedEntries.removeIf(e -> !bulkOperationResults.getRemainingEntries().contains(e.getUuid()));
						doBulkOperation(bulkOperationFunction, selectedEntries, initialEntryCount);
					}
				});
		} else {
			NotificationHelper.showNotification(
				String.format(
					I18nProperties.getString(Strings.messageCasesEditedExceptArchived),
					initialEntryCount - bulkOperationResults.getRemainingEntries().size()),
				Notification.Type.HUMANIZED_MESSAGE,
				-1);
		}
	}

}
