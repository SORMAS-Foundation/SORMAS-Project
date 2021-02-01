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
import de.symeda.sormas.api.caze.EmbeddedSampleExportDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.importexport.ExportProperty;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.BurialConductor;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Salutation;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.EnumHelper;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.PostalCodePseudonymizer;

public class EventParticipantExportDto implements Serializable {

	public static final String I18N_PREFIX = "EventParticipantExport";

	public static final String EVENT_DISEASE = "eventDisease";
	public static final String EVENT_TYPE_OF_PLACE = "eventTypeOfPlace";
	public static final String EVENT_START_DATE = "eventStartDate";
	public static final String EVENT_END_DATE = "eventEndDate";
	public static final String EVENT_TITLE = "eventTitle";
	public static final String EVENT_DESCRIPTION = "eventDescription";
	public static final String EVENT_REGION = "eventRegion";
	public static final String EVENT_DISTRICT = "eventDistrict";
	public static final String EVENT_COMMUNITY = "eventCommunity";
	public static final String EVENT_CITY = "eventCity";
	public static final String EVENT_STREET = "eventStreet";
	public static final String EVENT_HOUSE_NUMBER = "eventHouseNumber";
	public static final String AGE_GROUP = "ageGroup";
	public static final String ADDRESS_REGION = "addressRegion";
	public static final String ADDRESS_DISTRICT = "addressDistrict";
	public static final String ADDRESS_GPS_COORDINATES = "addressGpsCoordinates";
	public static final String BURIAL_INFO = "burialInfo";
	public static final String SAMPLE_INFORMATION = "sampleInformation";
	public static final String PERSON_NATIONAL_HEALTH_ID = "personNationalHealthId";
	public static final String EVENT_PARTICIPANT_INVOLVMENT_DESCRIPTION = "eventParticipantInvolvmentDescription";
	public static final String EVENT_PARTICIPANT_UUID = "eventParticipantUuid";
	public static final String CONTACT_COUNT = "contactCount";

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
	@SensitiveData
	private String salutation;
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
									 String birthName, String birthCountryIsoCode, String birthCountryName, String citizenshipIsoCode, String citizenshipCountryName) {
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
		this.salutation = EnumHelper.toString(salutation, otherSalutation, Salutation.OTHER);
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

		jurisdiction = new EventParticipantJurisdictionDto(reportingUserUuid);
	}

	@Order(9)
	@ExportProperty(EventParticipantExportDto.EVENT_PARTICIPANT_UUID)
	public String getEventParticipantUuid() {
		return eventParticipantUuid;
	}

	public long getPersonAddressId() {
		return personAddressId;
	}

	@Order(10)
	public String getPersonUuid() {
		return personUuid;
	}

	@Order(11)
	@ExportProperty(EventParticipantExportDto.PERSON_NATIONAL_HEALTH_ID)
	public String getPersonNationalHealthId() {
		return personNationalHealthId;
	}

	@Order(12)
	public String getCaseUuid() {
		return caseUuid;
	}

	@Order(13)
	public String getFirstName() {
		return firstName;
	}

	@Order(14)
	public String getLastName() {
		return lastName;
	}

	@Order(15)
	@HideForCountriesExcept
	public String getSalutation() {
		return salutation;
	}

	@Order(16)
	public Sex getSex() {
		return sex;
	}

	@Order(17)
	public String getApproximateAge() {
		return approximateAge;
	}

	@Order(18)
	@ExportProperty(EventParticipantExportDto.AGE_GROUP)
	public String getAgeGroup() {
		return ageGroup;
	}

	@Order(19)
	public BirthDateDto getBirthdate() {
		return birthdate;
	}

	@Order(20)
	public PresentCondition getPresentCondition() {
		return presentCondition;
	}

	@Order(21)
	public Date getDeathDate() {
		return deathDate;
	}

	@Order(22)
	@ExportProperty(EventParticipantExportDto.BURIAL_INFO)
	public BurialInfoDto getBurialInfo() {
		return burialInfo;
	}

	@Order(31)
	@ExportProperty(EventParticipantExportDto.ADDRESS_REGION)
	public String getAddressRegion() {
		return addressRegion;
	}

	@Order(32)
	@ExportProperty(EventParticipantExportDto.ADDRESS_DISTRICT)
	public String getAddressDistrict() {
		return addressDistrict;
	}

	@Order(32)
	public String getAddressCommunity() {
		return addressCommunity;
	}

	@Order(33)
	public String getCity() {
		return city;
	}

	@Order(34)
	public String getStreet() {
		return street;
	}

	@Order(35)
	public String getHouseNumber() {
		return houseNumber;
	}

	@Order(36)
	public String getAdditionalInformation() {
		return additionalInformation;
	}

	@Order(37)
	public String getPostalCode() {
		return postalCode;
	}

	@Order(38)
	@ExportProperty(EventParticipantExportDto.ADDRESS_GPS_COORDINATES)
	public String getAddressGpsCoordinates() {
		return addressGpsCoordinates;
	}

	@Order(40)
	public String getPhone() {
		return phone;
	}

	@Order(41)
	public String getEmailAddress() {
		return emailAddress;
	}

	@Order(42)
	@ExportProperty(PersonDto.BIRTH_NAME)
	@HideForCountriesExcept
	public String getBirthName() {
		return birthName;
	}

	@Order(43)
	@ExportProperty(PersonDto.BIRTH_COUNTRY)
	@HideForCountriesExcept
	public String getBirthCountry() {
		return birthCountry;
	}

	@Order(44)
	@ExportProperty(PersonDto.CITIZENSHIP)
	@HideForCountriesExcept
	public String getCitizenship() {
		return citizenship;
	}

	@Order(45)
	@ExportProperty(EventParticipantExportDto.SAMPLE_INFORMATION)
	public String getOtherSamplesString() {
		StringBuilder samples = new StringBuilder();
		String separator = ", ";

		for (EmbeddedSampleExportDto sample : eventParticipantSamples) {
			samples.append(sample.formatString()).append(separator);
		}

		return samples.length() > 0 ? samples.substring(0, samples.length() - separator.length()) : "";
	}

	@Order(46)
	@ExportProperty(EventParticipantExportDto.EVENT_PARTICIPANT_INVOLVMENT_DESCRIPTION)
	public String getInvolvmentDescription() {
		return involvmentDescription;
	}

	@Order(50)
	@ExportProperty(EventDto.UUID)
	public String getEventUuid() {
		return eventUuid;
	}

	@Order(51)
	@ExportProperty(EventDto.EVENT_STATUS)
	public EventStatus getEventStatus() {
		return eventStatus;
	}

	@Order(52)
	@ExportProperty(EventDto.EVENT_INVESTIGATION_STATUS)
	public EventInvestigationStatus getEventInvestigationStatus() {
		return eventInvestigationStatus;
	}

	@Order(53)
	@ExportProperty(EventParticipantExportDto.EVENT_DISEASE)
	public Disease getEventDisease() {
		return eventDisease;
	}

	@Order(54)
	@ExportProperty(EventParticipantExportDto.EVENT_TYPE_OF_PLACE)
	public TypeOfPlace getTypeOfPlace() {
		return typeOfPlace;
	}

	@Order(55)
	@ExportProperty(EventParticipantExportDto.EVENT_START_DATE)
	public Date getEventStartDate() {
		return eventStartDate;
	}

	@Order(56)
	@ExportProperty(EventParticipantExportDto.EVENT_END_DATE)
	public Date getEventEndDate() {
		return eventEndDate;
	}

	@Order(57)
	@ExportProperty(EventParticipantExportDto.EVENT_TITLE)
	public String getEventTitle() {
		return eventTitle;
	}

	@Order(58)
	@ExportProperty(EventParticipantExportDto.EVENT_DESCRIPTION)
	public String getEventDesc() {
		return eventDesc;
	}

	@Order(59)
	@ExportProperty(EventParticipantExportDto.EVENT_REGION)
	public String getEventRegion() {
		return eventRegion;
	}

	@Order(60)
	@ExportProperty(EventParticipantExportDto.EVENT_DISTRICT)
	public String getEventDistrict() {
		return eventDistrict;
	}

	@Order(61)
	@ExportProperty(EventParticipantExportDto.EVENT_COMMUNITY)
	public String getEventCommunity() {
		return eventCommunity;
	}

	@Order(62)
	@ExportProperty(EventParticipantExportDto.EVENT_CITY)
	public String getEventCity() {
		return eventCity;
	}

	@Order(63)
	@ExportProperty(EventParticipantExportDto.EVENT_STREET)
	public String getEventStreet() {
		return eventStreet;
	}

	@Order(64)
	@ExportProperty(EventParticipantExportDto.EVENT_HOUSE_NUMBER)
	public String getEventHouseNumber() {
		return eventHouseNumber;
	}

	@Order(65)
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
