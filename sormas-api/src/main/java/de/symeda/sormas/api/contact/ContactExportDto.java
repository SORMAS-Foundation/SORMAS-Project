/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.caze.VaccineManufacturer;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.facility.FacilityHelper;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.importexport.ExportGroup;
import de.symeda.sormas.api.importexport.ExportGroupType;
import de.symeda.sormas.api.importexport.ExportProperty;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.ArmedForcesRelationType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Salutation;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.PostalCodePseudonymizer;
import de.symeda.sormas.api.vaccinationinfo.VaccinationInfoDto;

public class ContactExportDto implements Serializable {

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
	public static final String BIRTH_DATE = "birthDate";

	private long id;
	private long personId;
	private String uuid;
	private String sourceCaseUuid;
	private CaseClassification caseClassification;
	private Disease disease;
	private String diseaseDetails;
	private ContactClassification contactClassification;
	private Boolean multiDayContact;
	private Date firstContactDate;
	private Date lastContactDate;
	@PersonalData
	private String firstName;
	@PersonalData
	private String lastName;
	@SensitiveData
	private Salutation salutation;
	@SensitiveData
	private String otherSalutation;
	private Sex sex;
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
	private YesNoUnknown returningTraveler;

	private Vaccination vaccination;
	private String vaccinationDoses;
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

	@PersonalData
	@SensitiveData
	private String birthName;
	private String birthCountry;
	private String citizenship;

	private String reportingDistrict;

	private ContactJurisdictionDto jurisdiction;

