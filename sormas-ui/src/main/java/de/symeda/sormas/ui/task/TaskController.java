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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.server.Page;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskIndexDto;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
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

	public void edit(TaskIndexDto dto, Runnable callback, boolean editedFromTaskGrid, Disease disease) {

		// get fresh data
		TaskDto newDto = FacadeProvider.getTaskFacade().getByUuid(dto.getUuid());

		TaskEditForm form = new TaskEditForm(false, editedFromTaskGrid, disease);
		form.setValue(newDto);
		final CommitDiscardWrapperComponent<TaskEditForm> editView =
			new CommitDiscardWrapperComponent<TaskEditForm>(form, UserProvider.getCurrent().hasUserRight(UserRight.TASK_EDIT), form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingEditTask));

		editView.addCommitListener(() -> {
			if (!form.getFieldGroup().isModified()) {
				TaskDto dto1 = form.getValue();
				FacadeProvider.getTaskFacade().saveTask(dto1);

				if (!editedFromTaskGrid && dto1.getCaze() != null) {
					ControllerProvider.getCaseController().navigateToCase(dto1.getCaze().getUuid());
				}

				popupWindow.close();
				callback.run();
			}
		});

		editView.addDiscardListener(popupWindow::close);

		if (UserProvider.getCurrent().hasUserRight(UserRight.TASK_DELETE)) {
			editView.addDeleteListener(() -> {
				FacadeProvider.getTaskFacade().deleteTask(newDto);
				UI.getCurrent().removeWindow(popupWindow);
				callback.run();
			}, I18nProperties.getString(Strings.entityTask));
		}
	}

	private TaskDto createNewTask(TaskContext context, ReferenceDto entityRef) {
		TaskDto task = TaskDto.build(context, entityRef);
		task.setCreatorUser(UserProvider.getCurrent().getUserReference());
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

	public void showBulkTaskDataEditComponent(Collection<? extends TaskIndexDto> selectedTasks, Runnable callback) {
		if (selectedTasks.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoTasksSelected),
				I18nProperties.getString(Strings.messageNoTasksSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
			return;
		}

		// Check if tasks with multiple districts have been selected
		// In that situation we won't allow editing them
		List<String> taskUuids = selectedTasks.stream().map(TaskIndexDto::getUuid).collect(Collectors.toList());
		List<DistrictReferenceDto> districts = FacadeProvider.getTaskFacade().getDistrictsByTaskUuids(taskUuids, 2L);
		if (districts.size() == 2) {
			new Notification(
				I18nProperties.getString(Strings.headingUnavailableTaskEdition),
				I18nProperties.getString(Strings.messageUnavailableTaskEditionDueToDifferentDistricts),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
			return;
		}

		// Create a temporary task in order to use the CommitDiscardWrapperComponent
		TaskBulkEditData bulkEditData = new TaskBulkEditData();
		BulkTaskDataForm form = new BulkTaskDataForm(districts.stream().findFirst().orElse(null));
		form.setValue(bulkEditData);
		final CommitDiscardWrapperComponent<BulkTaskDataForm> editView = new CommitDiscardWrapperComponent<>(form, form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingEditTask));

		editView.addCommitListener(() -> {
			TaskBulkEditData updatedBulkEditData = form.getValue();
			for (TaskIndexDto indexDto : selectedTasks) {
				TaskDto dto = FacadeProvider.getTaskFacade().getByUuid(indexDto.getUuid());
				if (form.getPriorityCheckbox().getValue()) {
					dto.setPriority(updatedBulkEditData.getTaskPriority());
				}
				if (form.getAssigneeCheckbox().getValue()) {
					dto.setAssigneeUser(updatedBulkEditData.getTaskAssignee());
				}
				if (form.getTaskStatusCheckbox().getValue()) {
					dto.setTaskStatus(updatedBulkEditData.getTaskStatus());
				}

				FacadeProvider.getTaskFacade().saveTask(dto);
			}
			popupWindow.close();
			Notification.show(I18nProperties.getString(Strings.messageTasksEdited), Type.HUMANIZED_MESSAGE);
			callback.run();
		});

		editView.addDiscardListener(popupWindow::close);
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
