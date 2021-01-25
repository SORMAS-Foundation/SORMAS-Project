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
package de.symeda.sormas.api.event;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.action.ActionMeasure;
import de.symeda.sormas.api.action.ActionPriority;
import de.symeda.sormas.api.action.ActionStatus;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.Order;

public class EventActionExportDto implements Serializable {

	private static final long serialVersionUID = 8231951545991794808L;

	public static final String I18N_PREFIX = "EventActionExport";

	private String eventUuid;
	private String eventTitle;
	private String eventDesc;
	private String eventDate;
	private Date eventEvolutionDate;
	private String eventEvolutionComment;
	private EventStatus eventStatus;
	private EventInvestigationStatus eventInvestigationStatus;
	private UserReferenceDto eventReportingUser;
	private UserReferenceDto eventResponsibleUser;
	private ActionMeasure actionMeasure;
	private String actionTitle;
	private Date actionCreationDate;
	private Date actionChangeDate;
	private ActionStatus actionStatus;
	private ActionPriority actionPriority;
	private UserReferenceDto actionLastModifiedBy;

	public EventActionExportDto(
		String eventUuid,
		String eventTitle,
		String eventDesc,
		Date eventStartDate,
		Date eventEndDate,
		Date eventEvolutionDate,
		String eventEvolutionComment,
		EventStatus eventStatus,
		EventInvestigationStatus eventInvestigationStatus,
		String eventReportingUserUuid,
		String eventReportingUserFirstName,
		String eventReportingUserLastName,
		String eventResponsibleUserUuid,
		String eventResponsibleUserFirstName,
		String eventResponsibleUserLastName,
		ActionMeasure actionMeasure,
		String actionTitle,
		Date actionCreationDate,
		Date actionChangeDate,
		ActionStatus actionStatus,
		ActionPriority actionPriority,
		String actionLastModifiedByUuid,
		String actionLastModifiedByFirstName,
		String actionLastModifiedByLastName,
		String actionCreatorUserUuid,
		String actionCreatorUserFirstName,
		String actionCreatorUserLastName) {

		this.eventUuid = eventUuid;
		this.eventTitle = eventTitle;
		this.eventDesc = eventDesc;
		this.eventDate = EventHelper.buildEventDateString(eventStartDate, eventEndDate);
		this.eventEvolutionDate = eventEvolutionDate;
		this.eventEvolutionComment = eventEvolutionComment;
		this.eventStatus = eventStatus;
		this.eventInvestigationStatus = eventInvestigationStatus;
		this.eventReportingUser = new UserReferenceDto(eventReportingUserUuid, eventReportingUserFirstName, eventReportingUserLastName, null);
		this.eventResponsibleUser = new UserReferenceDto(eventResponsibleUserUuid, eventResponsibleUserFirstName, eventResponsibleUserLastName, null);
		this.actionMeasure = actionMeasure;
		this.actionTitle = actionTitle;
		this.actionCreationDate = actionCreationDate;
		this.actionChangeDate = actionChangeDate;
		this.actionStatus = actionStatus;
		this.actionPriority = actionPriority;
		this.actionLastModifiedBy = actionLastModifiedByUuid != null
			? new UserReferenceDto(actionLastModifiedByUuid, actionLastModifiedByFirstName, actionLastModifiedByLastName, null)
			: new UserReferenceDto(actionCreatorUserUuid, actionCreatorUserFirstName, actionCreatorUserLastName, null);
	}

	@Order(0)
	public String getEventUuid() {
		return eventUuid;
	}

	@Order(1)
	public String getEventTitle() {
		return eventTitle;
	}

	@Order(2)
	public String getEventDesc() {
		return eventDesc;
	}

	@Order(3)
	public String getEventDate() {
		return eventDate;
	}

	@Order(4)
	public Date getEventEvolutionDate() {
		return eventEvolutionDate;
	}

	@Order(5)
	public String getEventEvolutionComment() {
		return eventEvolutionComment;
	}

	@Order(6)
	public EventStatus getEventStatus() {
		return eventStatus;
	}

	@Order(7)
	public EventInvestigationStatus getEventInvestigationStatus() {
		return eventInvestigationStatus;
	}

	@Order(8)
	public UserReferenceDto getEventReportingUser() {
		return eventReportingUser;
	}

	@Order(9)
	public UserReferenceDto getEventResponsibleUser() {
		return eventResponsibleUser;
	}

	@Order(10)
	public ActionMeasure getActionMeasure() {
		return actionMeasure;
	}

	@Order(11)
	public String getActionTitle() {
		return actionTitle;
	}

	@Order(12)
	public Date getActionCreationDate() {
		return actionCreationDate;
	}

	@Order(13)
	public Date getActionChangeDate() {
		return actionChangeDate;
	}

	@Order(14)
	public ActionStatus getActionStatus() {
		return actionStatus;
	}

	@Order(15)
	public ActionPriority getActionPriority() {
		return actionPriority;
	}

	@Order(16)
	public UserReferenceDto getActionLastModifiedBy() {
		return actionLastModifiedBy;
	}
}
