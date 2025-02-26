/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.action.ActionMeasure;
import de.symeda.sormas.api.action.ActionPriority;
import de.symeda.sormas.api.action.ActionStatus;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.disease.DiseaseVariantConverter;
import de.symeda.sormas.api.importexport.format.ExportFormat;
import de.symeda.sormas.api.importexport.format.ImportExportFormat;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.Order;

public class EventActionExportDto implements Serializable {

	private static final long serialVersionUID = 8231951545991794808L;

	public static final String I18N_PREFIX = "EventActionExport";

	private String eventUuid;
	private String eventTitle;
	private Disease eventDisease;
	private DiseaseVariant eventDiseaseVariant;
	private String eventDiseaseDetails;
	private String eventDesc;
	private EventIdentificationSource eventIdentificationSource;
	private String eventDate;
	private Date eventEvolutionDate;
	private String eventEvolutionComment;
	private EventStatus eventStatus;
	private RiskLevel eventRiskLevel;
	private EventInvestigationStatus eventInvestigationStatus;
	private UserReferenceDto eventReportingUser;
	private UserReferenceDto eventResponsibleUser;
	private ActionMeasure actionMeasure;
	private String actionTitle;
	private Date actionCreationDate;
	private Date actionChangeDate;
	private Date actionDate;
	private ActionStatus actionStatus;
	private ActionPriority actionPriority;
	private UserReferenceDto actionLastModifiedBy;

	public EventActionExportDto(
		String eventUuid,
		String eventTitle,
		Disease eventDisease,
		String eventDiseaseVariant,
		String eventDiseaseDetails,
		String eventDesc,
		EventIdentificationSource eventIdentificationSource,
		Date eventStartDate,
		Date eventEndDate,
		Date eventEvolutionDate,
		String eventEvolutionComment,
		EventStatus eventStatus,
		RiskLevel eventRiskLevel,
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
		Date actionDate,
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
		this.eventDisease = eventDisease;
		this.eventDiseaseVariant = new DiseaseVariantConverter().convertToEntityAttribute(eventDisease, eventDiseaseVariant);
		this.eventDiseaseDetails = eventDiseaseDetails;
		this.eventDesc = eventDesc;
		this.eventIdentificationSource = eventIdentificationSource;
		this.eventDate = EventHelper.buildEventDateString(eventStartDate, eventEndDate);
		this.eventEvolutionDate = eventEvolutionDate;
		this.eventEvolutionComment = eventEvolutionComment;
		this.eventStatus = eventStatus;
		this.eventRiskLevel = eventRiskLevel;
		this.eventInvestigationStatus = eventInvestigationStatus;
		this.eventReportingUser = new UserReferenceDto(eventReportingUserUuid, eventReportingUserFirstName, eventReportingUserLastName);
		this.eventResponsibleUser = new UserReferenceDto(eventResponsibleUserUuid, eventResponsibleUserFirstName, eventResponsibleUserLastName);
		this.actionMeasure = actionMeasure;
		this.actionTitle = actionTitle;
		this.actionCreationDate = actionCreationDate;
		this.actionChangeDate = actionChangeDate;
		this.actionDate = actionDate;
		this.actionStatus = actionStatus;
		this.actionPriority = actionPriority;
		this.actionLastModifiedBy = actionLastModifiedByUuid != null
			? new UserReferenceDto(actionLastModifiedByUuid, actionLastModifiedByFirstName, actionLastModifiedByLastName)
			: new UserReferenceDto(actionCreatorUserUuid, actionCreatorUserFirstName, actionCreatorUserLastName);
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
	public Disease getEventDisease() {
		return eventDisease;
	}

	@Order(3)
	public DiseaseVariant getEventDiseaseVariant() {
		return eventDiseaseVariant;
	}

	@Order(4)
	public String getEventDiseaseDetails() {
		return eventDiseaseDetails;
	}

	@Order(5)
	public String getEventDesc() {
		return eventDesc;
	}

	@Order(6)
	public EventIdentificationSource getEventIdentificationSource() {
		return eventIdentificationSource;
	}

	@Order(7)
	public String getEventDate() {
		return eventDate;
	}

	@Order(8)
	public Date getEventEvolutionDate() {
		return eventEvolutionDate;
	}

	@Order(9)
	public String getEventEvolutionComment() {
		return eventEvolutionComment;
	}

	@Order(10)
	public EventStatus getEventStatus() {
		return eventStatus;
	}

	@Order(11)
	public RiskLevel getEventRiskLevel() {
		return eventRiskLevel;
	}

	@Order(12)
	public EventInvestigationStatus getEventInvestigationStatus() {
		return eventInvestigationStatus;
	}

	@Order(13)
	public UserReferenceDto getEventReportingUser() {
		return eventReportingUser;
	}

	@Order(14)
	public UserReferenceDto getEventResponsibleUser() {
		return eventResponsibleUser;
	}

	@Order(15)
	public ActionMeasure getActionMeasure() {
		return actionMeasure;
	}

	@Order(16)
	public String getActionTitle() {
		return actionTitle;
	}

	@Order(17)
	@ExportFormat(ImportExportFormat.DATE_TIME)
	public Date getActionCreationDate() {
		return actionCreationDate;
	}

	@Order(18)
	@ExportFormat(ImportExportFormat.DATE_TIME)
	public Date getActionChangeDate() {
		return actionChangeDate;
	}

	@Order(19)
	@ExportFormat(ImportExportFormat.DATE_TIME)
	public Date getActionDate() {
		return actionDate;
	}

	@Order(20)
	public ActionStatus getActionStatus() {
		return actionStatus;
	}

	@Order(21)
	public ActionPriority getActionPriority() {
		return actionPriority;
	}

	@Order(22)
	public UserReferenceDto getActionLastModifiedBy() {
		return actionLastModifiedBy;
	}
}
