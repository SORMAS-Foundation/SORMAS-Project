/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.api.event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.caze.BurialInfoDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.EmbeddedSampleExportDto;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.caze.VaccineManufacturer;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.importexport.ExportEntity;
import de.symeda.sormas.api.importexport.ExportProperty;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.BurialConductor;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Salutation;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.PostalCodePseudonymizer;
import de.symeda.sormas.api.vaccinationinfo.VaccinationInfoDto;

@ExportEntity(EventParticipantDto.class)
public class EventParticipantExportDto implements Serializable {

	public static final String I18N_PREFIX = "EventParticipantExport";

	public static final String EVENT_DISEASE = "eventDisease";
	public static final String EVENT_START_DATE = "eventStartDate";
	public static final String EVENT_END_DATE = "eventEndDate";
	public static final String EVENT_TITLE = "eventTitle";
	public static final String EVENT_REGION = "eventRegion";
	public static final String EVENT_DISTRICT = "eventDistrict";
	public static final String EVENT_COMMUNITY = "eventCommunity";
	public static final String EVENT_CITY = "eventCity";
	public static final String EVENT_STREET = "eventStreet";
	public static final String ADDRESS_GPS_COORDINATES = "addressGpsCoordinates";
	public static final String BURIAL_INFO = "burialInfo";
	public static final String SAMPLE_INFORMATION = "sampleInformation";
	public static final String CONTACT_COUNT = "contactCount";
	public static final String BIRTH_DATE = "birthdate";

	private long id;
	private long personId;
	private long personAddressId;

	private String eventUuid;

	private final EventStatus eventStatus;
	private final EventInvestigationStatus eventInvestigationStatus;
	private final Disease eventDisease;
	private TypeOfPlace typeOfPlace;
	private final Date eventStartDate;
	private final Date eventEndDate;
	private final String eventTitle;
	private final String eventDesc;

	private final String eventRegion;
	private final String eventDistrict;
	private final String eventCommunity;
	private final String eventCity;
	private final String eventStreet;
	private final String eventHouseNumber;

	private final String personUuid;
	private final String eventParticipantUuid;

	private final String involvmentDescription;

	@PersonalData
	@SensitiveData
	private String firstName;
	@PersonalData
	@SensitiveData
	private String lastName;
	private Salutation salutation;
	@SensitiveData
	private String otherSalutation;
	private Sex sex;
	private String approximateAge;
	private String ageGroup;
	private BirthDateDto birthdate;
	private String personNationalHealthId;

	private PresentCondition presentCondition;
	private Date deathDate;
	private BurialInfoDto burialInfo;
	private String addressRegion;
	private String addressDistrict;
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
	private String addressGpsCoordinates;
	@SensitiveData
	private String phone;
	@SensitiveData
	private String emailAddress;
	@PersonalData
	@SensitiveData
	private String birthName;
	private String birthCountry;
	private String citizenship;

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

	private String caseUuid;

	private List<EmbeddedSampleExportDto> eventParticipantSamples = new ArrayList<>();

	private EventParticipantJurisdictionDto jurisdiction;

	private long contactCount;

