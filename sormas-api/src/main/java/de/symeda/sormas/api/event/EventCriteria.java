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
import java.util.Set;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.action.ActionStatus;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.share.ExternalShareCriteria;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.CriteriaDateType;
import de.symeda.sormas.api.utils.criteria.CriteriaWithDateType;

public class EventCriteria extends CriteriaWithDateType implements ExternalShareCriteria, Serializable {

	private static final long serialVersionUID = 2194071020732246594L;

	public static final String REPORTING_USER_ROLE = "reportingUserRole";
	public static final String RESPONSIBLE_USER = "responsibleUser";
	public static final String FREE_TEXT = "freeText";
	public static final String FREE_TEXT_EVENT_PARTICIPANTS = "freeTextEventParticipants";
	public static final String FREE_TEXT_EVENT_GROUPS = "freeTextEventGroups";
	public static final String EVENT_STATUS = "eventStatus";
	public static final String RISK_LEVEL = "riskLevel";
	public static final String SPECIFIC_RISK = "specificRisk";
	public static final String EVENT_INVESTIGATION_STATUS = "eventInvestigationStatus";
	public static final String DISTRICT = "district";
	public static final String REGION = "region";
	public static final String EVENT_MANAGEMENT_STATUS = "eventManagementStatus";
	public static final String EVENT_IDENTIFICATION_SOURCE = "eventIdentificationSource";
	public static final String ONLY_ENTITIES_NOT_SHARED_WITH_EXTERNAL_SURV_TOOL = "onlyEntitiesNotSharedWithExternalSurvTool";
	public static final String ONLY_ENTITIES_SHARED_WITH_EXTERNAL_SURV_TOOL = "onlyEntitiesSharedWithExternalSurvTool";
	public static final String ONLY_ENTITIES_CHANGED_SINCE_LAST_SHARED_WITH_EXTERNAL_SURV_TOOL =
		"onlyEntitiesChangedSinceLastSharedWithExternalSurvTool";

	private EventStatus eventStatus;
	private RiskLevel riskLevel;
	private SpecificRisk specificRisk;
	private EventInvestigationStatus eventInvestigationStatus;
	private Disease disease;
	private DiseaseVariant diseaseVariant;
	private UserRoleReferenceDto reportingUserRole;
	private Boolean deleted = Boolean.FALSE;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community;
	private EntityRelevanceStatus relevanceStatus;
	private Date eventDateFrom;
	private Date eventDateTo;
	private CriteriaDateType eventDateType = EventCriteriaDateType.EVENT_DATE;
	private DateFilterOption dateFilterOption = DateFilterOption.DATE;
	private Date eventEvolutionDateFrom;
	private Date eventEvolutionDateTo;
	private DateFilterOption evolutionDateFilterOption = DateFilterOption.DATE;
	private UserReferenceDto responsibleUser;
	private String freeText;
	private String freeTextEventParticipants;
	private String freeTextEventGroups;
	private EventSourceType srcType;
	private CaseReferenceDto caze;
	private Boolean userFilterIncluded = true;
	private TypeOfPlace typeOfPlace;
	private PersonReferenceDto person;
	private FacilityType facilityType;
	private FacilityReferenceDto facility;
	private EventReferenceDto superordinateEvent;
	private EventGroupReferenceDto eventGroup;
	private Set<String> excludedUuids;
	private Boolean hasNoSuperordinateEvent;
	private EventManagementStatus eventManagementStatus;
	private EventIdentificationSource eventIdentificationSource;

	// Actions criterias
	private ActionStatus actionStatus;
	private Date actionChangeDateFrom;
	private Date actionChangeDateTo;
	private DateFilterOption actionChangeDateFilterOption = DateFilterOption.DATE;
	private Date actionDateFrom;
	private Date actionDateTo;
	private DateFilterOption actionDateFilterOption = DateFilterOption.DATE;
	private Boolean onlyEntitiesNotSharedWithExternalSurvTool;
	private Boolean onlyEntitiesSharedWithExternalSurvTool;
	private Boolean onlyEntitiesChangedSinceLastSharedWithExternalSurvTool;
	private Date reportedDateFrom;
	private Date reportedDateTo;
	public EventCriteria() {
		super(EventCriteriaDateType.class);
	}

