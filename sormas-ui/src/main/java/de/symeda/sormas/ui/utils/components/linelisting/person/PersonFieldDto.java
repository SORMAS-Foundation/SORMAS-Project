package de.symeda.sormas.ui.utils.components.linelisting.person;

import java.io.Serializable;

import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.person.Sex;

public class PersonFieldDto implements Serializable {

	public static final String I18N_PREFIX = "Person";

	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String BIRTH_DATE = "birthDate";
	public static final String SEX = "sex";

	private String firstName;
	private String lastName;
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
