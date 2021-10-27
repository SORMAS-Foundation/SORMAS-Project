package de.symeda.sormas.ui.utils.components.linelisting.person;

import java.io.Serializable;

import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.person.ApproximateAgeType;

public class PersonFieldDto implements Serializable {

	public static final String I18N_PREFIX = "Person";

	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String APPROXIMATE_AGE = "approximateAge";
	public static final String APPROXIMATE_AGE_TYPE = "approximateAgeType";
	public static final String BIRTH_DATE = "birthDate";
	public static final String SEX = "sex";

	private String firstName;
	private String lastName;
	private Integer approximateAge;
	private ApproximateAgeType approximateAgeType;
	private BirthDateDto birthDate;
	private Sex sex;

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
	
	public BirthDateDto getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(BirthDateDto birthDate) {
		this.birthDate = birthDate;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}
}
