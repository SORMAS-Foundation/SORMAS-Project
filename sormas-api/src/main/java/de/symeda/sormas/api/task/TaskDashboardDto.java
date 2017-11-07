package de.symeda.sormas.api.task;

import de.symeda.sormas.api.DataTransferObject;

public class TaskDashboardDto extends DataTransferObject {

	private static final long serialVersionUID = -6333336451531224822L;

	public static final String I18N_PREFIX = "Task";
	
	public static final String PRIORITY = "priority";
	public static final String TASK_STATUS = "taskStatus";

	private TaskPriority priority;
	private TaskStatus taskStatus;
	
	public TaskDashboardDto(String uuid, TaskPriority priority, TaskStatus taskStatus) {
		setUuid(uuid);
		this.priority = priority;
		this.taskStatus = taskStatus;
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
