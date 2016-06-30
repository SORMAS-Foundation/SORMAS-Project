package de.symeda.sormas.api.user;

import de.symeda.sormas.api.I18nProperties;

public enum UserRole {

	SURVEILLANCE_SUPERVISOR,
	SURVEILLANCE_OFFICER,
	INFORMANT,
	CASE_SUPERVISOR,
	CASE_OFFICER,
	CONTACT_SUPERVISOR,
	CONTACT_OFFICER
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
