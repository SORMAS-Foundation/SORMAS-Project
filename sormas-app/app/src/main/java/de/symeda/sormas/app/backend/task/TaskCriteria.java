/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.task;

import java.io.Serializable;

import de.symeda.sormas.api.task.TaskAssignee;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.Event;

public class TaskCriteria implements Serializable {

	private TaskStatus taskStatus;
	private TaskAssignee taskAssignee;
	private Case associatedCase;
	private Contact associatedContact;
	private Event associatedEvent;

	public TaskCriteria taskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
		return this;
	}

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public TaskCriteria taskAssignee(TaskAssignee taskAssignee) {
		this.taskAssignee = taskAssignee;
		return this;
	}

	public TaskAssignee getTaskAssignee() {
		return taskAssignee;
	}

	public TaskCriteria associatedCase(Case associatedCase) {
		this.associatedCase = associatedCase;
		return this;
	}

	public Case getAssociatedCase() {
		return associatedCase;
	}

	public TaskCriteria associatedContact(Contact associatedContact) {
		this.associatedContact = associatedContact;
		return this;
	}

	public Contact getAssociatedContact() {
		return associatedContact;
	}

	public TaskCriteria associatedEvent(Event associatedEvent) {
		this.associatedEvent = associatedEvent;
		return this;
	}

	public Event getAssociatedEvent() {
		return associatedEvent;
	}
}
