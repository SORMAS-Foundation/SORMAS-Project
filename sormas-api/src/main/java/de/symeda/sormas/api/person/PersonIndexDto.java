package de.symeda.sormas.api.person;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.utils.DataHelper.Pair;

public class PersonIndexDto implements Serializable {

	private static final long serialVersionUID = -8558187171374254398L;

	public static final String I18N_PREFIX = "CasePerson";

	public static final String UUID = "uuid";
	public static final String SEX = "sex";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String PRESENT_CONDITION = "presentCondition";
	public static final String APPROXIMATE_AGE = "approximateAge";
	public static final String APPROXIMATE_AGE_TYPE = "approximateAgeType";
	
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

	public PersonIndexDto(String uuid, Sex sex, String firstName, String lastName, PresentCondition presentCondition,
			Integer birthdateDD, Integer birthdateMM, Integer birthdateYYYY,
			Integer approximateAge, ApproximateAgeType approximateAgeType, Date deathDate) {
		this.uuid = uuid;
		this.sex = sex;
		this.firstName = firstName;
		this.lastName = lastName;
		this.presentCondition = presentCondition;
		this.birthdateDD = birthdateDD;
		this.birthdateMM = birthdateMM;
		this.birthdateYYYY = birthdateYYYY;
		this.deathDate = deathDate;
		
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
}
