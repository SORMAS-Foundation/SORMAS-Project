package de.symeda.sormas.api.person;

import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

@SuppressWarnings("serial")
public class PersonSimilarityCriteria extends BaseCriteria implements Cloneable {

	public static final String NAME_UUID_EXTERNAL_ID_EXTERNAL_TOKEN_LIKE = "nameUuidExternalIdExternalTokenLike";
	public static final String BIRTHDATE_YYYY = "birthdateYYYY";
	public static final String BIRTHDATE_MM = "birthdateMM";
	public static final String BIRTHDATE_DD = "birthdateDD";

	private String firstName;
	private String lastName;
	private Sex sex;
	private Integer birthdateYYYY;
	private Integer birthdateMM;
	private Integer birthdateDD;
	private String passportNumber;
	private String nationalHealthId;
	private String nameUuidExternalIdExternalTokenLike;
	/**
	 * If true, compare the name of the person only to the first and last name fields of the database; if false, compare the
	 * name of the person to other fields like UUID and external ID as well.
	 */
	private Boolean strictNameComparison = Boolean.FALSE;

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

	public Boolean getStrictNameComparison() {
		return strictNameComparison;
	}

	public PersonSimilarityCriteria strictNameComparison(Boolean strictNameComparison) {
		this.strictNameComparison = strictNameComparison;
		return this;
	}

	public void setBirthdateYYYY(Integer birthdateYYYY) {
		this.birthdateYYYY = birthdateYYYY;
	}

	public void setBirthdateMM(Integer birthdateMM) {
		this.birthdateMM = birthdateMM;
	}

	public void setBirthdateDD(Integer birthdateDD) {
		this.birthdateDD = birthdateDD;
	}

	public static PersonSimilarityCriteria forPerson(PersonDto person) {
		return forPerson(person, false);
	}

	/**
	 * @param strictNameComparison
	 *            If true, compares the name of the person only to the first and last name fields of the database; if false, compares the
	 *            name of the person to other fields like UUID and external ID as well.
	 */
	public static PersonSimilarityCriteria forPerson(PersonDto person, boolean strictNameComparison) {

		PersonSimilarityCriteria personSimilarityCriteria = new PersonSimilarityCriteria().sex(person.getSex())
			.birthdateDD(person.getBirthdateDD())
			.birthdateMM(person.getBirthdateMM())
			.birthdateYYYY(person.getBirthdateYYYY())
			.passportNumber(person.getPassportNumber())
			.nationalHealthId(person.getNationalHealthId());
		if (strictNameComparison) {
			personSimilarityCriteria.firstName(person.getFirstName()).lastName(person.getLastName()).strictNameComparison(Boolean.TRUE);
		} else {
			personSimilarityCriteria.setNameUuidExternalIdExternalTokenLike(person.getFirstName() + " " + person.getLastName());
		}
		return personSimilarityCriteria;
	}

}
