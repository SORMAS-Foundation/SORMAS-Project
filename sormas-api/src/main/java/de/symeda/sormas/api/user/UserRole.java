package de.symeda.sormas.api.user;

import java.util.Collection;
import java.util.HashSet;

import de.symeda.sormas.api.I18nProperties;

/**
 * These are also used as user groups in the server realm
 *  
 * @author Martin Wahnschaffe
 */
public enum UserRole {

	ADMIN(false, false),
	SURVEILLANCE_SUPERVISOR(true, false),
	SURVEILLANCE_OFFICER(false, true),
	INFORMANT(false, false),
	CASE_SUPERVISOR(true, false),
	CASE_OFFICER(false, true),
	CONTACT_SUPERVISOR(true, false),
	CONTACT_OFFICER(false, true),
	RUMOR_MANAGER(true, false),
	LAB_USER(false, false),
	;
	
	private final boolean supervisor;
	private final boolean officer;
	
	private UserRole(boolean supervisor, boolean officer) {
		this.supervisor = supervisor;
		this.officer = officer;
	}
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
	
	public String toShortString() {
		return I18nProperties.getEnumCaption(this, "Short");
	}
	
	public boolean isSupervisor() {
		return supervisor;
	}
	
	public boolean isOfficer() {
		return officer;
	}
	
	public void addAssignableRoles(Collection<UserRole> collection) {
		switch (this) {
		case ADMIN:
			for(UserRole role : UserRole.values()) {
				collection.add(role);
			}
			break;
		case SURVEILLANCE_SUPERVISOR:
			collection.add(SURVEILLANCE_OFFICER);
			collection.add(INFORMANT);
			break;
		case CASE_SUPERVISOR:
			collection.add(CASE_OFFICER);
			break;
		case CONTACT_SUPERVISOR:
			collection.add(CONTACT_OFFICER);
			break;
		case RUMOR_MANAGER:
			collection.add(RUMOR_MANAGER);
			break;
		case LAB_USER:
			collection.add(LAB_USER);
			break;
		default:
			break;
		}
	}
	
	public static HashSet<UserRole> getAssignableRoles(Collection<UserRole> roles) {
		HashSet<UserRole> result = new HashSet<UserRole>();
		for (UserRole role : roles) {
			role.addAssignableRoles(result);
		}
		return result;
	}
	
	public static boolean isSupervisor(Collection<UserRole> roles) {
		for (UserRole role : roles) {
			if (role.isSupervisor()) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isOfficer(Collection<UserRole> roles) {
		for (UserRole role : roles) {
			if (role.isOfficer()) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isInformant(Collection<UserRole> roles) {
		return roles.contains(UserRole.INFORMANT);
	}
	
	public static boolean isLabUser(Collection<UserRole> roles) {
		return roles.contains(UserRole.LAB_USER);
	}
}