	//@formatter:off
    public EventParticipantExportDto(long id, long personId, String personUuid, String eventParticipantUuid, String personNationalHealthId, long personAddressId, String reportingUserUuid, String eventUuid,

									 EventStatus eventStatus, EventInvestigationStatus eventInvestigationStatus, Disease eventDisease, TypeOfPlace typeOfPlace, Date eventStartDate, Date eventEndDate, String eventTitle, String eventDesc,
									 String eventRegion, String eventDistrict, String eventCommunity, String eventCity, String eventStreet, String eventHouseNumber,
									 String firstName, String lastName, Salutation salutation, String otherSalutation, Sex sex, String involvmentDescription, Integer approximateAge, ApproximateAgeType approximateAgeType,
									 Integer birthdateDD, Integer birthdateMM, Integer birthdateYYYY, PresentCondition presentCondition, Date deathDate, Date burialDate,
									 BurialConductor burialConductor, String burialPlaceDescription, String addressRegion, String addressDistrict, String addressCommunity, String city, String street, String houseNumber,
									 String additionalInformation, String postalCode, String phone, String emailAddress, String caseUuid,
									 String birthName, String birthCountryIsoCode, String birthCountryName, String citizenshipIsoCode, String citizenshipCountryName,
									 // vaccination info
									 Vaccination vaccination, String vaccinationDoses, VaccinationInfoSource vaccinationInfoSource, Date firstVaccinationDate, Date lastVaccinationDate,
									 Vaccine vaccineName, String otherVaccineName, VaccineManufacturer vaccineManufacturer, String otherVaccineManufacturer,
									 String vaccineInn, String vaccineBatchNumber, String vaccineUniiCode, String vaccineAtcCode
									 ) {
    	//@formatter:on

		this.id = id;
		this.personId = personId;
		this.personUuid = personUuid;
		this.eventParticipantUuid = eventParticipantUuid;
		this.personNationalHealthId = personNationalHealthId;
		this.personAddressId = personAddressId;
		this.eventUuid = eventUuid;

		this.eventStatus = eventStatus;
		this.eventInvestigationStatus = eventInvestigationStatus;
		this.eventDisease = eventDisease;
		this.typeOfPlace = typeOfPlace;
		this.eventStartDate = eventStartDate;
		this.eventEndDate = eventEndDate;
		this.eventTitle = eventTitle;
		this.eventDesc = eventDesc;
		this.eventRegion = eventRegion;
		this.eventDistrict = eventDistrict;
		this.eventCommunity = eventCommunity;
		this.eventCity = eventCity;
		this.eventStreet = eventStreet;
		this.eventHouseNumber = eventHouseNumber;

		this.firstName = firstName;
		this.lastName = lastName;
		this.salutation = salutation;
		this.otherSalutation = otherSalutation;
		this.sex = sex;
		this.involvmentDescription = involvmentDescription;
		this.approximateAge = ApproximateAgeType.ApproximateAgeHelper.formatApproximateAge(approximateAge, approximateAgeType);
		this.ageGroup = ApproximateAgeType.ApproximateAgeHelper.getAgeGroupFromAge(approximateAge, approximateAgeType);
		birthdate = new BirthDateDto(birthdateDD, birthdateMM, birthdateYYYY);
		this.presentCondition = presentCondition;
		this.deathDate = deathDate;
		this.burialInfo = new BurialInfoDto(burialDate, burialConductor, burialPlaceDescription);
		this.addressRegion = addressRegion;
		this.addressDistrict = addressDistrict;
		this.addressCommunity = addressCommunity;
		this.city = city;
		this.street = street;
		this.houseNumber = houseNumber;
		this.additionalInformation = additionalInformation;
		this.postalCode = postalCode;
		this.phone = phone;
		this.emailAddress = emailAddress;
		this.caseUuid = caseUuid;
		this.birthName = birthName;
		this.birthCountry = I18nProperties.getCountryName(birthCountryIsoCode, birthCountryName);
		this.citizenship = I18nProperties.getCountryName(citizenshipIsoCode, citizenshipCountryName);

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

		jurisdiction = new EventParticipantJurisdictionDto(reportingUserUuid);
	}

	@Order(9)
	@ExportProperty(EventParticipantDto.UUID)
	public String getEventParticipantUuid() {
		return eventParticipantUuid;
	}

	public long getPersonAddressId() {
		return personAddressId;
	}

	@Order(10)
	@ExportEntity(PersonDto.class)
	@ExportProperty({
		EventParticipantDto.PERSON,
		PersonDto.UUID })
	public String getPersonUuid() {
		return personUuid;
	}

	@Order(11)
	@ExportEntity(PersonDto.class)
	@ExportProperty({
		EventParticipantDto.PERSON,
		PersonDto.NATIONAL_HEALTH_ID })
	public String getPersonNationalHealthId() {
		return personNationalHealthId;
	}

