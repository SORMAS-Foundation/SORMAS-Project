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

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseExportType;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.caze.VaccineManufacturer;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.importexport.ExportEntity;
import de.symeda.sormas.api.importexport.ExportGroup;
import de.symeda.sormas.api.importexport.ExportGroupType;
import de.symeda.sormas.api.importexport.ExportProperty;
import de.symeda.sormas.api.importexport.ExportTarget;
import de.symeda.sormas.api.infrastructure.facility.FacilityHelper;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.ArmedForcesRelationType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Salutation;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.person.SymptomJournalStatus;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.PostalCodePseudonymizer;
import de.symeda.sormas.api.uuid.AbstractUuidDto;
import de.symeda.sormas.api.vaccination.VaccinationDto;

@ExportEntity(ContactDto.class)
public class ContactExportDto extends AbstractUuidDto {

	private static final long serialVersionUID = 2054231712903661096L;

	public static final String I18N_PREFIX = "ContactExport";
	public static final String SOURECE_CASE_ID = "sourceCaseUuid";
	public static final String QUARANTINE_INFORMATION = "quarantineInformation";
	public static final String FACILITY = "facility";
	public static final String NUMBER_OF_VISITS = "numberOfVisits";
	public static final String LAST_COOPERATIVE_VISIT_SYMPTOMATIC = "lastCooperativeVisitSymptomatic";
	public static final String LAST_COOPERATIVE_VISIT_DATE = "lastCooperativeVisitDate";
	public static final String LAST_COOPERATIVE_VISIT_SYMPTOMS = "lastCooperativeVisitSymptoms";
	public static final String TRAVELED = "traveled";
	public static final String TRAVEL_HISTORY = "travelHistory";
	public static final String BURIAL_ATTENDED = "burialAttended";
	public static final String LATEST_EVENT_ID = "latestEventId";
	public static final String LATEST_EVENT_TITLE = "latestEventTitle";
	public static final String EVENT_COUNT = "eventCount";
	public static final String BIRTH_DATE = "birthdate";
	public static final String COMPLETENESS = "completeness";

	private long id;
	private long personId;
	private String sourceCaseUuid;
	private CaseClassification caseClassification;
	private Disease disease;
	private String diseaseDetails;
	private ContactClassification contactClassification;
	private Boolean multiDayContact;
	private Date firstContactDate;
	private Date lastContactDate;
	private Date creationDate;
	private String personUuid;
	@PersonalData
	private String firstName;
	@PersonalData
	private String lastName;
	@SensitiveData
	private Salutation salutation;
	@SensitiveData
	private String otherSalutation;
	private Sex sex;
	@EmbeddedPersonalData
	private BirthDateDto birthdate;
	private String approximateAge;
	private Date reportDate;
	private ContactIdentificationSource contactIdentificationSource;
	@SensitiveData
	private String contactIdentificationSourceDetails;
	private TracingApp tracingApp;
	@SensitiveData
	private String tracingAppDetails;
	private ContactProximity contactProximity;
	private ContactStatus contactStatus;
	private Float completeness;
	private FollowUpStatus followUpStatus;
	private Date followUpUntil;
	private PresentCondition presentCondition;
	private Date deathDate;
	private String addressRegion;
	private String addressDistrict;
	@PersonalData
	@SensitiveData
	private String addressCommunity;
	@PersonalData
	@SensitiveData
	private String city;
	@PersonalData
	@SensitiveData
	private String street;
	@PersonalData
	@SensitiveData
	private String houseNumber;
	@PersonalData
	@SensitiveData
	private String additionalInformation;
	@PersonalData
	@SensitiveData
	@Pseudonymizer(PostalCodePseudonymizer.class)
	private String postalCode;
	@PersonalData
	@SensitiveData
	private String facility;
	@PersonalData
	@SensitiveData
	private String facilityDetails;
	@SensitiveData
	private String phone;
	@SensitiveData
	private String phoneOwner;
	@SensitiveData
	private String emailAddress;
	@SensitiveData
	private String otherContactDetails;
	private OccupationType occupationType;
	@SensitiveData
	private String occupationDetails;
	private ArmedForcesRelationType armedForcesRelationType;
	private int numberOfVisits;
	private YesNoUnknown lastCooperativeVisitSymptomatic;
	private Date lastCooperativeVisitDate;
	private String lastCooperativeVisitSymptoms;
	private String region;
	private String district;
	private String community;

	private QuarantineType quarantine;
	private String quarantineTypeDetails;
	private Date quarantineFrom;
	private Date quarantineTo;
	private Date previousQuarantineTo;
	@SensitiveData
	private String quarantineChangeComment;
	@SensitiveData
	private String quarantineHelpNeeded;
	private long epiDataId;
	private boolean traveled;
	private String travelHistory;
	private boolean burialAttended;
	private YesNoUnknown contactWithSourceCaseKnown;

