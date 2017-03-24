package de.symeda.sormas.backend.user;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;

@Entity(name="users")
public class User extends AbstractDomainObject {
	
	private static final long serialVersionUID = -629432920970152112L;

	public static final String USER_NAME = "userName";
	public static final String PASSWORD = "password";
	public static final String SEED = "seed";
	public static final String ACTIVE = "active";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String USER_EMAIL = "userEmail";
	public static final String PHONE = "phone";
	public static final String ADDRESS = "address";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String USER_ROLES = "userRoles";
	public static final String LABORATORY = "laboratory";

	
	private String userName;
	private String password;
	private String seed;

	private boolean active = true;

	private String firstName;
	private String lastName;
	private String userEmail;
	private String phone;
	private Location address;
	
	private Set<UserRole> userRoles;

	private Region region;
	private District district;
	// facility of informant
	private Facility healthFacility;
	// laboratory of lab user
	private Facility laboratory;
	
	private User associatedOfficer;
	
	@Column(nullable = false)
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@Size(max = 64)
	@Column(name = "password", nullable = false, length = 64)
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Column(name = "seed", nullable = false, length = 16)
	public String getSeed() {
		return seed;
	}
	public void setSeed(String seed) {
		this.seed = seed;
	}
	
	@Column(nullable = false)
	public boolean isAktiv() {
		return active;
	}
	public void setAktiv(boolean aktiv) {
		this.active = aktiv;
	}
	
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
	
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@ManyToOne(cascade = CascadeType.ALL)
	public Location getAddress() {
		if (address == null) {
			address = new Location();
		}
		return address;
	}
	public void setAddress(Location address) {
		this.address = address;
	}

	@ManyToOne(cascade = {})
	public Region getRegion() {
		return region;
	}
	public void setRegion(Region region) {
		this.region = region;
	}
	
	/**
	 * TODO we need a main user role
	 * @return
	 */
	@ElementCollection(fetch=FetchType.LAZY)
	@Enumerated(EnumType.STRING)
	@CollectionTable(
	        name="userroles",
	        joinColumns=@JoinColumn(name="user_id", referencedColumnName=User.ID, nullable = false),
	        uniqueConstraints=@UniqueConstraint(columnNames={"user_id", "userrole"})
	  )
	@Column(name = "userrole", nullable = false)
	public Set<UserRole> getUserRoles() {
		return userRoles;
	}
	public void setUserRoles(Set<UserRole> userRoles) {
		this.userRoles = userRoles;
	}
	
	@ManyToOne(cascade = {})
	public User getAssociatedOfficer() {
		return associatedOfficer;
	}
	public void setAssociatedOfficer(User associatedOfficer) {
		this.associatedOfficer = associatedOfficer;
	}
	@Override
	public String toString() {
		String result = firstName + " " + lastName.toUpperCase();
		// TODO we need a main user role
		if (userRoles.size() > 0) {
			result += " - " + userRoles.iterator().next().toShortString();					
		}
		return result;
	}
	
	@Transient
	public boolean isSupervisor() {
		for (UserRole userRole : getUserRoles()) {
			if (userRole.isSupervisor()) {
				return true;
			}
		}
		return false;
	}
	
	@ManyToOne(cascade = {})
	public District getDistrict() {
		return district;
	}
	public void setDistrict(District district) {
		this.district = district;
	}
	
	@ManyToOne(cascade = {})
	public Facility getHealthFacility() {
		return healthFacility;
	}
	public void setHealthFacility(Facility healthFacility) {
		this.healthFacility = healthFacility;
	}
	
	@ManyToOne(cascade = {})
	public Facility getLaboratory() {
		return laboratory;
	}
	public void setLaboratory(Facility laboratory) {
		this.laboratory = laboratory;
	}
}
