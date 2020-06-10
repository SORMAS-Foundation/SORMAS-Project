package de.symeda.sormas.api.person;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.utils.IgnoreForUrl;

@SuppressWarnings("serial")
public class PersonSimilarityCriteria extends BaseCriteria implements Cloneable {

	private String firstName;
	private String lastName;
	private Sex sex;
	private Integer birthdateYYYY;
	private Integer birthdateMM;
	private Integer birthdateDD;

	@IgnoreForUrl
	public String getFirstName() {
		return firstName;
	}

	public PersonSimilarityCriteria firstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	@IgnoreForUrl
	public String getLastName() {
		return lastName;
	}

	public PersonSimilarityCriteria lastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	@IgnoreForUrl
	public Sex getSex() {
		return sex;
	}

	public PersonSimilarityCriteria sex(Sex sex) {
		this.sex = sex;
		return this;
	}

	@IgnoreForUrl
	public Integer getBirthdateYYYY() {
		return birthdateYYYY;
	}

	public PersonSimilarityCriteria birthdateYYYY(Integer birthdateYYYY) {
		this.birthdateYYYY = birthdateYYYY;
		return this;
	}

	@IgnoreForUrl
	public Integer getBirthdateMM() {
		return birthdateMM;
	}

	public PersonSimilarityCriteria birthdateMM(Integer birthdateMM) {
		this.birthdateMM = birthdateMM;
		return this;
	}

	@IgnoreForUrl
	public Integer getBirthdateDD() {
		return birthdateDD;
	}

	public PersonSimilarityCriteria birthdateDD(Integer birthdateDD) {
		this.birthdateDD = birthdateDD;
		return this;
	}
}
