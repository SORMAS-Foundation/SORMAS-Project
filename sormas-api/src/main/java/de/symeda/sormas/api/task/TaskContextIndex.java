package de.symeda.sormas.api.task;

import java.io.Serializable;

public class TaskContextIndex implements Serializable, Cloneable {

	private TaskContext taskContext;
	private String uuid;

	public TaskContextIndex() {
	}

	public TaskContextIndex(TaskContext taskContext) {
		this.taskContext = taskContext;
	}

	public TaskContext getTaskContext() {
		return taskContext;
	}

	public String getUuid() {
		return uuid;
	}
}
