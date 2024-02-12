/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.api.contact;

import static de.symeda.sormas.api.CountryHelper.COUNTRY_CODE_GERMANY;
import static de.symeda.sormas.api.CountryHelper.COUNTRY_CODE_SWITZERLAND;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_BIG;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.MappingException;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.sormastosormas.S2SIgnoreProperty;
import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareableDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.Outbreaks;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.LatitudePseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.LongitudePseudonymizer;

@DependingOnFeatureType(featureType = FeatureType.CONTACT_TRACING)
public class ContactDto extends SormasToSormasShareableDto {

	private static final long serialVersionUID = -7764607075875188799L;

	public static final long APPROXIMATE_JSON_SIZE_IN_BYTES = 96657;

	public static final String I18N_PREFIX = "Contact";

	public static final String ADDITIONAL_DETAILS = "additionalDetails";
	public static final String CARE_FOR_PEOPLE_OVER_60 = "careForPeopleOver60";
	public static final String CASE_ID_EXTERNAL_SYSTEM = "caseIdExternalSystem";
	public static final String CASE_OR_EVENT_INFORMATION = "caseOrEventInformation";
	public static final String CAZE = "caze";
	public static final String COMMUNITY = "community";
	public static final String CONTACT_CATEGORY = "contactCategory";
	public static final String CONTACT_CLASSIFICATION = "contactClassification";
	public static final String CONTACT_IDENTIFICATION_SOURCE = "contactIdentificationSource";
	public static final String CONTACT_IDENTIFICATION_SOURCE_DETAILS = "contactIdentificationSourceDetails";
	public static final String CONTACT_OFFICER = "contactOfficer";
	public static final String CONTACT_PROXIMITY = "contactProximity";
	public static final String CONTACT_PROXIMITY_DETAILS = "contactProximityDetails";
	public static final String CONTACT_STATUS = "contactStatus";
	public static final String DESCRIPTION = "description";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String DISEASE_VARIANT = "diseaseVariant";
	public static final String DISTRICT = "district";
	public static final String END_OF_QUARANTINE_REASON = "endOfQuarantineReason";
	public static final String END_OF_QUARANTINE_REASON_DETAILS = "endOfQuarantineReasonDetails";
	public static final String EPI_DATA = "epiData";
	public static final String EXTERNAL_ID = "externalID";
	public static final String EXTERNAL_TOKEN = "externalToken";
	public static final String FIRST_CONTACT_DATE = "firstContactDate";
	public static final String FOLLOW_UP_COMMENT = "followUpComment";
	public static final String FOLLOW_UP_STATUS = "followUpStatus";
	public static final String FOLLOW_UP_STATUS_CHANGE_DATE = "followUpStatusChangeDate";
	public static final String FOLLOW_UP_STATUS_CHANGE_USER = "followUpStatusChangeUser";
	public static final String FOLLOW_UP_UNTIL = "followUpUntil";
	public static final String HEALTH_CONDITIONS = "healthConditions";
	public static final String HIGH_PRIORITY = "highPriority";
	public static final String IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE = "immunosuppressiveTherapyBasicDisease";
	public static final String IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE_DETAILS = "immunosuppressiveTherapyBasicDiseaseDetails";
	public static final String INTERNAL_TOKEN = "internalToken";
	public static final String LAST_CONTACT_DATE = "lastContactDate";
	public static final String MULTI_DAY_CONTACT = "multiDayContact";
	public static final String OVERWRITE_FOLLOW_UP_UNTIL = "overwriteFollowUpUntil";
	public static final String PERSON = "person";
	public static final String PROHIBITION_TO_WORK = "prohibitionToWork";
	public static final String PROHIBITION_TO_WORK_FROM = "prohibitionToWorkFrom";
	public static final String PROHIBITION_TO_WORK_UNTIL = "prohibitionToWorkUntil";
	public static final String QUARANTINE = "quarantine";
	public static final String QUARANTINE_EXTENDED = "quarantineExtended";
	public static final String QUARANTINE_FROM = "quarantineFrom";
	public static final String QUARANTINE_HELP_NEEDED = "quarantineHelpNeeded";
	public static final String QUARANTINE_HOME_POSSIBLE = "quarantineHomePossible";
	public static final String QUARANTINE_HOME_POSSIBLE_COMMENT = "quarantineHomePossibleComment";
	public static final String QUARANTINE_HOME_SUPPLY_ENSURED = "quarantineHomeSupplyEnsured";
	public static final String QUARANTINE_HOME_SUPPLY_ENSURED_COMMENT = "quarantineHomeSupplyEnsuredComment";
	public static final String QUARANTINE_OFFICIAL_ORDER_SENT = "quarantineOfficialOrderSent";
	public static final String QUARANTINE_OFFICIAL_ORDER_SENT_DATE = "quarantineOfficialOrderSentDate";
	public static final String QUARANTINE_ORDERED_OFFICIAL_DOCUMENT = "quarantineOrderedOfficialDocument";
	public static final String QUARANTINE_ORDERED_OFFICIAL_DOCUMENT_DATE = "quarantineOrderedOfficialDocumentDate";
	public static final String QUARANTINE_ORDERED_VERBALLY = "quarantineOrderedVerbally";
	public static final String QUARANTINE_ORDERED_VERBALLY_DATE = "quarantineOrderedVerballyDate";
	public static final String QUARANTINE_REDUCED = "quarantineReduced";
	public static final String QUARANTINE_TO = "quarantineTo";
	public static final String QUARANTINE_TYPE_DETAILS = "quarantineTypeDetails";
	public static final String REGION = "region";
	public static final String RELATION_DESCRIPTION = "relationDescription";
	public static final String RELATION_TO_CASE = "relationToCase";
	public static final String REPORTING_DISTRICT = "reportingDistrict";
	public static final String REPORTING_USER = "reportingUser";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String REPORT_LAT = "reportLat";
	public static final String REPORT_LON = "reportLon";
	public static final String RESULTING_CASE = "resultingCase";
	public static final String RESULTING_CASE_USER = "resultingCaseUser";
	public static final String RETURNING_TRAVELER = "returningTraveler";
	public static final String TRACING_APP = "tracingApp";
	public static final String TRACING_APP_DETAILS = "tracingAppDetails";
	public static final String VACCINATION_STATUS = "vaccinationStatus";
	public static final String VISITS = "visits";
	public static final String VACCINATION_INFO = "vaccinationInfo";
	public static final String PREVIOUS_QUARANTINE_TO = "previousQuarantineTo";
	public static final String QUARANTINE_CHANGE_COMMENT = "quarantineChangeComment";
	public static final String DELETION_REASON = "deletionReason";
	public static final String OTHER_DELETION_REASON = "otherDeletionReason";

