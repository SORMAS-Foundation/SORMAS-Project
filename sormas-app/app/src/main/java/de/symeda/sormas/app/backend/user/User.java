package de.symeda.sormas.app.backend.user;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.region.Region;

@Entity(name=User.TABLE_NAME)
@DatabaseTable(tableName = User.TABLE_NAME)
public class User extends AbstractDomainObject {
	
	private static final long serialVersionUID = -629432920970152112L;

	public static final String TABLE_NAME = "users";

	public static final String USER_NAME = "userName";
	public static final String ACTIVE = "active";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String USER_EMAIL = "userEmail";
	public static final String PHONE = "phone";
	public static final String ADDRESS = "address";
	public static final String REGION = "region";
	public static final String USER_ROLE = "userRole";

	@Column(nullable = false)
	private String userName;
	@Column(nullable = false)
	private boolean active;

	@Column(nullable = false)
	private String firstName;
	@Column(nullable = false)
	private String lastName;
	@Column
	private String userEmail;
	@Column
	private String phone;
	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private Location address;
	@ManyToOne(cascade = {})
	private Region region;
	@Enumerated(EnumType.STRING)
	private UserRole userRole;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public boolean isAktiv() {
		return active;
	}
	public void setAktiv(boolean aktiv) {
		this.active = aktiv;
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
	
	public Location getAddress() {
		return address;
	}
	public void setAddress(Location address) {
		this.address = address;
	}

	public Region getRegion() {
		return region;
	}
	public void setRegion(Region region) {
		this.region = region;
	}
	
	@Override
	public String toString() {

		String result = getFirstName() + " " + getLastName().toUpperCase();
		if (getUserRole() != null) {
			result += " (" + getUserRole() + ")";
		}
		return result;
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}
}
