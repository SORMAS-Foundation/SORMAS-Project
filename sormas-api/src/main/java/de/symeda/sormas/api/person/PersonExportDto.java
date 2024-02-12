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

package de.symeda.sormas.api.person;

import java.util.Date;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.importexport.ExportGroup;
import de.symeda.sormas.api.importexport.ExportGroupType;
import de.symeda.sormas.api.importexport.ExportProperty;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.HideForCountries;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.Outbreaks;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.PostalCodePseudonymizer;
import de.symeda.sormas.api.uuid.AbstractUuidDto;

public class PersonExportDto extends AbstractUuidDto {

	private static final long serialVersionUID = -6902138630884671263L;

	public static final String I18N_PREFIX = "PersonExport";

	public static final String ADDRESS_GPS_COORDINATES = "addressGpsCoordinates";

	@PersonalData
	@SensitiveData
	private String firstName;
	@PersonalData
	@SensitiveData
	private String lastName;
	@PersonalData
	@SensitiveData
	private Salutation salutation;
	@PersonalData
	@SensitiveData
	private String otherSalutation;
	private Sex sex;
	@EmbeddedPersonalData
	private BirthDateDto birthdate;
	private String approximateAge;
	private String ageGroup;

	@PersonalData
	@SensitiveData
	private String birthName;
	@PersonalData
	@SensitiveData
	private String nickname;
	@PersonalData
	@SensitiveData
	private String mothersName;
	@PersonalData
	@SensitiveData
	private String mothersMaidenName;
	@PersonalData
	@SensitiveData
	private String fathersName;
	@PersonalData
	@SensitiveData
	private String namesOfGuardians;

	@Outbreaks
	private PresentCondition presentCondition;
	private Date deathDate;
	private CauseOfDeath causeOfDeath;
	private Disease causeOfDeathDisease;
	@SensitiveData
	private String causeOfDeathDetails;

	private String region;
	private String district;
	@PersonalData
	@SensitiveData
	private String community;
	@PersonalData
	@SensitiveData
	private String street;
	@PersonalData
	@SensitiveData
	private String houseNumber;
	@PersonalData()
	@SensitiveData()
	@Pseudonymizer(PostalCodePseudonymizer.class)
	private String postalCode;
	@PersonalData
	@SensitiveData
	private String city;
	@PersonalData
	@SensitiveData
	private String additionalInformation;
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

	private EducationType educationType;
	@SensitiveData
	private String educationDetails;

	private OccupationType occupationType;
	@SensitiveData
	private String occupationDetails;
	@SensitiveData
	private ArmedForcesRelationType armedForcesRelationType;
	@SensitiveData
	private String passportNumber;
	@SensitiveData
	private String nationalHealthId;

	private boolean hasCovidApp;
	private boolean covidCodeDelivered;

	private SymptomJournalStatus symptomJournalStatus;
	private String externalId;
	private String externalToken;
	private String internalToken;

	@SensitiveData
	private String birthCountry;
	@SensitiveData
	private String citizenship;
	@SensitiveData
	private String additionalDetails;

	private Boolean isInJurisdiction;

