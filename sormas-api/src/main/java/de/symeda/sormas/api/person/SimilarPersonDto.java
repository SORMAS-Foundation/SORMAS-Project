package de.symeda.sormas.api.person;

import java.io.Serializable;

public class SimilarPersonDto implements Serializable {

	public static final String I18N_PREFIX = "Person";

	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String NICKNAME = "nickname";
	public static final String APPROXIMATE_AGE = "approximateAge";
	public static final String SEX = "sex";
	public static final String PRESENT_CONDITION = "presentCondition";
	public static final String DISTRICT_NAME = "districtName";
	public static final String COMMUNITY_NAME = "communityName";
	public static final String CITY = "city";
	public static final String NATIONAL_HEALTH_ID = "nationalHealthId";
	public static final String PASSPORT_NUMBER = "passportNumber";

	private String uuid;
	private String firstName;
	private String lastName;
	private String nickname;
	private Integer approximateAge;
	private Sex sex;
	private PresentCondition presentCondition;
	private String districtName;
	private String communityName;
	private String city;
	private String nationalHealthId;
	private String passportNumber;

	public SimilarPersonDto(
		String uuid,
		String firstName,
		String lastName,
		String nickname,
		Integer approximateAge,
		Sex sex,
		PresentCondition presentCondition,
		String districtName,
		String communityName,
		String city,
		String nationalHealthId,
		String passportNumber) {
		this.uuid = uuid;
		this.firstName = firstName;
		this.lastName = lastName;
		this.nickname = nickname;
		this.approximateAge = approximateAge;
		this.sex = sex;
		this.presentCondition = presentCondition;
		this.districtName = districtName;
		this.communityName = communityName;
		this.city = city;
		this.nationalHealthId = nationalHealthId;
		this.passportNumber = passportNumber;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Integer getApproximateAge() {
		return approximateAge;
	}

	public void setApproximateAge(Integer approximateAge) {
		this.approximateAge = approximateAge;
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

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getCommunityName() {
		return communityName;
	}

	public void setCommunityName(String communityName) {
		this.communityName = communityName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getNationalHealthId() {
		return nationalHealthId;
	}

	public void setNationalHealthId(String nationalHealthId) {
		this.nationalHealthId = nationalHealthId;
	}

	public String getPassportNumber() {
		return passportNumber;
	}

	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}

	public PersonReferenceDto toReference() {
		return new PersonReferenceDto(getUuid(), firstName, lastName);
	}

	@Override
	public String toString() {
		return PersonDto.buildCaption(firstName, lastName);
	}
}
