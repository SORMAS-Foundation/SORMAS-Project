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
import org.slf4j.LoggerFactory;

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
	private int ineligibleEntryCount;
	private int eligibleEntryCount;
	private boolean areIneligibleEntriesSelected = false;
	private Window window;

	private final String allEntriesProcessedMessageProperty;
	private final String ineligibleEntriesNotProcessedMessageProperty;
	private final String headingSomeEntitiesNotProcessed;
	private final String headingNoProcessedEntities;
	private final String countEntriesNotProcessedMessageProperty;
	private final String countEntriesNotProcessedExternalReasonProperty;
	private final String countEntriesNotProcessedSormasToSormasReasonProperty;
	private final String countEntriesNotProcessedAccessDeniedReasonProperty;
	private final String noEligibleEntityMessageProperty;
	private final String infoBulkProcessFinishedWithSkipsProperty;
	private final String infoBulkProcessFinishedWithoutSuccess;

	public BulkOperationHandler(
		String allEntriesProcessedMessageProperty,
		String ineligibleEntriesNotProcessedMessageProperty,
		String headingSomeEntitiesNotProcessed,
		String headingNoProcessedEntities,
		String countEntriesNotProcessedMessageProperty,
		String countEntriesNotProcessedExternalReasonProperty,
		String countEntriesNotProcessedSormasToSormasReasonProperty,
		String countEntriesNotProcessedAccessDeniedReasonProperty,
		String noEligibleEntityMessageProperty,
		String infoBulkProcessFinishedWithSkipsProperty,
		String infoBulkProcessFinishedWithoutSuccess) {

		this.allEntriesProcessedMessageProperty = allEntriesProcessedMessageProperty;
		this.ineligibleEntriesNotProcessedMessageProperty = ineligibleEntriesNotProcessedMessageProperty;
		this.headingSomeEntitiesNotProcessed = headingSomeEntitiesNotProcessed;
		this.headingNoProcessedEntities = headingNoProcessedEntities;
		this.countEntriesNotProcessedMessageProperty = countEntriesNotProcessedMessageProperty;
		this.countEntriesNotProcessedExternalReasonProperty = countEntriesNotProcessedExternalReasonProperty;
		this.countEntriesNotProcessedSormasToSormasReasonProperty = countEntriesNotProcessedSormasToSormasReasonProperty;
		this.countEntriesNotProcessedAccessDeniedReasonProperty = countEntriesNotProcessedAccessDeniedReasonProperty;
		this.noEligibleEntityMessageProperty = noEligibleEntityMessageProperty;
		this.infoBulkProcessFinishedWithSkipsProperty = infoBulkProcessFinishedWithSkipsProperty;
		this.infoBulkProcessFinishedWithoutSuccess = infoBulkProcessFinishedWithoutSuccess;
	}

	public static <E extends HasUuid> BulkOperationHandler<E> forBulkEdit() {
		return new BulkOperationHandler<>(
			Strings.messageEntriesEdited,
			Strings.messageEntitiesNotEditable,
			Strings.headingSomeEntitiesNotEdited,
			Strings.headingEntitiesNotEdited,
			Strings.messageCountEntitiesNotEdited,
			null,
			null,
			Strings.messageCountEntitiesNotEditedAccessDeniedReason,
			Strings.messageNoEligibleEntityForEditing,
			Strings.infoBulkProcessFinishedWithSkips,
			Strings.infoBulkProcessFinishedWithoutSuccess);
	}

	public void doBulkOperation(
		Function<List<T>, List<ProcessedEntity>> bulkOperationFunction,
		List<T> selectedEntries,
		Consumer<List<T>> bulkOperationDoneCallback) {

		initialEntryCount = selectedEntries.size();
		if (selectedEntries.size() < BULK_ACTION_PROGRESS_THRESHOLD) {
			processEntriesWithoutProgressBar(bulkOperationFunction, selectedEntries);
			bulkOperationDoneCallback.accept(Collections.emptyList());
		} else {
			List<ProcessedEntity> entitiesToBeProcessed = new ArrayList<>();
			UserDto currentUser = FacadeProvider.getUserFacade().getCurrentUser();
			UI currentUI = UI.getCurrent();

			BulkProgressLayout bulkProgressLayout = new BulkProgressLayout(currentUI, selectedEntries.size(), this::handleCancelButtonClicked);
			addWindow(bulkProgressLayout, currentUI);

			Thread bulkThread = new Thread(() -> {
				currentUI.setPollInterval(300);
				I18nProperties.setUserLanguage(currentUser.getLanguage());
				FacadeProvider.getI18nFacade().setUserLanguage(currentUser.getLanguage());

				try {
					List<T> remainingEntries =
						performBulkOperation(bulkOperationFunction, selectedEntries, entitiesToBeProcessed, bulkProgressLayout::updateProgress);

					currentUI.access(() -> {
						window.setClosable(true);

						//all the selected items were ineligible
						if (eligibleEntryCount == 0 && successfulEntryCount == 0) {
							bulkProgressLayout.finishProgress(
								ProgressResult.FAILURE,
								I18nProperties.getString(Strings.infoBulkProcessNoEligibleEntries),
								null,
								() -> {
									window.close();
									bulkOperationDoneCallback.accept(remainingEntries);
								});
							return;
						}

						areIneligibleEntriesSelected = ineligibleEntryCount > 0;
						List<ProcessedEntity> ineligibleEntries = getEntriesByStatus(entitiesToBeProcessed, ProcessedEntityStatus.NOT_ELIGIBLE);
						String ineligibleEntriesDescription = buildIneligibleEntriesDescription(areIneligibleEntriesSelected, ineligibleEntries);

						String description = buildDescription(ineligibleEntriesDescription, entitiesToBeProcessed);

						if (cancelAfterCurrentBatch) {
							handleProgressResultBasedOnSuccessfulEntryCount(
								bulkProgressLayout,
								true,
								description,
								remainingEntries,
								bulkOperationDoneCallback);
						} else if (eligibleEntryCount == successfulEntryCount) {
							if (initialEntryCount == eligibleEntryCount) {
								bulkProgressLayout
									.finishProgress(ProgressResult.SUCCESS, I18nProperties.getString(Strings.infoBulkProcessFinished), null, () -> {
										window.close();
										bulkOperationDoneCallback.accept(remainingEntries);
									});
							} else {
								bulkProgressLayout.finishProgress(
									ProgressResult.SUCCESS_WITH_WARNING,
									I18nProperties.getString(Strings.infoBulkProcessFinishedWithIneligibleItems),
									ineligibleEntriesDescription,
									() -> {
										window.close();
										bulkOperationDoneCallback.accept(remainingEntries);
									});
							}
						} else {
							handleProgressResultBasedOnSuccessfulEntryCount(
								bulkProgressLayout,
								false,
								description,
								remainingEntries,
								bulkOperationDoneCallback);
						}
					});
				} catch (Exception e) {
					LoggerFactory.getLogger(BulkOperationHandler.class).error("Error during bulk operation", e);
					bulkProgressLayout.finishProgress(ProgressResult.FAILURE, I18nProperties.getString(Strings.errorWasReported), null, () -> {
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

	public void handleProgressResultBasedOnSuccessfulEntryCount(
		BulkProgressLayout bulkProgressLayout,
		boolean cancelAfterCurrentBatch,
		String description,
		List<T> remainingEntries,
		Consumer<List<T>> bulkOperationDoneCallback) {

		if (successfulEntryCount > 0) {
			bulkProgressLayout.finishProgress(
				ProgressResult.SUCCESS_WITH_WARNING,
				cancelAfterCurrentBatch
					? I18nProperties.getString(Strings.infoBulkProcessCancelled)
					: I18nProperties.getString(infoBulkProcessFinishedWithSkipsProperty),
				description,
				() -> {
					window.close();
					bulkOperationDoneCallback.accept(remainingEntries);
				});
		} else {
			bulkProgressLayout.finishProgress(
				ProgressResult.FAILURE,
				cancelAfterCurrentBatch
					? I18nProperties.getString(Strings.infoBulkProcessCancelled)
					: I18nProperties.getString(infoBulkProcessFinishedWithoutSuccess),
				description,
				() -> {
					window.close();
					bulkOperationDoneCallback.accept(remainingEntries);
				});
		}
	}

	public void processEntriesWithoutProgressBar(Function<List<T>, List<ProcessedEntity>> bulkOperationFunction, List<T> selectedEntries) {
		List<ProcessedEntity> processedEntities = new ArrayList<>();

		if (initialEntryCount > 0) {
			processedEntities = bulkOperationFunction.apply(selectedEntries);

			successfulEntryCount = getEntriesByStatus(processedEntities, ProcessedEntityStatus.SUCCESS).size();
			ineligibleEntryCount = getEntriesByStatus(processedEntities, ProcessedEntityStatus.NOT_ELIGIBLE).size();
			eligibleEntryCount = getEligibleEntryCount(processedEntities);

			areIneligibleEntriesSelected = ineligibleEntryCount > 0;
		}

		if (eligibleEntryCount == 0 && successfulEntryCount == 0) {
			//all the selected items were ineligible
			NotificationHelper.showNotification(I18nProperties.getString(noEligibleEntityMessageProperty), Notification.Type.WARNING_MESSAGE, -1);
			return;
		}

		List<ProcessedEntity> ineligibleEntries = getEntriesByStatus(processedEntities, ProcessedEntityStatus.NOT_ELIGIBLE);
		String ineligibleEntriesDescription = buildIneligibleEntriesDescription(areIneligibleEntriesSelected, ineligibleEntries);

		String heading = successfulEntryCount > 0
			? I18nProperties.getString(headingSomeEntitiesNotProcessed)
			: I18nProperties.getString(headingNoProcessedEntities);

		if (eligibleEntryCount > successfulEntryCount) {
			String description = buildDescription(ineligibleEntriesDescription, processedEntities);

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

	public List<ProcessedEntity> getEntriesByStatus(List<ProcessedEntity> processedEntities, ProcessedEntityStatus status) {
		return processedEntities.stream()
			.filter(processedEntity -> processedEntity.getProcessedEntityStatus().equals(status))
			.collect(Collectors.toList());
	}

	public int getEligibleEntryCount(List<ProcessedEntity> processedEntities) {
		return (int) processedEntities.stream()
			.filter(processedEntity -> !processedEntity.getProcessedEntityStatus().equals(ProcessedEntityStatus.NOT_ELIGIBLE))
			.count();
	}

	private List<T> performBulkOperation(
		Function<List<T>, List<ProcessedEntity>> bulkOperationFunction,
		List<T> selectedEntities,
		List<ProcessedEntity> entitiesToBeProcessed,
		Consumer<BulkProgressUpdateInfo> progressUpdateCallback)
		throws InterruptedException {

		cancelLock.lock();
		int lastProcessedEntry = 0;

		try {
			for (int i = 0; i < selectedEntities.size(); i += BULK_ACTION_BATCH_SIZE) {
				synchronized (cancelLock) {
					while (cancelButtonClicked) {
						cancelLock.wait();
					}
					if (cancelAfterCurrentBatch) {
						break;
					}

					int entriesInBatch = Math.min(BULK_ACTION_BATCH_SIZE, selectedEntities.size() - i);

					List<T> entitiesFromBatch = selectedEntities.subList(i, Math.min(i + BULK_ACTION_BATCH_SIZE, selectedEntities.size()));
					List<ProcessedEntity> processedEntitiesFromBatch = bulkOperationFunction.apply(entitiesFromBatch);

					if (processedEntitiesFromBatch.size() > 0) {
						entitiesToBeProcessed.addAll(processedEntitiesFromBatch);
					}

					int successfullyProcessedInBatch = getEntriesByStatus(processedEntitiesFromBatch, ProcessedEntityStatus.SUCCESS).size();
					int ineligibleEntriesInBatch = getEntriesByStatus(processedEntitiesFromBatch, ProcessedEntityStatus.NOT_ELIGIBLE).size();
					int eligibleEntriesInBatch = getEligibleEntryCount(processedEntitiesFromBatch);

					successfulEntryCount += successfullyProcessedInBatch;
					ineligibleEntryCount += ineligibleEntriesInBatch;
					eligibleEntryCount += eligibleEntriesInBatch;

					lastProcessedEntry = Math.min(i + BULK_ACTION_BATCH_SIZE, selectedEntities.size() - 1);
					progressUpdateCallback.accept(
						new BulkProgressUpdateInfo(entriesInBatch, successfullyProcessedInBatch, entriesInBatch - successfullyProcessedInBatch));
				}
			}
		} finally {
			cancelLock.unlock();
		}

		return lastProcessedEntry == selectedEntities.size() - 1
			? Collections.emptyList()
			: selectedEntities.subList(lastProcessedEntry, selectedEntities.size());
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

	public String buildDescription(String ineligibleEntriesDescription, List<ProcessedEntity> processedEntities) {
		String failedProcessingDescription = buildFailedProcessingDescription(processedEntities);
		return !ineligibleEntriesDescription.isEmpty()
			? ineligibleEntriesDescription.concat(failedProcessingDescription)
			: failedProcessingDescription;
	}

	public String buildIneligibleEntriesDescription(boolean areIneligibleEntriesSelected, List<ProcessedEntity> selectedIneligibleEntries) {
		String ineligibleEntriesDescription = StringUtils.EMPTY;
		if (areIneligibleEntriesSelected) {
			ineligibleEntriesDescription = getErrorDescription(
				selectedIneligibleEntries.stream().map(ProcessedEntity::getEntityUuid).collect(Collectors.toList()),
				I18nProperties.getString(countEntriesNotProcessedMessageProperty),
				ineligibleEntriesNotProcessedMessageProperty != null ? I18nProperties.getString(ineligibleEntriesNotProcessedMessageProperty) : "");
		}

		return ineligibleEntriesDescription;
	}

	public String buildFailedProcessingDescription(List<ProcessedEntity> processedEntities) {
		List<String> entityUuidsNotProcessedExternalSurveillanceFailure =
			getFailedEntityUuidsByStatus(processedEntities, ProcessedEntityStatus.EXTERNAL_SURVEILLANCE_FAILURE);
		List<String> entityUuidsNotProcessedSormasToSormasFailure =
			getFailedEntityUuidsByStatus(processedEntities, ProcessedEntityStatus.SORMAS_TO_SORMAS_FAILURE);
		List<String> entityUuidsNotProcessedAccessDeniedFailure =
			getFailedEntityUuidsByStatus(processedEntities, ProcessedEntityStatus.ACCESS_DENIED_FAILURE);
		List<String> entityUuidsNotProcessedInternalFailure = getFailedEntityUuidsByStatus(processedEntities, ProcessedEntityStatus.INTERNAL_FAILURE);

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
				I18nProperties.getString(countEntriesNotProcessedAccessDeniedReasonProperty),
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

		return String.format(
			"%1s <br/> %2s",
			String.format(
				messageCountEntries,
				String.format("<b>%s</b>", entityUuids.size()),
				String.format("<b>%s</b>", HtmlHelper.cleanHtml(buildEntitiesString(entityUuids)))),
			messageEntriesNotSuccessfullyProcessed) + "<br/> <br/>";
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

	public List<ProcessedEntity> getEntitiesByProcessingStatus(List<ProcessedEntity> processedEntities, ProcessedEntityStatus status) {
		return processedEntities.stream()
			.filter(processedEntity -> processedEntity.getProcessedEntityStatus().equals(status))
			.collect(Collectors.toList());
	}

	public List<String> getFailedEntityUuidsByStatus(List<ProcessedEntity> processedEntities, ProcessedEntityStatus status) {
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
