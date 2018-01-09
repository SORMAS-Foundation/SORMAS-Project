package de.symeda.sormas.api.person;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Diseases;

public class PersonDto extends EntityDto {

	private static final long serialVersionUID = -8558187171374254398L;

	public static final String I18N_PREFIX = "Person";

	public static final String SEX = "sex";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";

	public static final String PRESENT_CONDITION = "presentCondition";
	public static final String BIRTH_DATE_DD = "birthdateDD";
	public static final String BIRTH_DATE_MM = "birthdateMM";
	public static final String BIRTH_DATE_YYYY = "birthdateYYYY";
	
	public static final String APPROXIMATE_AGE = "approximateAge";
	public static final String APPROXIMATE_AGE_TYPE = "approximateAgeType";
	
	public static final String CAUSE_OF_DEATH = "causeOfDeath";
	public static final String CAUSE_OF_DEATH_DETAILS = "causeOfDeathDetails";
	public static final String CAUSE_OF_DEATH_DISEASE = "causeOfDeathDisease";
	public static final String CAUSE_OF_DEATH_DISEASE_DETAILS = "causeOfDeathDiseaseDetails";
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
	private String firstName;
	private String lastName;
		
	private PresentCondition presentCondition;
	private Integer birthdateDD;
	private Integer birthdateMM;
	private Integer birthdateYYYY;
	private Integer approximateAge;
	private ApproximateAgeType approximateAgeType;
	private Date deathDate;
	private CauseOfDeath causeOfDeath;
	private String causeOfDeathDetails;
	private Disease causeOfDeathDisease;
	private String causeOfDeathDiseaseDetails;
	@Diseases({Disease.EVD,Disease.OTHER})
	private DeathPlaceType deathPlaceType;
	@Diseases({Disease.EVD,Disease.OTHER})
	private String deathPlaceDescription;
	@Diseases({Disease.EVD,Disease.OTHER})
	private Date burialDate;
	@Diseases({Disease.EVD,Disease.OTHER})
	private String burialPlaceDescription;
	@Diseases({Disease.EVD,Disease.OTHER})
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

	public Date getBurialDate() {
		return burialDate;
	}
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

	public CauseOfDeath getCauseOfDeath() {
		return causeOfDeath;
	}

	public void setCauseOfDeath(CauseOfDeath causeOfDeath) {
		this.causeOfDeath = causeOfDeath;
	}

	public String getCauseOfDeathDetails() {
		return causeOfDeathDetails;
	}

	public void setCauseOfDeathDetails(String causeOfDeathDetails) {
		this.causeOfDeathDetails = causeOfDeathDetails;
	}

	public Disease getCauseOfDeathDisease() {
		return causeOfDeathDisease;
	}

	public void setCauseOfDeathDisease(Disease causeOfDeathDisease) {
		this.causeOfDeathDisease = causeOfDeathDisease;
	}

	public String getCauseOfDeathDiseaseDetails() {
		return causeOfDeathDiseaseDetails;
	}

	public void setCauseOfDeathDiseaseDetails(String causeOfDeathDiseaseDetails) {
		this.causeOfDeathDiseaseDetails = causeOfDeathDiseaseDetails;
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
	
	@Override
	public String toString() {
		return buildCaption(firstName, lastName);
	}
	
	public PersonReferenceDto toReference() {
		return new PersonReferenceDto(getUuid());
	}
	
	public static String buildCaption(String firstName, String lastName) {
		return DataHelper.toStringNullable(firstName) + " " + DataHelper.toStringNullable(lastName).toUpperCase();
	}

}