	public PersonExportDto(
		String uuid,
		String firstName,
		String lastName,
		Salutation salutation,
		String otherSalutation,
		Sex sex,
		Integer age,
		ApproximateAgeType ageType,
		Integer birthdateDD,
		Integer birthdateMM,
		Integer birthdateYYYY,
		String nickname,
		String mothersName,
		String mothersMaidenName,
		String fathersName,
		String namesOfGuardians,
		PresentCondition presentCondition,
		Date deathDate,
		CauseOfDeath causeOfDeath,
		String causeOfDeathDetails,
		Disease causeOfDeathDisease,
		String region,
		String district,
		String community,
		String street,
		String houseNumber,
		String postalCode,
		String city,
		String additionalInformation,
		String facility,
		String facilityDetails,
		String phone,
		String phoneOwner,
		String emailAddress,
		String otherContactDetails,
		EducationType educationType,
		String educationDetails,
		OccupationType occupationType,
		String occupationDetails,
		ArmedForcesRelationType armedForcesRelationType,
		String passportNumber,
		String nationalHealthId,
		boolean hasCovidApp,
		boolean covidCodeDelivered,
		SymptomJournalStatus symptomJournalStatus,
		String externalId,
		String externalToken,
		String internalToken,
		String birthCountryIsoCode,
		String birthCountryName,
		String citizenshipIsoCode,
		String citizenshipCountryName,
		String additionalDetails,
		Boolean isInJurisdiction) {
		super(uuid);
		this.firstName = firstName;
		this.lastName = lastName;
		this.salutation = salutation;
		this.otherSalutation = otherSalutation;
		this.sex = sex;
		this.approximateAge = ApproximateAgeType.ApproximateAgeHelper.formatApproximateAge(age, ageType);
		this.ageGroup = ApproximateAgeType.ApproximateAgeHelper.getAgeGroupFromAge(age, ageType);
		this.birthdate = new BirthDateDto(birthdateDD, birthdateMM, birthdateYYYY);
		this.birthName = birthName;
		this.nickname = nickname;
		this.mothersName = mothersName;
		this.mothersMaidenName = mothersMaidenName;
		this.fathersName = fathersName;
		this.namesOfGuardians = namesOfGuardians;
		this.presentCondition = presentCondition;
		this.deathDate = deathDate;
		this.causeOfDeath = causeOfDeath;
		this.causeOfDeathDetails = causeOfDeathDetails;
		this.causeOfDeathDisease = causeOfDeathDisease;
		this.region = region;
		this.district = district;
		this.community = community;
		this.street = street;
		this.houseNumber = houseNumber;
		this.postalCode = postalCode;
		this.city = city;
		this.additionalInformation = additionalInformation;
		this.facility = facility;
		this.facilityDetails = facilityDetails;
		this.phone = phone;
		this.phoneOwner = phoneOwner;
		this.emailAddress = emailAddress;
		this.otherContactDetails = otherContactDetails;
		this.educationType = educationType;
		this.educationDetails = educationDetails;
		this.occupationType = occupationType;
		this.occupationDetails = occupationDetails;
		this.armedForcesRelationType = armedForcesRelationType;
		this.passportNumber = passportNumber;
		this.nationalHealthId = nationalHealthId;
		this.hasCovidApp = hasCovidApp;
		this.covidCodeDelivered = covidCodeDelivered;
		this.symptomJournalStatus = symptomJournalStatus;
		this.externalId = externalId;
		this.externalToken = externalToken;
		this.internalToken = internalToken;
		this.birthCountry = I18nProperties.getCountryName(birthCountryIsoCode, birthCountryName);
		this.citizenship = I18nProperties.getCountryName(citizenshipIsoCode, citizenshipCountryName);
		this.additionalDetails = additionalDetails;
		this.isInJurisdiction = isInJurisdiction;
	}

	public PersonExportDto(
		String uuid,
		String firstName,
		String lastName,
		Sex sex,
		String district,
		String street,
		String houseNumber,
		String postalCode,
		String city,
		String phone,
		String email,
		boolean isInJurisdiction) {

		super(uuid);
		this.firstName = firstName;
		this.lastName = lastName;
		this.sex = sex;
		this.district = district;
		this.street = street;
		this.houseNumber = houseNumber;
		this.postalCode = postalCode;
		this.city = city;
		this.phone = phone;
		this.emailAddress = email;
		this.isInJurisdiction = isInJurisdiction;
	}

	@Order(0)
	@ExportProperty(PersonDto.UUID)
	@ExportGroup(ExportGroupType.CORE)
	public String getUuid() {
		return super.getUuid();
	}

	@Order(1)
	@ExportProperty(PersonDto.FIRST_NAME)
	@ExportGroup(ExportGroupType.CORE)
	public String getFirstName() {
		return firstName;
	}

	@Order(2)
	@ExportProperty(PersonDto.LAST_NAME)
	@ExportGroup(ExportGroupType.CORE)
	public String getLastName() {
		return lastName;
	}

	@Order(3)
	@ExportProperty(PersonDto.SALUTATION)
	@ExportGroup(ExportGroupType.CORE)
	@HideForCountriesExcept
	public Salutation getSalutation() {
		return salutation;
	}

	@Order(4)
	@ExportProperty(PersonDto.OTHER_SALUTATION)
	@ExportGroup(ExportGroupType.CORE)
	@HideForCountriesExcept
	public String getOtherSalutation() {
		return otherSalutation;
	}

	@Order(5)
	@ExportProperty(PersonDto.SEX)
	@ExportGroup(ExportGroupType.CORE)
	public Sex getSex() {
		return sex;
	}

	@Order(6)
	@ExportProperty(PersonDto.BIRTH_DATE)
	@ExportGroup(ExportGroupType.CORE)
	public BirthDateDto getBirthdate() {
		return birthdate;
	}

	@Order(7)
	@ExportProperty(PersonDto.APPROXIMATE_AGE)
	@ExportGroup(ExportGroupType.CORE)
	public String getApproximateAge() {
		return approximateAge;
	}

