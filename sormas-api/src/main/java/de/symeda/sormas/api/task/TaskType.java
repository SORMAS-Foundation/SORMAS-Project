package de.symeda.sormas.api.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.I18nProperties;

public enum TaskType {
	
	CASE_ISOLATION(TaskContext.CASE),
	CASE_INVESTIGATION(TaskContext.CASE),
	CASE_MANAGEMENT(TaskContext.CASE),
	CASE_BURIAL(TaskContext.CASE),
	CONTACT_TRACING(TaskContext.CASE),
	CONTACT_INVESTIGATION(TaskContext.CONTACT),
	CONTACT_FOLLOW_UP(TaskContext.CONTACT),
	ANIMAL_TESTING(TaskContext.EVENT),
	EVENT_INVESTIGATION(TaskContext.EVENT),
	TREATMENT_CENTER_ESTABLISHMENT(TaskContext.CASE, TaskContext.EVENT),
	ENVIRONMENTAL_HEALTH_ACTIVITIES(TaskContext.CASE, TaskContext.EVENT),
	DECONTAMINATION_DISINFECTION_ACTIVITIES(TaskContext.CASE, TaskContext.EVENT),
	QUARANTINE_PLACE(TaskContext.EVENT, TaskContext.CASE),
	VACCINATION_ACTIVITIES(TaskContext.EVENT, TaskContext.CASE),
	ANIMAL_DEPOPULATION(TaskContext.EVENT, TaskContext.CASE),
	OTHER(true, TaskContext.CASE, TaskContext.CONTACT, TaskContext.EVENT, TaskContext.GENERAL),
	DAILY_REPORT_GENERATION(TaskContext.GENERAL),
	SURVEILLANCE_REPORT_GENERATION(TaskContext.GENERAL);
	
	private final boolean creatorCommentRequired;
	private final TaskContext[] taskContexts;

	private TaskType(TaskContext... _taskContexts) {
		this(false, _taskContexts);
	}

	private TaskType(boolean _creatoreCommentRequired, TaskContext... _taskContexts) {
		creatorCommentRequired = _creatoreCommentRequired;
		taskContexts = _taskContexts;
	}

	public TaskContext[] getTaskContexts() {
		return taskContexts;
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
	private static final EnumMap<TaskContext, List<TaskType>> taskTypesByContext;
	
	static {
		taskTypesByContext = new EnumMap<TaskContext, List<TaskType>> (TaskContext.class);
		for (TaskType taskType : TaskType.values()) {
			TaskContext[] taskContexts = taskType.getTaskContexts();
			for (TaskContext taskContext : taskContexts) {
				if (!taskTypesByContext.containsKey(taskContext)) {
					taskTypesByContext.put(taskContext, new ArrayList<TaskType>());
				}
				taskTypesByContext.get(taskContext).add(taskType);
			}
		}
		
		// make lists in the map unmodifiable
		for (Map.Entry<TaskContext, List<TaskType>> taskContext : taskTypesByContext.entrySet()) {
			taskTypesByContext.put(taskContext.getKey(), Collections.unmodifiableList(taskContext.getValue())); // TODO @Martin please review if this is correct
		}
	}
	
	public static List<TaskType> getTaskTypes(TaskContext taskContext) {
		if (taskContext == null) {
			return Arrays.asList(TaskType.values());
		}
		return taskTypesByContext.get(taskContext);
	}

	public boolean isCreatorCommentRequired() {
		return creatorCommentRequired;
	}
}
