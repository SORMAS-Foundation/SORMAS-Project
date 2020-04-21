/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum TaskType {

	ACTIVE_SEARCH_FOR_OTHER_CASES(TaskContext.CASE),
	CASE_ISOLATION(TaskContext.CASE),
	CASE_INVESTIGATION(TaskContext.CASE),
	CASE_MANAGEMENT(TaskContext.CASE),
	CASE_BURIAL(TaskContext.CASE),
	CONTACT_TRACING(TaskContext.CASE),
	SAMPLE_COLLECTION(TaskContext.CASE),
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
	SURVEILLANCE_REPORT_GENERATION(TaskContext.GENERAL),
	WEEKLY_REPORT_GENERATION(TaskContext.GENERAL);

	private final boolean creatorCommentRequired;
	private final TaskContext[] taskContexts;

	TaskType(TaskContext... taskContexts) {
		this(false, taskContexts);
	}

	TaskType(boolean creatorCommentRequired, TaskContext... taskContexts) {
		this.creatorCommentRequired = creatorCommentRequired;
		this.taskContexts = taskContexts;
	}

	public boolean isCreatorCommentRequired() {
		return creatorCommentRequired;
	}

	public TaskContext[] getTaskContexts() {
		return taskContexts;
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	private static final EnumMap<TaskContext, List<TaskType>> TASK_TYPES_BY_CONTEXT;
	static {
		TASK_TYPES_BY_CONTEXT = new EnumMap<TaskContext, List<TaskType>>(TaskContext.class);
		for (TaskType taskType : TaskType.values()) {
			TaskContext[] taskContexts = taskType.getTaskContexts();
			for (TaskContext taskContext : taskContexts) {
				if (!TASK_TYPES_BY_CONTEXT.containsKey(taskContext)) {
					TASK_TYPES_BY_CONTEXT.put(taskContext, new ArrayList<TaskType>());
				}
				TASK_TYPES_BY_CONTEXT.get(taskContext).add(taskType);
			}
		}

		// make lists in the map unmodifiable
		for (Map.Entry<TaskContext, List<TaskType>> taskContext : TASK_TYPES_BY_CONTEXT.entrySet()) {
			TASK_TYPES_BY_CONTEXT.put(taskContext.getKey(), Collections.unmodifiableList(taskContext.getValue()));
		}
	}

	public static List<TaskType> getTaskTypes(TaskContext taskContext) {

		if (taskContext == null) {
			return Arrays.asList(TaskType.values());
		}
		return TASK_TYPES_BY_CONTEXT.get(taskContext);
	}
}
