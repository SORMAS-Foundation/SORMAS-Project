package de.symeda.sormas.app.backend.person;

import android.databinding.Bindable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.BurialConductor;
import de.symeda.sormas.api.person.CauseOfDeath;
import de.symeda.sormas.api.person.DeathPlaceType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;

@Entity(name=Person.TABLE_NAME)
@DatabaseTable(tableName = Person.TABLE_NAME)
public class Person extends AbstractDomainObject {
	
	private static final long serialVersionUID = -1735038738114840087L;

	public static final String TABLE_NAME = "person";
	public static final String I18N_PREFIX = "Person";

	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String APPROXIMATE_AGE = "approximateAge";
	public static final String ADDRESS = "address";
	public static final String SEX = "sex";
	public static final String NICKNAME = "nickname";
	public static final String MOTHERS_MAIDEN_NAME = "mothersMaidenName";

	@Column(nullable = false)
	private String firstName;
	@Column(nullable = false)
	private String lastName;
	@Column(length = 255)
	private String nickname;
	@Column(length = 255)
	private String mothersMaidenName;
	@Column
	private Integer birthdateDD;
	@Column
	private Integer birthdateMM;
	@Column
	private Integer birthdateYYYY;
	@Column
	private Integer approximateAge;
	@Enumerated(EnumType.STRING)
	private ApproximateAgeType approximateAgeType;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 2)
	private Location address;
	@Column(length = 255)
	private String phone;
	@Column(length = 255)
	private String phoneOwner;

	// TODO private Ethnicity ethnicity;
	@Enumerated(EnumType.STRING)
	private Sex sex;

	@Enumerated(EnumType.STRING)
	private PresentCondition presentCondition;
	@Enumerated(EnumType.STRING)
	private CauseOfDeath causeOfDeath;
	@Column
	private String causeOfDeathDetails;
	@Enumerated(EnumType.STRING)
	private Disease causeOfDeathDisease;
	//@Deprecated
	//private String causeOfDeathDiseaseDetails;
	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date deathDate;
	@Enumerated(EnumType.STRING)
	private DeathPlaceType deathPlaceType;
	@Column(length = 255)
	private String deathPlaceDescription;
	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date burialDate;
	@Column(length=255)
	private String burialPlaceDescription;
	@Enumerated(EnumType.STRING)
	private BurialConductor burialConductor;

	@Enumerated(EnumType.STRING)
	private OccupationType occupationType;
	@Column
	private String occupationDetails;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Region occupationRegion;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private District occupationDistrict;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Community occupationCommunity;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Facility occupationFacility;
	@Column(length=512)
	private String occupationFacilityDetails;


	//Orson added, talk to Martin
	private Region occupationRegion;
	private District occupationDistrict;
	private Community occupationCommunity;

	@Bindable
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Bindable
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

	@Bindable
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
	
	public Location getAddress() {
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
	
	public Date getDeathDate() {
		return deathDate;
	}
	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	public Date getBurialDate() {
		return burialDate;
	}
	public void setBurialDate(Date burialDate) {
		this.burialDate = burialDate;
	}
	
	public BurialConductor getBurialConductor() {
		return burialConductor;
	}
	public void setBurialConductor(BurialConductor burialConductor) {
		this.burialConductor = burialConductor;
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

	public String getBurialPlaceDescription() {
		return burialPlaceDescription;
	}

	public void setBurialPlaceDescription(String burialPlaceDescription) {
		this.burialPlaceDescription = burialPlaceDescription;
	}

	public OccupationType getOccupationType() {
		return occupationType;
	}
	public void setOccupationType(OccupationType occupationType) {
		this.occupationType = occupationType;
	}

	@Bindable
	public String getOccupationDetails() {
		return occupationDetails;
	}
	public void setOccupationDetails(String occupationDetails) {
		this.occupationDetails = occupationDetails;
	}

	public Region getOccupationRegion() {
		return occupationRegion;
	}
	public void setOccupationRegion(Region occupationRegion) {
		this.occupationRegion = occupationRegion;
	}

	public District getOccupationDistrict() {
		return occupationDistrict;
	}
	public void setOccupationDistrict(District occupationDistrict) {
		this.occupationDistrict = occupationDistrict;
	}

	public Community getOccupationCommunity() {
		return occupationCommunity;
	}
	public void setOccupationCommunity(Community occupationCommunity) {
		this.occupationCommunity = occupationCommunity;
	}

	public Facility getOccupationFacility() {
		return occupationFacility;
	}

	public void setOccupationFacility(Facility occupationFacility) {
		this.occupationFacility = occupationFacility;
	}
	public Region getOccupationRegion() {
		return occupationRegion;
	}

	public void setOccupationRegion(Region occupationRegion) {
		this.occupationRegion = occupationRegion;
	}

	public District getOccupationDistrict() {
		return occupationDistrict;
	}

	public void setOccupationDistrict(District occupationDistrict) {
		this.occupationDistrict = occupationDistrict;
	}

	public Community getOccupationCommunity() {
		return occupationCommunity;
	}

	public void setOccupationCommunity(Community occupationCommunity) {
		this.occupationCommunity = occupationCommunity;
	}

	public String getOccupationFacilityDetails() {
		return occupationFacilityDetails;
	}
	public void setOccupationFacilityDetails(String occupationFacilityDetails) {
		this.occupationFacilityDetails = occupationFacilityDetails;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getFirstName() != null? getFirstName() : "").append(" ").append((getLastName() != null? getLastName() : "").toUpperCase());
		return builder.toString();
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

}
