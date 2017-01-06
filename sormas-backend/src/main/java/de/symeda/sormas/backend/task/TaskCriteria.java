package de.symeda.sormas.backend.task;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.user.User;

public class TaskCriteria {

	private TaskStatus[] taskStatuses;
	private TaskType taskType;
	private User assigneeUser;
	private Case caze;
	private Contact contact;
	private Event event;
	
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
	public User getAssigneeUser() {
		return assigneeUser;
	}
	public TaskCriteria assigneeUserEquals(User assigneeUser) {
		this.assigneeUser = assigneeUser;
		return this;
	}
	public Case getCaze() {
		return caze;
	}
	public TaskCriteria cazeEquals(Case caze) {
		this.caze = caze;
		return this;
	}
	public Contact getContact() {
		return contact;
	}
	public TaskCriteria contactEquals(Contact contact) {
		this.contact = contact;
		return this;
	}
	public Event getEvent() {
		return event;
	}
	public TaskCriteria eventEquals(Event event) {
		this.event = event;
		return this;
	}
	
}