	@Order(12)
	@ExportEntity(CaseDataDto.class)
	@ExportProperty({
		EventParticipantDto.RESULTING_CASE,
		CaseDataDto.UUID })
	public String getCaseUuid() {
		return caseUuid;
	}

	@Order(13)
	@ExportEntity(PersonDto.class)
	@ExportProperty({
		EventParticipantDto.PERSON,
		PersonDto.FIRST_NAME })
	public String getFirstName() {
		return firstName;
	}

	@Order(14)
	@ExportEntity(PersonDto.class)
	@ExportProperty({
		EventParticipantDto.PERSON,
		PersonDto.LAST_NAME })
	public String getLastName() {
		return lastName;
	}

	@Order(15)
	@HideForCountriesExcept
	@ExportEntity(PersonDto.class)
	@ExportProperty({
		EventParticipantDto.PERSON,
		PersonDto.SALUTATION })
	public Salutation getSalutation() {
		return salutation;
	}

	@Order(16)
	@HideForCountriesExcept
	@ExportEntity(PersonDto.class)
	@ExportProperty({
		EventParticipantDto.PERSON,
		PersonDto.OTHER_SALUTATION })
	public String getOtherSalutation() {
		return otherSalutation;
	}

	@Order(17)
	@ExportEntity(PersonDto.class)
	@ExportProperty({
		EventParticipantDto.PERSON,
		PersonDto.SEX })
	public Sex getSex() {
		return sex;
	}

	@Order(18)
	@ExportEntity(PersonDto.class)
	@ExportProperty({
		EventParticipantDto.PERSON,
		PersonDto.APPROXIMATE_AGE })
	public String getApproximateAge() {
		return approximateAge;
	}

	@Order(19)
	public String getAgeGroup() {
		return ageGroup;
	}

	@Order(20)
	@ExportProperty(BIRTH_DATE)
	public BirthDateDto getBirthdate() {
		return birthdate;
	}

	@Order(21)
	@ExportEntity(PersonDto.class)
	@ExportProperty({
		EventParticipantDto.PERSON,
		PersonDto.PRESENT_CONDITION })
	public PresentCondition getPresentCondition() {
		return presentCondition;
	}

	@Order(22)
	@ExportEntity(PersonDto.class)
	@ExportProperty({
		EventParticipantDto.PERSON,
		PersonDto.DEATH_DATE })
	public Date getDeathDate() {
		return deathDate;
	}

	@Order(23)
	@ExportProperty(EventParticipantExportDto.BURIAL_INFO)
	public BurialInfoDto getBurialInfo() {
		return burialInfo;
	}

	@Order(31)
	@ExportEntity(LocationDto.class)
	@ExportProperty({
		EventParticipantDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.REGION })
	public String getAddressRegion() {
		return addressRegion;
	}

	@Order(32)
	@ExportEntity(LocationDto.class)
	@ExportProperty({
		EventParticipantDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.DISTRICT })
	public String getAddressDistrict() {
		return addressDistrict;
	}

	@Order(32)
	@ExportEntity(LocationDto.class)
	@ExportProperty({
		EventParticipantDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.COMMUNITY })
	public String getAddressCommunity() {
		return addressCommunity;
	}

	@Order(33)
	@ExportEntity(LocationDto.class)
	@ExportProperty({
		EventParticipantDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.CITY })
	public String getCity() {
		return city;
	}

	@Order(34)
	@ExportEntity(LocationDto.class)
	@ExportProperty({
		EventParticipantDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.STREET })
	public String getStreet() {
		return street;
	}

	@Order(35)
	@ExportEntity(LocationDto.class)
	@ExportProperty({
		EventParticipantDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.HOUSE_NUMBER })
	public String getHouseNumber() {
		return houseNumber;
	}

