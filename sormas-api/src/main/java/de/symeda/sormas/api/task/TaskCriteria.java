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

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public class TaskCriteria implements Serializable {

	private static final long serialVersionUID = -9174165215694877625L;

	private TaskStatus[] taskStatuses;
	private TaskType taskType;
	private UserReferenceDto assigneeUser;
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
	private Boolean archived;
	
	public TaskStatus[] getTaskStatuses() {
		return taskStatuses;
	}
	public TaskCriteria taskStatusEquals(TaskStatus ...taskStatuses) {
		this.taskStatuses = taskStatuses;
		return this;
	}
	public TaskType getTaskType() {
		return taskType;
	}
	
	public TaskCriteria taskTypeEquals(TaskType taskType) {
		this.taskType = taskType;
		return this;
	}
	public UserReferenceDto getAssigneeUser() {
		return assigneeUser;
	}
	public TaskCriteria assigneeUserEquals(UserReferenceDto assigneeUser) {
		this.assigneeUser = assigneeUser;
		return this;
	}
	public CaseReferenceDto getCaze() {
		return caze;
	}
	public TaskCriteria cazeEquals(CaseReferenceDto caze) {
		this.caze = caze;
		return this;
	}
	public ContactReferenceDto getContact() {
		return contact;
	}
	public TaskCriteria contactEquals(ContactReferenceDto contact) {
		this.contact = contact;
		return this;
	}
	public PersonReferenceDto getContactPerson() {
		return contactPerson;
	}
	public TaskCriteria contactPersonEquals(PersonReferenceDto contactPerson) {
		this.contactPerson = contactPerson;
		return this;
	}
	public EventReferenceDto getEvent() {
		return event;
	}
	public TaskCriteria eventEquals(EventReferenceDto event) {
		this.event = event;
		return this;
	}
	public Date getDueDateFrom() {
		return dueDateFrom;
	}
	public Date getDueDateTo() {
		return dueDateTo;
	}
	public TaskCriteria dueDateBetween(Date dueDateFrom, Date dueDateTo) {
		this.dueDateFrom = dueDateFrom;
		this.dueDateTo = dueDateTo;
		return this;
	}
	public Date getStartDateFrom() {
		return startDateFrom;
	}
	public Date getStartDateTo() {
		return startDateTo;
	}
	public TaskCriteria startDateBetween(Date startDateFrom, Date startDateTo) {
		this.startDateFrom = startDateFrom;
		this.startDateTo = startDateTo;
		return this;
	}
	public Date getStatusChangeDateFrom() {
		return statusChangeDateFrom;
	}
	public Date getStatusChangeDateTo() {
		return statusChangeDateTo;
	}
	public TaskCriteria statusChangeDateBetween(Date statusChangeDateFrom, Date statusChangeDateTo) {
		this.statusChangeDateFrom = statusChangeDateFrom;
		this.statusChangeDateTo = statusChangeDateTo;
		return this;
	}
	public Boolean getArchived() {
		return archived;
	}
	public TaskCriteria archived(Boolean archived) {
		this.archived = archived;
		return this;
	}
	
	public boolean hasContextCriteria() {
		return getCaze() != null || getEvent() != null || getContact() != null;
	}
}
