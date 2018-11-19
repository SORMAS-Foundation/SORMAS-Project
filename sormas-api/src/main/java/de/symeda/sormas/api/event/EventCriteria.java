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
