package de.symeda.sormas.api.person;

import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

@SuppressWarnings("serial")
public class PersonSimilarityCriteria extends BaseCriteria implements Cloneable {

	public static final String NAME_UUID_EXTERNAL_ID_EXTERNAL_TOKEN_LIKE = "nameUuidExternalIdExternalTokenLike";

	private Sex sex;
	private Integer birthdateYYYY;
	private Integer birthdateMM;
	private Integer birthdateDD;
	private String passportNumber;
	private String nationalHealthId;
	private String nameUuidExternalIdExternalTokenLike;

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

	@IgnoreForUrl
	public String getPassportNumber() {
		return passportNumber;
	}

	public PersonSimilarityCriteria passportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
		return this;
	}

	@IgnoreForUrl
	public String getNationalHealthId() {
		return nationalHealthId;
	}

	public PersonSimilarityCriteria nationalHealthId(String nationalHealthId) {
		this.nationalHealthId = nationalHealthId;
		return this;
	}

	public String getNameUuidExternalIdExternalTokenLike() {
		return nameUuidExternalIdExternalTokenLike;
	}

	public void setNameUuidExternalIdExternalTokenLike(String nameUuidExternalIdExternalTokenLike) {
		this.nameUuidExternalIdExternalTokenLike = nameUuidExternalIdExternalTokenLike;
	}

	public void setName(PersonDto person) {
		this.nameUuidExternalIdExternalTokenLike = person.getFirstName() + " " + person.getLastName();
	}

	public static PersonSimilarityCriteria forPerson(PersonDto person) {
		PersonSimilarityCriteria personSimilarityCriteria = new PersonSimilarityCriteria()
				.sex(person.getSex())
				.birthdateDD(person.getBirthdateDD())
				.birthdateMM(person.getBirthdateMM())
				.birthdateYYYY(person.getBirthdateYYYY())
				.passportNumber(person.getPassportNumber())
				.nationalHealthId(person.getNationalHealthId());
		personSimilarityCriteria.setNameUuidExternalIdExternalTokenLike(person.getFirstName() + " " + person.getLastName());
		return personSimilarityCriteria;
	}
}
