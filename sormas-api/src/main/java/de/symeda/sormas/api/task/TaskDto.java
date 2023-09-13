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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.task;

import java.util.Date;
import java.util.Set;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.EmbeddedSensitiveData;
import de.symeda.sormas.api.utils.FieldConstraints;

@DependingOnFeatureType(featureType = FeatureType.TASK_MANAGEMENT)
public class TaskDto extends EntityDto {

	private static final long serialVersionUID = 2439546041916003653L;

	public static final long APPROXIMATE_JSON_SIZE_IN_BYTES = 2094;

	public static final String I18N_PREFIX = "Task";

	public static final String ASSIGNEE_REPLY = "assigneeReply";
	public static final String ASSIGNEE_USER = "assigneeUser";
	public static final String ASSIGNED_BY_USER = "assignedByUser";
	public static final String CAZE = "caze";
	public static final String CONTACT = "contact";
	public static final String CREATOR_COMMENT = "creatorComment";
	public static final String CREATOR_USER = "creatorUser";
	public static final String PRIORITY = "priority";
	public static final String DUE_DATE = "dueDate";
	public static final String SUGGESTED_START = "suggestedStart";
	public static final String EVENT = "event";
	public static final String PERCEIVED_START = "perceivedStart";
	public static final String STATUS_CHANGE_DATE = "statusChangeDate";
	public static final String TASK_CONTEXT = "taskContext";
	public static final String TASK_STATUS = "taskStatus";
	public static final String TASK_TYPE = "taskType";
	public static final String CONTEXT_REFERENCE = "contextReference";
	public static final String CLOSED_LAT = "closedLat";
	public static final String CLOSED_LON = "closedLon";
	public static final String TRAVEL_ENTRY = "travelEntry";
	public static final String ENVIRONMENT = "environment";
	public static final String OBSERVER_USERS = "observerUsers";

	@NotNull(message = Validations.requiredField)
	private TaskContext taskContext;
	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	private CaseReferenceDto caze;
	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	private EventReferenceDto event;
	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	private ContactReferenceDto contact;

	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	private TravelEntryReferenceDto travelEntry;

	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	private EnvironmentReferenceDto environment;

	@NotNull(message = Validations.requiredField)
	private TaskType taskType;
	private TaskPriority priority;
	@NotNull(message = Validations.requiredField)
	private Date dueDate;
	private Date suggestedStart;
	private TaskStatus taskStatus;
	private Date statusChangeDate;
	private Date perceivedStart;

	private UserReferenceDto creatorUser;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String creatorComment;
	@NotNull(message = Validations.requiredField)
	private UserReferenceDto assigneeUser;
	private UserReferenceDto assignedByUser;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String assigneeReply;
	private Set<UserReferenceDto> observerUsers;

	@Min(value = -90, message = Validations.numberTooSmall)
	@Max(value = 90, message = Validations.numberTooBig)
	private Double closedLat;
	@Min(value = -180, message = Validations.numberTooSmall)
	@Max(value = 180, message = Validations.numberTooBig)
	private Double closedLon;
	private Float closedLatLonAccuracy;

	public static TaskDto build(TaskContext context, ReferenceDto entityRef) {

		TaskDto task = new TaskDto();
		task.setUuid(DataHelper.createUuid());
		task.setSuggestedStart(TaskHelper.getDefaultSuggestedStart());
		task.setDueDate(TaskHelper.getDefaultDueDate());
		task.setTaskStatus(TaskStatus.PENDING);
		task.setPriority(TaskPriority.NORMAL);
		task.setTaskContext(context);
		switch (context) {
		case CASE:
			task.setCaze((CaseReferenceDto) entityRef);
			break;
		case CONTACT:
			task.setContact((ContactReferenceDto) entityRef);
			break;
		case EVENT:
			task.setEvent((EventReferenceDto) entityRef);
			break;
		case GENERAL:
			break;
		case TRAVEL_ENTRY:
			task.setTravelEntry((TravelEntryReferenceDto) entityRef);
			break;
		case ENVIRONMENT:
			task.setEnvironment((EnvironmentReferenceDto) entityRef);
			break;
		}
		return task;
	}