	@Order(8)
	@ExportProperty(PersonDto.APPROXIMATE_AGE_GROUP)
	@ExportGroup(ExportGroupType.CORE)
	public String getAgeGroup() {
		return ageGroup;
	}

	@Order(15)
	@ExportProperty(PersonDto.BIRTH_NAME)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept
	public String getBirthName() {
		return birthName;
	}

	@Order(16)
	@ExportProperty(PersonDto.NICKNAME)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountries
	public String getNickname() {
		return nickname;
	}

	@Order(17)
	@ExportProperty(PersonDto.MOTHERS_NAME)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_FRANCE })
	public String getMothersName() {
		return mothersName;
	}

	@Order(18)
	@ExportProperty(PersonDto.MOTHERS_MAIDEN_NAME)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_FRANCE })
	public String getMothersMaidenName() {
		return mothersMaidenName;
	}

	@Order(19)
	@ExportProperty(PersonDto.FATHERS_NAME)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_FRANCE })
	public String getFathersName() {
		return fathersName;
	}

	@Order(20)
	@ExportProperty(PersonDto.NAMES_OF_GUARDIANS)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept
	public String getNamesOfGuardians() {
		return namesOfGuardians;
	}

	@Order(25)
	@ExportProperty(PersonDto.PRESENT_CONDITION)
	@ExportGroup(ExportGroupType.PERSON)
	public PresentCondition getPresentCondition() {
		return presentCondition;
	}

	@Order(26)
	@ExportProperty(PersonDto.DEATH_DATE)
	@ExportGroup(ExportGroupType.PERSON)
	public Date getDeathDate() {
		return deathDate;
	}

	@Order(27)
	@ExportProperty(PersonDto.CAUSE_OF_DEATH)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public CauseOfDeath getCauseOfDeath() {
		return causeOfDeath;
	}

	@Order(28)
	@ExportProperty(PersonDto.CAUSE_OF_DEATH_DETAILS)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getCauseOfDeathDetails() {
		return causeOfDeathDetails;
	}

	@Order(29)
	@ExportProperty(PersonDto.CAUSE_OF_DEATH_DISEASE)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public Disease getCauseOfDeathDisease() {
		return causeOfDeathDisease;
	}

	@Order(35)
	@ExportProperty({
		PersonDto.ADDRESS,
		LocationDto.REGION })
	@ExportGroup(ExportGroupType.LOCATION)
	public String getRegion() {
		return region;
	}

	@Order(36)
	@ExportProperty({
		PersonDto.ADDRESS,
		LocationDto.DISTRICT })
	@ExportGroup(ExportGroupType.LOCATION)
	public String getDistrict() {
		return district;
	}

	@Order(37)
	@ExportProperty({
		PersonDto.ADDRESS,
		LocationDto.COMMUNITY })
	@ExportGroup(ExportGroupType.LOCATION)
	public String getCommunity() {
		return community;
	}

	@Order(38)
	@ExportProperty({
		PersonDto.ADDRESS,
		LocationDto.STREET })
	@ExportGroup(ExportGroupType.LOCATION)
	public String getStreet() {
		return street;
	}

	@Order(39)
	@ExportProperty({
		PersonDto.ADDRESS,
		LocationDto.HOUSE_NUMBER })
	@ExportGroup(ExportGroupType.LOCATION)
	public String getHouseNumber() {
		return houseNumber;
	}

	@Order(40)
	@ExportProperty({
		PersonDto.ADDRESS,
		LocationDto.POSTAL_CODE })
	@ExportGroup(ExportGroupType.LOCATION)
	public String getPostalCode() {
		return postalCode;
	}

	@Order(41)
	@ExportProperty({
		PersonDto.ADDRESS,
		LocationDto.CITY })
	@ExportGroup(ExportGroupType.LOCATION)
	public String getCity() {
		return city;
	}

	@Order(42)
	@ExportProperty({
		PersonDto.ADDRESS,
		LocationDto.ADDITIONAL_INFORMATION })
	@ExportGroup(ExportGroupType.LOCATION)
	public String getAdditionalInformation() {
		return additionalInformation;
	}

	@Order(43)
	@ExportProperty({
		PersonDto.ADDRESS,
		LocationDto.FACILITY })
	@ExportGroup(ExportGroupType.LOCATION)
	public String getFacility() {
		return facility;
	}

	@Order(44)
	@ExportProperty({
		PersonDto.ADDRESS,
		LocationDto.FACILITY_DETAILS })
	@ExportGroup(ExportGroupType.LOCATION)
	public String getFacilityDetails() {
		return facilityDetails;
	}

	@Order(50)
	@ExportProperty(PersonDto.PHONE)
	@ExportGroup(ExportGroupType.CORE)
	public String getPhone() {
		return phone;
	}

	@Order(51)
	@ExportProperty(PersonDto.PHONE_OWNER)
	@ExportGroup(ExportGroupType.CORE)
	public String getPhoneOwner() {
		return phoneOwner;
	}

	@Order(52)
	@ExportProperty(PersonDto.EMAIL_ADDRESS)
	@ExportGroup(ExportGroupType.CORE)
	public String getEmailAddress() {
		return emailAddress;
	}

	@Order(53)
	@ExportProperty(PersonDto.OTHER_CONTACT_DETAILS)
	@ExportGroup(ExportGroupType.CORE)
	public String getOtherContactDetails() {
		return otherContactDetails;
	}

	@Order(54)
	@ExportProperty(value = PersonDto.EDUCATION_TYPE, combined = true)
	@ExportGroup(ExportGroupType.PERSON)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_FRANCE })
	public EducationType getEducationType() {
		return educationType;
	}

	@Order(55)
	@ExportProperty(value = PersonDto.EDUCATION_TYPE, combined = true)
	@ExportGroup(ExportGroupType.PERSON)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_FRANCE })
	public String getEducationDetails() {
		return educationDetails;
	}

	@Order(56)
	@ExportProperty(value = PersonDto.OCCUPATION_TYPE, combined = true)
	@ExportGroup(ExportGroupType.PERSON)
	public OccupationType getOccupationType() {
		return occupationType;
	}

	@Order(57)
	@ExportProperty(value = PersonDto.OCCUPATION_TYPE, combined = true)
	@ExportGroup(ExportGroupType.PERSON)
	public String getOccupationDetails() {
		return occupationDetails;
	}

	@Order(58)
	@ExportProperty(PersonDto.ARMED_FORCES_RELATION_TYPE)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept
	public ArmedForcesRelationType getArmedForcesRelationType() {
		return armedForcesRelationType;
	}

	@Order(60)
	@ExportProperty(PersonDto.PASSPORT_NUMBER)
	@ExportGroup(ExportGroupType.CORE)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_FRANCE })
	public String getPassportNumber() {
		return passportNumber;
	}

	@Order(61)
	@ExportProperty(PersonDto.NATIONAL_HEALTH_ID)
	@ExportGroup(ExportGroupType.CORE)
	@HideForCountries
	public String getNationalHealthId() {
		return nationalHealthId;
	}

	@Order(62)
	@ExportProperty(PersonDto.HAS_COVID_APP)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_SWITZERLAND)
	public boolean isHasCovidApp() {
		return hasCovidApp;
	}

	@Order(63)
	@ExportProperty(PersonDto.COVID_CODE_DELIVERED)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_SWITZERLAND)
	public boolean isCovidCodeDelivered() {
		return covidCodeDelivered;
	}

	@Order(64)
	@ExportProperty(PersonDto.SYMPTOM_JOURNAL_STATUS)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public SymptomJournalStatus getSymptomJournalStatus() {
		return symptomJournalStatus;
	}

	@Order(70)
	@ExportProperty(PersonDto.EXTERNAL_ID)
	@ExportGroup(ExportGroupType.CORE)
	@HideForCountriesExcept
	public String getExternalId() {
		return externalId;
	}

	@Order(71)
	@ExportProperty(PersonDto.EXTERNAL_TOKEN)
	@ExportGroup(ExportGroupType.CORE)
	@HideForCountriesExcept
	public String getExternalToken() {
		return externalToken;
	}

	@Order(72)
	@ExportProperty(PersonDto.INTERNAL_TOKEN)
	@ExportGroup(ExportGroupType.CORE)
	public String getInternalToken() {
		return internalToken;
	}

	@Order(73)
	@ExportProperty(PersonDto.BIRTH_COUNTRY)
	@ExportGroup(ExportGroupType.PERSON)
	@HideForCountriesExcept(countries = {})
	public String getBirthCountry() {
		return birthCountry;
	}

	@Order(74)
	@ExportProperty(PersonDto.CITIZENSHIP)
	@ExportGroup(ExportGroupType.PERSON)
	@HideForCountriesExcept(countries = {})
	public String getCitizenship() {
		return citizenship;
	}

	@Order(75)
	@ExportProperty(PersonDto.ADDITIONAL_DETAILS)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getAdditionalDetails() {
		return additionalDetails;
	}

	public Boolean getInJurisdiction() {
		return isInJurisdiction;
	}
}
