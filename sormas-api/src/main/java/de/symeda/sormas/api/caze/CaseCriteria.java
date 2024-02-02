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
package de.symeda.sormas.api.caze;

import java.util.Date;
import java.util.Set;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.SymptomJournalStatus;
import de.symeda.sormas.api.share.ExternalShareCriteria;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.CriteriaDateType;
import de.symeda.sormas.api.utils.criteria.CriteriaWithDateType;

public class CaseCriteria extends CriteriaWithDateType implements ExternalShareCriteria, Cloneable {

	private static final long serialVersionUID = 5114202107622217837L;

	public static final String PRESENT_CONDITION = "presentCondition";
	public static final String REPORTING_USER_ROLE = "reportingUserRole";
	public static final String MUST_HAVE_NO_GEO_COORDINATES = "mustHaveNoGeoCoordinates";
	public static final String MUST_BE_PORT_HEALTH_CASE_WITHOUT_FACILITY = "mustBePortHealthCaseWithoutFacility";
	public static final String MUST_HAVE_CASE_MANAGEMENT_DATA = "mustHaveCaseManagementData";
	public static final String WITHOUT_RESPONSIBLE_OFFICER = "withoutResponsibleOfficer";
	public static final String WITH_EXTENDED_QUARANTINE = "withExtendedQuarantine";
	public static final String WITH_REDUCED_QUARANTINE = "withReducedQuarantine";
	public static final String ONLY_QUARANTINE_HELP_NEEDED = "onlyQuarantineHelpNeeded";
	public static final String CREATION_DATE_FROM = "creationDateFrom";
	public static final String CREATION_DATE_TO = "creationDateTo";
	public static final String CASE_LIKE = "caseLike";
	public static final String EVENT_LIKE = "eventLike";
	public static final String ONLY_CASES_WITH_EVENTS = "onlyCasesWithEvents";
	public static final String REPORTING_USER_LIKE = "reportingUserLike";
	public static final String NEW_CASE_DATE_TYPE = "newCaseDateType";
	public static final String NEW_CASE_DATE_FROM = "newCaseDateFrom";
	public static final String NEW_CASE_DATE_TO = "newCaseDateTo";
	public static final String BIRTHDATE_YYYY = "birthdateYYYY";
	public static final String BIRTHDATE_MM = "birthdateMM";
	public static final String BIRTHDATE_DD = "birthdateDD";
	public static final String QUARANTINE_TYPE = "quarantineType";
	public static final String FOLLOW_UP_UNTIL_TO = "followUpUntilTo";
	public static final String SYMPTOM_JOURNAL_STATUS = "symptomJournalStatus";
	public static final String VACCINATION_STATUS = "vaccinationStatus";
	public static final String REINFECTION_STATUS = "reinfectionStatus";
	public static final String FACILITY_TYPE_GROUP = "facilityTypeGroup";
	public static final String FACILITY_TYPE = "facilityType";
	public static final String INCLUDE_CASES_FROM_OTHER_JURISDICTIONS = "includeCasesFromOtherJurisdictions";
	public static final String ONLY_CONTACTS_FROM_OTHER_INSTANCES = "onlyContactsFromOtherInstances";
	public static final String ONLY_CASES_WITH_REINFECTION = "onlyCasesWithReinfection";
	public static final String ONLY_ENTITIES_NOT_SHARED_WITH_EXTERNAL_SURV_TOOL = "onlyEntitiesNotSharedWithExternalSurvTool";
	public static final String ONLY_ENTITIES_SHARED_WITH_EXTERNAL_SURV_TOOL = "onlyEntitiesSharedWithExternalSurvTool";
	public static final String ONLY_ENTITIES_CHANGED_SINCE_LAST_SHARED_WITH_EXTERNAL_SURV_TOOL =
		"onlyEntitiesChangedSinceLastSharedWithExternalSurvTool";
	public static final String ONLY_CASES_WITH_DONT_SHARE_WITH_EXTERNAL_SURV_TOOL = "onlyCasesWithDontShareWithExternalSurvTool";
	public static final String ONLY_SHOW_CASES_WITH_FULFILLED_REFERENCE_DEFINITION = "onlyShowCasesWithFulfilledReferenceDefinition";
	public static final String PERSON_LIKE = "personLike";
	public static final String JURISDICTION_TYPE = "jurisdictionType";
	public static final String ENTITY_RELEVANCE_STATUS = "relevanceStatus";

