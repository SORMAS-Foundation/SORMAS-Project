package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.I18nProperties;

public enum ContactStatus {
	POSSIBLE,
	FOLLOW_UP,
	DONE,
	NO_CONTACT
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
