package de.symeda.sormas.ui.task;

import java.util.Collection;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskHelper;
import de.symeda.sormas.api.task.TaskIndexDto;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DeleteListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DiscardListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class TaskController {

	public TaskController() {

	}

	public void create(TaskContext context, ReferenceDto entityRef, Runnable callback) {
		TaskEditForm createForm = new TaskEditForm(true, UserRight.TASK_CREATE);
		createForm.setValue(createNewTask(context, entityRef));
		final CommitDiscardWrapperComponent<TaskEditForm> editView = new CommitDiscardWrapperComponent<TaskEditForm>(createForm, createForm.getFieldGroup());

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

		VaadinUiUtil.showModalPopupWindow(editView, "Create new task");   
	}

	public void createSampleCollectionTask(TaskContext context, ReferenceDto entityRef, SampleDto sample) {
		TaskEditForm createForm = new TaskEditForm(true, UserRight.TASK_CREATE);
		TaskDto taskDto = createNewTask(context, entityRef);
		taskDto.setTaskType(TaskType.SAMPLE_COLLECTION);
		taskDto.setCreatorComment(sample.getNoTestPossibleReason());
		taskDto.setAssigneeUser(sample.getReportingUser());
		createForm.setValue(taskDto);

		final CommitDiscardWrapperComponent<TaskEditForm> createView = new CommitDiscardWrapperComponent<TaskEditForm>(createForm, createForm.getFieldGroup());
		createView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!createForm.getFieldGroup().isModified()) {
					TaskDto dto = createForm.getValue();
					FacadeProvider.getTaskFacade().saveTask(dto);
				}
			}
		});

		VaadinUiUtil.showModalPopupWindow(createView, "Create new task");
	}

	public void edit(TaskIndexDto dto, Runnable callback) {
		// get fresh data
		TaskDto newDto = FacadeProvider.getTaskFacade().getByUuid(dto.getUuid());

		TaskEditForm form = new TaskEditForm(false, UserRight.TASK_EDIT);
		form.setValue(newDto);
		final CommitDiscardWrapperComponent<TaskEditForm> editView = new CommitDiscardWrapperComponent<TaskEditForm>(form, form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, "Edit task");

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

		editView.addDiscardListener(new DiscardListener() {
			@Override
			public void onDiscard() {
				popupWindow.close();
			}
		});

		if (LoginHelper.getCurrentUserRoles().contains(UserRole.ADMIN)) {
			editView.addDeleteListener(new DeleteListener() {
				@Override
				public void onDelete() {
					FacadeProvider.getTaskFacade().deleteTask(newDto, LoginHelper.getCurrentUserAsReference().getUuid());
					UI.getCurrent().removeWindow(popupWindow);
					callback.run();
				}
			}, I18nProperties.getFieldCaption("Task"));
		}
	}

	private TaskDto createNewTask(TaskContext context, ReferenceDto entityRef) {
		TaskDto task = new TaskDto();
		task.setUuid(DataHelper.createUuid());
		task.setSuggestedStart(TaskHelper.getDefaultSuggestedStart());
		task.setDueDate(TaskHelper.getDefaultDueDate());
		task.setCreatorUser(LoginHelper.getCurrentUserAsReference());
		task.setTaskStatus(TaskStatus.PENDING);
		task.setPriority(TaskPriority.NORMAL);
		task.setTaskContext(context);
		switch(context) {
		case CASE:
			task.setCaze((CaseReferenceDto) entityRef); 
			break;
		case CONTACT:
			task.setContact((ContactReferenceDto) entityRef);
			break;
		case EVENT:
			task.setEvent((EventReferenceDto) entityRef);
			break;
		case GENERAL:
			break;
		}
		return task;
	}

	public String getUserCaptionWithPendingTaskCount(UserReferenceDto user) {
		long taskCount = FacadeProvider.getTaskFacade().getPendingTaskCount(user.getUuid());
		return user.getCaption() + " (" + taskCount + ")";
	}
	
	public void deleteAllSelectedItems(Collection<Object> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification("No tasks selected", "You have not selected any tasks.", Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow("Are you sure you want to delete all " + selectedRows.size() + " selected tasks?", new Runnable() {
				public void run() {
					for (Object selectedRow : selectedRows) {
						FacadeProvider.getTaskFacade().deleteTask(FacadeProvider.getTaskFacade().getByUuid(((TaskIndexDto) selectedRow).getUuid()), LoginHelper.getCurrentUser().getUuid());
					}
					callback.run();
					new Notification("Tasks deleted", "All selected tasks have been deleted.", Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
				}
			});
		}
	}
}