	private UserRoleReferenceDto reportingUserRole;
	private Disease disease;
	private DiseaseVariant diseaseVariant;
	private CaseOutcome outcome;
	private CaseClassification caseClassification;
	private InvestigationStatus investigationStatus;
	private PresentCondition presentCondition;
	private CaseJurisdictionType jurisdictionType;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community;
	private FacilityReferenceDto healthFacility;
	private PointOfEntryReferenceDto pointOfEntry;
	private UserReferenceDto surveillanceOfficer;
	private Date newCaseDateFrom;
	private Date newCaseDateTo;
	private Date creationDateFrom;
	private Date creationDateTo;
	private CriteriaDateType newCaseDateType;
	// Used to re-construct whether users have filtered by epi weeks or dates
	private DateFilterOption dateFilterOption = DateFilterOption.DATE;
	private PersonReferenceDto person;
	private Boolean mustHaveNoGeoCoordinates;
	private Boolean mustBePortHealthCaseWithoutFacility;
	private Boolean mustHaveCaseManagementData;
	private Boolean withoutResponsibleOfficer;
	private Boolean withExtendedQuarantine;
	private Boolean withReducedQuarantine;
	private Boolean onlyQuarantineHelpNeeded;
	private String caseLike;
	private String eventLike;
	private Boolean onlyCasesWithEvents = Boolean.FALSE;
	private String reportingUserLike;
	private CaseOrigin caseOrigin;
	private EntityRelevanceStatus relevanceStatus;
	private String sourceCaseInfoLike;
	private Date quarantineTo;
	private Integer birthdateYYYY;
	private Integer birthdateMM;
	private Integer birthdateDD;
	private QuarantineType quarantineType;
	private FollowUpStatus followUpStatus;
	private Date followUpUntilTo;
	private Date followUpUntilFrom;
	private Date followUpVisitsFrom;
	private Date followUpVisitsTo;
	private Integer followUpVisitsInterval;
	private SymptomJournalStatus symptomJournalStatus;
	private VaccinationStatus vaccinationStatus;
	private ReinfectionStatus reinfectionStatus;
	private Date reportDateTo;
	private FacilityTypeGroup facilityTypeGroup;
	private FacilityType facilityType;
	private Boolean includeCasesFromOtherJurisdictions = Boolean.FALSE;
	private Boolean onlyContactsFromOtherInstances;
	private Boolean onlyCasesWithReinfection;
	private Boolean onlyEntitiesNotSharedWithExternalSurvTool;
	private Boolean onlyEntitiesSharedWithExternalSurvTool;
	private Boolean onlyEntitiesChangedSinceLastSharedWithExternalSurvTool;
	private Boolean onlyCasesWithDontShareWithExternalSurvTool;
	private Boolean onlyShowCasesWithFulfilledReferenceDefinition;
	private String personLike;
	private Boolean withOwnership;
	/**
	 * Used for filtering merge-able cases to filter both lead and similar cases.
	 */
	private Set<String> caseUuidsForMerge;
	private String nameUuidEpidNumberLike;

	private Boolean deleted = Boolean.FALSE;
	public Boolean excludeSharedCases;

	private Boolean includeNotACaseClassification;


	public CaseCriteria() {
		super(NewCaseDateType.class);
	}