	@Order(36)
	@ExportEntity(LocationDto.class)
	@ExportProperty({
		EventParticipantDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.ADDITIONAL_INFORMATION })
	public String getAdditionalInformation() {
		return additionalInformation;
	}

	@Order(37)
	@ExportEntity(LocationDto.class)
	@ExportProperty({
		EventParticipantDto.PERSON,
		PersonDto.ADDRESS,
		LocationDto.POSTAL_CODE })
	public String getPostalCode() {
		return postalCode;
	}

	@Order(38)
	@ExportProperty(EventParticipantExportDto.ADDRESS_GPS_COORDINATES)
	public String getAddressGpsCoordinates() {
		return addressGpsCoordinates;
	}

	@Order(40)
	@ExportEntity(PersonDto.class)
	@ExportProperty({
		EventParticipantDto.PERSON,
		PersonDto.PHONE })
	public String getPhone() {
		return phone;
	}

	@Order(41)
	@ExportEntity(PersonDto.class)
	@ExportProperty({
		EventParticipantDto.PERSON,
		PersonDto.EMAIL_ADDRESS })
	public String getEmailAddress() {
		return emailAddress;
	}

	@Order(42)
	@ExportEntity(PersonDto.class)
	@HideForCountriesExcept
	@ExportProperty({
		EventParticipantDto.PERSON,
		PersonDto.BIRTH_NAME })
	public String getBirthName() {
		return birthName;
	}

	@Order(43)
	@ExportEntity(PersonDto.class)
	@ExportProperty({
		EventParticipantDto.PERSON,
		PersonDto.BIRTH_COUNTRY })
	@HideForCountriesExcept
	public String getBirthCountry() {
		return birthCountry;
	}

	@Order(44)
	@ExportEntity(PersonDto.class)
	@ExportProperty({
		EventParticipantDto.PERSON,
		PersonDto.CITIZENSHIP })
	@HideForCountriesExcept
	public String getCitizenship() {
		return citizenship;
	}

	@Order(45)
	@ExportEntity(VaccinationInfoDto.class)
	@ExportProperty({
		EventParticipantDto.VACCINATION_INFO,
		VaccinationInfoDto.VACCINATION })
	public Vaccination getVaccination() {
		return vaccination;
	}

	@Order(46)
	@ExportEntity(VaccinationInfoDto.class)
	@ExportProperty({
		EventParticipantDto.VACCINATION_INFO,
		VaccinationInfoDto.VACCINATION_DOSES })
	public String getVaccinationDoses() {
		return vaccinationDoses;
	}

	@Order(47)
	@ExportEntity(VaccinationInfoDto.class)
	@ExportProperty({
		EventParticipantDto.VACCINATION_INFO,
		VaccinationInfoDto.VACCINATION_INFO_SOURCE })
	public VaccinationInfoSource getVaccinationInfoSource() {
		return vaccinationInfoSource;
	}

	@Order(48)
	@ExportEntity(VaccinationInfoDto.class)
	@ExportProperty({
		EventParticipantDto.VACCINATION_INFO,
		VaccinationInfoDto.FIRST_VACCINATION_DATE })
	public Date getFirstVaccinationDate() {
		return firstVaccinationDate;
	}

	@Order(49)
	@ExportEntity(VaccinationInfoDto.class)
	@ExportProperty({
		EventParticipantDto.VACCINATION_INFO,
		VaccinationInfoDto.LAST_VACCINATION_DATE })
	public Date getLastVaccinationDate() {
		return lastVaccinationDate;
	}

	@Order(50)
	@ExportEntity(VaccinationInfoDto.class)
	@ExportProperty({
		EventParticipantDto.VACCINATION_INFO,
		VaccinationInfoDto.VACCINE_NAME })
	public Vaccine getVaccineName() {
		return vaccineName;
	}

	@Order(51)
	@ExportEntity(VaccinationInfoDto.class)
	@ExportProperty({
		EventParticipantDto.VACCINATION_INFO,
		VaccinationInfoDto.OTHER_VACCINE_NAME })
	public String getOtherVaccineName() {
		return otherVaccineName;
	}