	private CaseReferenceDto caze;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String caseIdExternalSystem;
	@SensitiveData
	@Size(max = CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String caseOrEventInformation;
	@NotNull(message = Validations.validDisease)
	private Disease disease;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String diseaseDetails;
	@MappingException(reason = MappingException.FILLED_FROM_OTHER_ENTITY)
	private DiseaseVariant diseaseVariant;

	@NotNull(message = Validations.validReportDateTime)
	private Date reportDateTime;
	private UserReferenceDto reportingUser;
	@SensitiveData
	@Pseudonymizer(LatitudePseudonymizer.class)
	@Min(value = -90, message = Validations.numberTooSmall)
	@Max(value = 90, message = Validations.numberTooBig)
	private Double reportLat;
	@SensitiveData
	@Pseudonymizer(LongitudePseudonymizer.class)
	@Min(value = -180, message = Validations.numberTooSmall)
	@Max(value = 180, message = Validations.numberTooBig)
	private Double reportLon;

	private Float reportLatLonAccuracy;

	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community;
	private boolean multiDayContact;
	private Date firstContactDate;
	private Date lastContactDate;
	@HideForCountriesExcept
	private ContactIdentificationSource contactIdentificationSource;
	@HideForCountriesExcept
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String contactIdentificationSourceDetails;
	@HideForCountriesExcept
	private TracingApp tracingApp;
	@HideForCountriesExcept
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String tracingAppDetails;
	private ContactProximity contactProximity;
	@HideForCountriesExcept
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String contactProximityDetails;
	@HideForCountriesExcept
	@Diseases({
		Disease.CORONAVIRUS })
	private ContactCategory contactCategory;
	private ContactClassification contactClassification;
	private ContactStatus contactStatus;
	private FollowUpStatus followUpStatus;
	@SensitiveData
	@Size(max = CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String followUpComment;
	private Date followUpUntil;
	private boolean overwriteFollowUpUntil;
	@SensitiveData
	@Size(max = CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String description;
	private ContactRelation relationToCase;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String relationDescription;
	@HideForCountriesExcept(countries = {
		COUNTRY_CODE_GERMANY,
		COUNTRY_CODE_SWITZERLAND })
	@S2SIgnoreProperty(configProperty = SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_ID)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalID;
	@S2SIgnoreProperty(configProperty = SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_TOKEN)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalToken;
	@S2SIgnoreProperty(configProperty = SormasToSormasConfig.SORMAS2SORMAS_IGNORE_INTERNAL_TOKEN)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String internalToken;

	private boolean highPriority;
	private YesNoUnknown immunosuppressiveTherapyBasicDisease;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String immunosuppressiveTherapyBasicDiseaseDetails;
	private YesNoUnknown careForPeopleOver60;

	private QuarantineType quarantine;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String quarantineTypeDetails;
	private Date quarantineFrom;
	private Date quarantineTo;

	@NotNull(message = Validations.validPerson)
	@EmbeddedPersonalData
	private PersonReferenceDto person;

	@SensitiveData
	private UserReferenceDto contactOfficer;

	private CaseReferenceDto resultingCase; // read-only now, but editable long-term
	@SensitiveData
	private UserReferenceDto resultingCaseUser;

	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String quarantineHelpNeeded;
	@HideForCountriesExcept(countries = {
		COUNTRY_CODE_GERMANY,
		COUNTRY_CODE_SWITZERLAND })
	private boolean quarantineOrderedVerbally;
	@HideForCountriesExcept(countries = {
		COUNTRY_CODE_GERMANY,
		COUNTRY_CODE_SWITZERLAND })
	private boolean quarantineOrderedOfficialDocument;
	@HideForCountriesExcept(countries = {
		COUNTRY_CODE_GERMANY,
		COUNTRY_CODE_SWITZERLAND })
	private Date quarantineOrderedVerballyDate;
	@HideForCountriesExcept(countries = {
		COUNTRY_CODE_GERMANY,
		COUNTRY_CODE_SWITZERLAND })
	private Date quarantineOrderedOfficialDocumentDate;
	@HideForCountriesExcept
	private YesNoUnknown quarantineHomePossible;
	@HideForCountriesExcept
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String quarantineHomePossibleComment;
	@HideForCountriesExcept
	private YesNoUnknown quarantineHomeSupplyEnsured;
	@HideForCountriesExcept
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String quarantineHomeSupplyEnsuredComment;
	private boolean quarantineExtended;
	private boolean quarantineReduced;
	@HideForCountriesExcept(countries = {
		COUNTRY_CODE_GERMANY,
		COUNTRY_CODE_SWITZERLAND })
	private boolean quarantineOfficialOrderSent;
	@HideForCountriesExcept(countries = {
		COUNTRY_CODE_GERMANY,
		COUNTRY_CODE_SWITZERLAND })
	private Date quarantineOfficialOrderSentDate;
	@SensitiveData
	@S2SIgnoreProperty(configProperty = SormasToSormasConfig.SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String additionalDetails;
	@Valid
	private EpiDataDto epiData;
	@Valid
	private HealthConditionsDto healthConditions;
	private YesNoUnknown returningTraveler;

	@HideForCountriesExcept(countries = {
		COUNTRY_CODE_SWITZERLAND })
	private EndOfQuarantineReason endOfQuarantineReason;
	@HideForCountriesExcept(countries = {
		COUNTRY_CODE_SWITZERLAND })
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String endOfQuarantineReasonDetails;

	@HideForCountriesExcept
	private YesNoUnknown prohibitionToWork;
	@HideForCountriesExcept
	private Date prohibitionToWorkFrom;
	@HideForCountriesExcept
	private Date prohibitionToWorkUntil;

	@HideForCountriesExcept
	private DistrictReferenceDto reportingDistrict;

	private Date followUpStatusChangeDate;
	private UserReferenceDto followUpStatusChangeUser;

	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.MEASLES,
		Disease.POLIO,
		Disease.YELLOW_FEVER,
		Disease.CSM,
		Disease.RABIES,
		Disease.UNSPECIFIED_VHF,
		Disease.ANTHRAX,
		Disease.CORONAVIRUS,
		Disease.OTHER })
	@Outbreaks
	private VaccinationStatus vaccinationStatus;

	private Date previousQuarantineTo;
	@SensitiveData
	@Size(max = CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String quarantineChangeComment;
	private boolean deleted;
	private DeletionReason deletionReason;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String otherDeletionReason;

	public static ContactDto build() {
		final ContactDto contact = new ContactDto();
		contact.setUuid(DataHelper.createUuid());
		contact.setPerson(new PersonReferenceDto(DataHelper.createUuid()));
		contact.setReportDateTime(new Date());
		contact.setContactClassification(ContactClassification.UNCONFIRMED);
		contact.setContactStatus(ContactStatus.ACTIVE);
		contact.setEpiData(EpiDataDto.build());
		contact.setHealthConditions(HealthConditionsDto.build());

		return contact;
	}

	public static ContactDto build(EventParticipantDto eventParticipant) {
		final ContactDto contact = build();
		contact.setPerson(eventParticipant.getPerson().toReference());

		return contact;
	}

	public static ContactDto build(PersonDto person) {
		final ContactDto contact = build();
		contact.setPerson(person.toReference());

		return contact;
	}

	public static ContactDto build(CaseDataDto caze) {
		return build(caze.toReference(), caze.getDisease(), caze.getDiseaseDetails(), caze.getDiseaseVariant());
	}

	public static ContactDto build(CaseReferenceDto caze, Disease disease, String diseaseDetails, DiseaseVariant diseaseVariant) {
		final ContactDto contact = build();
		contact.assignCase(caze, disease, diseaseDetails, diseaseVariant);

		return contact;
	}

	public void assignCase(CaseDataDto caze) {
		assignCase(caze.toReference(), caze.getDisease(), caze.getDiseaseDetails(), caze.getDiseaseVariant());
	}

	public void assignCase(CaseReferenceDto caze, Disease disease, String diseaseDetails, DiseaseVariant diseaseVariant) {
		setCaze(caze);
		setDisease(disease);
		setDiseaseDetails(diseaseDetails);
		setDiseaseVariant(diseaseVariant);
	}

	public PersonReferenceDto getPerson() {
		return person;
	}

	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public void setCaze(CaseReferenceDto caze) {
		this.caze = caze;
	}

	public Date getReportDateTime() {
		return reportDateTime;
	}

	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}

	@Override
	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	@Override
	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}

	public boolean isMultiDayContact() {
		return multiDayContact;
	}

	public void setMultiDayContact(boolean multiDayContact) {
		this.multiDayContact = multiDayContact;
	}

	public Date getFirstContactDate() {
		return firstContactDate;
	}

	public void setFirstContactDate(Date firstContactDate) {
		this.firstContactDate = firstContactDate;
	}

	public Date getLastContactDate() {
		return lastContactDate;
	}

	public void setLastContactDate(Date lastContactDate) {
		this.lastContactDate = lastContactDate;
	}

	public ContactIdentificationSource getContactIdentificationSource() {
		return contactIdentificationSource;
	}

	public void setContactIdentificationSource(ContactIdentificationSource contactIdentificationSource) {
		this.contactIdentificationSource = contactIdentificationSource;
	}

	public String getContactIdentificationSourceDetails() {
		return contactIdentificationSourceDetails;
	}

	public void setContactIdentificationSourceDetails(String contactIdentificationSourceDetails) {
		this.contactIdentificationSourceDetails = contactIdentificationSourceDetails;
	}

	public TracingApp getTracingApp() {
		return tracingApp;
	}

	public void setTracingApp(TracingApp tracingApp) {
		this.tracingApp = tracingApp;
	}

	public String getTracingAppDetails() {
		return tracingAppDetails;
	}

	public void setTracingAppDetails(String tracingAppDetails) {
		this.tracingAppDetails = tracingAppDetails;
	}

	public ContactProximity getContactProximity() {
		return contactProximity;
	}

	public void setContactProximity(ContactProximity contactProximity) {
		this.contactProximity = contactProximity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UserReferenceDto getContactOfficer() {
		return contactOfficer;
	}

	public void setContactOfficer(UserReferenceDto contactOfficer) {
		this.contactOfficer = contactOfficer;
	}

	public FollowUpStatus getFollowUpStatus() {
		return followUpStatus;
	}

	public void setFollowUpStatus(FollowUpStatus followUpStatus) {
		this.followUpStatus = followUpStatus;
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

	public void setContactStatus(ContactStatus contactStatus) {
		this.contactStatus = contactStatus;
	}

	public Date getFollowUpUntil() {
		return followUpUntil;
	}

	public void setFollowUpUntil(Date followUpUntil) {
		this.followUpUntil = followUpUntil;
	}

	public ContactRelation getRelationToCase() {
		return relationToCase;
	}

	public void setRelationToCase(ContactRelation relationToCase) {
		this.relationToCase = relationToCase;
	}

	public String getRelationDescription() {
		return relationDescription;
	}

	public void setRelationDescription(String relationDescription) {
		this.relationDescription = relationDescription;
	}

	public CaseReferenceDto getResultingCase() {
		return resultingCase;
	}

	public void setResultingCase(CaseReferenceDto resultingCase) {
		this.resultingCase = resultingCase;
	}

	public Double getReportLat() {
		return reportLat;
	}

	public void setReportLat(Double reportLat) {
		this.reportLat = reportLat;
	}

	public Double getReportLon() {
		return reportLon;
	}

	public void setReportLon(Double reportLon) {
		this.reportLon = reportLon;
	}

	public String getFollowUpComment() {
		return followUpComment;
	}

	public void setFollowUpComment(String followUpComment) {
		this.followUpComment = followUpComment;
	}

	public void addToFollowUpComment(String comment) {
		followUpComment = DataHelper.joinStrings("\n", followUpComment, comment);
	}

	public Float getReportLatLonAccuracy() {
		return reportLatLonAccuracy;
	}

	public void setReportLatLonAccuracy(Float reportLatLonAccuracy) {
		this.reportLatLonAccuracy = reportLatLonAccuracy;
	}

	public ContactReferenceDto toReference() {
		return new ContactReferenceDto(getUuid(), getPerson().getFirstName(), getPerson().getLastName(), getCaze());
	}

	public UserReferenceDto getResultingCaseUser() {
		return resultingCaseUser;
	}

	public void setResultingCaseUser(UserReferenceDto resultingCaseUser) {
		this.resultingCaseUser = resultingCaseUser;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public String getDiseaseDetails() {
		return diseaseDetails;
	}

	public void setDiseaseDetails(String diseaseDetails) {
		this.diseaseDetails = diseaseDetails;
	}

	public String getExternalID() {
		return externalID;
	}

	public void setExternalID(String externalID) {
		this.externalID = externalID;
	}

	public String getExternalToken() {
		return externalToken;
	}

	public void setExternalToken(String externalToken) {
		this.externalToken = externalToken;
	}

	public String getInternalToken() {
		return internalToken;
	}

	public void setInternalToken(String internalToken) {
		this.internalToken = internalToken;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public CommunityReferenceDto getCommunity() {
		return community;
	}

	public void setCommunity(CommunityReferenceDto community) {
		this.community = community;
	}

	public boolean isHighPriority() {
		return highPriority;
	}

	public void setHighPriority(boolean highPriority) {
		this.highPriority = highPriority;
	}

	public YesNoUnknown getImmunosuppressiveTherapyBasicDisease() {
		return immunosuppressiveTherapyBasicDisease;
	}

	public void setImmunosuppressiveTherapyBasicDisease(YesNoUnknown immunosuppressiveTherapyBasicDisease) {
		this.immunosuppressiveTherapyBasicDisease = immunosuppressiveTherapyBasicDisease;
	}

	public String getImmunosuppressiveTherapyBasicDiseaseDetails() {
		return immunosuppressiveTherapyBasicDiseaseDetails;
	}

	public void setImmunosuppressiveTherapyBasicDiseaseDetails(String immunosuppressiveTherapyBasicDiseaseDetails) {
		this.immunosuppressiveTherapyBasicDiseaseDetails = immunosuppressiveTherapyBasicDiseaseDetails;
	}

	public YesNoUnknown getCareForPeopleOver60() {
		return careForPeopleOver60;
	}

	public void setCareForPeopleOver60(YesNoUnknown careForPeopleOver60) {
		this.careForPeopleOver60 = careForPeopleOver60;
	}

	public QuarantineType getQuarantine() {
		return quarantine;
	}

	public void setQuarantine(QuarantineType quarantine) {
		this.quarantine = quarantine;
	}

	public String getQuarantineTypeDetails() {
		return quarantineTypeDetails;
	}

	public void setQuarantineTypeDetails(String quarantineTypeDetails) {
		this.quarantineTypeDetails = quarantineTypeDetails;
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

	public String getCaseIdExternalSystem() {
		return caseIdExternalSystem;
	}

	public void setCaseIdExternalSystem(String caseIdExternalSystem) {
		this.caseIdExternalSystem = caseIdExternalSystem;
	}

	public String getCaseOrEventInformation() {
		return caseOrEventInformation;
	}

	public void setCaseOrEventInformation(String caseOrEventInformation) {
		this.caseOrEventInformation = caseOrEventInformation;
	}

	public boolean isOverwriteFollowUpUntil() {
		return overwriteFollowUpUntil;
	}

	public void setOverwriteFollowUpUntil(boolean overwriteFollowUpUntil) {
		this.overwriteFollowUpUntil = overwriteFollowUpUntil;
	}

	public String getContactProximityDetails() {
		return contactProximityDetails;
	}

	public void setContactProximityDetails(String contactProximityDetails) {
		this.contactProximityDetails = contactProximityDetails;
	}

	public ContactCategory getContactCategory() {
		return contactCategory;
	}

	public void setContactCategory(ContactCategory contactCategory) {
		this.contactCategory = contactCategory;
	}

	public String getQuarantineHelpNeeded() {
		return quarantineHelpNeeded;
	}

	public void setQuarantineHelpNeeded(String quarantineHelpNeeded) {
		this.quarantineHelpNeeded = quarantineHelpNeeded;
	}

	public boolean isQuarantineOrderedVerbally() {
		return quarantineOrderedVerbally;
	}

	public void setQuarantineOrderedVerbally(boolean quarantineOrderedVerbally) {
		this.quarantineOrderedVerbally = quarantineOrderedVerbally;
	}

	public boolean isQuarantineOrderedOfficialDocument() {
		return quarantineOrderedOfficialDocument;
	}

	public void setQuarantineOrderedOfficialDocument(boolean quarantineOrderedOfficialDocument) {
		this.quarantineOrderedOfficialDocument = quarantineOrderedOfficialDocument;
	}

	public Date getQuarantineOrderedVerballyDate() {
		return quarantineOrderedVerballyDate;
	}

	public void setQuarantineOrderedVerballyDate(Date quarantineOrderedVerballyDate) {
		this.quarantineOrderedVerballyDate = quarantineOrderedVerballyDate;
	}

	public Date getQuarantineOrderedOfficialDocumentDate() {
		return quarantineOrderedOfficialDocumentDate;
	}

	public void setQuarantineOrderedOfficialDocumentDate(Date quarantineOrderedOfficialDocumentDate) {
		this.quarantineOrderedOfficialDocumentDate = quarantineOrderedOfficialDocumentDate;
	}

	public YesNoUnknown getQuarantineHomePossible() {
		return quarantineHomePossible;
	}

	public void setQuarantineHomePossible(YesNoUnknown quarantineHomePossible) {
		this.quarantineHomePossible = quarantineHomePossible;
	}

	public String getQuarantineHomePossibleComment() {
		return quarantineHomePossibleComment;
	}

	public void setQuarantineHomePossibleComment(String quarantineHomePossibleComment) {
		this.quarantineHomePossibleComment = quarantineHomePossibleComment;
	}

	public YesNoUnknown getQuarantineHomeSupplyEnsured() {
		return quarantineHomeSupplyEnsured;
	}

	public void setQuarantineHomeSupplyEnsured(YesNoUnknown quarantineHomeSupplyEnsured) {
		this.quarantineHomeSupplyEnsured = quarantineHomeSupplyEnsured;
	}

	public String getQuarantineHomeSupplyEnsuredComment() {
		return quarantineHomeSupplyEnsuredComment;
	}

	public void setQuarantineHomeSupplyEnsuredComment(String quarantineHomeSupplyEnsuredComment) {
		this.quarantineHomeSupplyEnsuredComment = quarantineHomeSupplyEnsuredComment;
	}

	public boolean isQuarantineExtended() {
		return quarantineExtended;
	}

	public void setQuarantineExtended(boolean quarantineExtended) {
		this.quarantineExtended = quarantineExtended;
	}

	public boolean isQuarantineReduced() {
		return quarantineReduced;
	}

	public void setQuarantineReduced(boolean quarantineReduced) {
		this.quarantineReduced = quarantineReduced;
	}

	public boolean isQuarantineOfficialOrderSent() {
		return quarantineOfficialOrderSent;
	}

	public void setQuarantineOfficialOrderSent(boolean quarantineOfficialOrderSent) {
		this.quarantineOfficialOrderSent = quarantineOfficialOrderSent;
	}

	public Date getQuarantineOfficialOrderSentDate() {
		return quarantineOfficialOrderSentDate;
	}

	public void setQuarantineOfficialOrderSentDate(Date quarantineOfficialOrderSentDate) {
		this.quarantineOfficialOrderSentDate = quarantineOfficialOrderSentDate;
	}

	public String getAdditionalDetails() {
		return additionalDetails;
	}

	public void setAdditionalDetails(String additionalDetails) {
		this.additionalDetails = additionalDetails;
	}

	public EpiDataDto getEpiData() {
		return epiData;
	}

	public void setEpiData(EpiDataDto epiData) {
		this.epiData = epiData;
	}

	public HealthConditionsDto getHealthConditions() {
		return healthConditions;
	}

	public void setHealthConditions(HealthConditionsDto healthConditions) {
		this.healthConditions = healthConditions;
	}

	public EndOfQuarantineReason getEndOfQuarantineReason() {
		return endOfQuarantineReason;
	}

	public void setEndOfQuarantineReason(EndOfQuarantineReason endOfQuarantineReason) {
		this.endOfQuarantineReason = endOfQuarantineReason;
	}

	public String getEndOfQuarantineReasonDetails() {
		return endOfQuarantineReasonDetails;
	}

	public void setEndOfQuarantineReasonDetails(String endOfQuarantineReasonDetails) {
		this.endOfQuarantineReasonDetails = endOfQuarantineReasonDetails;
	}

	public YesNoUnknown getProhibitionToWork() {
		return prohibitionToWork;
	}

	public void setProhibitionToWork(YesNoUnknown prohibitionToWork) {
		this.prohibitionToWork = prohibitionToWork;
	}

	public Date getProhibitionToWorkFrom() {
		return prohibitionToWorkFrom;
	}

	public void setProhibitionToWorkFrom(Date prohibitionToWorkFrom) {
		this.prohibitionToWorkFrom = prohibitionToWorkFrom;
	}

	public Date getProhibitionToWorkUntil() {
		return prohibitionToWorkUntil;
	}

	public void setProhibitionToWorkUntil(Date prohibitionToWorkUntil) {
		this.prohibitionToWorkUntil = prohibitionToWorkUntil;
	}

	public DistrictReferenceDto getReportingDistrict() {
		return reportingDistrict;
	}

	public void setReportingDistrict(DistrictReferenceDto reportingDistrict) {
		this.reportingDistrict = reportingDistrict;
	}

	public YesNoUnknown getReturningTraveler() {
		return returningTraveler;
	}

	public void setReturningTraveler(YesNoUnknown returningTraveler) {
		this.returningTraveler = returningTraveler;
	}

	public Date getFollowUpStatusChangeDate() {
		return followUpStatusChangeDate;
	}

	public void setFollowUpStatusChangeDate(Date followUpStatusChangeDate) {
		this.followUpStatusChangeDate = followUpStatusChangeDate;
	}

	public UserReferenceDto getFollowUpStatusChangeUser() {
		return followUpStatusChangeUser;
	}

	public void setFollowUpStatusChangeUser(UserReferenceDto followUpStatusChangeUser) {
		this.followUpStatusChangeUser = followUpStatusChangeUser;
	}

	public DiseaseVariant getDiseaseVariant() {
		return diseaseVariant;
	}

	public void setDiseaseVariant(DiseaseVariant diseaseVariant) {
		this.diseaseVariant = diseaseVariant;
	}

	public VaccinationStatus getVaccinationStatus() {
		return vaccinationStatus;
	}

	public void setVaccinationStatus(VaccinationStatus vaccinationStatus) {
		this.vaccinationStatus = vaccinationStatus;
	}

	public Date getPreviousQuarantineTo() {
		return previousQuarantineTo;
	}

	public void setPreviousQuarantineTo(Date previousQuarantineTo) {
		this.previousQuarantineTo = previousQuarantineTo;
	}

	public String getQuarantineChangeComment() {
		return quarantineChangeComment;
	}

	public void setQuarantineChangeComment(String quarantineChangeComment) {
		this.quarantineChangeComment = quarantineChangeComment;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public DeletionReason getDeletionReason() {
		return deletionReason;
	}

	public void setDeletionReason(DeletionReason deletionReason) {
		this.deletionReason = deletionReason;
	}

	public String getOtherDeletionReason() {
		return otherDeletionReason;
	}

	public void setOtherDeletionReason(String otherDeletionReason) {
		this.otherDeletionReason = otherDeletionReason;
	}
}
