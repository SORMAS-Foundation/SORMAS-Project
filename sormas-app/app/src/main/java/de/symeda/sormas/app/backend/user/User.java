package de.symeda.sormas.app.backend.user;

import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.region.Region;

@Entity(name=User.TABLE_NAME)
@DatabaseTable(tableName = User.TABLE_NAME)
public class User extends AbstractDomainObject {
	
	private static final long serialVersionUID = -629432920970152112L;

	public static final String TABLE_NAME = "users";

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
	public static final String USER_ROLES = "userRoles";

	
	private String userName;

	private boolean active = true;

	private String firstName;
	private String lastName;
	private String userEmail;
	private String phone;
	private Location address;
	
	private Region region;
	@ForeignCollectionField(eager = true)
	private Collection<UserRoleToUser> userRoles;
	
	@Column(nullable = false)
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
	
//	@ElementCollection(fetch=FetchType.LAZY)
//	@Enumerated(EnumType.STRING)
//	@CollectionTable(
//	        name="userroles",
//	        joinColumns=@JoinColumn(name="user_id", referencedColumnName=User.ID, nullable = false),
//	        uniqueConstraints=@UniqueConstraint(columnNames={"user_id", "userrole"})
//	  )
//	@Column(name = "userrole", nullable = false)
	public Collection<UserRoleToUser> getUserRoles() {
		return userRoles;
	}
	public void setUserRoles(Collection<UserRoleToUser> userRoles) {
		this.userRoles = userRoles;
	}
	
	@Override
	public String toString() {
		return getFirstName() + " " + getLastName();
	}
}
