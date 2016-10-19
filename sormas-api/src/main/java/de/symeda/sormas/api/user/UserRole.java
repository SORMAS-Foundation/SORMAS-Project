package de.symeda.sormas.api.user;

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
}
