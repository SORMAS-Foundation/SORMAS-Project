package de.symeda.sormas.ui.utils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.HtmlHelper;
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
	private int initialEligibleEntryCount;
	private int successfulEntryCount;
	private Window window;

	private final String allEntriesProcessedMessageProperty;
	private final String ineligibleEntriesNotProcessedMessageProperty;
	private final String headingSomeEntitiesNotDeleted;
	private final String countEntriesNotDeletedMessageProperty;
	private final String someEntriesProcessedMessageProperty;
	private final String noEligibleEntityMessageProperty;
	private final String infoBulkProcessFinishedWithSkipsProperty;

	public BulkOperationHandler(
		String allEntriesProcessedMessageProperty,
		String ineligibleEntriesNotProcessedMessageProperty,
		String headingSomeEntitiesNotDeleted,
		String countEntriesNotProcessedMessageProperty,
		String someEntriesProcessedMessageProperty,
		String noEligibleEntityMessageProperty,
		String infoBulkProcessFinishedWithSkipsProperty) {
		this.allEntriesProcessedMessageProperty = allEntriesProcessedMessageProperty;
		this.ineligibleEntriesNotProcessedMessageProperty = ineligibleEntriesNotProcessedMessageProperty;
		this.headingSomeEntitiesNotDeleted = headingSomeEntitiesNotDeleted;
		this.countEntriesNotDeletedMessageProperty = countEntriesNotProcessedMessageProperty;
		this.someEntriesProcessedMessageProperty = someEntriesProcessedMessageProperty;
		this.noEligibleEntityMessageProperty = noEligibleEntityMessageProperty;
		this.infoBulkProcessFinishedWithSkipsProperty = infoBulkProcessFinishedWithSkipsProperty;
	}

	public static <E extends HasUuid> BulkOperationHandler<E> forBulkEdit() {
		return new BulkOperationHandler<E>(
			Strings.messageEntriesEdited,
			null,
			null,
			null,
			Strings.messageEntriesEditedExceptArchived,
			null,
			Strings.infoBulkProcessFinishedWithSkips);
	}

	public void doBulkOperation(
		Function<List<T>, Integer> bulkOperationFunction,
		List<T> selectedEntries,
		List<T> selectedEligibleEntries,
		List<T> selectedIneligibleEntries,
		Consumer<List<T>> bulkOperationDoneCallback) {

		initialEntryCount = selectedEntries.size();
		initialEligibleEntryCount = getInitialEligibleEntryCount(selectedEntries, selectedIneligibleEntries, selectedEligibleEntries);
		selectedEligibleEntries = !areIneligibleEntriesSelected(selectedIneligibleEntries) ? selectedEntries : selectedEligibleEntries;

		if (selectedEntries.size() < BULK_ACTION_PROGRESS_THRESHOLD) {
			if (initialEligibleEntryCount > 0) {
				if (areIneligibleEntriesSelected(selectedIneligibleEntries)) {
					successfulEntryCount =
						areIneligibleEntriesSelected(selectedIneligibleEntries) ? bulkOperationFunction.apply(selectedEligibleEntries) : 0;
				} else {
					successfulEntryCount = bulkOperationFunction.apply(selectedEntries);
				}
			}

			if (initialEligibleEntryCount == 0 && successfulEntryCount == 0) {
				//all the selected items were ineligible
				NotificationHelper.showNotification(I18nProperties.getString(noEligibleEntityMessageProperty), Notification.Type.WARNING_MESSAGE, -1);
				return;
			}

			if (initialEligibleEntryCount > successfulEntryCount) {
				NotificationHelper.showNotification(
					String.format(I18nProperties.getString(someEntriesProcessedMessageProperty), successfulEntryCount),
					Notification.Type.HUMANIZED_MESSAGE,
					-1);

			} else {
				if (areIneligibleEntriesSelected(selectedIneligibleEntries)) {
					String description = getErrorDescription(
						selectedIneligibleEntries.stream().map(HasUuid::getUuid).collect(Collectors.toList()),
						I18nProperties.getString(countEntriesNotDeletedMessageProperty),
						I18nProperties.getString(ineligibleEntriesNotProcessedMessageProperty));

					Window response =
						VaadinUiUtil.showSimplePopupWindow(I18nProperties.getString(headingSomeEntitiesNotDeleted), description, ContentMode.HTML);

					response.setWidth(600, Sizeable.Unit.PIXELS);
				} else {
					//all the selected eligible entities were deleted
					NotificationHelper
						.showNotification(I18nProperties.getString(allEntriesProcessedMessageProperty), Notification.Type.HUMANIZED_MESSAGE, -1);
				}
			}
			bulkOperationDoneCallback.accept(Collections.emptyList());
		} else {
			UserDto currentUser = FacadeProvider.getUserFacade().getCurrentUser();
			UI currentUI = UI.getCurrent();

			BulkProgressLayout bulkProgressLayout = new BulkProgressLayout(currentUI, selectedEntries.size(), this::handleCancelButtonClicked);
			addWindow(bulkProgressLayout, currentUI);

			List<T> finalSelectedEligibleEntries = selectedEligibleEntries;
			Thread bulkThread = new Thread(() -> {
				currentUI.setPollInterval(300);
				I18nProperties.setUserLanguage(currentUser.getLanguage());
				FacadeProvider.getI18nFacade().setUserLanguage(currentUser.getLanguage());

				try {
					List<T> remainingEntries =
						performBulkOperation(bulkOperationFunction, finalSelectedEligibleEntries, bulkProgressLayout::updateProgress);

					currentUI.access(() -> {
						window.setClosable(true);

						if (initialEligibleEntryCount == 0) {
							bulkProgressLayout.finishProgress(
								ProgressResult.SUCCESS_WITH_WARNING,
								I18nProperties.getString(Strings.infoBulkProcessNoEligibleEntries),
								() -> {
									window.close();
									bulkOperationDoneCallback.accept(remainingEntries);
								});
							return;
						}

						if (cancelAfterCurrentBatch) {
							bulkProgressLayout.finishProgress(
								ProgressResult.SUCCESS_WITH_WARNING,
								I18nProperties.getString(Strings.infoBulkProcessCancelled),
								() -> {
									window.close();
									bulkOperationDoneCallback.accept(remainingEntries);
								});
						} else if (initialEligibleEntryCount == successfulEntryCount) {

							if (initialEntryCount == initialEligibleEntryCount) {
								bulkProgressLayout
									.finishProgress(ProgressResult.SUCCESS, I18nProperties.getString(Strings.infoBulkProcessFinished), () -> {
										window.close();
										bulkOperationDoneCallback.accept(remainingEntries);
									});
							} else {

								bulkProgressLayout.finishProgress(
									ProgressResult.SUCCESS_WITH_WARNING,
									I18nProperties.getString(Strings.infoBulkProcessFinishedWithIneligibleItems),
									() -> {
										window.close();
										bulkOperationDoneCallback.accept(remainingEntries);
									});
							}
						} else {
							bulkProgressLayout.finishProgress(
								ProgressResult.SUCCESS_WITH_WARNING,
								I18nProperties.getString(infoBulkProcessFinishedWithSkipsProperty),
								() -> {
									window.close();
									bulkOperationDoneCallback.accept(remainingEntries);
								});
						}

					});
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
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

	private String getErrorDescription(
		List<String> ineligibleEntityUuids,
		String messageCountEntriesNotDeleted,
		String messageIneligibleEntriesNotProcessed) {
		StringBuilder description = new StringBuilder();
		description.append(
			String.format(
				"%1s <br/> %2s",
				String.format(
					messageCountEntriesNotDeleted,
					String.format("<b>%s</b>", ineligibleEntityUuids.size()),
					String.format("<b>%s</b>", HtmlHelper.cleanHtml(getIneligibleItemsString(ineligibleEntityUuids)))),
				messageIneligibleEntriesNotProcessed))
			.append("<br/> <br/>");

		return description.toString();
	}

	public String getIneligibleItemsString(List<String> ineligibleEntities) {
		StringBuilder ineligibleItems = new StringBuilder();
		for (String ineligibleEntity : ineligibleEntities) {
			ineligibleItems.append(ineligibleEntity, 0, 6).append(", ");
		}

		if (ineligibleItems.length() > 0) {
			ineligibleItems = new StringBuilder(" " + ineligibleItems.substring(0, ineligibleItems.length() - 2) + ". ");
		}

		return ineligibleItems.toString();
	}

	public int getInitialEligibleEntryCount(List<T> selectedEntries, List<T> selectedIneligibleEntries, List<T> selectedEligibleEntries) {
		return selectedIneligibleEntries != null && selectedIneligibleEntries.size() > 0 ? selectedEligibleEntries.size() : selectedEntries.size();
	}

	public boolean areIneligibleEntriesSelected(List<T> selectedIneligibleEntries) {
		return selectedIneligibleEntries != null && selectedIneligibleEntries.size() > 0;
	}

	public Window getWindow() {
		return window;
	}

	public void addWindow(BulkProgressLayout layout, UI currentUI) {
		window = VaadinUiUtil.createPopupWindow();
		window.setCaption(I18nProperties.getString(Strings.headingBulkOperationProgress));
		window.setWidth(800, Sizeable.Unit.PIXELS);
		window.setContent(layout);
		window.setClosable(false);
		currentUI.addWindow(window);
	}
}
