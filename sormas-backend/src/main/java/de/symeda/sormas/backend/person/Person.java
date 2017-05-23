package de.symeda.sormas.backend.person;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.BurialConductor;
import de.symeda.sormas.api.person.DeathPlaceType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.location.Location;

@Entity
public class Person extends AbstractDomainObject {
	
	private static final long serialVersionUID = -1735038738114840087L;
	
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String NICKNAME = "nickname";
	public static final String MOTHERS_MAIDEN_NAME = "mothersMaidenName";
	public static final String APPROXIMATE_AGE = "approximateAge";
	public static final String DEATH_PLACE_TYPE = "deathPlaceType";
	public static final String DEATH_PLACE_DESCRIPTION = "deathPlaceDescription";
	public static final String BURIAL_DATE = "burialDate";
	public static final String BURIAL_PLACE_DESCRIPTION = "burialPlaceDescription";
	public static final String BURIAL_CONDUCTOR = "burialConductor";
	public static final String ADDRESS = "address";
	public static final String DEATH_LOCATION = "deathLocation";
	public static final String BURIAL_LOCATION = "burialLocation";
	public static final String SEX = "sex";
	public static final String CAZE = "caze";
	public static final String DEATH_DATE = "deathDate";

	private String firstName;
	private String lastName;
	private String nickname;
	private String mothersMaidenName;
	private Integer approximateAge;
	private ApproximateAgeType approximateAgeType;
	
	private DeathPlaceType deathPlaceType;
	private String deathPlaceDescription;
	private Date burialDate;
	private String burialPlaceDescription;
	private BurialConductor burialConductor;
	
	private Location address;
	private String phone;
	private String phoneOwner;
	
	// TODO private Ethnicity ethnicity;
	private Sex sex;
	
	private PresentCondition presentCondition;
	private Integer birthdateDD;
	private Integer birthdateMM;
	private Integer birthdateYYYY;
	private Date deathDate;

	private OccupationType occupationType;
	private String occupationDetails;
	private Facility occupationFacility;
	
	@Column(nullable = false)
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	@Column(nullable = false)
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
	
	public String getMothersMaidenName() {
		return mothersMaidenName;
	}
	
	public void setMothersMaidenName(String mothersMaidenName) {
		this.mothersMaidenName = mothersMaidenName;
	}

	@Column(name = "birthdate_dd")
	public Integer getBirthdateDD() {
		return birthdateDD;
	}

	public void setBirthdateDD(Integer birthdateDD) {
		this.birthdateDD = birthdateDD;
	}
	
	@Column(name = "birthdate_mm")
	public Integer getBirthdateMM() {
		return birthdateMM;
	}
	
	public void setBirthdateMM(Integer birthdateMM) {
		this.birthdateMM = birthdateMM;
	}
	
	@Column(name = "birthdate_yyyy")
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

	@Enumerated(EnumType.STRING)
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
	
	public String getBurialPlaceDescription() {
		return burialPlaceDescription;
	}
	public void setBurialPlaceDescription(String burialPlaceDescription) {
		this.burialPlaceDescription = burialPlaceDescription;
	}
	
	@OneToOne(cascade = CascadeType.ALL)
	public Location getAddress() {
		if (address == null) {
			address = new Location();
		}
		return address;
	}
	public void setAddress(Location address) {
		this.address = address;
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
	
	@Enumerated(EnumType.STRING)
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
	
	@Temporal(TemporalType.DATE)
	public Date getDeathDate() {
		return deathDate;
	}
	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}
	
	@Temporal(TemporalType.DATE)
	public Date getBurialDate() {
		return burialDate;
	}

	public void setBurialDate(Date burialDate) {
		this.burialDate = burialDate;
	}
	
	@Enumerated(EnumType.STRING)
	public BurialConductor getBurialConductor() {
		return burialConductor;
	}
	public void setBurialConductor(BurialConductor burialConductor) {
		this.burialConductor = burialConductor;
	}
	
	@Enumerated(EnumType.STRING)
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
	
	@ManyToOne(cascade = {})
	public Facility getOccupationFacility() {
		return occupationFacility;
	}
	public void setOccupationFacility(Facility occupationFacility) {
		this.occupationFacility = occupationFacility;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getFirstName()).append(" ").append(getLastName().toUpperCase());
//		if (getBirthdateDD() != null && getBirthdateMM() != null && getBirthdateYYYY() != null) {
//			Calendar birthdate = new GregorianCalendar();
//			birthdate.set(getBirthdateYYYY(), getBirthdateMM(), getBirthdateDD());
//			
//			builder.append(" (").append(DateHelper.formatDMY(birthdate.getTime())).append(")");
//		}
		if (getAddress() != null && getAddress().getCommunity() != null)
			builder.append(" - ").append(getAddress().getCommunity().getName());
		return builder.toString();
	}
	
}
