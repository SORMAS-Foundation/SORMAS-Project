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
package de.symeda.sormas.backend.task;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_BIG;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.task.ITask;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.NotExposedToApi;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.user.User;

@Entity
public class Task extends AbstractDomainObject implements ITask {

	private static final long serialVersionUID = -4754578341242164661L;

	public static final String TABLE_NAME = "task";

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
	public static final String CLOSED_LAT = "closedLat";
	public static final String CLOSED_LON = "closedLon";
	public static final String ARCHIVED = "archived";
	public static final String TRAVEL_ENTRY = "travelEntry";
	public static final String ENVIRONMENT = "environment";
	public static final String OBSERVER_USER = "observerUsers";

	public static final String TASK_OBSERVER_TABLE = "task_observer";
	public static final String TASK_OBSERVER_JOIN_COLUMN = "task_id";
	public static final String TASK_OBSERVER_INVERSE_JOIN_COLUMN = "user_id";

	private TaskContext taskContext;
	private Case caze;
	private Contact contact;
	private Event event;
	private TravelEntry travelEntry;
	private Environment environment;

	private TaskType taskType;
	private TaskPriority priority;
	private Date dueDate;
	private Date suggestedStart;
	private TaskStatus taskStatus;
	private Date statusChangeDate;
	private Date perceivedStart;

	private User creatorUser;
	private String creatorComment;
	private User assigneeUser;
	private User assignedByUser;
	private String assigneeReply;
	private List<User> observerUsers;

	private Double closedLat;
	private Double closedLon;
	private Float closedLatLonAccuracy;

	@NotExposedToApi
	private boolean archived;

	@Enumerated(EnumType.STRING)
	public TaskContext getTaskContext() {
		return taskContext;
	}

	public void setTaskContext(TaskContext taskContext) {
		this.taskContext = taskContext;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public Case getCaze() {
		return caze;
	}

	public void setCaze(Case caze) {
		this.caze = caze;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	@Enumerated(EnumType.STRING)
	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	@Enumerated(EnumType.STRING)
	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getStatusChangeDate() {
		return statusChangeDate;
	}

	public void setStatusChangeDate(Date statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getPerceivedStart() {
		return perceivedStart;
	}

	public void setPerceivedStart(Date perceivedStart) {
		this.perceivedStart = perceivedStart;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public User getCreatorUser() {
		return creatorUser;
	}

	public void setCreatorUser(User creatorUser) {
		this.creatorUser = creatorUser;
	}

	@Column(length = CHARACTER_LIMIT_BIG)
	public String getCreatorComment() {
		return creatorComment;
	}

	public void setCreatorComment(String creatorComment) {
		this.creatorComment = creatorComment;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public User getAssigneeUser() {
		return assigneeUser;
	}

	public void setAssigneeUser(User assigneeUser) {
		this.assigneeUser = assigneeUser;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public User getAssignedByUser() {
		return assignedByUser;
	}

	public void setAssignedByUser(User assignedByUser) {
		this.assignedByUser = assignedByUser;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = TASK_OBSERVER_TABLE,
		joinColumns = @JoinColumn(name = TASK_OBSERVER_JOIN_COLUMN),
		inverseJoinColumns = @JoinColumn(name = TASK_OBSERVER_INVERSE_JOIN_COLUMN))
	public List<User> getObserverUsers() {
		return observerUsers;
	}

	public void setObserverUsers(List<User> observerUsers) {
		this.observerUsers = observerUsers;
	}

	@Column(length = CHARACTER_LIMIT_BIG)
	public String getAssigneeReply() {
		return assigneeReply;
	}

	public void setAssigneeReply(String assigneeReply) {
		this.assigneeReply = assigneeReply;
	}

	@Enumerated(EnumType.STRING)
	public TaskPriority getPriority() {
		return priority;
	}

	public void setPriority(TaskPriority priority) {
		this.priority = priority;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getSuggestedStart() {
		return suggestedStart;
	}

	public void setSuggestedStart(Date suggestedStart) {
		this.suggestedStart = suggestedStart;
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

	public Float getClosedLatLonAccuracy() {
		return closedLatLonAccuracy;
	}

	public void setClosedLatLonAccuracy(Float closedLatLonAccuracy) {
		this.closedLatLonAccuracy = closedLatLonAccuracy;
	}

	@Column
	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public TravelEntry getTravelEntry() {
		return travelEntry;
	}

	public void setTravelEntry(TravelEntry travelEntry) {
		this.travelEntry = travelEntry;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}
