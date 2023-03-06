package de.symeda.sormas.ui.utils;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.vaadin.shared.ui.ContentMode;
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
		Function<List<? extends HasUuid>, BulkOperationResults<?>> bulkOperationFunction,
		List<? extends HasUuid> selectedEntries,
		int initialEntryCount,
		Consumer<BulkOperationResults<?>> bulkOperationDoneCallback) {

		BulkOperationResults<?> bulkOperationResults = bulkOperationFunction.apply(selectedEntries);
		BulkOperationHelper
			.handleBulkOperationResult(bulkOperationResults, bulkOperationFunction, selectedEntries, initialEntryCount, bulkOperationDoneCallback);
	}

	public static void handleBulkOperationResult(
		BulkOperationResults<?> bulkOperationResults,
		Function<List<? extends HasUuid>, BulkOperationResults<?>> bulkOperationFunction,
		List<? extends HasUuid> selectedEntries,
		int initialEntryCount,
		Consumer<BulkOperationResults<?>> bulkOperationDoneCallback) {

		if (bulkOperationResults.getRemainingEntries().isEmpty()) {
			Notification.show(I18nProperties.getString(Strings.messageEntriesEdited), Notification.Type.HUMANIZED_MESSAGE);
			bulkOperationDoneCallback.accept(bulkOperationResults);
		} else if (bulkOperationResults.hasReachedEntryLimit()) {
			VaadinUiUtil.showChooseOptionPopup(
				I18nProperties.getString(Strings.headingBulkOperationProgress),
				new Label(
					String.format(
						I18nProperties.getString(Strings.messageBulkOperationEntryLimitReached),
						DataHelper.BULK_EDIT_ENTRY_LIMIT,
						initialEntryCount - bulkOperationResults.getRemainingEntries().size(),
						bulkOperationResults.getRemainingEntries().size()),
					ContentMode.HTML),
				I18nProperties.getCaption(Captions.actionYes),
				I18nProperties.getCaption(Captions.actionNo),
				640,
				confirmed -> {
					if (Boolean.TRUE.equals(confirmed)) {
						selectedEntries.removeIf(e -> !bulkOperationResults.getRemainingEntries().contains(e.getUuid()));
						doBulkOperation(bulkOperationFunction, selectedEntries, initialEntryCount, bulkOperationDoneCallback);
					} else {
						bulkOperationDoneCallback.accept(bulkOperationResults);
					}
				});
		} else if (bulkOperationResults.hasReachedTimeLimit()) {
			VaadinUiUtil.showChooseOptionPopup(
				I18nProperties.getString(Strings.headingBulkOperationProgress),
				new Label(
					String.format(
						I18nProperties.getString(Strings.messageBulkOperationTimeLimitReached),
						DataHelper.BULK_EDIT_TIME_LIMIT / 1000,
						initialEntryCount - bulkOperationResults.getRemainingEntries().size(),
						bulkOperationResults.getRemainingEntries().size()),
					ContentMode.HTML),
				I18nProperties.getCaption(Captions.actionYes),
				I18nProperties.getCaption(Captions.actionNo),
				640,
				confirmed -> {
					if (Boolean.TRUE.equals(confirmed)) {
						selectedEntries.removeIf(e -> !bulkOperationResults.getRemainingEntries().contains(e.getUuid()));
						doBulkOperation(bulkOperationFunction, selectedEntries, initialEntryCount, bulkOperationDoneCallback);
					} else {
						bulkOperationDoneCallback.accept(bulkOperationResults);
					}
				});
		} else {
			NotificationHelper.showNotification(
				String.format(
					I18nProperties.getString(Strings.messageEntriesEditedExceptArchived),
					initialEntryCount - bulkOperationResults.getRemainingEntries().size()),
				Notification.Type.HUMANIZED_MESSAGE,
				-1);
			bulkOperationDoneCallback.accept(bulkOperationResults);
		}
	}

}
