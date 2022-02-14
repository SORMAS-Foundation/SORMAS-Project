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
import de.symeda.sormas.api.action.ActionPriority;
import de.symeda.sormas.api.action.ActionStatus;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.user.UserReferenceDto;

public class EventActionIndexDto implements Serializable {

	private static final long serialVersionUID = 8231951545991794808L;

	public static final String I18N_PREFIX = "EventAction";

	public static final String EVENT_UUID = "eventUuid";
	public static final String EVENT_TITLE = "eventTitle";
	public static final String EVENT_DISEASE = "eventDisease";
	public static final String EVENT_DISEASE_VARIANT = "eventDiseaseVariant";
	public static final String EVENT_DISEASE_DETAILS = "eventDiseaseDetails";
	public static final String EVENT_IDENTIFICATION_SOURCE = "eventIdentificationSource";
	public static final String EVENT_START_DATE = "eventStartDate";
	public static final String EVENT_END_DATE = "eventEndDate";
	public static final String EVENT_STATUS = "eventStatus";
	public static final String EVENT_RISK_LEVEL = "eventRiskLevel";
	public static final String EVENT_INVESTIGATION_STATUS = "eventInvestigationStatus";
	public static final String EVENT_MANAGEMENT_STATUS = "eventManagementStatus";
	public static final String EVENT_REPORTING_USER = "eventReportingUser";
	public static final String EVENT_RESPONSIBLE_USER = "eventResponsibleUser";
	public static final String EVENT_EVOLUTION_DATE = "eventEvolutionDate";
	public static final String ACTION_TITLE = "actionTitle";
	public static final String ACTION_CREATION_DATE = "actionCreationDate";
	public static final String ACTION_CHANGE_DATE = "actionChangeDate";
	public static final String ACTION_DATE = "actionDate";
	public static final String ACTION_STATUS = "actionStatus";
	public static final String ACTION_PRIORITY = "actionPriority";
	public static final String ACTION_LAST_MODIFIED_BY = "actionLastModifiedBy";
	public static final String ACTION_CREATOR_USER = "actionCreatorUser";

	private String eventUuid;
	private String eventTitle;
	private Disease eventDisease;
	private DiseaseVariant eventDiseaseVariant;
	private String eventDiseaseDetails;
	private EventIdentificationSource eventIdentificationSource;
	private Date eventStartDate;
	private Date eventEndDate;
	private EventStatus eventStatus;
	private RiskLevel eventRiskLevel;
	private EventInvestigationStatus eventInvestigationStatus;
	private EventManagementStatus eventManagementStatus;
	private UserReferenceDto eventReportingUser;
	private UserReferenceDto eventResponsibleUser;
	private Date eventEvolutionDate;
	private String actionTitle;
	private Date actionCreationDate;
	private Date actionChangeDate;
	private Date actionDate;
	private ActionStatus actionStatus;
	private ActionPriority actionPriority;
	private UserReferenceDto actionLastModifiedBy;
	private UserReferenceDto actionCreatorUser;

	public EventActionIndexDto(
		String eventUuid,
		String eventTitle,
		Disease eventDisease,
		DiseaseVariant eventDiseaseVariant,
		String eventDiseaseDetails,
		EventIdentificationSource eventIdentificationSource,
		Date eventStartDate,
		Date eventEndDate,
		EventStatus eventStatus,
		RiskLevel eventRiskLevel,
		EventInvestigationStatus eventInvestigationStatus,
		EventManagementStatus eventManagementStatus,
		UserReferenceDto eventReportingUser,
		UserReferenceDto eventResponsibleUser,
		String actionTitle,
		Date eventEvolutionDate,
		Date actionCreationDate,
		Date actionChangeDate,
		Date actionDate,
		ActionStatus actionStatus,
		ActionPriority actionPriority,
		UserReferenceDto actionLastModifiedBy,
		UserReferenceDto actionCreatorUser) {

		this.eventUuid = eventUuid;
		this.eventTitle = eventTitle;
		this.eventDisease = eventDisease;
		this.eventDiseaseVariant = eventDiseaseVariant;
		this.eventDiseaseDetails = eventDiseaseDetails;
		this.eventIdentificationSource = eventIdentificationSource;
		this.eventStartDate = eventStartDate;
		this.eventEndDate = eventEndDate;
		this.eventStatus = eventStatus;
		this.eventRiskLevel = eventRiskLevel;
		this.eventInvestigationStatus = eventInvestigationStatus;
		this.eventManagementStatus = eventManagementStatus;
		this.eventReportingUser = eventReportingUser;
		this.eventResponsibleUser = eventResponsibleUser;
		this.actionTitle = actionTitle;
		this.eventEvolutionDate = eventEvolutionDate;
		this.actionCreationDate = actionCreationDate;
		this.actionChangeDate = actionChangeDate;
		this.actionDate = actionDate;
		this.actionStatus = actionStatus;
		this.actionPriority = actionPriority;
		this.actionLastModifiedBy = actionLastModifiedBy;
		this.actionCreatorUser = actionCreatorUser;
	}