	public TaskContext getTaskContext() {
		return taskContext;
	}

	public void setTaskContext(TaskContext taskContext) {
		this.taskContext = taskContext;
	}

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public void setCaze(CaseReferenceDto caze) {
		this.caze = caze;
	}

	public EventReferenceDto getEvent() {
		return event;
	}

	public void setEvent(EventReferenceDto event) {
		this.event = event;
	}

	public ContactReferenceDto getContact() {
		return contact;
	}

	public void setContact(ContactReferenceDto contact) {
		this.contact = contact;
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Date getSuggestedStart() {
		return suggestedStart;
	}

	public void setSuggestedStart(Date suggestedStart) {
		this.suggestedStart = suggestedStart;
	}

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Date getStatusChangeDate() {
		return statusChangeDate;
	}

	public void setStatusChangeDate(Date statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}

	public Date getPerceivedStart() {
		return perceivedStart;
	}

	public void setPerceivedStart(Date perceivedStart) {
		this.perceivedStart = perceivedStart;
	}

	public UserReferenceDto getCreatorUser() {
		return creatorUser;
	}

	public void setCreatorUser(UserReferenceDto creatorUser) {
		this.creatorUser = creatorUser;
	}

	public String getCreatorComment() {
		return creatorComment;
	}

	public void setCreatorComment(String creatorComment) {
		this.creatorComment = creatorComment;
	}

	public UserReferenceDto getAssigneeUser() {
		return assigneeUser;
	}

	public void setAssigneeUser(UserReferenceDto assigneeUser) {
		this.assigneeUser = assigneeUser;
	}

	public UserReferenceDto getAssignedByUser() {
		return assignedByUser;
	}

	public void setAssignedByUser(UserReferenceDto assignedByUser) {
		this.assignedByUser = assignedByUser;
	}

	public String getAssigneeReply() {
		return assigneeReply;
	}

	public void setAssigneeReply(String assigneeReply) {
		this.assigneeReply = assigneeReply;
	}

	public Set<UserReferenceDto> getObserverUsers() {
		return observerUsers;
	}

	public void setObserverUsers(Set<UserReferenceDto> observerUsers) {
		this.observerUsers = observerUsers;
	}

	public TaskPriority getPriority() {
		return priority;
	}

	public void setPriority(TaskPriority priority) {
		this.priority = priority;
	}

	public Double getClosedLat() {
		return closedLat;
	}

	public void setClosedLat(Double closedLat) {
		this.closedLat = closedLat;
	}

	public Double getClosedLon() {
		return closedLon;
	}

	public void setClosedLon(Double closedLon) {
		this.closedLon = closedLon;
	}

	public ReferenceDto getContextReference() {

		switch (taskContext) {
		case CASE:
			return getCaze();
		case CONTACT:
			return getContact();
		case EVENT:
			return getEvent();
		case ENVIRONMENT:
			return getEnvironment();
		case GENERAL:
			return null;
		case TRAVEL_ENTRY:
			return getTravelEntry();
		default:
			throw new IndexOutOfBoundsException(taskContext.toString());
		}
	}

	public Float getClosedLatLonAccuracy() {
		return closedLatLonAccuracy;
	}

	public void setClosedLatLonAccuracy(Float closedLatLonAccuracy) {
		this.closedLatLonAccuracy = closedLatLonAccuracy;
	}

	public TravelEntryReferenceDto getTravelEntry() {
		return travelEntry;
	}

	public void setTravelEntry(TravelEntryReferenceDto travelEntry) {
		this.travelEntry = travelEntry;
	}

	public EnvironmentReferenceDto getEnvironment() {
		return environment;
	}

	public void setEnvironment(EnvironmentReferenceDto environment) {
		this.environment = environment;
	}
}