	@Order(52)
	@ExportEntity(VaccinationInfoDto.class)
	@ExportProperty({
		EventParticipantDto.VACCINATION_INFO,
		VaccinationInfoDto.VACCINE_MANUFACTURER })
	public VaccineManufacturer getVaccineManufacturer() {
		return vaccineManufacturer;
	}

	@Order(53)
	@ExportEntity(VaccinationInfoDto.class)
	@ExportProperty({
		EventParticipantDto.VACCINATION_INFO,
		VaccinationInfoDto.OTHER_VACCINE_MANUFACTURER })
	public String getOtherVaccineManufacturer() {
		return otherVaccineManufacturer;
	}

	@Order(54)
	@ExportEntity(VaccinationInfoDto.class)
	@ExportProperty({
		EventParticipantDto.VACCINATION_INFO,
		VaccinationInfoDto.VACCINE_INN })
	public String getVaccineInn() {
		return vaccineInn;
	}

	@Order(55)
	@ExportEntity(VaccinationInfoDto.class)
	@ExportProperty({
		EventParticipantDto.VACCINATION_INFO,
		VaccinationInfoDto.VACCINE_BATCH_NUMBER })
	public String getVaccineBatchNumber() {
		return vaccineBatchNumber;
	}

	@Order(56)
	@ExportEntity(VaccinationInfoDto.class)
	@ExportProperty({
		EventParticipantDto.VACCINATION_INFO,
		VaccinationInfoDto.VACCINE_UNII_CODE })
	public String getVaccineUniiCode() {
		return vaccineUniiCode;
	}

	@Order(57)
	@ExportEntity(VaccinationInfoDto.class)
	@ExportProperty({
		EventParticipantDto.VACCINATION_INFO,
		VaccinationInfoDto.VACCINE_ATC_CODE })
	public String getVaccineAtcCode() {
		return vaccineAtcCode;
	}

	@Order(60)
	@ExportProperty(EventParticipantExportDto.SAMPLE_INFORMATION)
	public String getOtherSamplesString() {
		StringBuilder samples = new StringBuilder();
		String separator = ", ";

		for (EmbeddedSampleExportDto sample : eventParticipantSamples) {
			samples.append(sample.formatString()).append(separator);
		}

		return samples.length() > 0 ? samples.substring(0, samples.length() - separator.length()) : "";
	}

	@Order(61)
	@ExportProperty(EventParticipantDto.INVOLVEMENT_DESCRIPTION)
	public String getInvolvmentDescription() {
		return involvmentDescription;
	}

	@Order(62)
	@ExportEntity(EventDto.class)
	@ExportProperty({
		EventParticipantDto.EVENT,
		EventDto.UUID })
	public String getEventUuid() {
		return eventUuid;
	}

	@Order(63)
	@ExportEntity(EventDto.class)
	@ExportProperty(EventDto.EVENT_STATUS)
	public EventStatus getEventStatus() {
		return eventStatus;
	}

	@Order(64)
	@ExportEntity(EventDto.class)
	@ExportProperty(EventDto.EVENT_INVESTIGATION_STATUS)
	public EventInvestigationStatus getEventInvestigationStatus() {
		return eventInvestigationStatus;
	}

	@Order(65)
	@ExportEntity(EventDto.class)
	@ExportProperty(EventDto.DISEASE)
	public Disease getEventDisease() {
		return eventDisease;
	}

	@Order(66)
	@ExportEntity(EventDto.class)
	@ExportProperty(EventDto.TYPE_OF_PLACE)
	public TypeOfPlace getTypeOfPlace() {
		return typeOfPlace;
	}

	@Order(67)
	@ExportEntity(EventDto.class)
	@ExportProperty(EventDto.START_DATE)
	public Date getEventStartDate() {
		return eventStartDate;
	}