	public String getEventUuid() {
		return eventUuid;
	}

	public void setEventUuid(String eventUuid) {
		this.eventUuid = eventUuid;
	}

	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public Disease getEventDisease() {
		return eventDisease;
	}

	public void setEventDisease(Disease eventDisease) {
		this.eventDisease = eventDisease;
	}

	public DiseaseVariant getEventDiseaseVariant() {
		return eventDiseaseVariant;
	}

	public void setEventDiseaseVariant(DiseaseVariant eventDiseaseVariant) {
		this.eventDiseaseVariant = eventDiseaseVariant;
	}

	public String getEventDiseaseDetails() {
		return eventDiseaseDetails;
	}

	public void setEventDiseaseDetails(String eventDiseaseDetails) {
		this.eventDiseaseDetails = eventDiseaseDetails;
	}

	public EventIdentificationSource getEventIdentificationSource() {
		return eventIdentificationSource;
	}

	public void setEventIdentificationSource(EventIdentificationSource eventIdentificationSource) {
		this.eventIdentificationSource = eventIdentificationSource;
	}

	public Date getEventStartDate() {
		return eventStartDate;
	}

	public void setEventStartDate(Date eventStartDate) {
		this.eventStartDate = eventStartDate;
	}

	public Date getEventEndDate() {
		return eventEndDate;
	}

	public void setEventEndDate(Date eventEndDate) {
		this.eventEndDate = eventEndDate;
	}

	public EventStatus getEventStatus() {
		return eventStatus;
	}

	public void setEventStatus(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
	}

	public RiskLevel getEventRiskLevel() {
		return eventRiskLevel;
	}

	public void setEventRiskLevel(RiskLevel eventRiskLevel) {
		this.eventRiskLevel = eventRiskLevel;
	}

	public EventInvestigationStatus getEventInvestigationStatus() {
		return eventInvestigationStatus;
	}

	public void setEventInvestigationStatus(EventInvestigationStatus eventInvestigationStatus) {
		this.eventInvestigationStatus = eventInvestigationStatus;
	}

	public UserReferenceDto getEventReportingUser() {
		return eventReportingUser;
	}

	public void setEventReportingUser(UserReferenceDto eventReportingUser) {
		this.eventReportingUser = eventReportingUser;
	}

	public UserReferenceDto getEventResponsibleUser() {
		return eventResponsibleUser;
	}

	public void setEventResponsibleUser(UserReferenceDto eventResponsibleUser) {
		this.eventResponsibleUser = eventResponsibleUser;
	}

	public Date getEventEvolutionDate() {
		return eventEvolutionDate;
	}

	public void setEventEvolutionDate(Date eventEvolutionDate) {
		this.eventEvolutionDate = eventEvolutionDate;
	}

	public String getActionTitle() {
		return actionTitle;
	}

	public void setActionTitle(String actionTitle) {
		this.actionTitle = actionTitle;
	}

	public Date getActionCreationDate() {
		return actionCreationDate;
	}

	public void setActionCreationDate(Date actionCreationDate) {
		this.actionCreationDate = actionCreationDate;
	}

	public Date getActionChangeDate() {
		return actionChangeDate;
	}

	public void setActionChangeDate(Date actionChangeDate) {
		this.actionChangeDate = actionChangeDate;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	public ActionStatus getActionStatus() {
		return actionStatus;
	}

	public void setActionStatus(ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
	}

	public ActionPriority getActionPriority() {
		return actionPriority;
	}

	public void setActionPriority(ActionPriority actionPriority) {
		this.actionPriority = actionPriority;
	}

	public UserReferenceDto getActionLastModifiedBy() {
		return actionLastModifiedBy;
	}

	public void setActionLastModifiedBy(UserReferenceDto actionLastModifiedBy) {
		this.actionLastModifiedBy = actionLastModifiedBy;
	}

	public UserReferenceDto getActionCreatorUser() {
		return actionCreatorUser;
	}

	public void setActionCreatorUser(UserReferenceDto actionCreatorUser) {
		this.actionCreatorUser = actionCreatorUser;
	}

	public EventManagementStatus getEventManagementStatus() {
		return eventManagementStatus;
	}

	public void setEventManagementStatus(EventManagementStatus eventManagementStatus) {
		this.eventManagementStatus = eventManagementStatus;
	}
}
