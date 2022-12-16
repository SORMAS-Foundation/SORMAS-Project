package de.symeda.sormas.api.person;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.PostalCodePseudonymizer;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;

import org.apache.commons.lang3.StringUtils;

public class PersonIndexDto extends PseudonymizableIndexDto implements Serializable, Cloneable {

	public static final String I18N_PREFIX = "Person";

	public static final String UUID = "uuid";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String AGE_AND_BIRTH_DATE = "ageAndBirthDate";
	public static final String SEX = "sex";
	public static final String DISTRICT = "district";
	public static final String STREET = "street";
	public static final String HOUSE_NUMBER = "houseNumber";
	public static final String POSTAL_CODE = "postalCode";
	public static final String CITY = "city";
	public static final String PHONE = "phone";
	public static final String EMAIL_ADDRESS = "emailAddress";

	@PersonalData
	@SensitiveData
	@Schema(description = "First name(s) of the person")
	private String firstName;
	@PersonalData
	@SensitiveData
	@Schema(description = "Last name of the person")
	private String lastName;
	private AgeAndBirthDateDto ageAndBirthDate;
	private Sex sex;
	@Schema(description = "Name of the district the person lives in")
	private String district;
	@PersonalData
	@SensitiveData
	@Schema(description = "Name of the street the person lives in")
	private String street;
	@PersonalData
	@SensitiveData
	@Schema(description = "Number of the house the person lives in")
	private String houseNumber;
	@PersonalData()
	@SensitiveData()
	@Pseudonymizer(PostalCodePseudonymizer.class)
	@Schema(description = "Postal code of the community the person lives in")
	private String postalCode;
	@PersonalData
	@SensitiveData
	@Schema(description = "Name of the city/communty the person lives in")
	private String city;
	@SensitiveData
	@Schema(description = "Person's phone number")
	private String phone;
	@SensitiveData
	@Schema(description = "Person's e-mail address")
	private String emailAddress;
	@Schema(description = "The date the person data entry was last changed")
	private Date changeDate;

	@Schema(description = "Whether the DTO is in the user's jurisdiction. Used to determine which user right needs to be considered"
		+ "to decide whether sensitive and/or personal data is supposed to be shown.")
	private Boolean isInJurisdiction;

	public PersonIndexDto(
		String uuid,
		String firstName,
		String lastName,
		Integer age,
		ApproximateAgeType ageType,
		Integer birthdateDD,
		Integer birthdateMM,
		Integer birthdateYYYY,
		Sex sex,
		String district,
		String street,
		String houseNumber,
		String postalCode,
		String city,
		String phone,
		String email,
		Date changeDate,
		boolean isInJurisdiction) {

		super(uuid);
		this.firstName = firstName;
		this.lastName = lastName;
		this.ageAndBirthDate = new AgeAndBirthDateDto(age, ageType, birthdateDD, birthdateMM, birthdateYYYY);
		this.sex = sex;
		this.district = district;
		this.street = street;
		this.houseNumber = houseNumber;
		this.postalCode = postalCode;
		this.city = city;
		this.phone = phone;
		this.emailAddress = email;
		this.changeDate = changeDate;
		this.isInJurisdiction = isInJurisdiction;
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

	public AgeAndBirthDateDto getAgeAndBirthDate() {
		return ageAndBirthDate;
	}

	public void setAgeAndBirthDate(AgeAndBirthDateDto ageAndBirthDate) {
		this.ageAndBirthDate = ageAndBirthDate;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public Date getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	public Boolean getInJurisdiction() {
		return isInJurisdiction;
	}

	public void setInJurisdiction(Boolean inJurisdiction) {
		isInJurisdiction = inJurisdiction;
	}

	@Override
	public String getCaption() {
		return PersonDto.buildCaption(getFirstName(), getLastName());
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
