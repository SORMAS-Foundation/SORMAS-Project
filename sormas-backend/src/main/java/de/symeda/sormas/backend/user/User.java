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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.region.Region;

@Entity(name="users")
public class User extends AbstractDomainObject {
	
	private static final long serialVersionUID = -629432920970152112L;

	private String userName;
	private String password;
	private String seed;

	private boolean aktiv = true;

	private String firstName;
	private String lastName;
	private String userEmail;
	private String phone;
	private Location address;
	
	private Region region;
	private Set<UserRole> userRoles;
	
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
		return aktiv;
	}
	public void setAktiv(boolean aktiv) {
		this.aktiv = aktiv;
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
}
