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

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.IgnoreForUrl;

public class TaskCriteria extends BaseCriteria implements Serializable {

	private static final long serialVersionUID = -9174165215694877625L;

	private TaskStatus taskStatus;
	private TaskType taskType;
	private UserReferenceDto assigneeUser;
	private UserReferenceDto excludeAssigneeUser;
	private CaseReferenceDto caze;
	private ContactReferenceDto contact;
	private PersonReferenceDto contactPerson;
	private EventReferenceDto event;
	private Date dueDateFrom;
	private Date dueDateTo;
	private Date startDateFrom;
	private Date startDateTo;
	private Date statusChangeDateFrom;
	private Date statusChangeDateTo;
	private EntityRelevanceStatus relevanceStatus;

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	public TaskCriteria taskStatus(TaskStatus taskStatus) {
		setTaskStatus(taskStatus);
		return this;
	}

	public TaskCriteria taskType(TaskType taskType) {
		this.taskType = taskType;
		return this;
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public TaskCriteria assigneeUser(UserReferenceDto assigneeUser) {
		this.assigneeUser = assigneeUser;
		return this;
	}

	public UserReferenceDto getAssigneeUser() {
		return assigneeUser;
	}

	public TaskCriteria excludeAssigneeUser(UserReferenceDto excludeAssigneeUser) {
		this.excludeAssigneeUser = excludeAssigneeUser;
		return this;
	}

	public UserReferenceDto getExcludeAssigneeUser() {
		return excludeAssigneeUser;
	}

	public TaskCriteria caze(CaseReferenceDto caze) {
		this.caze = caze;
		return this;
	}

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public TaskCriteria contact(ContactReferenceDto contact) {
		this.contact = contact;
		return this;
	}

	public ContactReferenceDto getContact() {
		return contact;
	}

	public TaskCriteria contactPerson(PersonReferenceDto contactPerson) {
		this.contactPerson = contactPerson;
		return this;
	}

	public TaskCriteria event(EventReferenceDto event) {
		this.event = event;
		return this;
	}

	public PersonReferenceDto getContactPerson() {
		return contactPerson;
	}

	public EventReferenceDto getEvent() {
		return event;
	}

	public boolean hasContextCriteria() {
		return getCaze() != null || getEvent() != null || getContact() != null;
	}

	public TaskCriteria dueDateBetween(Date dueDateFrom, Date dueDateTo) {
		this.dueDateFrom = dueDateFrom;
		this.dueDateTo = dueDateTo;
		return this;
	}

	public TaskCriteria dueDateFrom(Date dueDateFrom) {
		this.dueDateFrom = dueDateFrom;
		return this;
	}

	public Date getDueDateFrom() {
		return dueDateFrom;
	}

	public TaskCriteria dueDateTo(Date dueDateTo) {
		this.dueDateTo = dueDateTo;
		return this;
	}

	public Date getDueDateTo() {
		return dueDateTo;
	}

	public TaskCriteria startDateBetween(Date startDateFrom, Date startDateTo) {
		this.startDateFrom = startDateFrom;
		this.startDateTo = startDateTo;
		return this;
	}

	public TaskCriteria startDateFrom(Date startDateFrom) {
		this.startDateFrom = startDateFrom;
		return this;
	}

	public Date getStartDateFrom() {
		return startDateFrom;
	}

	public TaskCriteria startDateTo(Date startDateTo) {
		this.startDateTo = startDateTo;
		return this;
	}

	public Date getStartDateTo() {
		return startDateTo;
	}

	public TaskCriteria statusChangeDateBetween(Date statusChangeDateFrom, Date statusChangeDateTo) {
		this.statusChangeDateFrom = statusChangeDateFrom;
		this.statusChangeDateTo = statusChangeDateTo;
		return this;
	}

	public TaskCriteria statusChangeDateFrom(Date statusChangeDateFrom) {
		this.statusChangeDateFrom = statusChangeDateFrom;
		return this;
	}

	public Date getStatusChangeDateFrom() {
		return statusChangeDateFrom;
	}

	public TaskCriteria statusChangeDateTo(Date statusChangeDateTo) {
		this.statusChangeDateTo = statusChangeDateTo;
		return this;
	}

	public Date getStatusChangeDateTo() {
		return statusChangeDateTo;
	}

	public TaskCriteria relevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
		return this;
	}

	@IgnoreForUrl
	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}
}
