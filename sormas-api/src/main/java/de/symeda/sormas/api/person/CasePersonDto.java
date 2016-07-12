package de.symeda.sormas.api.person;

import java.util.Date;

public class CasePersonDto extends PersonDto {

	private static final long serialVersionUID = -8558187171374254398L;

	public static final String I18N_PREFIX = "CasePerson";

	public static final String SEX = "sex";

	public static final String BIRTH_DATE = "birthDate";
	public static final String DEATH_DATE = "deathDate";
	public static final String APPROXIMATE_AGE = "approximateAge";
	
	public static final String PHONE = "phone";
	
	public static final String OCCUPATION_TYPE = "occupationType";
	public static final String OCCUPATION_DETAILS = "occupationDetails";
	public static final String OCCUPATION_FACILITY = "occupationFacility";
	
	
	private Sex sex;
	
	private Date birthDate;
	private Date deathDate;
	private String approximateAge;
		
	private String phone;
	
	private OccupationType occupationType;
	private String occupationDetails;
	private String occupationFacility;
	
	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getApproximateAge() {
		return approximateAge;
	}

	public void setApproximateAge(String approximateAge) {
		this.approximateAge = approximateAge;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public Date getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public OccupationType getOccupationType() {
		return occupationType;
	}

	public void setOccupationType(OccupationType occupationType) {
		this.occupationType = occupationType;
	}

	public String getOccupationDetails() {
		return occupationDetails;
	}

	public void setOccupationDetails(String occupationDetails) {
		this.occupationDetails = occupationDetails;
	}

	public String getOccupationFacility() {
		return occupationFacility;
	}

	public void setOccupationFacility(String occupationFacility) {
		this.occupationFacility = occupationFacility;
	}
	
	
}
