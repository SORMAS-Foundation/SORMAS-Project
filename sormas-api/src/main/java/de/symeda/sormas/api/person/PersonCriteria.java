package de.symeda.sormas.api.person;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public class PersonCriteria extends BaseCriteria implements Cloneable {

	public static final String BIRTHDATE_YYYY = "birthdateYYYY";
	public static final String BIRTHDATE_MM = "birthdateMM";
	public static final String BIRTHDATE_DD = "birthdateDD";
	public static final String NAME_ADDRESS_PHONE_EMAIL_LIKE = "nameAddressPhoneEmailLike";
	public static final String PRESENT_CONDITION = "presentCondition";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String PERSON_ASSOCIATION = "personAssociation";

	private Integer birthdateYYYY;
	private Integer birthdateMM;
	private Integer birthdateDD;
	private String nameAddressPhoneEmailLike;
	private PresentCondition presentCondition;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community;
	private PersonAssociation personAssociation;

	public Integer getBirthdateYYYY() {
		return birthdateYYYY;
	}

	public void setBirthdateYYYY(Integer birthdateYYYY) {
		this.birthdateYYYY = birthdateYYYY;
	}

	public Integer getBirthdateMM() {
		return birthdateMM;
	}

	public void setBirthdateMM(Integer birthdateMM) {
		this.birthdateMM = birthdateMM;
	}

	public Integer getBirthdateDD() {
		return birthdateDD;
	}

	public void setBirthdateDD(Integer birthdateDD) {
		this.birthdateDD = birthdateDD;
	}

	public String getNameAddressPhoneEmailLike() {
		return nameAddressPhoneEmailLike;
	}

	public void setNameAddressPhoneEmailLike(String nameAddressPhoneEmailLike) {
		this.nameAddressPhoneEmailLike = nameAddressPhoneEmailLike;
	}

	public PresentCondition getPresentCondition() {
		return presentCondition;
	}

	public void setPresentCondition(PresentCondition presentCondition) {
		this.presentCondition = presentCondition;
	}

	public PersonCriteria presentCondition(PresentCondition presentCondition) {
		this.presentCondition = presentCondition;
		return this;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public CommunityReferenceDto getCommunity() {
		return community;
	}

	public void setCommunity(CommunityReferenceDto community) {
		this.community = community;
	}

	public PersonAssociation getPersonAssociation() {
		return personAssociation;
	}

	public void setPersonAssociation(PersonAssociation personAssociation) {
		this.personAssociation = personAssociation;
	}

	public PersonCriteria personAssociation(PersonAssociation personAssociation) {
		this.personAssociation = personAssociation;
		return this;
	}
}
