package de.symeda.sormas.api.event;

import java.io.Serializable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.user.UserRole;

public class EventCriteria implements Serializable {

	private static final long serialVersionUID = 2194071020732246594L;
	
	private EventStatus eventStatus;
	private EventType eventType;
	private Disease disease;
	private UserRole reportingUserRole;
	private Boolean archived;
	
	public EventStatus getEventStatus() {
		return eventStatus;
	}
	public EventCriteria eventStatusEquals(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
		return this;
	}
	public EventType getEventType() {
		return eventType;
	}
	public EventCriteria eventTypeEquals(EventType eventType) {
		this.eventType = eventType;
		return this;
	}
	public Disease getDisease() {
		return disease;
	}
	public EventCriteria diseaseEquals(Disease disease) {
		this.disease = disease;
		return this;
	}
	public UserRole getReportingUserRole() {
		return reportingUserRole;
	}
	public EventCriteria reportingUserHasRole(UserRole reportingUserRole) {
		this.reportingUserRole = reportingUserRole;
		return this;
	}
	public Boolean getArchived() {
		return archived;
	}
	public EventCriteria archived(Boolean archived) {
		this.archived = archived;
		return this;
	}
	
}
