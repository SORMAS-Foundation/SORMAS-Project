package de.symeda.sormas.ui.task;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.user.UserReferenceDto;

public class TaskBulkEditData extends EntityDto {

	private static final long serialVersionUID = -7234609753914205675L;

	public static final String TASK_ASSIGNEE = "taskAssignee";
	public static final String TASK_PRIORITY = "taskPriority";
	public static final String TASK_STATUS = "taskStatus";


	private UserReferenceDto taskAssignee;
	private TaskPriority taskPriority;
	private TaskStatus taskStatus;

	public UserReferenceDto getTaskAssignee() {
		return taskAssignee;
	}

	public void setTaskAssignee(UserReferenceDto taskAssignee) {
		this.taskAssignee = taskAssignee;
	}

	public TaskPriority getTaskPriority() {
		return taskPriority;
	}

	public void setTaskPriority(TaskPriority taskPriority) {
		this.taskPriority = taskPriority;
	}

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}
}