	@Order(68)
	@ExportEntity(EventDto.class)
	@ExportProperty(EventDto.END_DATE)
	public Date getEventEndDate() {
		return eventEndDate;
	}

	@Order(69)
	@ExportEntity(EventDto.class)
	@ExportProperty(EventDto.EVENT_TITLE)
	public String getEventTitle() {
		return eventTitle;
	}

	@Order(70)
	@ExportEntity(EventDto.class)
	@ExportProperty(EventDto.EVENT_DESC)
	public String getEventDesc() {
		return eventDesc;
	}

	@Order(71)
	@ExportEntity(LocationDto.class)
	@ExportProperty({
		EventDto.EVENT_LOCATION,
		LocationDto.REGION })
	public String getEventRegion() {
		return eventRegion;
	}

	@Order(72)
	@ExportEntity(LocationDto.class)
	@ExportProperty({
		EventDto.EVENT_LOCATION,
		LocationDto.DISTRICT })
	public String getEventDistrict() {
		return eventDistrict;
	}

	@Order(73)
	@ExportEntity(LocationDto.class)
	@ExportProperty({
		EventDto.EVENT_LOCATION,
		LocationDto.COMMUNITY })
	public String getEventCommunity() {
		return eventCommunity;
	}

	@Order(74)
	@ExportEntity(LocationDto.class)
	@ExportProperty({
		EventDto.EVENT_LOCATION,
		LocationDto.CITY })
	public String getEventCity() {
		return eventCity;
	}

	@Order(75)
	@ExportEntity(LocationDto.class)
	@ExportProperty({
		EventDto.EVENT_LOCATION,
		LocationDto.STREET })
	public String getEventStreet() {
		return eventStreet;
	}

	@Order(76)
	@ExportEntity(LocationDto.class)
	@ExportProperty({
		EventDto.EVENT_LOCATION,
		LocationDto.HOUSE_NUMBER })
	public String getEventHouseNumber() {
		return eventHouseNumber;
	}

	@Order(77)
	@ExportProperty(EventParticipantExportDto.CONTACT_COUNT)
	public Long getContactCount() {
		return contactCount;
	}

	public void setContactCount(Long contactCount) {
		this.contactCount = contactCount;
	}

	public List<EmbeddedSampleExportDto> getEventParticipantSamples() {
		return eventParticipantSamples;
	}

	public void addEventParticipantSample(EmbeddedSampleExportDto exportSampleDto) {
		this.eventParticipantSamples.add(exportSampleDto);
	}

	public long getId() {
		return id;
	}

	public long getPersonId() {
		return personId;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setPersonId(long personId) {
		this.personId = personId;
	}

	public void setPersonAddressId(long personAddressId) {
		this.personAddressId = personAddressId;
	}

	public void setEventUuid(String eventUuid) {
		this.eventUuid = eventUuid;
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

	public void setAgeGroup(String ageGroup) {
		this.ageGroup = ageGroup;
	}

	public void setBirthdate(BirthDateDto birthdate) {
		this.birthdate = birthdate;
	}

	public void setPresentCondition(PresentCondition presentCondition) {
		this.presentCondition = presentCondition;
	}

	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	public void setBurialInfo(BurialInfoDto burialInfo) {
		this.burialInfo = burialInfo;
	}

	public void setAddressRegion(String addressRegion) {
		this.addressRegion = addressRegion;
	}

	public void setAddressDistrict(String addressDistrict) {
		this.addressDistrict = addressDistrict;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public void setAddressGpsCoordinates(String addressGpsCoordinates) {
		this.addressGpsCoordinates = addressGpsCoordinates;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public void setCaseUuid(String caseUuid) {
		this.caseUuid = caseUuid;
	}

	public void setEventParticipantSamples(List<EmbeddedSampleExportDto> eventParticipantSamples) {
		this.eventParticipantSamples = eventParticipantSamples;
	}

	public EventParticipantJurisdictionDto getJurisdiction() {
		return jurisdiction;
	}
}
