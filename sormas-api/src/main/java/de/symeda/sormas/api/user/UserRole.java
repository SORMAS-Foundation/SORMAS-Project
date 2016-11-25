package de.symeda.sormas.api.user;

import java.util.Collection;
import java.util.HashSet;

import de.symeda.sormas.api.I18nProperties;

public enum UserRole {

	SURVEILLANCE_SUPERVISOR(true),
	SURVEILLANCE_OFFICER(false),
	INFORMANT(false),
	CASE_SUPERVISOR(true),
	CASE_OFFICER(false),
	CONTACT_SUPERVISOR(true),
	CONTACT_OFFICER(false)
	;
	
	private final boolean supervisor;
	
	private UserRole(boolean supervisor) {
		this.supervisor = supervisor;
	}
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
	
	public boolean isSupervisor() {
		return supervisor;
	}
	
	public void addAssignableRoles(Collection<UserRole> collection) {
		switch (this) {
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
}