	public EventStatus getEventStatus() {
		return eventStatus;
	}

	public EventCriteria eventStatus(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
		return this;
	}

	public void setEventStatus(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
	}

	public RiskLevel getRiskLevel() {
		return riskLevel;
	}

	public EventCriteria riskLevel(RiskLevel riskLevel) {
		this.riskLevel = riskLevel;
		return this;
	}

	public void setRiskLevel(RiskLevel riskLevel) {
		this.riskLevel = riskLevel;
	}

	public SpecificRisk getSpecificRisk() {
		return specificRisk;
	}

	public EventCriteria specificRisk(SpecificRisk specificRisk) {
		this.specificRisk = specificRisk;
		return this;
	}

	public void setSpecificRisk(SpecificRisk specificRisk) {
		this.specificRisk = specificRisk;
	}

	public EventInvestigationStatus getEventInvestigationStatus() {
		return eventInvestigationStatus;
	}

	public void setEventInvestigationStatus(EventInvestigationStatus eventInvestigationStatus) {
		this.eventInvestigationStatus = eventInvestigationStatus;
	}

	public EventCriteria eventInvestigationStatus(EventInvestigationStatus eventInvestigationStatus) {
		this.eventInvestigationStatus = eventInvestigationStatus;
		return this;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public EventCriteria disease(Disease disease) {
		setDisease(disease);
		return this;
	}

	public DiseaseVariant getDiseaseVariant() {
		return diseaseVariant;
	}

	public void setDiseaseVariant(DiseaseVariant diseaseVariant) {
		this.diseaseVariant = diseaseVariant;
	}

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public EventCriteria caze(CaseReferenceDto caze) {
		this.caze = caze;
		return this;
	}

	public Boolean getUserFilterIncluded() {
		return userFilterIncluded;
	}

	public void setUserFilterIncluded(Boolean userFilterIncluded) {
		this.userFilterIncluded = userFilterIncluded;
	}

	public UserRoleReferenceDto getReportingUserRole() {
		return reportingUserRole;
	}

	public void setReportingUserRole(UserRoleReferenceDto reportingUserRole) {
		this.reportingUserRole = reportingUserRole;
	}

	public EventCriteria relevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
		return this;
	}

