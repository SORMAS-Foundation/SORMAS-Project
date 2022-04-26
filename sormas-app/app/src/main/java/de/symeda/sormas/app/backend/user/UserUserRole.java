package de.symeda.sormas.app.backend.user;

import javax.persistence.Entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@Entity(name = UserUserRole.TABLE_NAME)
@DatabaseTable(tableName = UserUserRole.TABLE_NAME)
public class UserUserRole {

	public static final String TABLE_NAME = "users_userRoles";
	public static final String USER = "user";
	public static final String USER_ROLE = "userRole";

	@DatabaseField(foreign = true, columnName = "user_id")
	private User user;
	@DatabaseField(foreign = true, columnName = "userRole_id")
	private UserRole userRole;

	//Needed for dto serialization
	public UserUserRole() {
	}

	public UserUserRole(User user, UserRole userRole) {
		this.user = user;
		this.userRole = userRole;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}
}
