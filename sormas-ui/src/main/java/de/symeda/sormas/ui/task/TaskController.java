/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskFacade;
import de.symeda.sormas.api.task.TaskIndexDto;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.BulkOperationResults;
import de.symeda.sormas.api.uuid.HasUuid;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ArchivingController;
import de.symeda.sormas.ui.utils.BulkOperationHelper;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DirtyCheckPopup;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class TaskController {

	public TaskController() {

	}

	public void create(TaskContext context, ReferenceDto entityRef, Disease disease, Runnable callback) {

		TaskEditForm createForm = new TaskEditForm(true, false, disease);
		createForm.setValue(createNewTask(context, entityRef));
		final CommitDiscardWrapperComponent<TaskEditForm> editView = new CommitDiscardWrapperComponent<TaskEditForm>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.TASK_CREATE),
			createForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				TaskDto dto = createForm.getValue();
				FacadeProvider.getTaskFacade().saveTask(dto);
				callback.run();
			}
		});

		VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingCreateNewTask));
	}

	public void createSampleCollectionTask(TaskContext context, ReferenceDto entityRef, SampleDto sample, Disease disease) {

		TaskEditForm createForm = new TaskEditForm(true, false, disease);
		TaskDto taskDto = createNewTask(context, entityRef);
		taskDto.setTaskType(TaskType.SAMPLE_COLLECTION);
		taskDto.setCreatorComment(sample.getNoTestPossibleReason());
		taskDto.setAssigneeUser(sample.getReportingUser());
		createForm.setValue(taskDto);

		final CommitDiscardWrapperComponent<TaskEditForm> createView = new CommitDiscardWrapperComponent<TaskEditForm>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.TASK_CREATE),
			createForm.getFieldGroup());
		createView.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				TaskDto dto = createForm.getValue();
				FacadeProvider.getTaskFacade().saveTask(dto);
			}
		});

		VaadinUiUtil.showModalPopupWindow(createView, I18nProperties.getString(Strings.headingCreateNewTask));
	}

	public void edit(TaskIndexDto taskIndex, Runnable callback, boolean editedFromTaskGrid, Disease disease) {

		// get fresh data
		TaskDto task = FacadeProvider.getTaskFacade().getByUuid(taskIndex.getUuid());

		TaskEditForm form = new TaskEditForm(false, editedFromTaskGrid, disease);
		form.setValue(task);

		EditPermissionType editPermissionType = FacadeProvider.getTaskFacade().getEditPermissionType(task.getUuid());
		boolean isEditingAllowed = UserProvider.getCurrent().hasUserRight(UserRight.TASK_EDIT) && editPermissionType == EditPermissionType.ALLOWED;
		boolean isEditingOrDeletingAllowed = isEditingAllowed || UserProvider.getCurrent().hasUserRight(UserRight.TASK_DELETE);

		final CommitDiscardWrapperComponent<TaskEditForm> editView =
			new CommitDiscardWrapperComponent<TaskEditForm>(form, true, form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(
			editView,
			isEditingOrDeletingAllowed ? I18nProperties.getString(Strings.headingEditTask) : I18nProperties.getString(Strings.headingViewTask));

		editView.addCommitListener(() -> {
			if (!form.getFieldGroup().isModified()) {
				TaskDto formValue = form.getValue();
				if (!formValue.getAssigneeUser().getUuid().equals(taskIndex.getAssigneeUser().getUuid())) {
					formValue.setAssignedByUser(UserProvider.getCurrent().getUserReference());
				}
				FacadeProvider.getTaskFacade().saveTask(formValue);

				if (!editedFromTaskGrid && formValue.getCaze() != null) {
					ControllerProvider.getCaseController().navigateToCase(formValue.getCaze().getUuid());
				}

				popupWindow.close();
				callback.run();
			}
		});

		editView.addDiscardListener(popupWindow::close);

		if (UserProvider.getCurrent().hasUserRight(UserRight.TASK_DELETE)) {
			editView.addDeleteListener(() -> {
				FacadeProvider.getTaskFacade().deleteTask(task);
				UI.getCurrent().removeWindow(popupWindow);
				callback.run();
			}, I18nProperties.getString(Strings.entityTask));
		}

		// Initialize 'Archive' button
		if (UserProvider.getCurrent().hasUserRight(UserRight.TASK_ARCHIVE)) {
			boolean archived = FacadeProvider.getTaskFacade().isArchived(task.getUuid());
			Button archiveButton = ButtonHelper.createButton(
				ArchivingController.ARCHIVE_DEARCHIVE_BUTTON_ID,
				I18nProperties.getCaption(archived ? Captions.actionDearchiveCoreEntity : Captions.actionArchiveCoreEntity),
				e -> {
					if (editView.isDirty()) {
						DirtyCheckPopup.show(editView, () -> archiveOrDearchive(task, !archived, () -> {
							popupWindow.close();
							callback.run();
						}));
					} else {
						archiveOrDearchive(task, !archived, () -> {
							popupWindow.close();
							callback.run();
						});
					}
				},
				ValoTheme.BUTTON_LINK);

			editView.getButtonsPanel().addComponentAsFirst(archiveButton);
			editView.getButtonsPanel().setComponentAlignment(archiveButton, Alignment.BOTTOM_LEFT);
		}

		editView.addToActiveButtonsList(ArchivingController.ARCHIVE_DEARCHIVE_BUTTON_ID);
		editView.restrictEditableComponentsOnEditView(UserRight.TASK_EDIT, UserRight.TASK_DELETE, editPermissionType);
	}

	private TaskDto createNewTask(TaskContext context, ReferenceDto entityRef) {
		TaskDto task = TaskDto.build(context, entityRef);
		task.setCreatorUser(UserProvider.getCurrent().getUserReference());
		task.setAssignedByUser(UserProvider.getCurrent().getUserReference());
		return task;
	}

	public void deleteAllSelectedItems(Collection<TaskIndexDto> selectedRows, Runnable callback) {

		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoTasksSelected),
				I18nProperties.getString(Strings.messageNoTasksSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil
				.showDeleteConfirmationWindow(String.format(I18nProperties.getString(Strings.confirmationDeleteTasks), selectedRows.size()), () -> {
					for (TaskIndexDto selectedRow : selectedRows) {
						FacadeProvider.getTaskFacade().deleteTask(FacadeProvider.getTaskFacade().getByUuid(selectedRow.getUuid()));
					}
					callback.run();
					new Notification(
						I18nProperties.getString(Strings.headingTasksDeleted),
						I18nProperties.getString(Strings.messageTasksDeleted),
						Type.HUMANIZED_MESSAGE,
						false).show(Page.getCurrent());
				});
		}
	}

	public void showBulkTaskDataEditComponent(Collection<TaskIndexDto> selectedTasks, TaskGrid taskGrid, Runnable noEntriesRemainingCallback) {

		if (selectedTasks.isEmpty()) {
			new Notification(
				I18nProperties.getString(Strings.headingNoTasksSelected),
				I18nProperties.getString(Strings.messageNoTasksSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
			return;
		}

		// Create a temporary task in order to use the CommitDiscardWrapperComponent
		TaskDto tempTask = new TaskDto();
		BulkTaskDataForm form = new BulkTaskDataForm(selectedTasks);
		form.setValue(tempTask);
		final CommitDiscardWrapperComponent<BulkTaskDataForm> editView = new CommitDiscardWrapperComponent<>(form, form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingEditTask));

		editView.addCommitListener(() -> {
			TaskDto updatedTempTask = form.getValue();
			TaskFacade taskFacade = FacadeProvider.getTaskFacade();

			List<TaskIndexDto> selectedTasksCpy = new ArrayList<>(selectedTasks);
			BulkOperationHelper.doBulkOperation(
				selectedEntries -> taskFacade.saveBulkTasks(
					selectedEntries.stream().map(HasUuid::getUuid).collect(Collectors.toList()),
					updatedTempTask,
					form.getPriorityCheckbox().getValue(),
					form.getAssigneeCheckbox().getValue(),
					form.getTaskStatusCheckbox().getValue()),
				selectedTasksCpy,
				selectedTasksCpy.size(),
				results -> handleBulkOperationDone(results, popupWindow, taskGrid, selectedTasks, noEntriesRemainingCallback));
		});

		editView.addDiscardListener(popupWindow::close);
	}

	private void handleBulkOperationDone(
		BulkOperationResults<?> results,
		Window popupWindow,
		TaskGrid taskGrid,
		Collection<TaskIndexDto> selectedTasks,
		Runnable noEntriesRemainingCallback) {

		popupWindow.close();
		taskGrid.reload();
		if (CollectionUtils.isNotEmpty(results.getRemainingEntries())) {
			taskGrid.asMultiSelect()
				.selectItems(selectedTasks.stream().filter(t -> results.getRemainingEntries().contains(t.getUuid())).toArray(TaskIndexDto[]::new));
		} else {
			noEntriesRemainingCallback.run();
		}
	}

	private void archiveOrDearchive(TaskDto task, boolean archive, Runnable callback) {

		VaadinUiUtil.showConfirmationPopup(
			archive ? I18nProperties.getString(Strings.headingConfirmArchiving) : I18nProperties.getString(Strings.headingConfirmDearchiving),
			new Label(
				archive ? I18nProperties.getString(Strings.confirmationArchiveTask) : I18nProperties.getString(Strings.confirmationDearchiveTask)),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			null,
			e -> {
				if (Boolean.TRUE.equals(e)) {
					FacadeProvider.getTaskFacade().updateArchived(task.getUuid(), archive);
					callback.run();
					Notification.show(
						archive ? I18nProperties.getString(Strings.messageTaskArchived) : I18nProperties.getString(Strings.messageTaskDearchived),
						Type.ASSISTIVE_NOTIFICATION);
				}
			});
	}

	public void archiveAllSelectedItems(Collection<? extends TaskIndexDto> selectedRows, Runnable callback) {

		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoTasksSelected),
				I18nProperties.getString(Strings.messageNoTasksSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingConfirmArchiving),
				new Label(String.format(I18nProperties.getString(Strings.confirmationArchiveTasks), selectedRows.size())),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				null,
				e -> {
					if (e.booleanValue()) {
						List<String> caseUuids = selectedRows.stream().map(TaskIndexDto::getUuid).collect(Collectors.toList());
						FacadeProvider.getTaskFacade().updateArchived(caseUuids, true);
						callback.run();
						new Notification(
							I18nProperties.getString(Strings.headingTasksArchived),
							I18nProperties.getString(Strings.messageTasksArchived),
							Type.HUMANIZED_MESSAGE,
							false).show(Page.getCurrent());
					}
				});
		}
	}

	public void dearchiveAllSelectedItems(Collection<? extends TaskIndexDto> selectedRows, Runnable callback) {

		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoTasksSelected),
				I18nProperties.getString(Strings.messageNoTasksSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingConfirmDearchiving),
				new Label(String.format(I18nProperties.getString(Strings.confirmationDearchiveTasks), selectedRows.size())),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				null,
				e -> {
					if (e.booleanValue() == true) {
						List<String> caseUuids = selectedRows.stream().map(TaskIndexDto::getUuid).collect(Collectors.toList());
						FacadeProvider.getTaskFacade().updateArchived(caseUuids, false);
						callback.run();
						new Notification(
							I18nProperties.getString(Strings.headingTasksDearchived),
							I18nProperties.getString(Strings.messageTasksDearchived),
							Type.HUMANIZED_MESSAGE,
							false).show(Page.getCurrent());
					}
				});
		}
	}
}