	private boolean quarantineOrderedVerbally;
	private boolean quarantineOrderedOfficialDocument;
	private Date quarantineOrderedVerballyDate;
	private Date quarantineOrderedOfficialDocumentDate;
	private boolean quarantineExtended;
	private boolean quarantineReduced;
	private boolean quarantineOfficialOrderSent;
	private Date quarantineOfficialOrderSentDate;

	private YesNoUnknown prohibitionToWork;
	private Date prohibitionToWorkFrom;
	private Date prohibitionToWorkUntil;

	private YesNoUnknown returningTraveler;

	private VaccinationStatus vaccinationStatus;
	private String numberOfDoses;
	private VaccinationInfoSource vaccinationInfoSource;
	private Date firstVaccinationDate;
	private Date lastVaccinationDate;
	private Vaccine vaccineName;
	private String otherVaccineName;
	private VaccineManufacturer vaccineManufacturer;
	private String otherVaccineManufacturer;
	private String vaccineInn;
	private String vaccineBatchNumber;
	private String vaccineUniiCode;
	private String vaccineAtcCode;

	private Long eventCount;
	private String latestEventId;
	private String latestEventTitle;
	private String externalID;
	private String externalToken;
	private String internalToken;

	@PersonalData
	@SensitiveData
	private String birthName;
	private String birthCountry;
	private String citizenship;

	private String reportingDistrict;

	private SymptomJournalStatus symptomJournalStatus;

	private Long reportingUserId;
	private Long followUpStatusChangeUserId;

	private String reportingUserName;
	private String reportingUserRoles;
	private String followUpStatusChangeUserName;
	private String followUpStatusChangeUserRoles;

	private Boolean isInJurisdiction;

