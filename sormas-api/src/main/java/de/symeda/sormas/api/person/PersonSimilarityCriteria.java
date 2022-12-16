package de.symeda.sormas.api.person;

import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;

@SuppressWarnings("serial")
public class PersonSimilarityCriteria extends BaseCriteria implements Cloneable {

	public static final String NAME_UUID_EXTERNAL_ID_EXTERNAL_TOKEN_LIKE = "nameUuidExternalIdExternalTokenLike";
	public static final String BIRTHDATE_YYYY = "birthdateYYYY";
	public static final String BIRTHDATE_MM = "birthdateMM";
	public static final String BIRTHDATE_DD = "birthdateDD";

	private Sex sex;
	@Schema(description = "Birth year that should be filtered for")
	private Integer birthdateYYYY;
	@Schema(description = "Birth month that should be filtered for")
	private Integer birthdateMM;
	@Schema(description = "Birth day that should be filtered for")
	private Integer birthdateDD;
	@Schema(description = "Passport number of a person that should be filtered for")
	private String passportNumber;
	@Schema(description = "National health ID of a person that should be filtered for")
	private String nationalHealthId;
	@Schema(description = "Filter pattern for name, UUID, external ID or external Token")
	private String nameUuidExternalIdExternalTokenLike;
	private Boolean matchMissingInfo = Boolean.FALSE;

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

	@Hidden
	public void setName(PersonDto person) {
		this.nameUuidExternalIdExternalTokenLike = person.getFirstName() + " " + person.getLastName();
	}

	public Boolean getMatchMissingInfo() {
		return matchMissingInfo;
	}

	public void setMatchMissingInfo(Boolean matchMissingInfo) {
		this.matchMissingInfo = matchMissingInfo;
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
		PersonSimilarityCriteria personSimilarityCriteria = new PersonSimilarityCriteria().sex(person.getSex())
			.birthdateDD(person.getBirthdateDD())
			.birthdateMM(person.getBirthdateMM())
			.birthdateYYYY(person.getBirthdateYYYY())
			.passportNumber(person.getPassportNumber())
			.nationalHealthId(person.getNationalHealthId());
		personSimilarityCriteria.setNameUuidExternalIdExternalTokenLike(person.getFirstName() + " " + person.getLastName());
		return personSimilarityCriteria;
	}
}
