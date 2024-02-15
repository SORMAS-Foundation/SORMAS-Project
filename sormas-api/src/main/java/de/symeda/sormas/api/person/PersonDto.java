/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ImportIgnore;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.sormastosormas.S2SIgnoreProperty;
import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.EmbeddedSensitiveData;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.HideForCountries;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.Outbreaks;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

@DependingOnFeatureType(featureType = {
	FeatureType.CASE_SURVEILANCE,
	FeatureType.CONTACT_TRACING,
	FeatureType.EVENT_SURVEILLANCE })
public class PersonDto extends PseudonymizableDto implements IsPerson {

	public static final long APPROXIMATE_JSON_SIZE_IN_BYTES = 42953;

	public static final String I18N_PREFIX = "Person";
	public static final String SEX = "sex";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String SALUTATION = "salutation";
	public static final String OTHER_SALUTATION = "otherSalutation";
	public static final String PRESENT_CONDITION = "presentCondition";
	public static final String BIRTH_DATE = "birthdate";
	public static final String BIRTH_DATE_DD = "birthdateDD";
	public static final String BIRTH_DATE_MM = "birthdateMM";
	public static final String BIRTH_DATE_YYYY = "birthdateYYYY";
	public static final String APPROXIMATE_AGE = "approximateAge";
	public static final String APPROXIMATE_AGE_GROUP = "approximateAgeGroup";
	public static final String APPROXIMATE_AGE_TYPE = "approximateAgeType";
	public static final String APPROXIMATE_AGE_REFERENCE_DATE = "approximateAgeReferenceDate";
	public static final String CAUSE_OF_DEATH = "causeOfDeath";
	public static final String CAUSE_OF_DEATH_DISEASE = "causeOfDeathDisease";
	public static final String CAUSE_OF_DEATH_DETAILS = "causeOfDeathDetails";
	public static final String CAUSE_OF_DEATH_DISEASE_DETAILS = "causeOfDeathDiseaseDetails";
	public static final String DEATH_DATE = "deathDate";
	public static final String DEATH_PLACE_TYPE = "deathPlaceType";
	public static final String DEATH_PLACE_DESCRIPTION = "deathPlaceDescription";
	public static final String BURIAL_DATE = "burialDate";
	public static final String BURIAL_PLACE_DESCRIPTION = "burialPlaceDescription";
	public static final String BURIAL_CONDUCTOR = "burialConductor";
	public static final String BIRTH_NAME = "birthName";
	public static final String NICKNAME = "nickname";
	public static final String MOTHERS_MAIDEN_NAME = "mothersMaidenName";
	public static final String PHONE = "phone";
	public static final String PHONE_OWNER = "phoneOwner";
	public static final String ADDRESS = "address";
	public static final String EDUCATION_TYPE = "educationType";
	public static final String EDUCATION_DETAILS = "educationDetails";
	public static final String OCCUPATION_TYPE = "occupationType";
	public static final String OCCUPATION_DETAILS = "occupationDetails";
	public static final String ARMED_FORCES_RELATION_TYPE = "armedForcesRelationType";
	public static final String FATHERS_NAME = "fathersName";
	public static final String MOTHERS_NAME = "mothersName";
	public static final String NAMES_OF_GUARDIANS = "namesOfGuardians";
	public static final String PLACE_OF_BIRTH_REGION = "placeOfBirthRegion";
	public static final String PLACE_OF_BIRTH_DISTRICT = "placeOfBirthDistrict";
	public static final String PLACE_OF_BIRTH_COMMUNITY = "placeOfBirthCommunity";
	public static final String PLACE_OF_BIRTH_FACILITY = "placeOfBirthFacility";
	public static final String PLACE_OF_BIRTH_FACILITY_DETAILS = "placeOfBirthFacilityDetails";
	public static final String GESTATION_AGE_AT_BIRTH = "gestationAgeAtBirth";
	public static final String BIRTH_WEIGHT = "birthWeight";
	public static final String PASSPORT_NUMBER = "passportNumber";
	public static final String NATIONAL_HEALTH_ID = "nationalHealthId";
	public static final String EMAIL_ADDRESS = "emailAddress";
	public static final String OTHER_CONTACT_DETAILS = "otherContactDetails";
	public static final String PLACE_OF_BIRTH_FACILITY_TYPE = "placeOfBirthFacilityType";
	public static final String ADDRESSES = "addresses";
	public static final String PERSON_CONTACT_DETAILS = "personContactDetails";
	public static final String SYMPTOM_JOURNAL_STATUS = "symptomJournalStatus";
	public static final String HAS_COVID_APP = "hasCovidApp";
	public static final String COVID_CODE_DELIVERED = "covidCodeDelivered";
	public static final String EXTERNAL_ID = "externalId";
	public static final String EXTERNAL_TOKEN = "externalToken";
	public static final String INTERNAL_TOKEN = "internalToken";
	public static final String BIRTH_COUNTRY = "birthCountry";
	public static final String CITIZENSHIP = "citizenship";
	public static final String ADDITIONAL_DETAILS = "additionalDetails";
	private static final long serialVersionUID = -8558187171374254398L;

