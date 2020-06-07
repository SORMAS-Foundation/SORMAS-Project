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

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskIndexDto;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DeleteListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class TaskController {

	public TaskController() {

	}

	public void create(TaskContext context, ReferenceDto entityRef, Runnable callback) {

		TaskEditForm createForm = new TaskEditForm(true);
		createForm.setValue(createNewTask(context, entityRef));
		final CommitDiscardWrapperComponent<TaskEditForm> editView = new CommitDiscardWrapperComponent<TaskEditForm>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.TASK_CREATE),
			createForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				if (!createForm.getFieldGroup().isModified()) {
					TaskDto dto = createForm.getValue();
					FacadeProvider.getTaskFacade().saveTask(dto);
					callback.run();
				}
			}
		});

		VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingCreateNewTask));
	}

	public void createSampleCollectionTask(TaskContext context, ReferenceDto entityRef, SampleDto sample) {

		TaskEditForm createForm = new TaskEditForm(true);
		TaskDto taskDto = createNewTask(context, entityRef);
		taskDto.setTaskType(TaskType.SAMPLE_COLLECTION);
		taskDto.setCreatorComment(sample.getNoTestPossibleReason());
		taskDto.setAssigneeUser(sample.getReportingUser());
		createForm.setValue(taskDto);

		final CommitDiscardWrapperComponent<TaskEditForm> createView = new CommitDiscardWrapperComponent<TaskEditForm>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.TASK_CREATE),
			createForm.getFieldGroup());
		createView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				if (!createForm.getFieldGroup().isModified()) {
					TaskDto dto = createForm.getValue();
					FacadeProvider.getTaskFacade().saveTask(dto);
				}
			}
		});

		VaadinUiUtil.showModalPopupWindow(createView, I18nProperties.getString(Strings.headingCreateNewTask));
	}

	public void edit(TaskIndexDto dto, Runnable callback) {

		// get fresh data
		TaskDto newDto = FacadeProvider.getTaskFacade().getByUuid(dto.getUuid());

		TaskEditForm form = new TaskEditForm(false);
		form.setValue(newDto);
		final CommitDiscardWrapperComponent<TaskEditForm> editView =
			new CommitDiscardWrapperComponent<TaskEditForm>(form, UserProvider.getCurrent().hasUserRight(UserRight.TASK_EDIT), form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingEditTask));

		editView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				if (!form.getFieldGroup().isModified()) {
					TaskDto dto = form.getValue();
					FacadeProvider.getTaskFacade().saveTask(dto);
					popupWindow.close();
					callback.run();
				}
			}
		});

		editView.addDiscardListener(() -> popupWindow.close());

		if (UserProvider.getCurrent().hasUserRole(UserRole.ADMIN)) {
			editView.addDeleteListener(new DeleteListener() {

				@Override
				public void onDelete() {
					FacadeProvider.getTaskFacade().deleteTask(newDto);
					UI.getCurrent().removeWindow(popupWindow);
					callback.run();
				}
			}, I18nProperties.getString(Strings.entityTask));
		}
	}

	private TaskDto createNewTask(TaskContext context, ReferenceDto entityRef) {
		TaskDto task = TaskDto.build(context, entityRef);
		task.setCreatorUser(UserProvider.getCurrent().getUserReference());
		return task;
	}

	public String getUserCaptionWithPendingTaskCount(UserReferenceDto user) {
		long taskCount = FacadeProvider.getTaskFacade().getPendingTaskCount(user.getUuid());
		return user.getCaption() + " (" + taskCount + ")";
	}

	public void deleteAllSelectedItems(Collection<TaskIndexDto> selectedRows, Runnable callback) {

		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoTasksSelected),
				I18nProperties.getString(Strings.messageNoTasksSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow(
				String.format(I18nProperties.getString(Strings.confirmationDeleteTasks), selectedRows.size()),
				new Runnable() {

					public void run() {
						for (TaskIndexDto selectedRow : selectedRows) {
							FacadeProvider.getTaskFacade().deleteTask(FacadeProvider.getTaskFacade().getByUuid(selectedRow.getUuid()));
						}
						callback.run();
						new Notification(
							I18nProperties.getString(Strings.headingTasksDeleted),
							I18nProperties.getString(Strings.messageTasksDeleted),
							Type.HUMANIZED_MESSAGE,
							false).show(Page.getCurrent());
					}
				});
		}
	}
}
