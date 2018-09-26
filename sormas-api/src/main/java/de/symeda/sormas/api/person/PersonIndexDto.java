package de.symeda.sormas.api.person;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.utils.DataHelper.Pair;

public class PersonIndexDto implements Serializable {

	private static final long serialVersionUID = -8558187171374254398L;

	public static final String I18N_PREFIX = "Person";

	public static final String UUID = "uuid";
	public static final String SEX = "sex";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String PRESENT_CONDITION = "presentCondition";
	public static final String APPROXIMATE_AGE = "approximateAge";
	public static final String APPROXIMATE_AGE_TYPE = "approximateAgeType";
	public static final String NICKNAME = "nickname";
	public static final String DISTRICT_NAME = "districtName";
	public static final String COMMUNITY_NAME = "communityName";
	public static final String CITY = "city";
	public static final String LAST_DISEASE = "lastDisease";
	public static final String LAST_DISEASE_START_DATE = "lastDiseaseStartDate";
	public static final String LAST_CASE_UUID = "lastCaseUuid";
	
	private String uuid;
	private Sex sex;
	private String firstName;
	private String lastName;
	private PresentCondition presentCondition;
	private Integer birthdateDD;
	private Integer birthdateMM;
	private Integer birthdateYYYY;
	private Integer approximateAge;
	private ApproximateAgeType approximateAgeType;
	private Date deathDate;
	private String nickname;
	private String districtName;
	private String communityName;
	private String city;
	private Disease lastDisease;
	private Date lastDiseaseStartDate;
	private String lastCaseUuid;

	public PersonIndexDto(String uuid, Sex sex, String firstName, String lastName, PresentCondition presentCondition,
			Integer birthdateDD, Integer birthdateMM, Integer birthdateYYYY, Integer approximateAge, 
			ApproximateAgeType approximateAgeType, Date deathDate, String nickname, String districtName,
			String communityName, String city) {
		this.uuid = uuid;
		this.sex = sex;
		this.firstName = firstName;
		this.lastName = lastName;
		this.presentCondition = presentCondition;
		this.birthdateDD = birthdateDD;
		this.birthdateMM = birthdateMM;
		this.birthdateYYYY = birthdateYYYY;
		this.deathDate = deathDate;
		this.nickname = nickname;
		this.districtName = districtName;
		this.communityName = communityName;
		this.city = city;
		
		if (birthdateYYYY != null) {
			Calendar birthdate = new GregorianCalendar();
			birthdate.set(birthdateYYYY, birthdateMM!=null?birthdateMM-1:0, birthdateDD!=null?birthdateDD:1);			
			Pair<Integer, ApproximateAgeType> pair = ApproximateAgeHelper.getApproximateAge(birthdate.getTime(), deathDate);
			this.approximateAge = pair.getElement0();
			this.approximateAgeType = pair.getElement1();
		}
		else {
			this.approximateAge = approximateAge;
			this.approximateAgeType = approximateAgeType;
		}
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
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

	public PresentCondition getPresentCondition() {
		return presentCondition;
	}
	public void setPresentCondition(PresentCondition presentCondition) {
		this.presentCondition = presentCondition;
	}

	@Override
	public String toString() {
		return PersonDto.buildCaption(firstName, lastName);
	}
	
	public PersonReferenceDto toReference() {
		return new PersonReferenceDto(getUuid(), firstName, lastName);
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

	public Date getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
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

	public Disease getLastDisease() {
		return lastDisease;
	}

	public void setLastDisease(Disease lastDisease) {
		this.lastDisease = lastDisease;
	}

	public Date getLastDiseaseStartDate() {
		return lastDiseaseStartDate;
	}

	public void setLastDiseaseStartDate(Date lastDiseaseStartDate) {
		this.lastDiseaseStartDate = lastDiseaseStartDate;
	}

	public String getLastCaseUuid() {
		return lastCaseUuid;
	}

	public void setLastCaseUuid(String lastCaseUuid) {
		this.lastCaseUuid = lastCaseUuid;
	}
	
}