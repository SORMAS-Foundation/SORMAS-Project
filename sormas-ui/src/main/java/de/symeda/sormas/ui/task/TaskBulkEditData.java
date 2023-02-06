package de.symeda.sormas.ui.task;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.user.UserReferenceDto;

public class TaskBulkEditData extends EntityDto {

	private static final long serialVersionUID = -7234609753914205675L;

	public static final String ASSIGNEE_USER = "assigneeUser";
	public static final String PRIORITY = "priority";
	public static final String TASK_STATUS = "taskStatus";

	private UserReferenceDto assigneeUser;
	private TaskPriority priority;
	private TaskStatus taskStatus;

	public UserReferenceDto getAssigneeUser() {
		return assigneeUser;
	}

	public void setAssigneeUser(UserReferenceDto assigneeUser) {
		this.assigneeUser = assigneeUser;
	}

	public TaskPriority getPriority() {
		return priority;
	}

	public void setPriority(TaskPriority priority) {
		this.priority = priority;
	}

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}
}
