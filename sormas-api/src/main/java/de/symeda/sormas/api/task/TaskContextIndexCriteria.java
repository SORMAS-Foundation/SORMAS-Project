package de.symeda.sormas.api.task;

import de.symeda.sormas.api.audit.AuditIncludeProperty;
import de.symeda.sormas.api.audit.AuditedClass;

import java.io.Serializable;

@AuditedClass
public class TaskContextIndexCriteria implements Serializable, Cloneable {

	private TaskContext taskContext;
	@AuditIncludeProperty
	private String uuid;

	public TaskContextIndexCriteria() {
	}

	public TaskContextIndexCriteria(TaskContext taskContext) {
		this.taskContext = taskContext;
	}

	public TaskContext getTaskContext() {
		return taskContext;
	}

	public String getUuid() {
		return uuid;
	}
}
