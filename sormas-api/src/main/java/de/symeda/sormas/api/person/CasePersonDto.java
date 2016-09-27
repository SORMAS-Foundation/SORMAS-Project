package de.symeda.sormas.api.person;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.utils.PreciseDateAdapter;

public class CasePersonDto extends PersonDto {

	private static final long serialVersionUID = -8558187171374254398L;

	public static final String I18N_PREFIX = "CasePerson";

	public static final String SEX = "sex";

	public static final String PRESENT_CONDITION = "presentCondition";
	public static final String BIRTH_DATE_DD = "birthdateDD";
	public static final String BIRTH_DATE_MM = "birthdateMM";
	public static final String BIRTH_DATE_YYYY = "birthdateYYYY";
	public static final String DEATH_DATE = "deathDate";
	public static final String APPROXIMATE_AGE = "approximateAge";
	public static final String APPROXIMATE_AGE_TYPE = "approximateAgeType";
	
	public static final String PHONE = "phone";
	public static final String PHONE_OWNER = "phoneOwner";
	public static final String ADDRESS = "address";
	
	public static final String OCCUPATION_TYPE = "occupationType";
	public static final String OCCUPATION_DETAILS = "occupationDetails";
	public static final String OCCUPATION_FACILITY = "occupationFacility";
	
	
	private Sex sex;
		
	private PresentCondition presentCondition;
	private Integer birthdateDD;
	private Integer birthdateMM;
	private Integer birthdateYYYY;
	private Date deathDate;
	private Integer approximateAge;
	private ApproximateAgeType approximateAgeType;
		
	private String phone;
	private String phoneOwner;
	private LocationDto address;
	
	private OccupationType occupationType;
	private String occupationDetails;
	private ReferenceDto occupationFacility;

	
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
	
	public PresentCondition getPresentCondition() {
		return presentCondition;
	}
	public void setPresentCondition(PresentCondition presentCondition) {
		this.presentCondition = presentCondition;
	}

	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getDeathDate() {
		return deathDate;
	}

	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhoneOwner() {
		return phoneOwner;
	}

	public void setPhoneOwner(String phoneOwner) {
		this.phoneOwner = phoneOwner;
	}

	public LocationDto getAddress() {
		return address;
	}

	public void setAddress(LocationDto address) {
		this.address = address;
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

	public ReferenceDto getOccupationFacility() {
		return occupationFacility;
	}

	public void setOccupationFacility(ReferenceDto occupationFacility) {
		this.occupationFacility = occupationFacility;
	}
}