	//@formatter:off
	public ContactExportDto(long id, long personId, String uuid, String sourceCaseUuid, CaseClassification caseClassification, Disease disease, String diseaseDetails,
							ContactClassification contactClassification, Boolean multiDayContact, Date firstContactDate, Date lastContactDate, Date creationDate,
							String personUuid, String firstName, String lastName,
							Salutation salutation, String otherSalutation, Sex sex,
							Integer birthdateDD, Integer birthdateMM, Integer birthdateYYYY,
							Integer approximateAge, ApproximateAgeType approximateAgeType, Date reportDate, ContactIdentificationSource contactIdentificationSource,
							String contactIdentificationSourceDetails, TracingApp tracingApp, String tracingAppDetails, ContactProximity contactProximity,
							ContactStatus contactStatus, Float completeness, FollowUpStatus followUpStatus, Date followUpUntil,
							QuarantineType quarantine, String quarantineTypeDetails, Date quarantineFrom, Date quarantineTo, String quarantineHelpNeeded,
							boolean quarantineOrderedVerbally, boolean quarantineOrderedOfficialDocument, Date quarantineOrderedVerballyDate, Date quarantineOrderedOfficialDocumentDate,
							boolean quarantineExtended, boolean quarantineReduced, boolean quarantineOfficialOrderSent, Date quarantineOfficialOrderSentDate,
							YesNoUnknown prohibitionToWork, Date prohibitionToWorkFrom, Date prohibitionToWorkUntil,
							PresentCondition presentCondition, Date deathDate,
							String addressRegion, String addressDistrict, String addressCommunity, String city, String street, String houseNumber, String additionalInformation, String postalCode,
							String facility, String facilityUuid, String facilityDetails,
							String phone, String phoneOwner, String emailAddress, String otherContactDetails, OccupationType occupationType, String occupationDetails, ArmedForcesRelationType armedForcesRelationType,
							String region, String district, String community,
							long epiDataId, YesNoUnknown contactWithSourceCaseKnown, YesNoUnknown returningTraveler,
							VaccinationStatus vaccinationStatus, String externalID, String externalToken, String internalToken,
							String birthName, String birthCountryIsoCode, String birthCountryName, String citizenshipIsoCode, String citizenshipCountryName,
							String reportingDistrict,
							SymptomJournalStatus symptomJournalStatus,
							// users
							Long reportingUserId, Long followUpStatusChangeUserId,
							Date previousQuarantineTo, String quarantineChangeComment,
							boolean isInJurisdiction
	) {
	//@formatter:on
		super(uuid);
		this.id = id;
		this.personId = personId;
		this.sourceCaseUuid = sourceCaseUuid;
		this.caseClassification = caseClassification;
		this.disease = disease;
		this.diseaseDetails = diseaseDetails;
		this.contactClassification = contactClassification;
		this.multiDayContact = multiDayContact;
		this.firstContactDate = firstContactDate;
		this.lastContactDate = lastContactDate;
		this.creationDate = creationDate;
		this.personUuid = personUuid;
		this.firstName = firstName;
		this.lastName = lastName;
		this.salutation = salutation;
		this.otherSalutation = otherSalutation;
		this.sex = sex;
		this.birthdate = new BirthDateDto(birthdateDD, birthdateMM, birthdateYYYY);
		this.approximateAge = ApproximateAgeHelper.formatApproximateAge(approximateAge, approximateAgeType);
		this.reportDate = reportDate;
		this.contactIdentificationSource = contactIdentificationSource;
		this.contactIdentificationSourceDetails = contactIdentificationSourceDetails;
		this.tracingApp = tracingApp;
		this.tracingAppDetails = tracingAppDetails;
		this.contactProximity = contactProximity;
		this.contactStatus = contactStatus;
		this.completeness = completeness;
		this.followUpStatus = followUpStatus;
		this.followUpUntil = followUpUntil;
		this.quarantine = quarantine;
		this.quarantineTypeDetails = quarantineTypeDetails;
		this.quarantineFrom = quarantineFrom;
		this.quarantineTo = quarantineTo;
		this.quarantineHelpNeeded = quarantineHelpNeeded;
		this.quarantineOrderedVerbally = quarantineOrderedVerbally;
		this.quarantineOrderedOfficialDocument = quarantineOrderedOfficialDocument;
		this.quarantineOrderedVerballyDate = quarantineOrderedVerballyDate;
		this.quarantineOrderedOfficialDocumentDate = quarantineOrderedOfficialDocumentDate;
		this.quarantineExtended = quarantineExtended;
		this.quarantineReduced = quarantineReduced;
		this.quarantineOfficialOrderSent = quarantineOfficialOrderSent;
		this.quarantineOfficialOrderSentDate = quarantineOfficialOrderSentDate;
		this.prohibitionToWork = prohibitionToWork;
		this.prohibitionToWorkFrom = prohibitionToWorkFrom;
		this.prohibitionToWorkUntil = prohibitionToWorkUntil;
		this.presentCondition = presentCondition;
		this.deathDate = deathDate;
		this.addressRegion = addressRegion;
		this.addressDistrict = addressDistrict;
		this.addressCommunity = addressCommunity;
		this.city = city;
		this.street = street;
		this.houseNumber = houseNumber;
		this.additionalInformation = additionalInformation;
		this.postalCode = postalCode;
		this.facility = FacilityHelper.buildFacilityString(facilityUuid, facility);
		this.facilityDetails = facilityDetails;
		this.phone = phone;
		this.phoneOwner = phoneOwner;
		this.emailAddress = emailAddress;
//		for (String otherContactDetail : otherContactDetails) {
//			this.otherContactDetails += this.otherContactDetails.equals("") ? otherContactDetail : ", " + otherContactDetail;
//		}
		this.otherContactDetails = otherContactDetails;
		this.occupationType = occupationType;
		this.occupationDetails = occupationDetails;
		this.armedForcesRelationType = armedForcesRelationType;
		this.region = region;
		this.district = district;
		this.community = community;
		this.epiDataId = epiDataId;
		this.contactWithSourceCaseKnown = contactWithSourceCaseKnown;
		this.returningTraveler = returningTraveler;

		this.vaccinationStatus = vaccinationStatus;
		this.externalID = externalID;
		this.externalToken = externalToken;
		this.internalToken = internalToken;
		this.birthName = birthName;
		this.birthCountry = I18nProperties.getCountryName(birthCountryIsoCode, birthCountryName);
		this.citizenship = I18nProperties.getCountryName(citizenshipIsoCode, citizenshipCountryName);
		this.reportingDistrict = reportingDistrict;

		this.symptomJournalStatus = symptomJournalStatus;

		this.reportingUserId = reportingUserId;
		this.followUpStatusChangeUserId = followUpStatusChangeUserId;

		this.previousQuarantineTo = previousQuarantineTo;
		this.quarantineChangeComment = quarantineChangeComment;

		this.isInJurisdiction = isInJurisdiction;
	}

	public ContactReferenceDto toReference() {
		return new ContactReferenceDto(getUuid());
	}

	public long getId() {
		return id;
	}

	public long getPersonId() {
		return personId;
	}

	public long getEpiDataId() {
		return epiDataId;
	}

	@Order(0)
	@ExportProperty(ContactDto.UUID)
	@ExportGroup(ExportGroupType.CORE)
	@Override
	public String getUuid() {
		return super.getUuid();
	}

	@Order(1)
	@ExportProperty(ContactDto.EXTERNAL_ID)
	@ExportGroup(ExportGroupType.CORE)
	public String getExternalID() {
		return externalID;
	}

	@Order(2)
	@ExportProperty(ContactExportDto.SOURECE_CASE_ID)
	@ExportGroup(ExportGroupType.CORE)
	public String getSourceCaseUuid() {
		return sourceCaseUuid;
	}

	@Order(3)
	@ExportProperty(CaseDataDto.CASE_CLASSIFICATION)
	@ExportGroup(ExportGroupType.CORE)
	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	@Order(4)
	@ExportProperty(ContactDto.DISEASE)
	@ExportGroup(ExportGroupType.CORE)
	public Disease getDisease() {
		return disease;
	}

