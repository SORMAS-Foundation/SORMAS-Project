package de.symeda.sormas.api.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.symeda.sormas.api.I18nProperties;

public enum TaskType {

	CASE_BURIAL(TaskContext.CASE),
	CASE_FINDING(TaskContext.CASE),
	CASE_INVESTIGATION(TaskContext.CASE),
	CASE_ISOLATION(TaskContext.CASE),
	CASE_MANAGEMENT(TaskContext.CASE),
	CASE_TRANSPORT(TaskContext.CASE),
	PSYCHOLOGICAL_SUPPORT(TaskContext.CASE),
	PSYCHOSOCIAL_SUPPORT(TaskContext.CASE),
	CONTACT_MANAGEMENT(TaskContext.CONTACT),
	CONTACT_TRACING(TaskContext.CONTACT),
	CONTACT_VISIT(TaskContext.CONTACT),
	INVESTIGATE_RUMOR(TaskContext.EVENT),
	RUMOR_CREATOR(TaskContext.EVENT),
	RUMOR_HANDLING(TaskContext.EVENT)
	;
	
	private final TaskContext taskContext;

	private TaskType(TaskContext _taskContext) {
		taskContext = _taskContext;
	}
	
	public TaskContext getTaskContext() {
		return taskContext;
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
	
	private static final HashMap<TaskContext, List<TaskType>> taskTypesByContext;
	
	static {
		taskTypesByContext = new HashMap<TaskContext, List<TaskType>>();
		for (TaskType taskType : TaskType.values()) {
			TaskContext taskContext = taskType.getTaskContext();
			if (!taskTypesByContext.containsKey(taskContext)) {
				taskTypesByContext.put(taskContext, new ArrayList<TaskType>());
			}
			taskTypesByContext.get(taskContext).add(taskType);
		}
		
		for (TaskContext taskcontext : taskTypesByContext.keySet()) {
			taskTypesByContext.replace(taskcontext, Collections.unmodifiableList(taskTypesByContext.get(taskcontext)));
		}
	}
	
	public static List<TaskType> getTaskTypes(TaskContext taskContext) {
		return taskTypesByContext.get(taskContext);
	}
}