	@Override
	public CaseCriteria clone() {

		try {
			return (CaseCriteria) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public void setReportingUserRole(UserRoleReferenceDto reportingUserRole) {
		this.reportingUserRole = reportingUserRole;
	}

	public UserRoleReferenceDto getReportingUserRole() {
		return reportingUserRole;
	}

	public void setOutcome(CaseOutcome outcome) {
		this.outcome = outcome;
	}

	public CaseOutcome getOutcome() {
		return outcome;
	}

	public void setCaseOrigin(CaseOrigin caseOrigin) {
		this.caseOrigin = caseOrigin;
	}

	public CaseOrigin getCaseOrigin() {
		return caseOrigin;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public CaseCriteria disease(Disease disease) {
		setDisease(disease);
		return this;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDiseaseVariant(DiseaseVariant diseaseVariant) {
		this.diseaseVariant = diseaseVariant;
	}

	public CaseCriteria diseaseVariant(DiseaseVariant diseaseVariant) {
		setDiseaseVariant(diseaseVariant);
		return this;
	}

	public DiseaseVariant getDiseaseVariant() {
		return diseaseVariant;
	}

	public void setJurisdictionType(CaseJurisdictionType jurisdictionType) {
		this.jurisdictionType = jurisdictionType;
	}

	public CaseCriteria jurisdictionType(CaseJurisdictionType jurisdictionType) {
		setJurisdictionType(jurisdictionType);
		return this;
	}

	public CaseJurisdictionType getJurisdictionType() {
		return jurisdictionType;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public CaseCriteria region(RegionReferenceDto region) {
		setRegion(region);
		return this;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public CaseCriteria district(DistrictReferenceDto district) {
		setDistrict(district);
		return this;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public CommunityReferenceDto getCommunity() {
		return community;
	}

	public void setCommunity(CommunityReferenceDto community) {
		this.community = community;
	}

	public CaseCriteria newCaseDateBetween(Date newCaseDateFrom, Date newCaseDateTo) {

		this.newCaseDateFrom = newCaseDateFrom;
		this.newCaseDateTo = newCaseDateTo;
		return this;
	}

	/**
	 * @param newCaseDateTo
	 *            will automatically be set to the end of the day
	 */
	public CaseCriteria newCaseDateBetween(Date newCaseDateFrom, Date newCaseDateTo, CriteriaDateType newCaseDateType) {

		this.newCaseDateFrom = newCaseDateFrom;
		this.newCaseDateTo = newCaseDateTo;
		this.newCaseDateType = newCaseDateType;
		return this;
	}

	public CaseCriteria newCaseDateFrom(Date newCaseDateFrom) {
		setNewCaseDateFrom(newCaseDateFrom);
		return this;
	}

	public Date getNewCaseDateFrom() {
		return newCaseDateFrom;
	}

	public void setNewCaseDateFrom(Date newCaseDateFrom) {
		this.newCaseDateFrom = newCaseDateFrom;
	}

	public Date getNewCaseDateTo() {
		return newCaseDateTo;
	}

	public void setNewCaseDateTo(Date newCaseDateTo) {
		this.newCaseDateTo = newCaseDateTo;
	}

	public CriteriaDateType getNewCaseDateType() {
		return newCaseDateType;
	}

	public void setNewCaseDateType(CriteriaDateType newCaseDateType) {
		this.newCaseDateType = newCaseDateType;
	}

	public CaseCriteria newCaseDateType(CriteriaDateType newCaseDateType) {
		setNewCaseDateType(newCaseDateType);
		return this;
	}

	public CaseCriteria dateFilterOption(DateFilterOption dateFilterOption) {
		this.dateFilterOption = dateFilterOption;
		return this;
	}

	public DateFilterOption getDateFilterOption() {
		return dateFilterOption;
	}

	public PersonReferenceDto getPerson() {
		return person;
	}

	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}

	public CaseCriteria person(PersonReferenceDto person) {
		setPerson(person);
		return this;
	}

	public void setMustHaveNoGeoCoordinates(Boolean mustHaveNoGeoCoordinates) {
		this.mustHaveNoGeoCoordinates = mustHaveNoGeoCoordinates;
	}

	public Boolean getMustHaveNoGeoCoordinates() {
		return mustHaveNoGeoCoordinates;
	}

	public void setMustBePortHealthCaseWithoutFacility(Boolean mustBePortHealthCaseWithoutFacility) {
		this.mustBePortHealthCaseWithoutFacility = mustBePortHealthCaseWithoutFacility;
	}

	public Boolean getMustBePortHealthCaseWithoutFacility() {
		return mustBePortHealthCaseWithoutFacility;
	}

	public void setMustHaveCaseManagementData(Boolean mustHaveCaseManagementData) {
		this.mustHaveCaseManagementData = mustHaveCaseManagementData;
	}

	public Boolean getMustHaveCaseManagementData() {
		return mustHaveCaseManagementData;
	}

	public void setWithoutResponsibleOfficer(Boolean withoutResponsibleOfficer) {
		this.withoutResponsibleOfficer = withoutResponsibleOfficer;
	}

	public Boolean getWithoutResponsibleOfficer() {
		return this.withoutResponsibleOfficer;
	}

	public Boolean getWithExtendedQuarantine() {
		return withExtendedQuarantine;
	}

	public void setWithExtendedQuarantine(Boolean withExtendedQuarantine) {
		this.withExtendedQuarantine = withExtendedQuarantine;
	}

	public Boolean getWithReducedQuarantine() {
		return withReducedQuarantine;
	}

	public void setWithReducedQuarantine(Boolean withReducedQuarantine) {
		this.withReducedQuarantine = withReducedQuarantine;
	}

	public Boolean getOnlyQuarantineHelpNeeded() {
		return onlyQuarantineHelpNeeded;
	}

	public void setOnlyQuarantineHelpNeeded(Boolean onlyQuarantineHelpNeeded) {
		this.onlyQuarantineHelpNeeded = onlyQuarantineHelpNeeded;
	}

	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}

	public CaseCriteria investigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
		return this;
	}

	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setPresentCondition(PresentCondition presentCondition) {
		this.presentCondition = presentCondition;
	}

	public PresentCondition getPresentCondition() {
		return presentCondition;
	}

	public void setHealthFacility(FacilityReferenceDto healthFacility) {
		this.healthFacility = healthFacility;
	}

	public FacilityReferenceDto getHealthFacility() {
		return healthFacility;
	}

	public void setPointOfEntry(PointOfEntryReferenceDto pointOfEntry) {
		this.pointOfEntry = pointOfEntry;
	}

	public PointOfEntryReferenceDto getPointOfEntry() {
		return pointOfEntry;
	}

	public void setSurveillanceOfficer(UserReferenceDto surveillanceOfficer) {
		this.surveillanceOfficer = surveillanceOfficer;
	}

	public UserReferenceDto getSurveillanceOfficer() {
		return surveillanceOfficer;
	}

	public CaseCriteria relevanceStatus(EntityRelevanceStatus relevanceStatus) {
		setRelevanceStatus(relevanceStatus);
		return this;
	}

	public void setRelevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
	}

	@IgnoreForUrl
	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}

	/**
	 * returns all entries that match ALL of the passed words
	 */
	public void setCaseLike(String caseLike) {
		this.caseLike = caseLike;
	}

	public CaseCriteria caseLike(String caseLike) {
		setCaseLike(caseLike);
		return this;
	}

	@IgnoreForUrl
	public String getCaseLike() {
		return caseLike;
	}

	public void setEventLike(String eventLike) {
		this.eventLike = eventLike;
	}

	public String getEventLike() {
		return eventLike;
	}

	public CaseCriteria eventLike(String eventLike) {
		setEventLike(eventLike);
		return this;
	}

	public void setOnlyCasesWithEvents(Boolean onlyCasesWithEvents) {
		this.onlyCasesWithEvents = onlyCasesWithEvents;
	}

	@IgnoreForUrl
	public Boolean getOnlyCasesWithEvents() {
		return onlyCasesWithEvents;
	}

	public CaseCriteria onlyCasesWithEvents(Boolean onlyCasesWithEvents) {
		this.onlyCasesWithEvents = onlyCasesWithEvents;
		return this;
	}

	@IgnoreForUrl
	public String getSourceCaseInfoLike() {
		return sourceCaseInfoLike;
	}

	public CaseCriteria setSourceCaseInfoLike(String sourceCaseInfoLike) {
		this.sourceCaseInfoLike = sourceCaseInfoLike;
		return this;
	}

	public void setReportingUserLike(String reportingUserLike) {
		this.reportingUserLike = reportingUserLike;
	}

	@IgnoreForUrl
	public String getReportingUserLike() {
		return reportingUserLike;
	}

	public Date getCreationDateFrom() {
		return creationDateFrom;
	}

	public void setCreationDateFrom(Date creationDateFrom) {
		this.creationDateFrom = creationDateFrom;
	}

	public CaseCriteria creationDateFrom(Date creationDateFrom) {
		setCreationDateFrom(creationDateFrom);
		return this;
	}

	public Date getCreationDateTo() {
		return creationDateTo;
	}

	public void setCreationDateTo(Date creationDateTo) {
		this.creationDateTo = creationDateTo;
	}

	public CaseCriteria creationDateTo(Date creationDateTo) {
		setCreationDateTo(creationDateTo);
		return this;
	}

	public Date getQuarantineTo() {
		return quarantineTo;
	}

	public void setQuarantineTo(Date quarantineTo) {
		this.quarantineTo = quarantineTo;
	}

	public Integer getBirthdateYYYY() {
		return birthdateYYYY;
	}

	public void setBirthdateYYYY(Integer birthdateYYYY) {
		this.birthdateYYYY = birthdateYYYY;
	}

	public Integer getBirthdateMM() {
		return birthdateMM;
	}

	public void setBirthdateMM(Integer birthdateMM) {
		this.birthdateMM = birthdateMM;
	}

	public Integer getBirthdateDD() {
		return birthdateDD;
	}

	public void setBirthdateDD(Integer birthdateDD) {
		this.birthdateDD = birthdateDD;
	}

	public QuarantineType getQuarantineType() {
		return quarantineType;
	}

	public void setQuarantineType(QuarantineType quarantineType) {
		this.quarantineType = quarantineType;
	}

	public FollowUpStatus getFollowUpStatus() {
		return followUpStatus;
	}

	public void setFollowUpStatus(FollowUpStatus followUpStatus) {
		this.followUpStatus = followUpStatus;
	}

	public void setFollowUpUntilTo(Date followUpUntilTo) {
		this.followUpUntilTo = followUpUntilTo;
	}

	public CaseCriteria followUpUntilTo(Date followUpUntilTo) {
		this.followUpUntilTo = followUpUntilTo;
		return this;
	}

	public Date getFollowUpUntilTo() {
		return followUpUntilTo;
	}

	public CaseCriteria followUpUntilFrom(Date followUpUntilFrom) {
		this.followUpUntilFrom = followUpUntilFrom;
		return this;
	}

	public Date getFollowUpUntilFrom() {
		return followUpUntilFrom;
	}

	public CaseCriteria reportDateTo(Date reportDateTo) {
		this.reportDateTo = reportDateTo;
		return this;
	}

	public SymptomJournalStatus getSymptomJournalStatus() {
		return symptomJournalStatus;
	}

	public void setSymptomJournalStatus(SymptomJournalStatus symptomJournalStatus) {
		this.symptomJournalStatus = symptomJournalStatus;
	}

	public VaccinationStatus getVaccinationStatus() {
		return vaccinationStatus;
	}

	public void setVaccinationStatus(VaccinationStatus vaccinationStatus) {
		this.vaccinationStatus = vaccinationStatus;
	}

	public ReinfectionStatus getReinfectionStatus() {
		return reinfectionStatus;
	}

	public void setReinfectionStatus(ReinfectionStatus reinfectionStatus) {
		this.reinfectionStatus = reinfectionStatus;
	}

	public Date getReportDateTo() {
		return reportDateTo;
	}

	public void setReportDateTo(Date reportDateTo) {
		this.reportDateTo = reportDateTo;
	}

	public FacilityTypeGroup getFacilityTypeGroup() {
		return facilityTypeGroup;
	}

	public void setFacilityTypeGroup(FacilityTypeGroup typeGroup) {
		this.facilityTypeGroup = typeGroup;
	}

	public FacilityType getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(FacilityType type) {
		this.facilityType = type;
	}

	public Boolean getIncludeCasesFromOtherJurisdictions() {
		return includeCasesFromOtherJurisdictions;
	}

	public void setIncludeCasesFromOtherJurisdictions(Boolean includeCasesFromOtherJurisdictions) {
		this.includeCasesFromOtherJurisdictions = includeCasesFromOtherJurisdictions;
	}

	public Boolean getOnlyContactsFromOtherInstances() {
		return onlyContactsFromOtherInstances;
	}

	public void setOnlyContactsFromOtherInstances(Boolean onlyContactsFromOtherInstances) {
		this.onlyContactsFromOtherInstances = onlyContactsFromOtherInstances;
	}

	public Boolean getOnlyCasesWithReinfection() {
		return onlyCasesWithReinfection;
	}

	public void setOnlyCasesWithReinfection(Boolean onlyCasesWithReinfection) {
		this.onlyCasesWithReinfection = onlyCasesWithReinfection;
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

	public Boolean getOnlyCasesWithDontShareWithExternalSurvTool() {
		return onlyCasesWithDontShareWithExternalSurvTool;
	}

	public void setOnlyCasesWithDontShareWithExternalSurvTool(Boolean onlyCasesWithDontShareWithExternalSurvTool) {
		this.onlyCasesWithDontShareWithExternalSurvTool = onlyCasesWithDontShareWithExternalSurvTool;
	}

	public Boolean getOnlyShowCasesWithFulfilledReferenceDefinition() {
		return onlyShowCasesWithFulfilledReferenceDefinition;
	}

	public void setOnlyShowCasesWithFulfilledReferenceDefinition(Boolean onlyShowCasesWithFulfilledReferenceDefinition) {
		this.onlyShowCasesWithFulfilledReferenceDefinition = onlyShowCasesWithFulfilledReferenceDefinition;
	}

	public Date getFollowUpVisitsFrom() {
		return followUpVisitsFrom;
	}

	public void setFollowUpVisitsFrom(Date followUpVisitsFrom) {
		this.followUpVisitsFrom = followUpVisitsFrom;
	}

	public Date getFollowUpVisitsTo() {
		return followUpVisitsTo;
	}

	public void setFollowUpVisitsTo(Date followUpVisitsTo) {
		this.followUpVisitsTo = followUpVisitsTo;
	}

	public Integer getFollowUpVisitsInterval() {
		return followUpVisitsInterval;
	}

	public void setFollowUpVisitsInterval(Integer followUpVisitsInterval) {
		this.followUpVisitsInterval = followUpVisitsInterval;
	}

	public String getPersonLike() {
		return personLike;
	}

	public void setPersonLike(String personLike) {
		this.personLike = personLike;
	}

	public CaseCriteria personLike(String personLike) {
		setPersonLike(personLike);
		return this;
	}

	@IgnoreForUrl
	public Boolean getWithOwnership() {
		return withOwnership;
	}

	public void setWithOwnership(Boolean withOwnership) {
		this.withOwnership = withOwnership;
	}

	@IgnoreForUrl
	public Set<String> getCaseUuidsForMerge() {
		return caseUuidsForMerge;
	}

	public CaseCriteria caseUuidsForMerge(Set<String> caseUuidsForMerge) {
		this.caseUuidsForMerge = caseUuidsForMerge;

		return this;
	}

	public void setNameUuidEpidNumberLike(String nameUuidEpidNumberLike) {
		this.nameUuidEpidNumberLike = nameUuidEpidNumberLike;
	}

	@IgnoreForUrl
	public String getNameUuidEpidNumberLike() {
		return nameUuidEpidNumberLike;
	}

	public CaseCriteria deleted(Boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	@IgnoreForUrl
	public Boolean getDeleted() {
		return deleted;
	}


	public Boolean getExcludeSharedCases() {
		return excludeSharedCases;
	}

	public void setExcludeSharedCases(Boolean excludeSharedCases) {
		this.excludeSharedCases = excludeSharedCases;
	}


	public CaseCriteria caseClassification(CaseClassification caseClassification) {
		setCaseClassification(caseClassification);
		return this;
	}



	public CaseCriteria includeNotACaseClassification(Boolean includeNotACaseClassification) {
		this.includeNotACaseClassification = includeNotACaseClassification;
		return this;
	}

	public Boolean isIncludeNotACaseClassification() {
		return includeNotACaseClassification;
	}
}
