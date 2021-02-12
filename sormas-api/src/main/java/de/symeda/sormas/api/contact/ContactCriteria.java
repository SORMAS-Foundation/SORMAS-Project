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
package de.symeda.sormas.api.contact;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.SymptomJournalStatus;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class ContactCriteria extends BaseCriteria implements Serializable {

	public static final String NAME_UUID_CASE_LIKE = "nameUuidCaseLike";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String CONTACT_OFFICER = "contactOfficer";
	public static final String REPORTING_USER_ROLE = "reportingUserRole";
	public static final String FOLLOW_UP_UNTIL_TO = "followUpUntilTo";
	public static final String SYMPTOM_JOURNAL_STATUS = "symptomJournalStatus";
	public static final String QUARANTINE_TYPE = "quarantineType";
	public static final String QUARANTINE_ORDERED_VERBALLY = "quarantineOrderedVerbally";
	public static final String QUARANTINE_ORDERED_OFFICIAL_DOCUMENT = "quarantineOrderedOfficialDocument";
	public static final String QUARANTINE_NOT_ORDERED = "quarantineNotOrdered";
	public static final String ONLY_QUARANTINE_HELP_NEEDED = "onlyQuarantineHelpNeeded";
	public static final String ONLY_HIGH_PRIORITY_CONTACTS = "onlyHighPriorityContacts";
	public static final String WITH_EXTENDED_QUARANTINE = "withExtendedQuarantine";
	public static final String WITH_REDUCED_QUARANTINE = "withReducedQuarantine";
	public static final String BIRTHDATE_YYYY = "birthdateYYYY";
	public static final String BIRTHDATE_MM = "birthdateMM";
	public static final String BIRTHDATE_DD = "birthdateDD";
	public static final String RETURNING_TRAVELER = "returningTraveler";
	public static final String EVENT_LIKE = "eventLike";
	public static final String INCLUDE_CONTACTS_FROM_OTHER_JURISDICTIONS = "includeContactsFromOtherJurisdictions";
	public static final String ONLY_CONTACTS_SHARING_EVENT_WITH_SOURCE_CASE = "onlyContactsSharingEventWithSourceCase";
	public static final String ONLY_CONTACTS_FROM_OTHER_INSTANCES = "onlyContactsFromOtherInstances";

	private static final long serialVersionUID = 5114202107622217837L;

	private UserRole reportingUserRole;
	private Disease disease;
	private CaseReferenceDto caze;
	private CaseReferenceDto resultingCase;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community;
	private UserReferenceDto contactOfficer;
	private ContactClassification contactClassification;
	private ContactStatus contactStatus;
	private FollowUpStatus followUpStatus;
	private Date reportDateFrom;
	private Date reportDateTo;
	// Used to re-construct whether users have filtered by epi weeks or dates
	private DateFilterOption dateFilterOption = DateFilterOption.DATE;
	private Date followUpUntilFrom;
	private Date followUpUntilTo;
	private Boolean followUpUntilToPrecise;
	/**
	 * If yes, the followUpUntilTo filter will search for strict matches instead of a period,
	 * even if a followUpUntilFrom is specified
	 */
	private SymptomJournalStatus symptomJournalStatus;
	private Date lastContactDateFrom;
	private Date lastContactDateTo;
	private Boolean deleted = Boolean.FALSE;
	private String nameUuidCaseLike;
	private EntityRelevanceStatus relevanceStatus;
	private Boolean onlyHighPriorityContacts;
	private ContactCategory contactCategory;
	private CaseClassification caseClassification;
	private QuarantineType quarantineType;
	private Date quarantineFrom;
	private Date quarantineTo;
	private Boolean onlyQuarantineHelpNeeded;
	private Boolean quarantineOrderedVerbally;
	private Boolean quarantineOrderedOfficialDocument;
	private Boolean quarantineNotOrdered;
	private Boolean withExtendedQuarantine;
	private Boolean withReducedQuarantine;
	private PersonReferenceDto person;
	private Integer birthdateYYYY;
	private Integer birthdateMM;
	private Integer birthdateDD;
	private YesNoUnknown returningTraveler;
	private String eventLike;
	private String eventUuid;
	private Boolean includeContactsFromOtherJurisdictions = Boolean.FALSE;
	private Boolean onlyContactsSharingEventWithSourceCase;
	private EventParticipantReferenceDto eventParticipant;
	private EventReferenceDto onlyContactsWithSourceCaseInGivenEvent;
	private Boolean onlyContactsFromOtherInstances;

	public UserRole getReportingUserRole() {
		return reportingUserRole;
	}

	public void setReportingUserRole(UserRole reportingUserRole) {
		this.reportingUserRole = reportingUserRole;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public ContactCriteria disease(Disease disease) {
		setDisease(disease);

		return this;
	}

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public ContactCriteria caze(CaseReferenceDto caze) {
		this.caze = caze;
		return this;
	}

	public CaseReferenceDto getResultingCase() {
		return resultingCase;
	}

	public ContactCriteria resultingCase(CaseReferenceDto resultingCase) {
		this.resultingCase = resultingCase;
		return this;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public ContactCriteria region(RegionReferenceDto region) {
		setRegion(region);

		return this;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public ContactCriteria district(DistrictReferenceDto district) {
		setDistrict(district);
		return this;
	}

	public CommunityReferenceDto getCommunity() {
		return community;
	}

	public void setCommunity(CommunityReferenceDto community) {
		this.community = community;
	}

	public ContactCriteria community(CommunityReferenceDto community) {
		setCommunity(community);
		return this;
	}

	public UserReferenceDto getContactOfficer() {
		return contactOfficer;
	}

	public void setContactOfficer(UserReferenceDto contactOfficer) {
		this.contactOfficer = contactOfficer;
	}

	public ContactClassification getContactClassification() {
		return contactClassification;
	}

	public void setContactClassification(ContactClassification contactClassification) {
		this.contactClassification = contactClassification;
	}

	public ContactStatus getContactStatus() {
		return contactStatus;
	}

	public ContactCriteria contactStatus(ContactStatus contactStatus) {
		this.contactStatus = contactStatus;
		return this;
	}

	public FollowUpStatus getFollowUpStatus() {
		return followUpStatus;
	}

	public void setFollowUpStatus(FollowUpStatus followUpStatus) {
		this.followUpStatus = followUpStatus;
	}

	public ContactCriteria reportDateBetween(Date reportDateFrom, Date reportDateTo) {
		this.reportDateFrom = reportDateFrom;
		this.reportDateTo = reportDateTo;
		return this;
	}

	public ContactCriteria reportDateFrom(Date reportDateFrom) {
		this.reportDateFrom = reportDateFrom;
		return this;
	}

	public Date getReportDateFrom() {
		return reportDateFrom;
	}

	public ContactCriteria reportDateTo(Date reportDateTo) {
		this.reportDateTo = reportDateTo;
		return this;
	}

	public Date getReportDateTo() {
		return reportDateTo;
	}

	public ContactCriteria lastContactDateBetween(Date lastContactDateFrom, Date lastContactDateTo) {
		this.lastContactDateFrom = lastContactDateFrom;
		this.lastContactDateTo = lastContactDateTo;
		return this;
	}

	public ContactCriteria lastContactDateFrom(Date lastContactDateFrom) {
		this.lastContactDateFrom = lastContactDateFrom;
		return this;
	}

	public Date getLastContactDateFrom() {
		return lastContactDateFrom;
	}

	public ContactCriteria lastContactDateTo(Date lastContactDateTo) {
		this.lastContactDateTo = lastContactDateTo;
		return this;
	}

	public Date getLastContactDateTo() {
		return lastContactDateTo;
	}

	public ContactCriteria dateFilterOption(DateFilterOption dateFilterOption) {
		this.dateFilterOption = dateFilterOption;
		return this;
	}

	public DateFilterOption getDateFilterOption() {
		return dateFilterOption;
	}

	public ContactCriteria followUpUntilBetween(Date followUpUntilFrom, Date followUpUntilTo) {
		this.followUpUntilFrom = followUpUntilFrom;
		this.followUpUntilTo = followUpUntilTo;
		return this;
	}

	public ContactCriteria followUpUntilFrom(Date followUpUntilFrom) {
		this.followUpUntilFrom = followUpUntilFrom;
		return this;
	}

	public Date getFollowUpUntilFrom() {
		return followUpUntilFrom;
	}

	public void setFollowUpUntilTo(Date followUpUntilTo) {
		this.followUpUntilTo = followUpUntilTo;
	}

	public Date getFollowUpUntilTo() {
		return followUpUntilTo;
	}

	public SymptomJournalStatus getSymptomJournalStatus() {
		return symptomJournalStatus;
	}

	public void setSymptomJournalStatus(SymptomJournalStatus symptomJournalStatus) {
		this.symptomJournalStatus = symptomJournalStatus;
	}

	public Boolean getFollowUpUntilToPrecise() {
		return followUpUntilToPrecise;
	}

	public ContactCriteria followUpUntilToPrecise(Boolean followUpUntilToPrecise) {
		this.followUpUntilToPrecise = followUpUntilToPrecise;
		return this;
	}

	public ContactCriteria relevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
		return this;
	}

	@IgnoreForUrl
	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}

	public ContactCriteria deleted(Boolean deleted) {
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
	public void setNameUuidCaseLike(String nameUuidCaseLike) {
		this.nameUuidCaseLike = nameUuidCaseLike;
	}

	@IgnoreForUrl
	public String getNameUuidCaseLike() {
		return nameUuidCaseLike;
	}

	public Boolean getOnlyHighPriorityContacts() {
		return onlyHighPriorityContacts;
	}

	public void setContactCategory(ContactCategory contactCategory) {
		this.contactCategory = contactCategory;
	}

	public ContactCategory getContactCategory() {
		return contactCategory;
	}

	public void setOnlyHighPriorityContacts(Boolean onlyHighPriorityContacts) {
		this.onlyHighPriorityContacts = onlyHighPriorityContacts;
	}

	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}

	public QuarantineType getQuarantineType() {
		return quarantineType;
	}

	public void setQuarantineType(QuarantineType quarantineType) {
		this.quarantineType = quarantineType;
	}

	public Boolean getOnlyQuarantineHelpNeeded() {
		return onlyQuarantineHelpNeeded;
	}

	public void setOnlyQuarantineHelpNeeded(Boolean onlyQuarantineHelpNeeded) {
		this.onlyQuarantineHelpNeeded = onlyQuarantineHelpNeeded;
	}

	public Date getQuarantineFrom() {
		return quarantineFrom;
	}

	public void setQuarantineFrom(Date quarantineFrom) {
		this.quarantineFrom = quarantineFrom;
	}

	public Date getQuarantineTo() {
		return quarantineTo;
	}

	public void setQuarantineTo(Date quarantineTo) {
		this.quarantineTo = quarantineTo;
	}

	public Boolean getQuarantineOrderedVerbally() {
		return quarantineOrderedVerbally;
	}

	public void setQuarantineOrderedVerbally(Boolean quarantineOrderedVerbally) {
		this.quarantineOrderedVerbally = quarantineOrderedVerbally;
	}

	public Boolean getQuarantineOrderedOfficialDocument() {
		return quarantineOrderedOfficialDocument;
	}

	public void setQuarantineOrderedOfficialDocument(Boolean quarantineOrderedOfficialDocument) {
		this.quarantineOrderedOfficialDocument = quarantineOrderedOfficialDocument;
	}

	public Boolean getQuarantineNotOrdered() {
		return quarantineNotOrdered;
	}

	public void setQuarantineNotOrdered(Boolean quarantineNotOrdered) {
		this.quarantineNotOrdered = quarantineNotOrdered;
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

	public PersonReferenceDto getPerson() {
		return person;
	}

	public ContactCriteria setPerson(PersonReferenceDto person) {
		this.person = person;
		return this;
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

	public YesNoUnknown getReturningTraveler() {
		return returningTraveler;
	}

	public void setReturningTraveler(YesNoUnknown returningTraveler) {
		this.returningTraveler = returningTraveler;
	}

	public void setEventLike(String eventLike) {
		this.eventLike = eventLike;
	}

	@IgnoreForUrl
	public String getEventLike() {
		return eventLike;
	}

	public ContactCriteria eventLike(String eventLike) {
		setEventLike(eventLike);
		return this;
	}

	public void setEventUuid(String eventUuid) {
		this.eventUuid = eventUuid;
	}

	public String getEventUuid() {
		return eventUuid;
	}

	public ContactCriteria eventUuid(String eventUuid) {
		setEventUuid(eventUuid);
		return this;
	}

	public void setOnlyContactsSharingEventWithSourceCase(Boolean onlyContactsSharingEventWithSourceCase) {
		this.onlyContactsSharingEventWithSourceCase = onlyContactsSharingEventWithSourceCase;
	}

	@IgnoreForUrl
	public Boolean getOnlyContactsSharingEventWithSourceCase() {
		return onlyContactsSharingEventWithSourceCase;
	}

	public ContactCriteria onlyContactsSharingEventWithSourceCase(Boolean onlyContactsSharingEventWithSourceCase) {
		this.onlyContactsSharingEventWithSourceCase = onlyContactsSharingEventWithSourceCase;
		return this;
	}

	public Boolean getIncludeContactsFromOtherJurisdictions() {
		return includeContactsFromOtherJurisdictions;
	}

	public void setIncludeContactsFromOtherJurisdictions(Boolean includeContactsFromOtherJurisdictions) {
		this.includeContactsFromOtherJurisdictions = includeContactsFromOtherJurisdictions;
	}

	public void setEventParticipant(EventParticipantReferenceDto eventParticipant) {
		this.eventParticipant = eventParticipant;
	}

	@IgnoreForUrl
	public EventParticipantReferenceDto getEventParticipant() {
		return eventParticipant;
	}

	public ContactCriteria eventParticipant(EventParticipantReferenceDto eventParticipant) {
		this.eventParticipant = eventParticipant;
		return this;
	}

	public void setOnlyContactsWithSourceCaseInGivenEvent(EventReferenceDto onlyContactsWithSourceCaseInGivenEvent) {
		this.onlyContactsWithSourceCaseInGivenEvent = onlyContactsWithSourceCaseInGivenEvent;
	}

	@IgnoreForUrl
	public EventReferenceDto getOnlyContactsWithSourceCaseInGivenEvent() {
		return onlyContactsWithSourceCaseInGivenEvent;
	}

	public ContactCriteria onlyContactsWithSourceCaseInGivenEvent(EventReferenceDto onlyContactsWithSourceCaseInGivenEvent) {
		this.onlyContactsWithSourceCaseInGivenEvent = onlyContactsWithSourceCaseInGivenEvent;
		return this;
	}

	public Boolean getOnlyContactsFromOtherInstances() {
		return onlyContactsFromOtherInstances;
	}

	public void setOnlyContactsFromOtherInstances(Boolean onlyContactsFromOtherInstances) {
		this.onlyContactsFromOtherInstances = onlyContactsFromOtherInstances;
	}
}