	@Order(5)
	@ExportProperty(ContactDto.DISEASE_DETAILS)
	@ExportGroup(ExportGroupType.CORE)
	public String getDiseaseDetails() {
		return diseaseDetails;
	}

	@Order(6)
	@ExportProperty(ContactDto.CONTACT_CLASSIFICATION)
	@ExportGroup(ExportGroupType.CORE)
	public ContactClassification getContactClassification() {
		return contactClassification;
	}

	@Order(7)
	@ExportProperty(ContactDto.MULTI_DAY_CONTACT)
	@ExportGroup(ExportGroupType.CORE)
	public Boolean getMultiDayContact() {
		return multiDayContact;
	}

	@Order(8)
	@ExportProperty(ContactDto.FIRST_CONTACT_DATE)
	@ExportGroup(ExportGroupType.CORE)
	public Date getFirstContactDate() {
		return firstContactDate;
	}

	@Order(9)
	@ExportProperty(ContactDto.LAST_CONTACT_DATE)
	@ExportGroup(ExportGroupType.CORE)
	public Date getLastContactDate() {
		return lastContactDate;
	}

	@Order(10)
	@ExportProperty(ContactDto.CREATION_DATE)
	@ExportGroup(ExportGroupType.CORE)
	public Date getCreationDate() {
		return creationDate;
	}

