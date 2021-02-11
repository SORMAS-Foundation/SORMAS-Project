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

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.disease.DiseaseVariantReferenceDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.facility.FacilityTypeGroup;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.SymptomJournalStatus;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.IgnoreForUrl;

public class CaseCriteria extends BaseCriteria implements Cloneable {

	private static final long serialVersionUID = 5114202107622217837L;

	public static final String PRESENT_CONDITION = "presentCondition";
	public static final String REPORTING_USER_ROLE = "reportingUserRole";
	public static final String MUST_HAVE_NO_GEO_COORDINATES = "mustHaveNoGeoCoordinates";
	public static final String MUST_BE_PORT_HEALTH_CASE_WITHOUT_FACILITY = "mustBePortHealthCaseWithoutFacility";
	public static final String MUST_HAVE_CASE_MANAGEMENT_DATA = "mustHaveCaseManagementData";
	public static final String WITHOUT_RESPONSIBLE_OFFICER = "withoutResponsibleOfficer";
	public static final String WITH_EXTENDED_QUARANTINE = "withExtendedQuarantine";
	public static final String WITH_REDUCED_QUARANTINE = "withReducedQuarantine";
	public static final String CREATION_DATE_FROM = "creationDateFrom";
	public static final String CREATION_DATE_TO = "creationDateTo";
	public static final String NAME_UUID_EPID_NUMBER_LIKE = "nameUuidEpidNumberLike";
	public static final String EVENT_LIKE = "eventLike";
	public static final String ONLY_CASES_WITH_EVENTS = "onlyCasesWithEvents";
	public static final String REPORTING_USER_LIKE = "reportingUserLike";
	public static final String NEW_CASE_DATE_TYPE = "newCaseDateType";
	public static final String NEW_CASE_DATE_FROM = "newCaseDateFrom";
	public static final String NEW_CASE_DATE_TO = "newCaseDateTo";
	public static final String BIRTHDATE_YYYY = "birthdateYYYY";
	public static final String BIRTHDATE_MM = "birthdateMM";
	public static final String BIRTHDATE_DD = "birthdateDD";
	public static final String FOLLOW_UP_UNTIL_TO = "followUpUntilTo";
	public static final String SYMPTOM_JOURNAL_STATUS = "symptomJournalStatus";
	public static final String FACILITY_TYPE_GROUP = "facilityTypeGroup";
	public static final String FACILITY_TYPE = "facilityType";
	public static final String INCLUDE_CASES_FROM_OTHER_JURISDICTIONS = "includeCasesFromOtherJurisdictions";
	public static final String ONLY_CONTACTS_FROM_OTHER_INSTANCES = "onlyContactsFromOtherInstances";

	private UserRole reportingUserRole;
	private Disease disease;
	private DiseaseVariantReferenceDto diseaseVariant;
	private CaseOutcome outcome;
	private CaseClassification caseClassification;
	private InvestigationStatus investigationStatus;
	private PresentCondition presentCondition;
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
	private NewCaseDateType newCaseDateType;
	// Used to re-construct whether users have filtered by epi weeks or dates
	private DateFilterOption dateFilterOption = DateFilterOption.DATE;
	private PersonReferenceDto person;
	private Boolean mustHaveNoGeoCoordinates;
	private Boolean mustBePortHealthCaseWithoutFacility;
	private Boolean mustHaveCaseManagementData;
	private Boolean withoutResponsibleOfficer;
	private Boolean withExtendedQuarantine;
	private Boolean withReducedQuarantine;
	private Boolean deleted = Boolean.FALSE;
	private String nameUuidEpidNumberLike;
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
	private FollowUpStatus followUpStatus;
	private Date followUpUntilTo;
	private Date followUpUntilFrom;
	private SymptomJournalStatus symptomJournalStatus;
	private Date reportDateTo;
	private FacilityTypeGroup facilityTypeGroup;
	private FacilityType facilityType;
	private Boolean includeCasesFromOtherJurisdictions = Boolean.FALSE;
	private Boolean onlyContactsFromOtherInstances;

	@Override
	public CaseCriteria clone() {

		try {
			return (CaseCriteria) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public void setReportingUserRole(UserRole reportingUserRole) {
		this.reportingUserRole = reportingUserRole;
	}

	public UserRole getReportingUserRole() {
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

	public void setDiseaseVariant(DiseaseVariantReferenceDto diseaseVariant) {
		this.diseaseVariant = diseaseVariant;
	}

	public CaseCriteria diseaseVariant(DiseaseVariantReferenceDto diseaseVariant) {
		setDiseaseVariant(diseaseVariant);
		return this;
	}

	public DiseaseVariantReferenceDto getDiseaseVariant() {
		return diseaseVariant;
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

	/**
	 * @param newCaseDateTo
	 *            will automatically be set to the end of the day
	 */
	public CaseCriteria newCaseDateBetween(Date newCaseDateFrom, Date newCaseDateTo, NewCaseDateType newCaseDateType) {

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

	public NewCaseDateType getNewCaseDateType() {
		return newCaseDateType;
	}

	public CaseCriteria dateFilterOption(DateFilterOption dateFilterOption) {
		this.dateFilterOption = dateFilterOption;
		return this;
	}

	public DateFilterOption getDateFilterOption() {
		return dateFilterOption;
	}

	public void setNewCaseDateType(NewCaseDateType newCaseDateType) {
		this.newCaseDateType = newCaseDateType;
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
		this.relevanceStatus = relevanceStatus;
		return this;
	}

	@IgnoreForUrl
	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}

	public CaseCriteria deleted(Boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	@IgnoreForUrl
	public Boolean getDeleted() {
		return deleted;
	}

	/**
	 * returns all entries that match ALL of the passed words
	 */
	public void setNameUuidEpidNumberLike(String nameUuidEpidNumberLike) {
		this.nameUuidEpidNumberLike = nameUuidEpidNumberLike;
	}

	public CaseCriteria nameUuidEpidNumberLike(String nameUuidEpidNumberLike) {
		setNameUuidEpidNumberLike(nameUuidEpidNumberLike);
		return this;
	}

	@IgnoreForUrl
	public String getNameUuidEpidNumberLike() {
		return nameUuidEpidNumberLike;
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
}
