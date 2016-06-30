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

import de.symeda.sormas.api.person.BurialConductor;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.location.Location;

@Entity
public class Person extends AbstractDomainObject {
	
	private static final long serialVersionUID = -1735038738114840087L;
	
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String BIRTH_DATE = "birthDate";
	public static final String APPROXIMATE_AGE = "approximateAge";
	public static final String ADDRESS = "address";
	public static final String SEX = "sex";
	public static final String CAZE = "caze";

	private String firstName;
	private String lastName;
	private Date birthDate;
	private Integer approximateAge;
	
	private Location address;
	private String phone;
	
	// TODO private Ethnicity ethnicity;
	private Sex sex;
	
	private Case caze;
	
	private boolean dead;
	private Date deathDate;
	private Location deathLocation;
	private Date burialDate;
	private Location burialLocation;
	private BurialConductor burialConductor;

	private OccupationType occupationType;
	private String occupationDetails;
	private String occupationFacility;
	
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

	@Temporal(TemporalType.DATE)
	public Date getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	
	public Integer getApproximateAge() {
		return approximateAge;
	}
	public void setApproximateAge(Integer approximateAge) {
		this.approximateAge = approximateAge;
	}
	
	@ManyToOne(cascade = CascadeType.ALL)
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
	
	@Enumerated(EnumType.STRING)
	public Sex getSex() {
		return sex;
	}
	public void setSex(Sex sex) {
		this.sex = sex;
	}

	@OneToOne(cascade = CascadeType.ALL, mappedBy=Case.PERSON)
	public Case getCaze() {
		return caze;
	}
	public void setCaze(Case caze) {
		this.caze = caze;
	}

	public boolean isDead() {
		return dead;
	}
	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	@Temporal(TemporalType.DATE)
	public Date getDeathDate() {
		return deathDate;
	}
	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}
	
	@ManyToOne(cascade = CascadeType.ALL)
	public Location getDeathLocation() {
		return deathLocation;
	}
	public void setDeathLocation(Location deathLocation) {
		this.deathLocation = deathLocation;
	}
	
	@Temporal(TemporalType.DATE)
	public Date getBurialDate() {
		return burialDate;
	}
	public void setBurialDate(Date burialDate) {
		this.burialDate = burialDate;
	}
	
	@ManyToOne(cascade = CascadeType.ALL)
	public Location getBurialLocation() {
		return burialLocation;
	}
	public void setBurialLocation(Location burialLocation) {
		this.burialLocation = burialLocation;
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
	
	public String getOccupationFacility() {
		return occupationFacility;
	}
	public void setOccupationFacility(String occupationFacility) {
		this.occupationFacility = occupationFacility;
	}
	
	@Override
	public String toString() {
		return getFirstName() + " " + getLastName();
	}
}