	// Fields are declared in the order they should appear in the import template
	@Outbreaks
	@NotBlank(message = Validations.specifyFirstName)
	@PersonalData(mandatoryField = true)
	@SensitiveData(mandatoryField = true)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String firstName;
	@Outbreaks
	@NotBlank(message = Validations.specifyLastName)
	@PersonalData(mandatoryField = true)
	@SensitiveData(mandatoryField = true)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String lastName;
	@HideForCountriesExcept
	@PersonalData
	@SensitiveData
	private Salutation salutation;
	@PersonalData
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String otherSalutation;
	@PersonalData
	@SensitiveData
	@HideForCountriesExcept
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String birthName;
	@PersonalData
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@HideForCountries
	private String nickname;
	@PersonalData
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_FRANCE })
	private String mothersName;
	@PersonalData
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_FRANCE })
	private String mothersMaidenName;
	@PersonalData
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_FRANCE })
	private String fathersName;
	@PersonalData
	@SensitiveData
	@HideForCountriesExcept
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String namesOfGuardians;
	@Outbreaks
	@NotNull(message = Validations.specifySex)
	private Sex sex;
	@Outbreaks
	@PersonalData
	@SensitiveData
	private Integer birthdateDD;
	@Outbreaks
	private Integer birthdateMM;
	@Outbreaks
	private Integer birthdateYYYY;
	@Outbreaks
	private Integer approximateAge;
	@Outbreaks
	private ApproximateAgeType approximateAgeType;
	@Outbreaks
	private Date approximateAgeReferenceDate;
	@Diseases({
		Disease.CONGENITAL_RUBELLA })
	@HideForCountries
	private RegionReferenceDto placeOfBirthRegion;
	@Diseases({
		Disease.CONGENITAL_RUBELLA })
	@HideForCountries
	private DistrictReferenceDto placeOfBirthDistrict;
	@Diseases({
		Disease.CONGENITAL_RUBELLA })
	@HideForCountries
	@SensitiveData
	private CommunityReferenceDto placeOfBirthCommunity;
	@Diseases({
		Disease.CONGENITAL_RUBELLA })
	@HideForCountries
	private FacilityType placeOfBirthFacilityType;
	@Diseases({
		Disease.CONGENITAL_RUBELLA })
	@HideForCountries
	@SensitiveData
	private FacilityReferenceDto placeOfBirthFacility;
	@Diseases({
		Disease.CONGENITAL_RUBELLA })
	@HideForCountries
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String placeOfBirthFacilityDetails;
	@Diseases({
		Disease.CONGENITAL_RUBELLA })
	@HideForCountries
	private Integer gestationAgeAtBirth;
	@Diseases({
		Disease.CONGENITAL_RUBELLA })
	@HideForCountries
	private Integer birthWeight;

	@Outbreaks
	private PresentCondition presentCondition;
	private Date deathDate;
	private CauseOfDeath causeOfDeath;
	private Disease causeOfDeathDisease;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String causeOfDeathDetails;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.POLIO,
		Disease.UNSPECIFIED_VHF,
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private DeathPlaceType deathPlaceType;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.POLIO,
		Disease.UNSPECIFIED_VHF,
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String deathPlaceDescription;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.POLIO,
		Disease.UNSPECIFIED_VHF,
		Disease.UNDEFINED,
		Disease.OTHER })
	@HideForCountries
	private Date burialDate;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.POLIO,
		Disease.UNSPECIFIED_VHF,
		Disease.UNDEFINED,
		Disease.OTHER })
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@HideForCountries
	private String burialPlaceDescription;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.POLIO,
		Disease.UNSPECIFIED_VHF,
		Disease.UNDEFINED,
		Disease.OTHER })
	@HideForCountries
	private BurialConductor burialConductor;
	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	@Valid
	private LocationDto address;

	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_FRANCE })
	private EducationType educationType;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_FRANCE })
	private String educationDetails;

	private OccupationType occupationType;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String occupationDetails;
	@SensitiveData
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private ArmedForcesRelationType armedForcesRelationType;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_FRANCE })
	private String passportNumber;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	@HideForCountries
	private String nationalHealthId;
	@Valid
	private List<LocationDto> addresses = new ArrayList<>();
	@Valid
	private List<PersonContactDetailDto> personContactDetails = new ArrayList<>();

	@Diseases(Disease.CORONAVIRUS)
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_SWITZERLAND)
	private boolean hasCovidApp;
	@Diseases(Disease.CORONAVIRUS)
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_SWITZERLAND)
	private boolean covidCodeDelivered;

	private SymptomJournalStatus symptomJournalStatus;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	@S2SIgnoreProperty(configProperty = SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_ID)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalId;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	@S2SIgnoreProperty(configProperty = SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_TOKEN)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalToken;
	@S2SIgnoreProperty(configProperty = SormasToSormasConfig.SORMAS2SORMAS_IGNORE_INTERNAL_TOKEN)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String internalToken;

	@HideForCountriesExcept(countries = {})
	@SensitiveData
	private CountryReferenceDto birthCountry;
	@HideForCountriesExcept(countries = {})
	@SensitiveData
	private CountryReferenceDto citizenship;
	@SensitiveData
	@S2SIgnoreProperty(configProperty = SormasToSormasConfig.SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String additionalDetails;

	@SuppressWarnings("serial")
	public static class SeveralNonPrimaryContactDetailsException extends RuntimeException {

		public SeveralNonPrimaryContactDetailsException(String message) {
			super(message);
		}
	}

	public static String buildCaption(String firstName, String lastName) {
		return DataHelper.toStringNullable(firstName) + " " + DataHelper.toStringNullable(replaceGermanChars(lastName)).toUpperCase();
	}

	/*
	 * Since there is a common problem in jdk when we call 'ß'.toUpperCase() => 'SS' , the simple workaround is to
	 * replace all 'ß' (lower-case) with the 'ẞ' (upper-case) using chars unicodes.
	 * - ß (lowercase) 00DF
	 * - ẞ (capital) 1E9E
	 */
	private static String replaceGermanChars(String value) {
		if (Strings.isNullOrEmpty(value)) {
			return value;
		}
		return value.replaceAll("\u00DF", "\u1E9E");
	}

	public static PersonDto build() {

		PersonDto person = new PersonDto();
		person.setUuid(DataHelper.createUuid());
		person.setAddress(LocationDto.build());
		return person;
	}

	public static PersonDto buildImportEntity() {

		PersonDto person = build();
		person.setSex(Sex.UNKNOWN);
		return person;
	}

	public Integer getBirthdateDD() {
		return birthdateDD;
	}

	public void setBirthdateDD(Integer birthdateDD) {
		this.birthdateDD = birthdateDD;
	}

	public Integer getBirthdateMM() {
		return birthdateMM;
	}

	public void setBirthdateMM(Integer birthdateMM) {
		this.birthdateMM = birthdateMM;
	}

	public Integer getBirthdateYYYY() {
		return birthdateYYYY;
	}

	public void setBirthdateYYYY(Integer birthdateYYYY) {
		this.birthdateYYYY = birthdateYYYY;
	}

	public Integer getApproximateAge() {
		return approximateAge;
	}

	public void setApproximateAge(Integer approximateAge) {
		this.approximateAge = approximateAge;
	}

	public ApproximateAgeType getApproximateAgeType() {
		return approximateAgeType;
	}

	public void setApproximateAgeType(ApproximateAgeType approximateAgeType) {
		this.approximateAgeType = approximateAgeType;
	}

	public Date getApproximateAgeReferenceDate() {
		return approximateAgeReferenceDate;
	}

	public void setApproximateAgeReferenceDate(Date approximateAgeReferenceDate) {
		this.approximateAgeReferenceDate = approximateAgeReferenceDate;
	}

	public DeathPlaceType getDeathPlaceType() {
		return deathPlaceType;
	}

	public void setDeathPlaceType(DeathPlaceType deathPlaceType) {
		this.deathPlaceType = deathPlaceType;
	}

	public String getDeathPlaceDescription() {
		return deathPlaceDescription;
	}

	public void setDeathPlaceDescription(String deathPlaceDescription) {
		this.deathPlaceDescription = deathPlaceDescription;
	}

	public Date getBurialDate() {
		return burialDate;
	}

	public void setBurialDate(Date burialDate) {
		this.burialDate = burialDate;
	}

	public String getBurialPlaceDescription() {
		return burialPlaceDescription;
	}

	public void setBurialPlaceDescription(String burialPlaceDescription) {
		this.burialPlaceDescription = burialPlaceDescription;
	}

	public BurialConductor getBurialConductor() {
		return burialConductor;
	}

	public void setBurialConductor(BurialConductor burialConductor) {
		this.burialConductor = burialConductor;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public PresentCondition getPresentCondition() {
		return presentCondition;
	}

	public void setPresentCondition(PresentCondition presentCondition) {
		this.presentCondition = presentCondition;
	}

	public CauseOfDeath getCauseOfDeath() {
		return causeOfDeath;
	}

	public void setCauseOfDeath(CauseOfDeath causeOfDeath) {
		this.causeOfDeath = causeOfDeath;
	}

	public String getCauseOfDeathDetails() {
		return causeOfDeathDetails;
	}

	public void setCauseOfDeathDetails(String causeOfDeathDetails) {
		this.causeOfDeathDetails = causeOfDeathDetails;
	}

	public Disease getCauseOfDeathDisease() {
		return causeOfDeathDisease;
	}

	public void setCauseOfDeathDisease(Disease causeOfDeathDisease) {
		this.causeOfDeathDisease = causeOfDeathDisease;
	}

	public Date getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	private void setPersonContactInformation(String contactInfo, PersonContactDetailType personContactDetailType, boolean primary) {
		for (PersonContactDetailDto contactDetailDto : getPersonContactDetails()) {
			if (contactDetailDto.getPersonContactDetailType() == personContactDetailType && contactDetailDto.isPrimaryContact()) {
				if (contactInfo.equals(contactDetailDto.getContactInformation())) {
					return;
				}
				if (primary) {
					contactDetailDto.setPrimaryContact(false);
				}
			}
		}
		final PersonContactDetailDto pcd =
			PersonContactDetailDto.build(this.toReference(), primary, personContactDetailType, null, null, contactInfo, null, false, null, null);
		getPersonContactDetails().add(pcd);
	}

	/**
	 *
	 * @return the String representation of the PRIMARY phone number. Phone numbers set with the {@link #setPhone(String)} method
	 *         automatically become primary.
	 *         A phone number entered in the personEditForm is not, and thus does not become primary phone number unless the user
	 *         specifically sets it.
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	public String getPhone() {
		return getPersonContactInformation(PersonContactDetailType.PHONE);
	}

	/**
	 *
	 * @param onlyPrimary
	 *            if true, the return value is same as in {@link #getPhone()}. Otherwise, this method tries to return the only phone
	 *            number for this person, no matter if primary or not. Results in an SeveralNonPrimaryContactDetailsException when there are
	 *            several phone numbers.
	 * @return String representation of the only phone number to be used.
	 * @throws SeveralNonPrimaryContactDetailsException
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	public String getPhone(boolean onlyPrimary) throws SeveralNonPrimaryContactDetailsException {
		String primaryPhone = getPhone();
		if (onlyPrimary || StringUtils.isNotBlank(primaryPhone)) {
			return primaryPhone;
		} else {
			List<String> allPhones = getAllPhoneNumbers();
			if (CollectionUtils.isEmpty(allPhones)) {
				return "";
			} else if (allPhones.size() > 1) {
				throw new SeveralNonPrimaryContactDetailsException("Too many results found, none of which is marked primary.");
			} else {
				return allPhones.get(0);
			}
		}
	}

	@JsonIgnore
	public PhoneNumberType getPhoneNumberType() {
		for (PersonContactDetailDto contactDetailDto : getPersonContactDetails()) {
			if (contactDetailDto.getPersonContactDetailType() == PersonContactDetailType.PHONE && contactDetailDto.isPrimaryContact()) {
				return contactDetailDto.getPhoneNumberType();
			}
		}
		return null;
	}

	@JsonIgnore
	public void setPhoneNumberType(PhoneNumberType phoneNumberType) {
		for (PersonContactDetailDto contactDetailDto : getPersonContactDetails()) {
			if (contactDetailDto.getPersonContactDetailType() == PersonContactDetailType.PHONE && contactDetailDto.isPrimaryContact()) {
				contactDetailDto.setPhoneNumberType(phoneNumberType);
				break;
			}
		}
	}

	@JsonIgnore
	public List<String> getAllPhoneNumbers() {
		ArrayList<String> result = new ArrayList<>();
		for (PersonContactDetailDto pcd : getPersonContactDetails()) {
			if (pcd.getPersonContactDetailType() == PersonContactDetailType.PHONE) {
				result.add(pcd.getContactInformation());
			}
		}
		return result;
	}

	/**
	 * @param phone
	 *            is automatically set as primary phone number, removing the primary status from another phone number if necessary.
	 */
	@JsonIgnore
	public void setPhone(String phone) {
		setPersonContactInformation(phone, PersonContactDetailType.PHONE, true);
	}

	/**
	 * @param phone
	 *            is set as an additional non-primary phone number
	 */
	@JsonIgnore
	public void setAdditionalPhone(String phone) {
		setPersonContactInformation(phone, PersonContactDetailType.PHONE, false);
	}

	/**
	 * 
	 * @return the PRIMARY email address. Email addresses set with the {@link #setEmailAddress(String)} method automatically become primary.
	 *         An email address entered in the personEditForm is not, and thus does not become primary email address unless the user
	 *         specifically sets it.
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	public String getEmailAddress() {
		return getPersonContactInformation(PersonContactDetailType.EMAIL);
	}

	/**
	 * 
	 * @param onlyPrimary
	 *            if true, the return value is same as in {@link #getEmailAddress()}. Otherwise, this method tries to return the only email
	 *            address for this person, no matter if primary or not. Results in an SeveralNonPrimaryContactDetailsException when there
	 *            are several email
	 *            addresses.
	 * @return the only email address to be used.
	 * @throws SeveralNonPrimaryContactDetailsException
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	public String getEmailAddress(boolean onlyPrimary) throws SeveralNonPrimaryContactDetailsException {
		String primaryEmail = getEmailAddress();
		if (onlyPrimary || StringUtils.isNotBlank(primaryEmail)) {
			return primaryEmail;
		} else {
			List<String> allEmails = getAllEmailAddresses();
			if (CollectionUtils.isEmpty(allEmails)) {
				return "";
			} else if (allEmails.size() > 1) {
				throw new SeveralNonPrimaryContactDetailsException("Too many results found, none of which is marked primary.");
			} else {
				return allEmails.get(0);
			}
		}
	}

	@JsonIgnore
	public List<String> getAllEmailAddresses() {
		ArrayList<String> result = new ArrayList<>();
		for (PersonContactDetailDto pcd : getPersonContactDetails()) {
			if (pcd.getPersonContactDetailType() == PersonContactDetailType.EMAIL) {
				result.add(pcd.getContactInformation());
			}
		}
		return result;
	}

	/**
	 * 
	 * @param email
	 *            is automatically set as primary email address, removing the primary status from another email address if necessary.
	 */
	@JsonIgnore
	public void setEmailAddress(String email) {
		setPersonContactInformation(email, PersonContactDetailType.EMAIL, true);
	}

	private String getPersonContactInformation(PersonContactDetailType personContactDetailType) {
		for (PersonContactDetailDto pcd : getPersonContactDetails()) {
			if (pcd.isPrimaryContact() && pcd.getPersonContactDetailType() == personContactDetailType) {
				return pcd.getContactInformation();
			}
		}
		return "";
	}

	public LocationDto getAddress() {
		return address;
	}

	public void setAddress(LocationDto address) {
		this.address = address;
	}

	public String getBirthName() {
		return birthName;
	}

	public void setBirthName(String birthName) {
		this.birthName = birthName;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getMothersMaidenName() {
		return mothersMaidenName;
	}

	public void setMothersMaidenName(String mothersMaidenName) {
		this.mothersMaidenName = mothersMaidenName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Salutation getSalutation() {
		return salutation;
	}

	public void setSalutation(Salutation salutation) {
		this.salutation = salutation;
	}

	public String getOtherSalutation() {
		return otherSalutation;
	}

	public void setOtherSalutation(String otherSalutation) {
		this.otherSalutation = otherSalutation;
	}

	public EducationType getEducationType() {
		return educationType;
	}

	public void setEducationType(EducationType educationType) {
		this.educationType = educationType;
	}

	public String getEducationDetails() {
		return educationDetails;
	}

	public void setEducationDetails(String educationDetails) {
		this.educationDetails = educationDetails;
	}

	public OccupationType getOccupationType() {
		return occupationType;
	}

	public void setOccupationType(OccupationType occupationType) {
		this.occupationType = occupationType;
	}

	public String getOccupationDetails() {
		return occupationDetails;
	}

	public void setOccupationDetails(String occupationDetails) {
		this.occupationDetails = occupationDetails;
	}

	public ArmedForcesRelationType getArmedForcesRelationType() {
		return armedForcesRelationType;
	}

	public void setArmedForcesRelationType(ArmedForcesRelationType armedForcesRelationType) {
		this.armedForcesRelationType = armedForcesRelationType;
	}

	public String getMothersName() {
		return mothersName;
	}

	public void setMothersName(String mothersName) {
		this.mothersName = mothersName;
	}

	public String getFathersName() {
		return fathersName;
	}

	public void setFathersName(String fathersName) {
		this.fathersName = fathersName;
	}

	public String getNamesOfGuardians() {
		return namesOfGuardians;
	}

	public void setNamesOfGuardians(String namesOfGuardians) {
		this.namesOfGuardians = namesOfGuardians;
	}

	public RegionReferenceDto getPlaceOfBirthRegion() {
		return placeOfBirthRegion;
	}

	public void setPlaceOfBirthRegion(RegionReferenceDto placeOfBirthRegion) {
		this.placeOfBirthRegion = placeOfBirthRegion;
	}

	public DistrictReferenceDto getPlaceOfBirthDistrict() {
		return placeOfBirthDistrict;
	}

	public void setPlaceOfBirthDistrict(DistrictReferenceDto placeOfBirthDistrict) {
		this.placeOfBirthDistrict = placeOfBirthDistrict;
	}

	public CommunityReferenceDto getPlaceOfBirthCommunity() {
		return placeOfBirthCommunity;
	}

	public void setPlaceOfBirthCommunity(CommunityReferenceDto placeOfBirthCommunity) {
		this.placeOfBirthCommunity = placeOfBirthCommunity;
	}

	public FacilityReferenceDto getPlaceOfBirthFacility() {
		return placeOfBirthFacility;
	}

	public void setPlaceOfBirthFacility(FacilityReferenceDto placeOfBirthFacility) {
		this.placeOfBirthFacility = placeOfBirthFacility;
	}

	public String getPlaceOfBirthFacilityDetails() {
		return placeOfBirthFacilityDetails;
	}

	public void setPlaceOfBirthFacilityDetails(String placeOfBirthFacilityDetails) {
		this.placeOfBirthFacilityDetails = placeOfBirthFacilityDetails;
	}

	public Integer getGestationAgeAtBirth() {
		return gestationAgeAtBirth;
	}

	public void setGestationAgeAtBirth(Integer gestationAgeAtBirth) {
		this.gestationAgeAtBirth = gestationAgeAtBirth;
	}

	public Integer getBirthWeight() {
		return birthWeight;
	}

	public void setBirthWeight(Integer birthWeight) {
		this.birthWeight = birthWeight;
	}

	public String getPassportNumber() {
		return passportNumber;
	}

	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}

	public String getNationalHealthId() {
		return nationalHealthId;
	}

	public void setNationalHealthId(String nationalHealthId) {
		this.nationalHealthId = nationalHealthId;
	}

	public FacilityType getPlaceOfBirthFacilityType() {
		return placeOfBirthFacilityType;
	}

	public void setPlaceOfBirthFacilityType(FacilityType placeOfBirthFacilityType) {
		this.placeOfBirthFacilityType = placeOfBirthFacilityType;
	}

	@ImportIgnore
	public List<LocationDto> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<LocationDto> addresses) {
		this.addresses = addresses;
	}

	public void addAddress(LocationDto address) {
		addresses.add(address);
	}

	@ImportIgnore
	public List<PersonContactDetailDto> getPersonContactDetails() {
		return personContactDetails;
	}

	public void setPersonContactDetails(List<PersonContactDetailDto> personContactDetails) {
		this.personContactDetails = personContactDetails;
	}

	public SymptomJournalStatus getSymptomJournalStatus() {
		return symptomJournalStatus;
	}

	public void setSymptomJournalStatus(SymptomJournalStatus symptomJournalStatus) {
		this.symptomJournalStatus = symptomJournalStatus;
	}

	@JsonIgnore
	public boolean isEnrolledInExternalJournal() {
		return SymptomJournalStatus.ACCEPTED.equals(symptomJournalStatus) || SymptomJournalStatus.REGISTERED.equals(symptomJournalStatus);
	}

	public boolean isHasCovidApp() {
		return hasCovidApp;
	}

	public void setHasCovidApp(boolean hasCovidApp) {
		this.hasCovidApp = hasCovidApp;
	}

	public boolean isCovidCodeDelivered() {
		return covidCodeDelivered;
	}

	public void setCovidCodeDelivered(boolean covidCodeDelivered) {
		this.covidCodeDelivered = covidCodeDelivered;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
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

	public CountryReferenceDto getBirthCountry() {
		return birthCountry;
	}

	public void setBirthCountry(CountryReferenceDto birthCountry) {
		this.birthCountry = birthCountry;
	}

	public CountryReferenceDto getCitizenship() {
		return citizenship;
	}

	public void setCitizenship(CountryReferenceDto citizenship) {
		this.citizenship = citizenship;
	}

	public String getAdditionalDetails() {
		return additionalDetails;
	}

	public void setAdditionalDetails(String additionalDetails) {
		this.additionalDetails = additionalDetails;
	}

	@Override
	public String buildCaption() {
		return buildCaption(firstName, lastName);
	}

	@JsonIgnore
	public String i18nPrefix() {
		return I18N_PREFIX;
	}

	public PersonReferenceDto toReference() {
		return new PersonReferenceDto(getUuid(), firstName, lastName);
	}

	@Override
	public PersonDto clone() throws CloneNotSupportedException {
		PersonDto clone = (PersonDto) super.clone();
		clone.setAddress((LocationDto) getAddress().clone());

		List<LocationDto> addressesClone = new ArrayList<>();
		for (LocationDto locationDto : getAddresses()) {
			addressesClone.add((LocationDto) locationDto.clone());
		}
		clone.setAddresses(addressesClone);

		List<PersonContactDetailDto> contactDetailsClone = new ArrayList<>();
		for (PersonContactDetailDto personContactDetailDto : getPersonContactDetails()) {
			contactDetailsClone.add((PersonContactDetailDto) personContactDetailDto.clone());
		}
		clone.setPersonContactDetails(contactDetailsClone);

		return clone;
	}
}
