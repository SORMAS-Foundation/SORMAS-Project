package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.I18nProperties;

public enum ContactStatus {
	ACTIVE,
	CONVERTED, // to case
	DROPPED; // case disproved or not a contact

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