	@Order(11)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.UUID })
	public String getPersonUuid() {
		return personUuid;
	}

	@Order(12)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.FIRST_NAME })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getFirstName() {
		return firstName;
	}

	@Order(13)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.LAST_NAME })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getLastName() {
		return lastName;
	}

	@Order(14)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.SALUTATION })
	@ExportGroup(ExportGroupType.PERSON)
	@HideForCountriesExcept
	public Salutation getSalutation() {
		return salutation;
	}

	@Order(15)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.OTHER_SALUTATION })
	@ExportGroup(ExportGroupType.PERSON)
	@HideForCountriesExcept
	public String getOtherSalutation() {
		return otherSalutation;
	}

	@Order(16)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.SEX })
	@ExportGroup(ExportGroupType.PERSON)
	public Sex getSex() {
		return sex;
	}

	@Order(17)
	@ExportProperty(BIRTH_DATE)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public BirthDateDto getBirthdate() {
		return birthdate;
	}

	@Order(18)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.APPROXIMATE_AGE })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getApproximateAge() {
		return approximateAge;
	}

	@Order(19)
	@ExportProperty(ContactDto.REPORT_DATE_TIME)
	@ExportGroup(ExportGroupType.CORE)
	public Date getReportDate() {
		return reportDate;
	}

	@Order(20)
	@ExportProperty(ContactDto.REGION)
	@ExportGroup(ExportGroupType.CORE)
	public String getRegion() {
		return region;
	}

	@Order(21)
	@ExportProperty(ContactDto.DISTRICT)
	@ExportGroup(ExportGroupType.CORE)
	public String getDistrict() {
		return district;
	}

	@Order(22)
	@ExportProperty(ContactDto.COMMUNITY)
	@ExportGroup(ExportGroupType.CORE)
	public String getCommunity() {
		return community;
	}

	@Order(23)
	@ExportProperty(ContactDto.CONTACT_IDENTIFICATION_SOURCE)
	@ExportGroup(ExportGroupType.CORE)
	public ContactIdentificationSource getContactIdentificationSource() {
		return contactIdentificationSource;
	}

	@Order(24)
	@ExportProperty(ContactDto.CONTACT_IDENTIFICATION_SOURCE_DETAILS)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getContactIdentificationSourceDetails() {
		return contactIdentificationSourceDetails;
	}

	@Order(25)
	@ExportProperty(ContactDto.TRACING_APP)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public TracingApp getTracingApp() {
		return tracingApp;
	}

	@Order(26)
	@ExportProperty(ContactDto.TRACING_APP_DETAILS)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getTracingAppDetails() {
		return tracingAppDetails;
	}

	@Order(27)
	@ExportProperty(ContactDto.CONTACT_PROXIMITY)
	@ExportGroup(ExportGroupType.CORE)
	public ContactProximity getContactProximity() {
		return contactProximity;
	}

	@Order(28)
	@ExportProperty(ContactDto.CONTACT_STATUS)
	@ExportGroup(ExportGroupType.CORE)
	public ContactStatus getContactStatus() {
		return contactStatus;
	}

	@Order(29)
	@ExportProperty(COMPLETENESS)
	@ExportGroup(ExportGroupType.CORE)
	public Float getCompleteness() {
		return completeness;
	}

	@Order(30)
	@ExportProperty(ContactDto.FOLLOW_UP_STATUS)
	@ExportGroup(ExportGroupType.FOLLOW_UP)
	public FollowUpStatus getFollowUpStatus() {
		return followUpStatus;
	}

	@Order(31)
	@ExportProperty(ContactDto.FOLLOW_UP_UNTIL)
	@ExportGroup(ExportGroupType.FOLLOW_UP)
	public Date getFollowUpUntil() {
		return followUpUntil;
	}

	@Order(32)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public QuarantineType getQuarantine() {
		return quarantine;
	}

	@Order(33)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getQuarantineTypeDetails() {
		return quarantineTypeDetails;
	}

	@Order(34)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public Date getQuarantineFrom() {
		return quarantineFrom;
	}

	@Order(35)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public Date getQuarantineTo() {
		return quarantineTo;
	}

	@Order(36)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public Date getPreviousQuarantineTo() {
		return previousQuarantineTo;
	}

	@Order(36)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getQuarantineChangeComment() {
		return quarantineChangeComment;
	}

	@Order(37)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getQuarantineHelpNeeded() {
		return quarantineHelpNeeded;
	}

	@Order(38)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept(countries = {
		"de",
		"ch" })
	public boolean isQuarantineOrderedVerbally() {
		return quarantineOrderedVerbally;
	}

	@Order(39)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept(countries = {
		"de",
		"ch" })
	public boolean isQuarantineOrderedOfficialDocument() {
		return quarantineOrderedOfficialDocument;
	}

	@Order(40)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept(countries = {
		"de",
		"ch" })
	public Date getQuarantineOrderedVerballyDate() {
		return quarantineOrderedVerballyDate;
	}

	@Order(41)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept(countries = {
		"de",
		"ch" })
	public Date getQuarantineOrderedOfficialDocumentDate() {
		return quarantineOrderedOfficialDocumentDate;
	}

	@Order(42)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept(countries = {
		"de",
		"ch" })
	public boolean isQuarantineOfficialOrderSent() {
		return quarantineOfficialOrderSent;
	}

	@Order(43)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept(countries = {
		"de",
		"ch" })
	public Date getQuarantineOfficialOrderSentDate() {
		return quarantineOfficialOrderSentDate;
	}

	@Order(44)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public boolean isQuarantineExtended() {
		return quarantineExtended;
	}

	@Order(45)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public boolean isQuarantineReduced() {
		return quarantineReduced;
	}

	@Order(46)
	@ExportProperty(value = ContactDto.PROHIBITION_TO_WORK, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public YesNoUnknown getProhibitionToWork() {
		return prohibitionToWork;
	}

	@Order(47)
	@ExportProperty(value = ContactDto.PROHIBITION_TO_WORK, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public Date getProhibitionToWorkFrom() {
		return prohibitionToWorkFrom;
	}

	@Order(48)
	@ExportProperty(value = ContactDto.PROHIBITION_TO_WORK, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public Date getProhibitionToWorkUntil() {
		return prohibitionToWorkUntil;
	}

	@Order(49)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.PRESENT_CONDITION })
	@ExportGroup(ExportGroupType.PERSON)
	public PresentCondition getPresentCondition() {
		return presentCondition;
	}

	@Order(50)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.DEATH_DATE })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public Date getDeathDate() {
		return deathDate;
	}

	@Order(53)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.REGION })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getAddressRegion() {
		return addressRegion;
	}

	@Order(54)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.DISTRICT })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getAddressDistrict() {
		return addressDistrict;
	}

	@Order(55)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.COMMUNITY })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getAddressCommunity() {
		return addressCommunity;
	}

	@Order(56)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.CITY })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getCity() {
		return city;
	}

	@Order(57)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.STREET })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getStreet() {
		return street;
	}

	@Order(58)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.HOUSE_NUMBER })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getHouseNumber() {
		return houseNumber;
	}

	@Order(59)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.ADDITIONAL_INFORMATION })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getAdditionalInformation() {
		return additionalInformation;
	}

	@Order(60)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.POSTAL_CODE })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getPostalCode() {
		return postalCode;
	}

	@Order(61)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.FACILITY })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getFacility() {
		return facility;
	}

	@Order(62)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.FACILITY_DETAILS })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getFacilityDetails() {
		return facilityDetails;
	}

	@Order(68)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.PHONE })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getPhone() {
		return phone;
	}

	@Order(69)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.PHONE_OWNER })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getPhoneOwner() {
		return phoneOwner;
	}

	@Order(70)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.EMAIL_ADDRESS })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getEmailAddress() {
		return emailAddress;
	}

	@Order(71)
	@ExportProperty({
		ContactDto.PERSON,
		PersonDto.OTHER_CONTACT_DETAILS })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getOtherContactDetails() {
		return otherContactDetails;
	}

	@Order(72)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.OCCUPATION_TYPE })
	@ExportGroup(ExportGroupType.PERSON)
	public OccupationType getOccupationType() {
		return occupationType;
	}

	@Order(73)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.OCCUPATION_DETAILS })
	@ExportGroup(ExportGroupType.PERSON)
	public String getOccupationDetails() {
		return occupationDetails;
	}

	@Order(74)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.ARMED_FORCES_RELATION_TYPE })
	@ExportGroup(ExportGroupType.PERSON)
	public ArmedForcesRelationType getArmedForcesRelationType() {
		return armedForcesRelationType;
	}

	@Order(78)
	@ExportProperty(NUMBER_OF_VISITS)
	@ExportGroup(ExportGroupType.FOLLOW_UP)
	public int getNumberOfVisits() {
		return numberOfVisits;
	}

	@Order(79)
	@ExportProperty(LAST_COOPERATIVE_VISIT_SYMPTOMATIC)
	@ExportGroup(ExportGroupType.FOLLOW_UP)
	public YesNoUnknown getLastCooperativeVisitSymptomatic() {
		return lastCooperativeVisitSymptomatic;
	}

	@Order(80)
	@ExportProperty(LAST_COOPERATIVE_VISIT_DATE)
	@ExportGroup(ExportGroupType.FOLLOW_UP)
	public Date getLastCooperativeVisitDate() {
		return lastCooperativeVisitDate;
	}

	@Order(81)
	@ExportProperty(LAST_COOPERATIVE_VISIT_SYMPTOMS)
	@ExportGroup(ExportGroupType.FOLLOW_UP)
	public String getLastCooperativeVisitSymptoms() {
		return lastCooperativeVisitSymptoms;
	}

	@Order(82)
	@ExportProperty(TRAVELED)
	@ExportGroup(ExportGroupType.EPIDEMIOLOGICAL)
	public boolean isTraveled() {
		return traveled;
	}

	public void setTraveled(boolean traveled) {
		this.traveled = traveled;
	}

	@Order(83)
	@ExportProperty(TRAVEL_HISTORY)
	@ExportGroup(ExportGroupType.EPIDEMIOLOGICAL)
	public String getTravelHistory() {
		return travelHistory;
	}

	public void setTravelHistory(String travelHistory) {
		this.travelHistory = travelHistory;
	}

	@Order(84)
	@ExportProperty(BURIAL_ATTENDED)
	@ExportGroup(ExportGroupType.EPIDEMIOLOGICAL)
	public boolean isBurialAttended() {
		return burialAttended;
	}

	public void setBurialAttended(boolean burialAttended) {
		this.burialAttended = burialAttended;
	}

	@Order(85)
	@ExportProperty({
		CaseDataDto.EPI_DATA,
		EpiDataDto.CONTACT_WITH_SOURCE_CASE_KNOWN })
	@ExportGroup(ExportGroupType.EPIDEMIOLOGICAL)
	public YesNoUnknown getContactWithSourceCaseKnown() {
		return contactWithSourceCaseKnown;
	}

	@Order(86)
	@ExportProperty(ContactDto.RETURNING_TRAVELER)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public YesNoUnknown getReturningTraveler() {
		return returningTraveler;
	}

	public void setReturningTraveler(YesNoUnknown returningTraveler) {
		this.returningTraveler = returningTraveler;
	}

	@Order(93)
	@ExportProperty(ContactDto.VACCINATION_STATUS)
	@ExportGroup(ExportGroupType.VACCINATION)
	public VaccinationStatus getVaccinationStatus() {
		return vaccinationStatus;
	}

	@Order(94)
	@ExportProperty(ImmunizationDto.NUMBER_OF_DOSES)
	@ExportGroup(ExportGroupType.VACCINATION)
	public String getNumberOfDoses() {
		return numberOfDoses;
	}

	@Order(95)
	@ExportProperty(VaccinationDto.VACCINATION_INFO_SOURCE)
	@ExportGroup(ExportGroupType.VACCINATION)
	public VaccinationInfoSource getVaccinationInfoSource() {
		return vaccinationInfoSource;
	}

	@Order(96)
	@ExportProperty(ImmunizationDto.FIRST_VACCINATION_DATE)
	@ExportGroup(ExportGroupType.VACCINATION)
	public Date getFirstVaccinationDate() {
		return firstVaccinationDate;
	}

	@Order(97)
	@ExportProperty(ImmunizationDto.LAST_VACCINATION_DATE)
	@ExportGroup(ExportGroupType.VACCINATION)
	public Date getLastVaccinationDate() {
		return lastVaccinationDate;
	}

	@Order(98)
	@ExportProperty(VaccinationDto.VACCINE_NAME)
	@ExportGroup(ExportGroupType.VACCINATION)
	public Vaccine getVaccineName() {
		return vaccineName;
	}

	@Order(99)
	@ExportProperty(VaccinationDto.OTHER_VACCINE_NAME)
	@ExportGroup(ExportGroupType.VACCINATION)
	public String getOtherVaccineName() {
		return otherVaccineName;
	}

	@Order(100)
	@ExportProperty(VaccinationDto.VACCINE_MANUFACTURER)
	@ExportGroup(ExportGroupType.VACCINATION)
	public VaccineManufacturer getVaccineManufacturer() {
		return vaccineManufacturer;
	}

	@Order(101)
	@ExportProperty(VaccinationDto.OTHER_VACCINE_MANUFACTURER)
	@ExportGroup(ExportGroupType.VACCINATION)
	public String getOtherVaccineManufacturer() {
		return otherVaccineManufacturer;
	}

	@Order(102)
	@ExportProperty(VaccinationDto.VACCINE_INN)
	@ExportGroup(ExportGroupType.VACCINATION)
	public String getVaccineInn() {
		return vaccineInn;
	}

	@Order(103)
	@ExportProperty(VaccinationDto.VACCINE_BATCH_NUMBER)
	@ExportGroup(ExportGroupType.VACCINATION)
	public String getVaccineBatchNumber() {
		return vaccineBatchNumber;
	}

	@Order(104)
	@ExportProperty(VaccinationDto.VACCINE_UNII_CODE)
	@ExportGroup(ExportGroupType.VACCINATION)
	public String getVaccineUniiCode() {
		return vaccineUniiCode;
	}

	@Order(105)
	@ExportProperty(VaccinationDto.VACCINE_ATC_CODE)
	@ExportGroup(ExportGroupType.VACCINATION)
	public String getVaccineAtcCode() {
		return vaccineAtcCode;
	}

	@Order(113)
	@ExportProperty(LATEST_EVENT_ID)
	@ExportGroup(ExportGroupType.EVENT)
	public String getLatestEventId() {
		return latestEventId;
	}

	public void setLatestEventId(String latestEventId) {
		this.latestEventId = latestEventId;
	}

	@Order(114)
	@ExportProperty(LATEST_EVENT_TITLE)
	@ExportGroup(ExportGroupType.EVENT)
	public String getLatestEventTitle() {
		return latestEventTitle;
	}

	public void setLatestEventTitle(String latestEventTitle) {
		this.latestEventTitle = latestEventTitle;
	}

	@Order(115)
	@ExportProperty(EVENT_COUNT)
	@ExportGroup(ExportGroupType.EVENT)
	public Long getEventCount() {
		return eventCount;
	}

	@Order(116)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.BIRTH_NAME })
	@ExportGroup(ExportGroupType.SENSITIVE)
	@HideForCountriesExcept
	public String getBirthName() {
		return birthName;
	}

	@Order(117)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.BIRTH_COUNTRY })
	@ExportGroup(ExportGroupType.SENSITIVE)
	@HideForCountriesExcept(countries = {})
	public String getBirthCountry() {
		return birthCountry;
	}

	@Order(118)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.CITIZENSHIP })
	@ExportGroup(ExportGroupType.SENSITIVE)
	@HideForCountriesExcept(countries = {})
	public String getCitizenship() {
		return citizenship;
	}

	@Order(119)
	@ExportProperty(ContactDto.REPORTING_DISTRICT)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept
	public String getReportingDistrict() {
		return reportingDistrict;
	}

	@Order(120)
	@ExportProperty(ContactDto.EXTERNAL_TOKEN)
	@ExportGroup(ExportGroupType.CORE)
	public String getExternalToken() {
		return externalToken;
	}

	@Order(121)
	@ExportProperty(ContactDto.INTERNAL_TOKEN)
	@ExportGroup(ExportGroupType.CORE)
	public String getInternalToken() {
		return internalToken;
	}

	@Order(122)
	@ExportProperty({
		ContactDto.PERSON,
		PersonDto.SYMPTOM_JOURNAL_STATUS })
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public SymptomJournalStatus getSymptomJournalStatus() {
		return symptomJournalStatus;
	}

	public Long getReportingUserId() {
		return reportingUserId;
	}

	public Long getFollowUpStatusChangeUserId() {
		return followUpStatusChangeUserId;
	}

	@Order(175)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(value = ContactDto.REPORTING_USER, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getReportingUserName() {
		return reportingUserName;
	}

	public void setReportingUserName(String reportingUserName) {
		this.reportingUserName = reportingUserName;
	}

	@Order(176)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(value = ContactDto.REPORTING_USER, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getReportingUserRoles() {
		return reportingUserRoles;
	}

	public void setReportingUserRoles(Set<UserRoleReferenceDto> roles) {
		this.reportingUserRoles = roles.stream().map(ReferenceDto::buildCaption).collect(Collectors.joining(", "));;
	}

	@Order(177)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(value = ContactDto.FOLLOW_UP_STATUS_CHANGE_USER, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getFollowUpStatusChangeUserName() {
		return followUpStatusChangeUserName;
	}

	public void setFollowUpStatusChangeUserName(String followUpStatusChangeUserName) {
		this.followUpStatusChangeUserName = followUpStatusChangeUserName;
	}

	@Order(178)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(value = ContactDto.FOLLOW_UP_STATUS_CHANGE_USER, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getFollowUpStatusChangeUserRoles() {
		return followUpStatusChangeUserRoles;
	}

	public void setFollowUpStatusChangeUserRoles(Set<UserRoleReferenceDto> roles) {
		this.followUpStatusChangeUserRoles = roles.stream().map(ReferenceDto::buildCaption).collect(Collectors.joining(", "));
	}

	public void setEventCount(Long eventCount) {
		this.eventCount = eventCount;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setPersonId(long personId) {
		this.personId = personId;
	}

	public void setSourceCaseUuid(String sourceCaseUuid) {
		this.sourceCaseUuid = sourceCaseUuid;
	}

	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}

	public void setContactClassification(ContactClassification contactClassification) {
		this.contactClassification = contactClassification;
	}

	public void setLastContactDate(Date lastContactDate) {
		this.lastContactDate = lastContactDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public void setApproximateAge(String approximateAge) {
		this.approximateAge = approximateAge;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public void setContactIdentificationSource(ContactIdentificationSource contactIdentificationSource) {
		this.contactIdentificationSource = contactIdentificationSource;
	}

	public void setContactIdentificationSourceDetails(String contactIdentificationSourceDetails) {
		this.contactIdentificationSourceDetails = contactIdentificationSourceDetails;
	}

	public void setTracingApp(TracingApp tracingApp) {
		this.tracingApp = tracingApp;
	}

	public void setTracingAppDetails(String tracingAppDetails) {
		this.tracingAppDetails = tracingAppDetails;
	}

	public void setContactProximity(ContactProximity contactProximity) {
		this.contactProximity = contactProximity;
	}

	public void setContactStatus(ContactStatus contactStatus) {
		this.contactStatus = contactStatus;
	}

	public void setCompleteness(Float completeness) {
		this.completeness = completeness;
	}

	public void setFollowUpStatus(FollowUpStatus followUpStatus) {
		this.followUpStatus = followUpStatus;
	}

	public void setFollowUpUntil(Date followUpUntil) {
		this.followUpUntil = followUpUntil;
	}

	public void setNumberOfVisits(int numberOfVisits) {
		this.numberOfVisits = numberOfVisits;
	}

	public void setPresentCondition(PresentCondition presentCondition) {
		this.presentCondition = presentCondition;
	}

	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public void setArmedForcesRelationType(ArmedForcesRelationType armedForcesRelationType) {
		this.armedForcesRelationType = armedForcesRelationType;
	}

	public void setLastCooperativeVisitSymptomatic(YesNoUnknown lastCooperativeVisitSymptomatic) {
		this.lastCooperativeVisitSymptomatic = lastCooperativeVisitSymptomatic;
	}

	public void setLastCooperativeVisitDate(Date lastCooperativeVisitDate) {
		this.lastCooperativeVisitDate = lastCooperativeVisitDate;
	}

	public void setLastCooperativeVisitSymptoms(String lastCooperativeVisitSymptoms) {
		this.lastCooperativeVisitSymptoms = lastCooperativeVisitSymptoms;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public void setEpiDataId(long epiDataId) {
		this.epiDataId = epiDataId;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}

	public void setOtherContactDetails(String otherContactDetails) {
		this.otherContactDetails = otherContactDetails;
	}

	public Boolean getInJurisdiction() {
		return isInJurisdiction;
	}

	public void setNumberOfDoses(String numberOfDoses) {
		this.numberOfDoses = numberOfDoses;
	}

	public void setVaccinationInfoSource(VaccinationInfoSource vaccinationInfoSource) {
		this.vaccinationInfoSource = vaccinationInfoSource;
	}

	public void setFirstVaccinationDate(Date firstVaccinationDate) {
		this.firstVaccinationDate = firstVaccinationDate;
	}

	public void setLastVaccinationDate(Date lastVaccinationDate) {
		this.lastVaccinationDate = lastVaccinationDate;
	}

	public void setVaccineName(Vaccine vaccineName) {
		this.vaccineName = vaccineName;
	}

	public void setOtherVaccineName(String otherVaccineName) {
		this.otherVaccineName = otherVaccineName;
	}

	public void setVaccineManufacturer(VaccineManufacturer vaccineManufacturer) {
		this.vaccineManufacturer = vaccineManufacturer;
	}

	public void setOtherVaccineManufacturer(String otherVaccineManufacturer) {
		this.otherVaccineManufacturer = otherVaccineManufacturer;
	}

	public void setVaccineInn(String vaccineInn) {
		this.vaccineInn = vaccineInn;
	}

	public void setVaccineBatchNumber(String vaccineBatchNumber) {
		this.vaccineBatchNumber = vaccineBatchNumber;
	}

	public void setVaccineUniiCode(String vaccineUniiCode) {
		this.vaccineUniiCode = vaccineUniiCode;
	}

	public void setVaccineAtcCode(String vaccineAtcCode) {
		this.vaccineAtcCode = vaccineAtcCode;
	}

	public void setPreviousQuarantineTo(Date previousQuarantineTo) {
		this.previousQuarantineTo = previousQuarantineTo;
	}

	public void setQuarantineChangeComment(String quarantineChangeComment) {
		this.quarantineChangeComment = quarantineChangeComment;
	}
}
