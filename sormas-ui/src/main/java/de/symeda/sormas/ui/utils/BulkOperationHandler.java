package de.symeda.sormas.ui.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.common.progress.ProcessedEntityStatus;
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
	//TODO: change back this
	public static final int BULK_ACTION_BATCH_SIZE = 5;
	/**
	 * Amount of DTOs that have to be selected for the progress layout to be displayed.
	 */
	public static final int BULK_ACTION_PROGRESS_THRESHOLD = 10;
	private boolean cancelAfterCurrentBatch;
	private boolean cancelButtonClicked;
	private final Lock cancelLock = new ReentrantLock();
	private int initialEntryCount;
	private int initialEligibleEntryCount;
	private int successfulEntryCount;
	private Window window;

	private final String allEntriesProcessedMessageProperty;
	private final String ineligibleEntriesNotProcessedMessageProperty;
	private final String headingSomeEntitiesNotProcessed;
	private final String headingNoProcessedEntities;

	private final String countEntriesNotProcessedMessageProperty;
	private final String countEntriesNotProcessedExternalReasonProperty;
	private final String countEntriesNotProcessedSormasToSormasReasonProperty;
	private final String someEntriesProcessedMessageProperty;
	private final String noEligibleEntityMessageProperty;
	private final String infoBulkProcessFinishedWithSkipsProperty;

	public BulkOperationHandler(
		String allEntriesProcessedMessageProperty,
		String ineligibleEntriesNotProcessedMessageProperty,
		String headingSomeEntitiesNotProcessed,
		String headingNoProcessedEntities,
		String countEntriesNotProcessedMessageProperty,
		String countEntriesNotProcessedExternalReasonProperty,
		String countEntriesNotProcessedSormasToSormasReasonProperty,
		String someEntriesProcessedMessageProperty,
		String noEligibleEntityMessageProperty,
		String infoBulkProcessFinishedWithSkipsProperty) {

		this.allEntriesProcessedMessageProperty = allEntriesProcessedMessageProperty;
		this.ineligibleEntriesNotProcessedMessageProperty = ineligibleEntriesNotProcessedMessageProperty;
		this.headingSomeEntitiesNotProcessed = headingSomeEntitiesNotProcessed;
		this.headingNoProcessedEntities = headingNoProcessedEntities;
		this.countEntriesNotProcessedMessageProperty = countEntriesNotProcessedMessageProperty;
		this.countEntriesNotProcessedExternalReasonProperty = countEntriesNotProcessedExternalReasonProperty;
		this.countEntriesNotProcessedSormasToSormasReasonProperty = countEntriesNotProcessedSormasToSormasReasonProperty;
		this.someEntriesProcessedMessageProperty = someEntriesProcessedMessageProperty;
		this.noEligibleEntityMessageProperty = noEligibleEntityMessageProperty;
		this.infoBulkProcessFinishedWithSkipsProperty = infoBulkProcessFinishedWithSkipsProperty;
	}

	//TODO: check if the 3 newly added fields can have value for bulk edit
	public static <E extends HasUuid> BulkOperationHandler<E> forBulkEdit() {
		return new BulkOperationHandler<E>(
			Strings.messageEntriesEdited,
			null,
			null,
			null,
			null,
			null,
			null,
			Strings.messageEntriesEditedExceptArchived,
			null,
			Strings.infoBulkProcessFinishedWithSkips);
	}

	public void doBulkOperation(
		Function<List<T>, List<ProcessedEntity>> bulkOperationFunction,
		List<T> selectedEntries,
		List<T> selectedEligibleEntries,
		List<T> selectedIneligibleEntries,
		Consumer<List<T>> bulkOperationDoneCallback) {

		//TODO: these will be deleted
		initialEntryCount = selectedEntries.size();
		initialEligibleEntryCount = getInitialEligibleEntryCount(selectedEntries, selectedIneligibleEntries, selectedEligibleEntries);
		selectedEligibleEntries = !areIneligibleEntriesSelected(selectedIneligibleEntries) ? selectedEntries : selectedEligibleEntries;

		if (selectedEntries.size() < BULK_ACTION_PROGRESS_THRESHOLD) {
			processEntriesWithoutProgressBar(bulkOperationFunction, selectedEntries, selectedEligibleEntries, selectedIneligibleEntries);
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
					List<String> entityUuidsNotProcessedExternalSurveillanceReason = new ArrayList<>();
					List<String> entityUuidsNotProcessedGeneralReason = new ArrayList<>();
					List<T> remainingEntries = performBulkOperation(
						bulkOperationFunction,
						finalSelectedEligibleEntries,
						entityUuidsNotProcessedExternalSurveillanceReason,
						entityUuidsNotProcessedGeneralReason,
						bulkProgressLayout::updateProgress);

					currentUI.access(() -> {
						window.setClosable(true);

						//If the user does not have the proper rights to perform the action, there will be no processed entities
						if (remainingEntries.size() == initialEligibleEntryCount) {
							bulkProgressLayout.finishProgress(
								ProgressResult.FAILURE,
								//I18nProperties.getString(Strings.infoBulkProcessNoEligibleEntries),
								I18nProperties.getString(Strings.errorForbidden),
								() -> {
									window.close();
									bulkOperationDoneCallback.accept(Collections.emptyList());
								});
							return;
						}

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

	public void processEntriesWithoutProgressBar(
		Function<List<T>, List<ProcessedEntity>> bulkOperationFunction,
		List<T> selectedEntries,
		List<T> selectedEligibleEntries,
		List<T> selectedIneligibleEntries) {

		initialEligibleEntryCount = getInitialEligibleEntryCount(selectedEntries, selectedIneligibleEntries, selectedEligibleEntries);

		boolean areIneligibleEntriesSelected = areIneligibleEntriesSelected(selectedIneligibleEntries);
		selectedEligibleEntries = !areIneligibleEntriesSelected ? selectedEntries : selectedEligibleEntries;

		List<ProcessedEntity> processedEntities = new ArrayList<>();
		if (initialEligibleEntryCount > 0) {
			processedEntities =
				areIneligibleEntriesSelected ? bulkOperationFunction.apply(selectedEligibleEntries) : bulkOperationFunction.apply(selectedEntries);

			//If the user does not have the proper rights to perform the action, there will be no processed entities
			if (processedEntities.size() == 0) {
				NotificationHelper.showNotification(I18nProperties.getString(Strings.errorForbidden), Notification.Type.WARNING_MESSAGE, -1);
				return;
			} else {
				successfulEntryCount = processedEntities.stream()
					.filter(processedEntity -> processedEntity.getProcessedEntityStatus().equals(ProcessedEntityStatus.SUCCESS))
					.collect(Collectors.toList())
					.size();
			}
		}

		if (initialEligibleEntryCount == 0 && successfulEntryCount == 0) {
			//all the selected items were ineligible
			NotificationHelper.showNotification(I18nProperties.getString(noEligibleEntityMessageProperty), Notification.Type.WARNING_MESSAGE, -1);
			return;
		}

		String ineligibleEntriesDescription = StringUtils.EMPTY;
		if (areIneligibleEntriesSelected) {
			ineligibleEntriesDescription = getErrorDescription(
				selectedIneligibleEntries.stream().map(HasUuid::getUuid).collect(Collectors.toList()),
				I18nProperties.getString(countEntriesNotProcessedMessageProperty),
				I18nProperties.getString(ineligibleEntriesNotProcessedMessageProperty));
		}

		String heading = successfulEntryCount > 0
			? I18nProperties.getString(headingSomeEntitiesNotProcessed)
			: I18nProperties.getString(headingNoProcessedEntities);

		if (initialEligibleEntryCount > successfulEntryCount) {
			String description = areIneligibleEntriesSelected
				? ineligibleEntriesDescription.concat(buildDescription(processedEntities))
				: buildDescription(processedEntities);

			Window response = VaadinUiUtil.showSimplePopupWindow(heading, description, ContentMode.HTML);
			response.setWidth(600, Sizeable.Unit.PIXELS);
		} else {
			if (areIneligibleEntriesSelected) {
				Window response = VaadinUiUtil.showSimplePopupWindow(heading, ineligibleEntriesDescription, ContentMode.HTML);
				response.setWidth(600, Sizeable.Unit.PIXELS);
			} else {
				//all the selected eligible entities were processed
				NotificationHelper
					.showNotification(I18nProperties.getString(allEntriesProcessedMessageProperty), Notification.Type.HUMANIZED_MESSAGE, -1);
			}
		}
	}

	private List<T> performBulkOperation(
		Function<List<T>, List<ProcessedEntity>> bulkOperationFunction,
		List<T> selectedEntries,
		List<String> entitiesNotProcessedExternalSurveillanceFailure,
		List<String> entitiesNotProcessedGeneralReason,
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

					List<ProcessedEntity> processedEntities =
						bulkOperationFunction.apply(selectedEntries.subList(i, Math.min(i + BULK_ACTION_BATCH_SIZE, selectedEntries.size())));

					//If the user does not have the proper rights to perform the action, there will be no processed entities
					//send as parameter initialEligibleEntryCount?
					if (processedEntities.size() == 0) {
						break;
					} else {
						entitiesNotProcessedExternalSurveillanceFailure
							.addAll(getList(processedEntities, ProcessedEntityStatus.EXTERNAL_SURVEILLANCE_FAILURE));
						// entitiesNotProcessedExternalSurveillanceFailure.addAll(getList(processedEntities, ProcessedEntityStatus.EXTERNAL_SURVEILLANCE_FAILURE));
						//   entitiesNotProcessedExternalSurveillanceFailure.addAll(getList(processedEntities, ProcessedEntityStatus.EXTERNAL_SURVEILLANCE_FAILURE));
					}

					int successfullyProcessedInBatch = processedEntities.stream()
						.filter(processedEntity -> processedEntity.getProcessedEntityStatus().equals(ProcessedEntityStatus.SUCCESS))
						.collect(Collectors.toList())
						.size();

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

	public String buildDescription(List<ProcessedEntity> processedEntities) {
		List<String> entityUuidsNotProcessedExternalSurveillanceFailure =
			getList(processedEntities, ProcessedEntityStatus.EXTERNAL_SURVEILLANCE_FAILURE);
		List<String> entityUuidsNotProcessedSormasToSormasFailure = getList(processedEntities, ProcessedEntityStatus.SORMAS_TO_SORMAS_FAILURE);
		List<String> entityUuidsNotProcessedAccessDeniedFailure = getList(processedEntities, ProcessedEntityStatus.ACCESS_DENIED_FAILURE);
		List<String> entityUuidsNotProcessedInternalFailure = getList(processedEntities, ProcessedEntityStatus.INTERNAL_FAILURE);

		String description = StringUtils.EMPTY;
		if (entityUuidsNotProcessedExternalSurveillanceFailure.size() > 0) {
			String description1 = getErrorDescription(
				entityUuidsNotProcessedExternalSurveillanceFailure,
				I18nProperties.getString(countEntriesNotProcessedExternalReasonProperty),
				"");
			description = description.concat(description1);
		}

		if (entityUuidsNotProcessedSormasToSormasFailure.size() > 0) {
			String description2 = getErrorDescription(
				entityUuidsNotProcessedSormasToSormasFailure,
				I18nProperties.getString(countEntriesNotProcessedSormasToSormasReasonProperty),
				"");
			description = description.concat(description2);
		}

		if (entityUuidsNotProcessedAccessDeniedFailure.size() > 0) {
			String description3 = getErrorDescription(
				entityUuidsNotProcessedAccessDeniedFailure,
				I18nProperties.getString(countEntriesNotProcessedMessageProperty),
				"");
			description = description.concat(description3);
		}

		if (entityUuidsNotProcessedInternalFailure.size() > 0) {
			String description4 =
				getErrorDescription(entityUuidsNotProcessedInternalFailure, I18nProperties.getString(countEntriesNotProcessedMessageProperty), "");
			description = description.concat(description4);
		}

		return description;
	}

	private String getErrorDescription(List<String> entityUuids, String messageCountEntries, String messageEntriesNotSuccessfullyProcessed) {
		StringBuilder description = new StringBuilder();
		description.append(
			String.format(
				"%1s <br/> %2s",
				String.format(
					messageCountEntries,
					String.format("<b>%s</b>", entityUuids.size()),
					String.format("<b>%s</b>", HtmlHelper.cleanHtml(buildEntitiesString(entityUuids)))),
				messageEntriesNotSuccessfullyProcessed))
			.append("<br/> <br/>");

		return description.toString();
	}

	public String buildEntitiesString(List<String> entityUuids) {
		StringBuilder entitiesString = new StringBuilder();
		for (String entityUuid : entityUuids) {
			entitiesString.append(entityUuid, 0, 6).append(", ");
		}

		if (entitiesString.length() > 0) {
			entitiesString = new StringBuilder(" " + entitiesString.substring(0, entitiesString.length() - 2) + ". ");
		}

		return entitiesString.toString();
	}

	public int getInitialEligibleEntryCount(List<T> selectedEntries, List<T> selectedIneligibleEntries, List<T> selectedEligibleEntries) {
		return selectedIneligibleEntries != null && selectedIneligibleEntries.size() > 0 ? selectedEligibleEntries.size() : selectedEntries.size();
	}

	public boolean areIneligibleEntriesSelected(List<T> selectedIneligibleEntries) {
		return selectedIneligibleEntries != null && selectedIneligibleEntries.size() > 0;
	}

	public List<String> getList(List<ProcessedEntity> processedEntities, ProcessedEntityStatus status) {
		return processedEntities.stream()
			.filter(processedEntity -> processedEntity.getProcessedEntityStatus().equals(status))
			.map(ProcessedEntity::getEntityUuid)
			.collect(Collectors.toList());
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
