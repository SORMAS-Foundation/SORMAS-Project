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
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskFacade;
import de.symeda.sormas.api.task.TaskIndexDto;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.uuid.HasUuid;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ArchiveHandlers;
import de.symeda.sormas.ui.utils.ArchivingController;
import de.symeda.sormas.ui.utils.BulkOperationHandler;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DeleteRestoreHandlers;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class TaskController {

	public TaskController() {

	}

	public void create(TaskContext context, ReferenceDto entityRef, Disease disease, Runnable callback) {

		TaskEditForm createForm = new TaskEditForm(true, false, disease);
		createForm.setValue(createNewTask(context, entityRef));
		final CommitDiscardWrapperComponent<TaskEditForm> editView =
			new CommitDiscardWrapperComponent<TaskEditForm>(createForm, UiUtil.permitted(UserRight.TASK_CREATE), createForm.getFieldGroup());

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

		final CommitDiscardWrapperComponent<TaskEditForm> createView =
			new CommitDiscardWrapperComponent<TaskEditForm>(createForm, UiUtil.permitted(UserRight.TASK_CREATE), createForm.getFieldGroup());
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
		boolean isEditingAllowed = UiUtil.permitted(editPermissionType, UserRight.TASK_EDIT);

		final CommitDiscardWrapperComponent<TaskEditForm> editView =
			new CommitDiscardWrapperComponent<TaskEditForm>(form, true, form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(
			editView,
			isEditingAllowed ? I18nProperties.getString(Strings.headingEditTask) : I18nProperties.getString(Strings.headingViewTask));

		editView.addCommitListener(() -> {
			if (!form.getFieldGroup().isModified()) {
				TaskDto formValue = form.getValue();
				if (!formValue.getAssigneeUser().getUuid().equals(taskIndex.getAssigneeUser().getUuid())) {
					formValue.setAssignedByUser(UiUtil.getUserReference());
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

		if (UiUtil.permitted(UserRight.TASK_DELETE)) {
			editView.addDeleteListener(() -> {
				FacadeProvider.getTaskFacade().delete(task.getUuid());
				UI.getCurrent().removeWindow(popupWindow);
				callback.run();
			}, I18nProperties.getString(Strings.entityTask));
		}

		// Initialize 'Archive' button
		if (UiUtil.permitted(UserRight.TASK_ARCHIVE)) {
			ControllerProvider.getArchiveController().addArchivingButtonWithDirtyCheck(task, ArchiveHandlers.forTask(), editView, () -> {
				popupWindow.close();
				callback.run();
			});
		}

		editView.addToActiveButtonsList(ArchivingController.ARCHIVE_DEARCHIVE_BUTTON_ID);

		editView
			.restrictEditableComponentsOnEditView(UserRight.TASK_EDIT, null, UserRight.TASK_DELETE, UserRight.TASK_ARCHIVE, editPermissionType, true);
	}

	private TaskDto createNewTask(TaskContext context, ReferenceDto entityRef) {
		TaskDto task = TaskDto.build(context, entityRef);
		task.setCreatorUser(UiUtil.getUserReference());
		task.setAssignedByUser(UiUtil.getUserReference());
		return task;
	}

	public void deleteAllSelectedItems(Collection<TaskIndexDto> selectedRows, TaskGrid taskGrid, Runnable noEntriesRemainingCallback) {

		ControllerProvider.getPermanentDeleteController()
			.deleteAllSelectedItems(
				selectedRows,
				DeleteRestoreHandlers.forTask(),
				true,
				bulkOperationCallback(taskGrid, noEntriesRemainingCallback, null));

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
			BulkOperationHandler.<TaskIndexDto> forBulkEdit()
				.doBulkOperation(
					selectedEntries -> taskFacade.saveBulkTasks(
						selectedEntries.stream().map(HasUuid::getUuid).collect(Collectors.toList()),
						updatedTempTask,
						form.getPriorityCheckbox().getValue(),
						form.getAssigneeCheckbox().getValue(),
						form.getTaskStatusCheckbox().getValue()),
					selectedTasksCpy,
					bulkOperationCallback(taskGrid, noEntriesRemainingCallback, popupWindow));
		});

		editView.addDiscardListener(popupWindow::close);
	}

	private Consumer<List<TaskIndexDto>> bulkOperationCallback(TaskGrid taskGrid, Runnable noEntriesRemainingCallback, Window popupWindow) {
		return remainingTasks -> {
			if (popupWindow != null) {
				popupWindow.close();
			}

			taskGrid.reload();
			if (CollectionUtils.isNotEmpty(remainingTasks)) {
				taskGrid.asMultiSelect().selectItems(remainingTasks.toArray(new TaskIndexDto[0]));
			} else {
				noEntriesRemainingCallback.run();
			}
		};
	}

	public void archiveAllSelectedItems(Collection<TaskIndexDto> selectedRows, TaskGrid taskGrid, Runnable noEntriesRemainingCallback) {
		ControllerProvider.getArchiveController()
			.archiveSelectedItems(selectedRows, ArchiveHandlers.forTask(), bulkOperationCallback(taskGrid, noEntriesRemainingCallback, null));
	}

	public void dearchiveAllSelectedItems(Collection<TaskIndexDto> selectedRows, TaskGrid taskGrid, Runnable noEntriesRemainingCallback) {
		ControllerProvider.getArchiveController()
			.dearchiveSelectedItems(selectedRows, ArchiveHandlers.forTask(), bulkOperationCallback(taskGrid, noEntriesRemainingCallback, null));
	}
}
