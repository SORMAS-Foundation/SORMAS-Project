package de.symeda.sormas.app.backend.person;

import android.databinding.Bindable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

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
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.location.Location;

@Entity(name=Person.TABLE_NAME)
@DatabaseTable(tableName = Person.TABLE_NAME)
public class Person extends AbstractDomainObject {
	
	private static final long serialVersionUID = -1735038738114840087L;

	public static final String TABLE_NAME = "person";

	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String BIRTH_DATE = "birthDate";
	public static final String APPROXIMATE_AGE = "approximateAge";
	public static final String ADDRESS = "address";
	public static final String SEX = "sex";
	public static final String CASE_UUID = "caseUuid";

	@Column(nullable = false)
	private String firstName;
	@Column(nullable = false)
	private String lastName;
	//@Temporal(TemporalType.DATE)
	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date birthDate;
	@Column
	private Integer approximateAge;
	@Enumerated(EnumType.STRING)
	private ApproximateAgeType approximateAgeType;

	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private Location address;
	@Column(length = 255)
	private String phone;
	@Column(length = 255)
	private String phoneOwner;

	// TODO private Ethnicity ethnicity;
	@Enumerated(EnumType.STRING)
	private Sex sex;

	// Make sure this is always in sync with Case.person
	@DatabaseField(unique=true, width = 29)
	private String caseUuid;

	@Enumerated(EnumType.STRING)
	private PresentCondition presentCondition;
	@Column
	private boolean dead;
	@Temporal(TemporalType.DATE)
	private Date deathDate;
	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private Location deathLocation;
	@Temporal(TemporalType.DATE)
	private Date burialDate;
	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private Location burialLocation;
	@Enumerated(EnumType.STRING)
	private BurialConductor burialConductor;

	@Enumerated(EnumType.STRING)
	private OccupationType occupationType;
	@Column
	private String occupationDetails;
	@ManyToOne(cascade = {})
	private Facility occupationFacility;

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

	public Date getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
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
		return phone;
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

	public boolean isDead() {
		return dead;
	}
	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	public Date getDeathDate() {
		return deathDate;
	}
	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}
	
	public Location getDeathLocation() {
		return deathLocation;
	}
	public void setDeathLocation(Location deathLocation) {
		this.deathLocation = deathLocation;
	}
	
	public Date getBurialDate() {
		return burialDate;
	}
	public void setBurialDate(Date burialDate) {
		this.burialDate = burialDate;
	}
	
	public Location getBurialLocation() {
		return burialLocation;
	}
	public void setBurialLocation(Location burialLocation) {
		this.burialLocation = burialLocation;
	}
	
	public BurialConductor getBurialConductor() {
		return burialConductor;
	}
	public void setBurialConductor(BurialConductor burialConductor) {
		this.burialConductor = burialConductor;
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
	
	public Facility getOccupationFacility() {
		return occupationFacility;
	}
	public void setOccupationFacility(Facility occupationFacility) {
		this.occupationFacility = occupationFacility;
	}
	
	@Override
	public String toString() {
		return getFirstName() + " " + getLastName();
	}

	public String getCaseUuid() {
		return caseUuid;
	}

	public void setCaseUuid(String caseUuid) {
		this.caseUuid = caseUuid;
	}
}
