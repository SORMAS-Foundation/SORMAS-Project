package de.symeda.sormas.api.person;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.PreciseDateAdapter;

public class PersonDto extends PersonReferenceDto {

	private static final long serialVersionUID = -8558187171374254398L;

	public static final String I18N_PREFIX = "Person";

	public static final String SEX = "sex";

	public static final String PRESENT_CONDITION = "presentCondition";
	public static final String BIRTH_DATE_DD = "birthdateDD";
	public static final String BIRTH_DATE_MM = "birthdateMM";
	public static final String BIRTH_DATE_YYYY = "birthdateYYYY";
	
	public static final String APPROXIMATE_AGE = "approximateAge";
	public static final String APPROXIMATE_AGE_TYPE = "approximateAgeType";
	
	public static final String DEATH_DATE = "deathDate";
	public static final String DEATH_PLACE_TYPE = "deathPlaceType";
	public static final String DEATH_PLACE_DESCRIPTION = "deathPlaceDescription";
	public static final String BURIAL_DATE = "burialDate";
	public static final String BURIAL_PLACE_DESCRIPTION = "burialPlaceDescription";
	public static final String BURIAL_CONDUCTOR = "burialConductor";
	
	public static final String NICKNAME = "nickname";
	public static final String MOTHERS_MAIDEN_NAME = "mothersMaidenName";
	
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
	private Integer approximateAge;
	private ApproximateAgeType approximateAgeType;
	private Date deathDate;
	@Diseases({Disease.EVD})
	private DeathPlaceType deathPlaceType;
	@Diseases({Disease.EVD})
	private String deathPlaceDescription;
	@Diseases({Disease.EVD})
	private Date burialDate;
	@Diseases({Disease.EVD})
	private String burialPlaceDescription;
	@Diseases({Disease.EVD})
	private BurialConductor burialConductor;
		
	private String nickname;
	private String mothersMaidenName;
	
	private String phone;
	private String phoneOwner;
	private LocationDto address;
	
	private OccupationType occupationType;
	private String occupationDetails;
	private FacilityReferenceDto occupationFacility;

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

	
	public DeathPlaceType getDeathPlaceType() {
		return deathPlaceType;
	}

	public void setDeathPlaceType(DeathPlaceType deathPlaceType) {
		this.deathPlaceType = deathPlaceType;
	}

	public String getDeathPlaceDescription() {
		return deathPlaceDescription;
	}

	public void setDeathPlaceDescription(String deathPlaceDescription) {
		this.deathPlaceDescription = deathPlaceDescription;
	}

	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getBurialDate() {
		return burialDate;
	}

	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setBurialDate(Date burialDate) {
		this.burialDate = burialDate;
	}

	public String getBurialPlaceDescription() {
		return burialPlaceDescription;
	}

	public void setBurialPlaceDescription(String burialPlaceDescription) {
		this.burialPlaceDescription = burialPlaceDescription;
	}

	public BurialConductor getBurialConductor() {
		return burialConductor;
	}

	public void setBurialConductor(BurialConductor burialConductor) {
		this.burialConductor = burialConductor;
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

	public FacilityReferenceDto getOccupationFacility() {
		return occupationFacility;
	}

	public void setOccupationFacility(FacilityReferenceDto occupationFacility) {
		this.occupationFacility = occupationFacility;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public String getMothersMaidenName() {
		return mothersMaidenName;
	}
	
	public void setMothersMaidenName(String mothersMaidenName) {
		this.mothersMaidenName = mothersMaidenName;
	}

}