	//@formatter:off
	public ContactExportDto(long id, long personId, String uuid, String sourceCaseUuid, CaseClassification caseClassification, Disease disease, String diseaseDetails,
							ContactClassification contactClassification, Boolean multiDayContact, Date firstContactDate, Date lastContactDate,
							String firstName, String lastName,
							Salutation salutation, String otherSalutation, Sex sex,
							Integer birthdateDD, Integer birthdateMM, Integer birthdateYYYY,
							Integer approximateAge, ApproximateAgeType approximateAgeType, Date reportDate, ContactIdentificationSource contactIdentificationSource,
							String contactIdentificationSourceDetails, TracingApp tracingApp, String tracingAppDetails, ContactProximity contactProximity,
							ContactStatus contactStatus, FollowUpStatus followUpStatus, Date followUpUntil,
							QuarantineType quarantine, String quarantineTypeDetails, Date quarantineFrom, Date quarantineTo, String quarantineHelpNeeded,
							boolean quarantineOrderedVerbally, boolean quarantineOrderedOfficialDocument, Date quarantineOrderedVerballyDate, Date quarantineOrderedOfficialDocumentDate,
							boolean quarantineExtended, boolean quarantineReduced, boolean quarantineOfficialOrderSent, Date quarantineOfficialOrderSentDate,
							PresentCondition presentCondition, Date deathDate,
							String addressRegion, String addressDistrict, String addressCommunity, String city, String street, String houseNumber, String additionalInformation, String postalCode,
							String facility, String facilityUuid, String facilityDetails,
							String phone, String phoneOwner, String emailAddress, OccupationType occupationType, String occupationDetails, ArmedForcesRelationType armedForcesRelationType,
							String region, String district, String community,
							long epiDataId, YesNoUnknown contactWithSourceCaseKnown, YesNoUnknown returningTraveler,
							// vaccination info
							Vaccination vaccination, String vaccinationDoses, VaccinationInfoSource vaccinationInfoSource, Date firstVaccinationDate, Date lastVaccinationDate,
							Vaccine vaccineName, String otherVaccineName, VaccineManufacturer vaccineManufacturer, String otherVaccineManufacturer,
							String vaccineInn, String vaccineBatchNumber, String vaccineUniiCode, String vaccineAtcCode,

							String externalID, String externalToken,
							String birthName, String birthCountryIsoCode, String birthCountryName, String citizenshipIsoCode, String citizenshipCountryName,
							String reportingDistrict,
							String reportingUserUuid, String regionUuid, String districtUuid, String communityUuid,
							String caseReportingUserUuid, String caseRegionUuid, String caseDistrictUuid, String caseCommunityUuid, String caseHealthFacilityUuid, String casePointOfEntryUuid
	) {
	//@formatter:on

		this.id = id;
		this.personId = personId;
		this.uuid = uuid;
		this.sourceCaseUuid = sourceCaseUuid;
		this.caseClassification = caseClassification;
		this.disease = disease;
		this.diseaseDetails = diseaseDetails;
		this.contactClassification = contactClassification;
		this.multiDayContact = multiDayContact;
		this.firstContactDate = firstContactDate;
		this.lastContactDate = lastContactDate;
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
		this.occupationType = occupationType;
		this.occupationDetails = occupationDetails;
		this.armedForcesRelationType = armedForcesRelationType;
		this.region = region;
		this.district = district;
		this.community = community;
		this.epiDataId = epiDataId;
		this.contactWithSourceCaseKnown = contactWithSourceCaseKnown;
		this.returningTraveler = returningTraveler;

		this.vaccination = vaccination;
		this.vaccinationDoses = vaccinationDoses;
		this.vaccinationInfoSource = vaccinationInfoSource;
		this.firstVaccinationDate = firstVaccinationDate;
		this.lastVaccinationDate = lastVaccinationDate;
		this.vaccineName = vaccineName;
		this.otherVaccineName = otherVaccineName;
		this.vaccineManufacturer = vaccineManufacturer;
		this.otherVaccineManufacturer = otherVaccineManufacturer;
		this.vaccineInn = vaccineInn;
		this.vaccineBatchNumber = vaccineBatchNumber;
		this.vaccineUniiCode = vaccineUniiCode;
		this.vaccineAtcCode = vaccineAtcCode;

		this.externalID = externalID;
		this.externalToken = externalToken;
		this.birthName = birthName;
		this.birthCountry = I18nProperties.getCountryName(birthCountryIsoCode, birthCountryName);
		this.citizenship = I18nProperties.getCountryName(citizenshipIsoCode, citizenshipCountryName);
		this.reportingDistrict = reportingDistrict;

		CaseJurisdictionDto caseJurisdiction = caseReportingUserUuid != null
			? null
			: new CaseJurisdictionDto(
				caseReportingUserUuid,
				caseRegionUuid,
				caseDistrictUuid,
				caseCommunityUuid,
				caseHealthFacilityUuid,
				casePointOfEntryUuid);
		this.jurisdiction = new ContactJurisdictionDto(reportingUserUuid, regionUuid, districtUuid, communityUuid, caseJurisdiction);
	}

	public ContactReferenceDto toReference() {
		return new ContactReferenceDto(uuid);
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
	public String getUuid() {
		return uuid;
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
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.FIRST_NAME })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getFirstName() {
		return firstName;
	}

