package de.symeda.sormas.ui.utils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.LoggerFactory;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.uuid.HasUuid;
import de.symeda.sormas.ui.utils.components.progress.BulkProgressLayout;
import de.symeda.sormas.ui.utils.components.progress.BulkProgressUpdateInfo;
import de.symeda.sormas.ui.utils.components.progress.ProgressResult;

public class BulkOperationHandler<T extends HasUuid> {

	/**
	 * Amount of DTOs that are forwarded to the backend in one single call
	 * when displaying a progress layout.
	 */
	public static final int BULK_ACTION_BATCH_SIZE = 20;
	/**
	 * Amount of DTOs that have to be selected for the progress layout to be displayed.
	 */
	public static final int BULK_ACTION_PROGRESS_THRESHOLD = 40;
	private boolean cancelAfterCurrentBatch;
	private boolean cancelButtonClicked;
	private final Lock cancelLock = new ReentrantLock();
	private int initialEntryCount;
	private int successfulEntryCount;

	private final String allEntriesProcessedMessage;
	private final String someEntriesProcessedMessage;

	private boolean messagesTranslated;

	public BulkOperationHandler(String allEntriesProcessedMessageProperty, String someEntriesProcessedMessageProperty) {
		this.allEntriesProcessedMessage = I18nProperties.getString(allEntriesProcessedMessageProperty);
		this.someEntriesProcessedMessage = I18nProperties.getString(someEntriesProcessedMessageProperty);
	}

	public BulkOperationHandler(String allEntriesProcessedMessage, String someEntriesProcessedMessage, boolean messagesTranslated) {
		this.allEntriesProcessedMessage = messagesTranslated ? allEntriesProcessedMessage : I18nProperties.getString(allEntriesProcessedMessage);
		this.someEntriesProcessedMessage = messagesTranslated ? someEntriesProcessedMessage : I18nProperties.getString(someEntriesProcessedMessage);
	}

	public static <E extends HasUuid> BulkOperationHandler<E> forBulkEdit() {
		return new BulkOperationHandler<>(Strings.messageEntriesEdited, Strings.messageEntriesEditedExceptArchived);
	}

	public void doBulkOperation(
		Function<List<T>, Integer> bulkOperationFunction,
		List<T> selectedEntries,
		Consumer<List<T>> bulkOperationDoneCallback) {

		initialEntryCount = selectedEntries.size();
		if (selectedEntries.size() < BULK_ACTION_PROGRESS_THRESHOLD) {
			successfulEntryCount = bulkOperationFunction.apply(selectedEntries);
			if (initialEntryCount > successfulEntryCount) {
				NotificationHelper
					.showNotification(String.format(someEntriesProcessedMessage, successfulEntryCount), Notification.Type.HUMANIZED_MESSAGE, -1);
			} else {
				NotificationHelper.showNotification(allEntriesProcessedMessage, Notification.Type.HUMANIZED_MESSAGE, -1);
			}
			bulkOperationDoneCallback.accept(Collections.emptyList());
		} else {
			UserDto currentUser = FacadeProvider.getUserFacade().getCurrentUser();
			UI currentUI = UI.getCurrent();

			BulkProgressLayout bulkProgressLayout = new BulkProgressLayout(currentUI, selectedEntries.size(), this::handleCancelButtonClicked);
			Window window = VaadinUiUtil.createPopupWindow();
			window.setCaption(I18nProperties.getString(Strings.headingBulkOperationProgress));
			window.setWidth(800, Sizeable.Unit.PIXELS);
			window.setContent(bulkProgressLayout);
			window.setClosable(false);
			currentUI.addWindow(window);

			Thread bulkThread = new Thread(() -> {
				currentUI.setPollInterval(300);
				I18nProperties.setUserLanguage(currentUser.getLanguage());
				FacadeProvider.getI18nFacade().setUserLanguage(currentUser.getLanguage());

				try {
					List<T> remainingEntries = performBulkOperation(bulkOperationFunction, selectedEntries, bulkProgressLayout::updateProgress);

					currentUI.access(() -> {
						window.setClosable(true);
						if (cancelAfterCurrentBatch) {
							bulkProgressLayout.finishProgress(
								ProgressResult.SUCCESS_WITH_WARNING,
								I18nProperties.getString(Strings.infoBulkProcessCancelled),
								() -> {
									window.close();
									bulkOperationDoneCallback.accept(remainingEntries);
								});
						} else if (initialEntryCount == successfulEntryCount) {
							bulkProgressLayout
								.finishProgress(ProgressResult.SUCCESS, I18nProperties.getString(Strings.infoBulkProcessFinished), () -> {
									window.close();
									bulkOperationDoneCallback.accept(remainingEntries);
								});
						} else {
							bulkProgressLayout.finishProgress(
								ProgressResult.SUCCESS_WITH_WARNING,
								I18nProperties.getString(Strings.infoBulkProcessFinishedWithSkips),
								() -> {
									window.close();
									bulkOperationDoneCallback.accept(remainingEntries);
								});
						}

					});
				} catch (Exception e) {
					LoggerFactory.getLogger(BulkOperationHandler.class).error("Error during bulk operation", e);
					bulkProgressLayout.finishProgress(ProgressResult.FAILURE, I18nProperties.getString(Strings.errorWasReported), () -> {
						window.close();
						bulkOperationDoneCallback.accept(selectedEntries);
					});
				} finally {
					currentUI.setPollInterval(-1);
				}
			});

			bulkThread.start();
		}
	}

	private List<T> performBulkOperation(
		Function<List<T>, Integer> bulkOperationFunction,
		List<T> selectedEntries,
		Consumer<BulkProgressUpdateInfo> progressUpdateCallback)
		throws InterruptedException {

		cancelLock.lock();
		int lastProcessedEntry = 0;

		try {
			for (int i = 0; i < selectedEntries.size(); i += BULK_ACTION_BATCH_SIZE) {
				synchronized (cancelLock) {
					while (cancelButtonClicked) {
						cancelLock.wait();
					}
					if (cancelAfterCurrentBatch) {
						break;
					}

					int entriesInBatch = Math.min(BULK_ACTION_BATCH_SIZE, selectedEntries.size() - i);
					int successfullyProcessedInBatch =
						bulkOperationFunction.apply(selectedEntries.subList(i, Math.min(i + BULK_ACTION_BATCH_SIZE, selectedEntries.size())));
					successfulEntryCount += successfullyProcessedInBatch;
					lastProcessedEntry = Math.min(i + BULK_ACTION_BATCH_SIZE, selectedEntries.size() - 1);
					progressUpdateCallback.accept(
						new BulkProgressUpdateInfo(entriesInBatch, successfullyProcessedInBatch, entriesInBatch - successfullyProcessedInBatch));
				}
			}
		} finally {
			cancelLock.unlock();
		}

		return lastProcessedEntry == selectedEntries.size() - 1
			? Collections.emptyList()
			: selectedEntries.subList(lastProcessedEntry, selectedEntries.size());
	}

	private void handleCancelButtonClicked() {

		cancelButtonClicked = true;
		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getCaption(Captions.actionConfirmAction),
			new Label(I18nProperties.getString(Strings.confirmationCancelBulkAction)),
			I18nProperties.getCaption(Captions.actionYes),
			I18nProperties.getCaption(Captions.actionNo),
			560,
			result -> {
				synchronized (cancelLock) {
					cancelButtonClicked = false;
					cancelLock.notify();
					cancelAfterCurrentBatch = result;
				}
			});

	}

}
