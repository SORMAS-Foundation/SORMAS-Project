package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.I18nProperties;

public enum ContactClassification {
	POSSIBLE,
	CONFIRMED,
	NO_CONTACT,
	CONVERTED, // to case
	DROPPED, // case disproved
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
