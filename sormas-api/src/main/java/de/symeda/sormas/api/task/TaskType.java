package de.symeda.sormas.api.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.symeda.sormas.api.I18nProperties;

public enum TaskType {
	
	CASE_ISOLATION(Arrays.asList(TaskContext.CASE)),
	CASE_INVESTIGATION(Arrays.asList(TaskContext.CASE)),
	CASE_MANAGEMENT(Arrays.asList(TaskContext.CASE)),
	CASE_BURIAL(Arrays.asList(TaskContext.CASE)),
	CONTACT_INVESTIGATION(Arrays.asList(TaskContext.CONTACT)),
	CONTACT_FOLLOW_UP(Arrays.asList(TaskContext.CONTACT)),
	CONTACT_TRACING(Arrays.asList(TaskContext.CONTACT)),
	ANIMAL_TESTING(Arrays.asList(TaskContext.EVENT)),
	EVENT_INVESTIGATION(Arrays.asList(TaskContext.EVENT)),
	TREATMENT_CENTER_ESTABLISHMENT(Arrays.asList(TaskContext.CASE, TaskContext.EVENT)),
	ENVIRONMENTAL_HEALTH_ACTIVITIES(Arrays.asList(TaskContext.CASE, TaskContext.EVENT)),
	DECONTAMINATION_DISINFECTION_ACTIVITIES(Arrays.asList(TaskContext.CASE, TaskContext.EVENT)),
	QUARANTINE_PLACE(Arrays.asList(TaskContext.EVENT, TaskContext.CASE)),
	VACCINATION_ACTIVITIES(Arrays.asList(TaskContext.EVENT, TaskContext.CASE)),
	ANIMAL_DEPOPULATION(Arrays.asList(TaskContext.EVENT, TaskContext.CASE)),
	OTHER(Arrays.asList(TaskContext.CASE, TaskContext.CONTACT, TaskContext.EVENT, TaskContext.GENERAL)),
	DAILY_REPORT_GENERATION(Arrays.asList(TaskContext.GENERAL)),
	SURVEILLANCE_REPORT_GENERATION(Arrays.asList(TaskContext.GENERAL));
	
	private final List<TaskContext> taskContexts;

	private TaskType(List<TaskContext> _taskContexts) {
		taskContexts = _taskContexts;
	}
	
	public List<TaskContext> getTaskContexts() {
		return taskContexts;
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
	
	private static final HashMap<TaskContext, List<TaskType>> taskTypesByContext;
	
	static {
		taskTypesByContext = new HashMap<TaskContext, List<TaskType>>();
		for (TaskType taskType : TaskType.values()) {
			List<TaskContext> taskContexts = taskType.getTaskContexts();
			for (TaskContext taskContext : taskContexts) {
				if (!taskTypesByContext.containsKey(taskContext)) {
					taskTypesByContext.put(taskContext, new ArrayList<TaskType>());
				}
				taskTypesByContext.get(taskContext).add(taskType);
			}
		}
		
		// make lists in the map unmodifiable
		for (TaskContext taskcontext : taskTypesByContext.keySet()) {
			taskTypesByContext.put(taskcontext, Collections.unmodifiableList(taskTypesByContext.get(taskcontext)));
		}
	}
	
	public static List<TaskType> getTaskTypes(TaskContext taskContext) {
		if (taskContext == null) {
			return Arrays.asList(TaskType.values());
		}
		return taskTypesByContext.get(taskContext);
	}
}