	@Order(11)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.LAST_NAME })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getLastName() {
		return lastName;
	}

	@Order(12)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.SALUTATION })
	@ExportGroup(ExportGroupType.PERSON)
	@HideForCountriesExcept
	public Salutation getSalutation() {
		return salutation;
	}

	@Order(13)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.OTHER_SALUTATION })
	@ExportGroup(ExportGroupType.PERSON)
	@HideForCountriesExcept
	public String getOtherSalutation() {
		return otherSalutation;
	}

	@Order(14)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.SEX })
	@ExportGroup(ExportGroupType.PERSON)
	public Sex getSex() {
		return sex;
	}

	@Order(15)
	@ExportProperty(BIRTH_DATE)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public BirthDateDto getBirthdate() {
		return birthdate;
	}

	@Order(16)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.APPROXIMATE_AGE })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getApproximateAge() {
		return approximateAge;
	}

	@Order(17)
	@ExportProperty(ContactDto.REPORT_DATE_TIME)
	@ExportGroup(ExportGroupType.CORE)
	public Date getReportDate() {
		return reportDate;
	}

	@Order(18)
	@ExportProperty(ContactDto.REGION)
	@ExportGroup(ExportGroupType.CORE)
	public String getRegion() {
		return region;
	}

	@Order(19)
	@ExportProperty(ContactDto.DISTRICT)
	@ExportGroup(ExportGroupType.CORE)
	public String getDistrict() {
		return district;
	}

	@Order(20)
	@ExportProperty(ContactDto.COMMUNITY)
	@ExportGroup(ExportGroupType.CORE)
	public String getCommunity() {
		return community;
	}

	@Order(21)
	@ExportProperty(ContactDto.CONTACT_IDENTIFICATION_SOURCE)
	@ExportGroup(ExportGroupType.CORE)
	public ContactIdentificationSource getContactIdentificationSource() {
		return contactIdentificationSource;
	}

	@Order(22)
	@ExportProperty(ContactDto.CONTACT_IDENTIFICATION_SOURCE_DETAILS)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getContactIdentificationSourceDetails() {
		return contactIdentificationSourceDetails;
	}

	@Order(23)
	@ExportProperty(ContactDto.TRACING_APP)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public TracingApp getTracingApp() {
		return tracingApp;
	}

	@Order(24)
	@ExportProperty(ContactDto.TRACING_APP_DETAILS)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getTracingAppDetails() {
		return tracingAppDetails;
	}

	@Order(25)
	@ExportProperty(ContactDto.CONTACT_PROXIMITY)
	@ExportGroup(ExportGroupType.CORE)
	public ContactProximity getContactProximity() {
		return contactProximity;
	}

	@Order(26)
	@ExportProperty(ContactDto.CONTACT_STATUS)
	@ExportGroup(ExportGroupType.CORE)
	public ContactStatus getContactStatus() {
		return contactStatus;
	}

	@Order(27)
	@ExportProperty(ContactDto.FOLLOW_UP_STATUS)
	@ExportGroup(ExportGroupType.FOLLOW_UP)
	public FollowUpStatus getFollowUpStatus() {
		return followUpStatus;
	}

	@Order(28)
	@ExportProperty(ContactDto.FOLLOW_UP_UNTIL)
	@ExportGroup(ExportGroupType.FOLLOW_UP)
	public Date getFollowUpUntil() {
		return followUpUntil;
	}

	@Order(29)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public QuarantineType getQuarantine() {
		return quarantine;
	}

	@Order(30)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getQuarantineTypeDetails() {
		return quarantineTypeDetails;
	}

	@Order(31)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public Date getQuarantineFrom() {
		return quarantineFrom;
	}

	@Order(32)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public Date getQuarantineTo() {
		return quarantineTo;
	}

	@Order(33)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getQuarantineHelpNeeded() {
		return quarantineHelpNeeded;
	}

	@Order(34)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept(countries = {
		"de",
		"ch" })
	public boolean isQuarantineOrderedVerbally() {
		return quarantineOrderedVerbally;
	}

	@Order(35)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept(countries = {
		"de",
		"ch" })
	public boolean isQuarantineOrderedOfficialDocument() {
		return quarantineOrderedOfficialDocument;
	}

	@Order(36)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept(countries = {
		"de",
		"ch" })
	public Date getQuarantineOrderedVerballyDate() {
		return quarantineOrderedVerballyDate;
	}

	@Order(37)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept(countries = {
		"de",
		"ch" })
	public Date getQuarantineOrderedOfficialDocumentDate() {
		return quarantineOrderedOfficialDocumentDate;
	}

	@Order(38)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept(countries = {
		"de",
		"ch" })
	public boolean isQuarantineOfficialOrderSent() {
		return quarantineOfficialOrderSent;
	}

	@Order(39)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept(countries = {
		"de",
		"ch" })
	public Date getQuarantineOfficialOrderSentDate() {
		return quarantineOfficialOrderSentDate;
	}

	@Order(40)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public boolean isQuarantineExtended() {
		return quarantineExtended;
	}

	@Order(41)
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public boolean isQuarantineReduced() {
		return quarantineReduced;
	}

	@Order(42)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.PRESENT_CONDITION })
	@ExportGroup(ExportGroupType.PERSON)
	public PresentCondition getPresentCondition() {
		return presentCondition;
	}

	@Order(43)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.DEATH_DATE })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public Date getDeathDate() {
		return deathDate;
	}

	@Order(50)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.REGION })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getAddressRegion() {
		return addressRegion;
	}

	@Order(51)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.DISTRICT })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getAddressDistrict() {
		return addressDistrict;
	}

	@Order(52)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.COMMUNITY })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getAddressCommunity() {
		return addressCommunity;
	}

	@Order(53)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.CITY })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getCity() {
		return city;
	}

	@Order(54)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.STREET })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getStreet() {
		return street;
	}

	@Order(55)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.HOUSE_NUMBER })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getHouseNumber() {
		return houseNumber;
	}

	@Order(56)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.ADDITIONAL_INFORMATION })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getAdditionalInformation() {
		return additionalInformation;
	}

	@Order(57)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.POSTAL_CODE })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getPostalCode() {
		return postalCode;
	}

	@Order(58)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.FACILITY })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getFacility() {
		return facility;
	}

	@Order(59)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.FACILITY_DETAILS })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getFacilityDetails() {
		return facilityDetails;
	}

	@Order(65)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.PHONE })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getPhone() {
		return phone;
	}

	@Order(66)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.PHONE_OWNER })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getPhoneOwner() {
		return phoneOwner;
	}

	@Order(67)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.EMAIL_ADDRESS })
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getEmailAddress() {
		return emailAddress;
	}

	@Order(68)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.OCCUPATION_TYPE })
	@ExportGroup(ExportGroupType.PERSON)
	public OccupationType getOccupationType() {
		return occupationType;
	}

	@Order(69)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.OCCUPATION_DETAILS })
	@ExportGroup(ExportGroupType.PERSON)
	public String getOccupationDetails() {
		return occupationDetails;
	}

	@Order(70)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.ARMED_FORCES_RELATION_TYPE })
	@ExportGroup(ExportGroupType.PERSON)
	public ArmedForcesRelationType getArmedForcesRelationType() {
		return armedForcesRelationType;
	}

	@Order(75)
	@ExportProperty(NUMBER_OF_VISITS)
	@ExportGroup(ExportGroupType.FOLLOW_UP)
	public int getNumberOfVisits() {
		return numberOfVisits;
	}

	@Order(76)
	@ExportProperty(LAST_COOPERATIVE_VISIT_SYMPTOMATIC)
	@ExportGroup(ExportGroupType.FOLLOW_UP)
	public YesNoUnknown getLastCooperativeVisitSymptomatic() {
		return lastCooperativeVisitSymptomatic;
	}

	@Order(77)
	@ExportProperty(LAST_COOPERATIVE_VISIT_DATE)
	@ExportGroup(ExportGroupType.FOLLOW_UP)
	public Date getLastCooperativeVisitDate() {
		return lastCooperativeVisitDate;
	}

	@Order(78)
	@ExportProperty(LAST_COOPERATIVE_VISIT_SYMPTOMS)
	@ExportGroup(ExportGroupType.FOLLOW_UP)
	public String getLastCooperativeVisitSymptoms() {
		return lastCooperativeVisitSymptoms;
	}

	@Order(79)
	@ExportProperty(TRAVELED)
	@ExportGroup(ExportGroupType.EPIDEMIOLOGICAL)
	public boolean isTraveled() {
		return traveled;
	}

	public void setTraveled(boolean traveled) {
		this.traveled = traveled;
	}

	@Order(80)
	@ExportProperty(TRAVEL_HISTORY)
	@ExportGroup(ExportGroupType.EPIDEMIOLOGICAL)
	public String getTravelHistory() {
		return travelHistory;
	}

	public void setTravelHistory(String travelHistory) {
		this.travelHistory = travelHistory;
	}

	@Order(81)
	@ExportProperty(BURIAL_ATTENDED)
	@ExportGroup(ExportGroupType.EPIDEMIOLOGICAL)
	public boolean isBurialAttended() {
		return burialAttended;
	}

	public void setBurialAttended(boolean burialAttended) {
		this.burialAttended = burialAttended;
	}

	@Order(82)
	@ExportProperty({
		CaseDataDto.EPI_DATA,
		EpiDataDto.CONTACT_WITH_SOURCE_CASE_KNOWN })
	@ExportGroup(ExportGroupType.EPIDEMIOLOGICAL)
	public YesNoUnknown getContactWithSourceCaseKnown() {
		return contactWithSourceCaseKnown;
	}

	@Order(83)
	@ExportProperty(ContactDto.RETURNING_TRAVELER)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public YesNoUnknown getReturningTraveler() {
		return returningTraveler;
	}

	public void setReturningTraveler(YesNoUnknown returningTraveler) {
		this.returningTraveler = returningTraveler;
	}

	@Order(90)
	@ExportProperty({
		ContactDto.VACCINATION_INFO,
		VaccinationInfoDto.VACCINATION })
	@ExportGroup(ExportGroupType.VACCINATION)
	public Vaccination getVaccination() {
		return vaccination;
	}

	@Order(91)
	@ExportProperty({
		ContactDto.VACCINATION_INFO,
		VaccinationInfoDto.VACCINATION_DOSES })
	@ExportGroup(ExportGroupType.VACCINATION)
	public String getVaccinationDoses() {
		return vaccinationDoses;
	}

	@Order(92)
	@ExportProperty({
		ContactDto.VACCINATION_INFO,
		VaccinationInfoDto.VACCINATION_INFO_SOURCE })
	@ExportGroup(ExportGroupType.VACCINATION)
	public VaccinationInfoSource getVaccinationInfoSource() {
		return vaccinationInfoSource;
	}

	@Order(93)
	@ExportProperty({
		ContactDto.VACCINATION_INFO,
		VaccinationInfoDto.FIRST_VACCINATION_DATE })
	@ExportGroup(ExportGroupType.VACCINATION)
	public Date getFirstVaccinationDate() {
		return firstVaccinationDate;
	}

	@Order(94)
	@ExportProperty({
		ContactDto.VACCINATION_INFO,
		VaccinationInfoDto.LAST_VACCINATION_DATE })
	@ExportGroup(ExportGroupType.VACCINATION)
	public Date getLastVaccinationDate() {
		return lastVaccinationDate;
	}

	@Order(95)
	@ExportProperty({
		ContactDto.VACCINATION_INFO,
		VaccinationInfoDto.VACCINE_NAME })
	@ExportGroup(ExportGroupType.VACCINATION)
	public Vaccine getVaccineName() {
		return vaccineName;
	}

	@Order(96)
	@ExportProperty({
		ContactDto.VACCINATION_INFO,
		VaccinationInfoDto.OTHER_VACCINE_NAME })
	@ExportGroup(ExportGroupType.VACCINATION)
	public String getOtherVaccineName() {
		return otherVaccineName;
	}

	@Order(97)
	@ExportProperty({
		ContactDto.VACCINATION_INFO,
		VaccinationInfoDto.VACCINE_MANUFACTURER })
	@ExportGroup(ExportGroupType.VACCINATION)
	public VaccineManufacturer getVaccineManufacturer() {
		return vaccineManufacturer;
	}

	@Order(98)
	@ExportProperty({
		ContactDto.VACCINATION_INFO,
		VaccinationInfoDto.OTHER_VACCINE_MANUFACTURER })
	@ExportGroup(ExportGroupType.VACCINATION)
	public String getOtherVaccineManufacturer() {
		return otherVaccineManufacturer;
	}

	@Order(99)
	@ExportProperty({
		ContactDto.VACCINATION_INFO,
		VaccinationInfoDto.VACCINE_INN })
	@ExportGroup(ExportGroupType.VACCINATION)
	public String getVaccineInn() {
		return vaccineInn;
	}

	@Order(100)
	@ExportProperty({
		ContactDto.VACCINATION_INFO,
		VaccinationInfoDto.VACCINE_BATCH_NUMBER })
	@ExportGroup(ExportGroupType.VACCINATION)
	public String getVaccineBatchNumber() {
		return vaccineBatchNumber;
	}

	@Order(101)
	@ExportProperty({
		ContactDto.VACCINATION_INFO,
		VaccinationInfoDto.VACCINE_UNII_CODE })
	@ExportGroup(ExportGroupType.VACCINATION)
	public String getVaccineUniiCode() {
		return vaccineUniiCode;
	}

	@Order(102)
	@ExportProperty({
		ContactDto.VACCINATION_INFO,
		VaccinationInfoDto.VACCINE_ATC_CODE })
	@ExportGroup(ExportGroupType.VACCINATION)
	public String getVaccineAtcCode() {
		return vaccineAtcCode;
	}

	@Order(110)
	@ExportProperty(LATEST_EVENT_ID)
	@ExportGroup(ExportGroupType.EVENT)
	public String getLatestEventId() {
		return latestEventId;
	}

	public void setLatestEventId(String latestEventId) {
		this.latestEventId = latestEventId;
	}

	@Order(111)
	@ExportProperty(LATEST_EVENT_TITLE)
	@ExportGroup(ExportGroupType.EVENT)
	public String getLatestEventTitle() {
		return latestEventTitle;
	}

	public void setLatestEventTitle(String latestEventTitle) {
		this.latestEventTitle = latestEventTitle;
	}

	@Order(112)
	@ExportProperty(EVENT_COUNT)
	@ExportGroup(ExportGroupType.EVENT)
	public Long getEventCount() {
		return eventCount;
	}

	@Order(113)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.BIRTH_NAME })
	@ExportGroup(ExportGroupType.SENSITIVE)
	@HideForCountriesExcept
	public String getBirthName() {
		return birthName;
	}

	@Order(114)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.BIRTH_COUNTRY })
	@ExportGroup(ExportGroupType.SENSITIVE)
	@HideForCountriesExcept
	public String getBirthCountry() {
		return birthCountry;
	}

	@Order(115)
	@ExportProperty({
		CaseDataDto.PERSON,
		PersonDto.CITIZENSHIP })
	@ExportGroup(ExportGroupType.SENSITIVE)
	@HideForCountriesExcept
	public String getCitizenship() {
		return citizenship;
	}

	@Order(116)
	@ExportProperty(ContactDto.REPORTING_DISTRICT)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept
	public String getReportingDistrict() {
		return reportingDistrict;
	}

	@Order(117)
	@ExportProperty(ContactDto.EXTERNAL_TOKEN)
	@ExportGroup(ExportGroupType.CORE)
	public String getExternalToken() {
		return externalToken;
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

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public String getReportingUserUuid() {
		return jurisdiction.getReportingUserUuid();
	}

	public String getRegionUuid() {
		return jurisdiction.getRegionUuid();
	}

	public String getDistrictUuid() {
		return jurisdiction.getDistrictUuid();
	}

	public String getCommunityUuid() {
		return jurisdiction.getCommunityUuid();
	}

	public ContactJurisdictionDto getJurisdiction() {
		return jurisdiction;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}
}