	@IgnoreForUrl
	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}

	public EventCriteria deleted(Boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	@IgnoreForUrl
	public Boolean getDeleted() {
		return deleted;
	}

	public EventCriteria region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public RegionReferenceDto getRegion() {
		return this.region;
	}

	public EventCriteria district(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public DistrictReferenceDto getDistrict() {
		return this.district;
	}

	public CommunityReferenceDto getCommunity() {
		return community;
	}

	public void setCommunity(CommunityReferenceDto community) {
		this.community = community;
	}

	public EventCriteria eventCommunity(CommunityReferenceDto eventCommunity) {
		this.community = eventCommunity;
		return this;
	}

	public EventCriteria eventDateBetween(Date eventDateFrom, Date eventDateTo) {
		this.eventDateFrom = eventDateFrom;
		this.eventDateTo = eventDateTo;
		return this;
	}

	public EventCriteria eventDateBetween(Date eventDateFrom, Date eventDateTo, CriteriaDateType eventDateType, DateFilterOption dateFilterOption) {
		this.eventDateFrom = eventDateFrom;
		this.eventDateTo = eventDateTo;
		this.eventDateType = eventDateType;
		this.dateFilterOption = dateFilterOption;
		return this;
	}

	public EventCriteria eventDateFrom(Date eventDateFrom) {
		this.eventDateFrom = eventDateFrom;
		return this;
	}

	public Date getEventDateFrom() {
		return eventDateFrom;
	}

	public EventCriteria eventDateTo(Date eventDateTo) {
		this.eventDateTo = eventDateTo;
		return this;
	}

	public Date getEventDateTo() {
		return eventDateTo;
	}

	public CriteriaDateType getEventDateType() {
		return eventDateType;
	}

	public void setEventDateType(CriteriaDateType eventDateType) {
		this.eventDateType = eventDateType;
	}

	public EventCriteria eventDateType(CriteriaDateType eventDateType) {
		this.eventDateType = eventDateType;
		return this;
	}

	public EventCriteria dateFilterOption(DateFilterOption dateFilterOption) {
		this.dateFilterOption = dateFilterOption;
		return this;
	}

	public DateFilterOption getDateFilterOption() {
		return dateFilterOption;
	}

	public EventCriteria eventEvolutionDateBetween(
		Date eventEvolutionDateFrom,
		Date eventEvolutionDateTo,
		DateFilterOption evolutionDateFilterOption) {
		this.eventEvolutionDateFrom = eventEvolutionDateFrom;
		this.eventEvolutionDateTo = eventEvolutionDateTo;
		this.evolutionDateFilterOption = evolutionDateFilterOption;
		return this;
	}

	public EventCriteria eventEvolutionDateFrom(Date eventEvolutionDateFrom) {
		this.eventEvolutionDateFrom = eventEvolutionDateFrom;
		return this;
	}

	public Date getEventEvolutionDateFrom() {
		return eventEvolutionDateFrom;
	}

	public EventCriteria eventEvolutionDateTo(Date eventEvolutionDateTo) {
		this.eventEvolutionDateTo = eventEvolutionDateTo;
		return this;
	}

	public Date getEventEvolutionDateTo() {
		return eventEvolutionDateTo;
	}

	public EventCriteria evolutionDateFilterOption(DateFilterOption evolutionDateFilterOption) {
		this.evolutionDateFilterOption = evolutionDateFilterOption;
		return this;
	}

	public DateFilterOption getEvolutionDateFilterOption() {
		return evolutionDateFilterOption;
	}

	public EventCriteria responsibleUser(UserReferenceDto responsibleUser) {
		this.responsibleUser = responsibleUser;
		return this;
	}

	public void setResponsibleUser(UserReferenceDto responsibleUser) {
		this.responsibleUser = responsibleUser;
	}

	public UserReferenceDto getResponsibleUser() {
		return responsibleUser;
	}

	public EventCriteria freeText(String freeText) {
		this.freeText = freeText;
		return this;
	}

	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}

	@IgnoreForUrl
	public String getFreeText() {
		return freeText;
	}

	public EventCriteria freeTextEventParticipants(String freeTextEventParticipants) {
		this.freeTextEventParticipants = freeTextEventParticipants;
		return this;
	}

	public void setFreeTextEventParticipants(String freeTextEventParticipants) {
		this.freeTextEventParticipants = freeTextEventParticipants;
	}

	@IgnoreForUrl
	public String getFreeTextEventParticipants() {
		return freeTextEventParticipants;
	}

	public EventCriteria freeTextEventGroups(String freeTextEventGroups) {
		this.freeTextEventGroups = freeTextEventGroups;
		return this;
	}

	public void setFreeTextEventGroups(String freeTextEventGroups) {
		this.freeTextEventGroups = freeTextEventGroups;
	}

	@IgnoreForUrl
	public String getFreeTextEventGroups() {
		return freeTextEventGroups;
	}

	public EventSourceType getSrcType() {
		return srcType;
	}

	public void setSrcType(EventSourceType srcType) {
		this.srcType = srcType;
	}

	public TypeOfPlace getTypeOfPlace() {
		return typeOfPlace;
	}

	public void setTypeOfPlace(TypeOfPlace typeOfPlace) {
		this.typeOfPlace = typeOfPlace;
	}

	public EventCriteria typeOfPlace(TypeOfPlace typeOfPlace) {
		setTypeOfPlace(typeOfPlace);
		return this;
	}

	public ActionStatus getActionStatus() {
		return actionStatus;
	}

	public void setActionStatus(ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
	}

	public EventCriteria actionStatus(ActionStatus actionStatus) {
		setActionStatus(actionStatus);
		return this;
	}

	public EventCriteria actionChangeDateBetween(Date actionChangeDateFrom, Date actionChangeDateTo, DateFilterOption actionChangeDateFilterOption) {
		this.actionChangeDateFrom = actionChangeDateFrom;
		this.actionChangeDateTo = actionChangeDateTo;
		this.actionChangeDateFilterOption = actionChangeDateFilterOption;
		return this;
	}

	public EventCriteria actionDateBetween(Date actionDateFrom, Date actionDateTo, DateFilterOption actionDateFilterOption) {
		this.actionDateFrom = actionDateFrom;
		this.actionDateTo = actionDateTo;
		this.actionDateFilterOption = actionDateFilterOption;
		return this;
	}

	public EventCriteria dateBetween(
		DateType dateType,
		Date dateFrom,
		Date dateTo,
		CriteriaDateType criteriaDateType,
		DateFilterOption dateFilterOption) {
		switch (dateType) {
		case EVENT:
			eventDateBetween(dateFrom, dateTo, criteriaDateType, dateFilterOption);
			break;
		case EVENT_SIGNAL_EVOLUTION:
			eventEvolutionDateBetween(dateFrom, dateTo, dateFilterOption);
			break;
		case ACTION_CHANGE:
			actionChangeDateBetween(dateFrom, dateTo, dateFilterOption);
			break;
		case ACTION:
			actionDateBetween(dateFrom, dateTo, dateFilterOption);
			break;
		}
		return this;
	}

	public Date getActionChangeDateFrom() {
		return actionChangeDateFrom;
	}

	public void setActionChangeDateFrom(Date actionChangeDateFrom) {
		this.actionChangeDateFrom = actionChangeDateFrom;
	}

	public EventCriteria actionChangeDateFrom(Date actionChangeDateFrom) {
		this.actionChangeDateFrom = actionChangeDateFrom;
		return this;
	}

	public Date getActionChangeDateTo() {
		return actionChangeDateTo;
	}

	public void setActionChangeDateTo(Date actionChangeDateTo) {
		this.actionChangeDateTo = actionChangeDateTo;
	}

	public EventCriteria actionChangeDateTo(Date actionChangeDateTo) {
		this.actionChangeDateTo = actionChangeDateTo;
		return this;
	}

	public void setActionChangeDateFilterOption(DateFilterOption actionChangeDateFilterOption) {
		this.actionChangeDateFilterOption = actionChangeDateFilterOption;
	}

	public EventCriteria actionChangeDateFilterOption(DateFilterOption actionChangeDateFilterOption) {
		this.actionChangeDateFilterOption = actionChangeDateFilterOption;
		return this;
	}

	public DateFilterOption getActionChangeDateFilterOption() {
		return actionChangeDateFilterOption;
	}

	public Date getActionDateFrom() {
		return actionDateFrom;
	}

	public void setActionDateFrom(Date actionDateFrom) {
		this.actionDateFrom = actionDateFrom;
	}

	public EventCriteria actionDateFrom(Date actionDateFrom) {
		this.actionDateFrom = actionDateFrom;
		return this;
	}

	public Date getActionDateTo() {
		return actionDateTo;
	}

	public void setActionDateTo(Date actionDateTo) {
		this.actionDateTo = actionDateTo;
	}

	public EventCriteria actionDateTo(Date actionDateTo) {
		this.actionDateTo = actionDateTo;
		return this;
	}

	public void setActionDateFilterOption(DateFilterOption actionDateFilterOption) {
		this.actionDateFilterOption = actionDateFilterOption;
	}

	public EventCriteria actionDateFilterOption(DateFilterOption actionDateFilterOption) {
		this.actionDateFilterOption = actionDateFilterOption;
		return this;
	}

	public DateFilterOption getActionDateFilterOption() {
		return actionDateFilterOption;
	}

	public EventCriteria reportedBetween(Date reportedDateFrom, Date reportedDateTo) {

		this.reportedDateFrom = reportedDateFrom;
		this.reportedDateTo = reportedDateTo;
		return this;
	}

    public enum DateType {
		EVENT,
		EVENT_SIGNAL_EVOLUTION,
		ACTION_CHANGE,
		ACTION
	}

	public PersonReferenceDto getPerson() {
		return person;
	}

	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}

	public EventCriteria person(PersonReferenceDto person) {
		this.person = person;
		return this;
	}

	public FacilityType getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(FacilityType facilityType) {
		this.facilityType = facilityType;
	}

	public EventCriteria facilityType(FacilityType facilityType) {
		this.facilityType = facilityType;
		return this;
	}

	public FacilityReferenceDto getFacility() {
		return facility;
	}

	public void setFacility(FacilityReferenceDto facility) {
		this.facility = facility;
	}

	public EventCriteria facility(FacilityReferenceDto facility) {
		this.facility = facility;
		return this;
	}

	@IgnoreForUrl
	public EventReferenceDto getSuperordinateEvent() {
		return superordinateEvent;
	}

	public void setSuperordinateEvent(EventReferenceDto superordinateEvent) {
		this.superordinateEvent = superordinateEvent;
	}

	public EventCriteria superordinateEvent(EventReferenceDto superordinateEvent) {
		this.superordinateEvent = superordinateEvent;
		return this;
	}

	public EventGroupReferenceDto getEventGroup() {
		return eventGroup;
	}

	public void setEventGroup(EventGroupReferenceDto eventGroup) {
		this.eventGroup = eventGroup;
	}

	public EventCriteria eventGroup(EventGroupReferenceDto eventGroup) {
		this.eventGroup = eventGroup;
		return this;
	}

	@IgnoreForUrl
	public Set<String> getExcludedUuids() {
		return excludedUuids;
	}

	public void setExcludedUuids(Set<String> excludedUuids) {
		this.excludedUuids = excludedUuids;
	}

	public EventCriteria excludedUuids(Set<String> excludedUuids) {
		this.excludedUuids = excludedUuids;
		return this;
	}

	@IgnoreForUrl
	public Boolean getHasNoSuperordinateEvent() {
		return hasNoSuperordinateEvent;
	}

	public void setHasNoSuperordinateEvent(Boolean hasNoSuperordinateEvent) {
		this.hasNoSuperordinateEvent = hasNoSuperordinateEvent;
	}

	public EventCriteria hasNoSuperordinateEvent(Boolean hasNoSuperordinateEvent) {
		this.hasNoSuperordinateEvent = hasNoSuperordinateEvent;
		return this;
	}

	public EventManagementStatus getEventManagementStatus() {
		return eventManagementStatus;
	}

	public void setEventManagementStatus(EventManagementStatus eventManagementStatus) {
		this.eventManagementStatus = eventManagementStatus;
	}

	public EventIdentificationSource getEventIdentificationSource() {
		return eventIdentificationSource;
	}

	public void setEventIdentificationSource(EventIdentificationSource eventIdentificationSource) {
		this.eventIdentificationSource = eventIdentificationSource;
	}

	public Boolean getOnlyEntitiesNotSharedWithExternalSurvTool() {
		return onlyEntitiesNotSharedWithExternalSurvTool;
	}

	public void setOnlyEntitiesNotSharedWithExternalSurvTool(Boolean onlyEntitiesNotSharedWithExternalSurvTool) {
		this.onlyEntitiesNotSharedWithExternalSurvTool = onlyEntitiesNotSharedWithExternalSurvTool;
	}

	public Boolean getOnlyEntitiesSharedWithExternalSurvTool() {
		return onlyEntitiesSharedWithExternalSurvTool;
	}

	public void setOnlyEntitiesSharedWithExternalSurvTool(Boolean onlyEntitiesSharedWithExternalSurvTool) {
		this.onlyEntitiesSharedWithExternalSurvTool = onlyEntitiesSharedWithExternalSurvTool;
	}

	public Boolean getOnlyEntitiesChangedSinceLastSharedWithExternalSurvTool() {
		return onlyEntitiesChangedSinceLastSharedWithExternalSurvTool;
	}

	public void setOnlyEntitiesChangedSinceLastSharedWithExternalSurvTool(Boolean onlyEntitiesChangedSinceLastSharedWithExternalSurvTool) {
		this.onlyEntitiesChangedSinceLastSharedWithExternalSurvTool = onlyEntitiesChangedSinceLastSharedWithExternalSurvTool;
	}

	public Date getReportedDateFrom() {
		return reportedDateFrom;
	}

	public Date getReportedDateTo() {
		return reportedDateTo;
	}
}
