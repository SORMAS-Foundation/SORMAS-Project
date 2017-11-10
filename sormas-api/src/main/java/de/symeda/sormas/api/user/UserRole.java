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
	NATIONAL_USER(false, false),	
	SURVEILLANCE_SUPERVISOR(true, false),	
	SURVEILLANCE_OFFICER(false, true),	
	INFORMANT(false, false),	
	CASE_SUPERVISOR(true, false),	
	CASE_OFFICER(false, true),	
	CONTACT_SUPERVISOR(true, false),	
	CONTACT_OFFICER(false, true),	
	RUMOR_MANAGER(true, false),	
	LAB_USER(false, false),
	NATIONAL_OBSERVER(false, false),
	;
	
	public static final String _SYSTEM = "SYSTEM";
	public static final String _USER = "USER";
	public static final String _ADMIN = ADMIN.name();
	public static final String _NATIONAL_USER = NATIONAL_USER.name();
	public static final String _SURVEILLANCE_SUPERVISOR = SURVEILLANCE_SUPERVISOR.name();
	public static final String _SURVEILLANCE_OFFICER = SURVEILLANCE_OFFICER.name();
	public static final String _INFORMANT = INFORMANT.name();
	public static final String _CASE_SUPERVISOR = CASE_SUPERVISOR.name();
	public static final String _CASE_OFFICER = CASE_OFFICER.name();
	public static final String _CONTACT_SUPERVISOR = CONTACT_SUPERVISOR.name();
	public static final String _CONTACT_OFFICER = CONTACT_OFFICER.name();
	public static final String _RUMOR_MANAGER = RUMOR_MANAGER.name();
	public static final String _LAB_USER = LAB_USER.name();
	public static final String _NATIONAL_OBSERVER = NATIONAL_OBSERVER.name();
	
	private final boolean supervisor;
	private final boolean officer;
	private HashSet<UserRight> userRights = null;
	
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
	
	private void initUserRights() {
		
		userRights = new HashSet<UserRight>();

		for (UserRight userRight : UserRight.values()) {
			if (userRight.isForRole(this)) {
				userRights.add(userRight);
			}
		}
	}
	
	public boolean hasRight(UserRight userRight) {
		if (userRights == null) {
			initUserRights();
		}
		return userRights.contains(userRight);
	}
	
	public void addAssignableRoles(Collection<UserRole> collection) {
		switch (this) {
		case ADMIN:
			for(UserRole role : UserRole.values()) {
				collection.add(role);
			}
			break;
		case NATIONAL_USER:
			collection.add(SURVEILLANCE_SUPERVISOR);
			collection.add(CASE_SUPERVISOR);
			collection.add(CONTACT_SUPERVISOR);
			collection.add(CASE_OFFICER);
			collection.add(CONTACT_OFFICER);
			collection.add(SURVEILLANCE_OFFICER);
			collection.add(LAB_USER);
			collection.add(NATIONAL_OBSERVER);
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
		case NATIONAL_OBSERVER:
			collection.add(NATIONAL_OBSERVER);
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
