package de.symeda.sormas.api.task;

import java.io.Serializable;

public class DashboardTaskDto implements Serializable {

	private static final long serialVersionUID = -4719548498678710837L;

	public static final String I18N_PREFIX = "Task";
	
	public static final String PRIORITY = "priority";
	public static final String TASK_STATUS = "taskStatus";

	private TaskPriority priority;
	private TaskStatus taskStatus;
	
	public DashboardTaskDto(TaskPriority priority, TaskStatus taskStatus) {
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
