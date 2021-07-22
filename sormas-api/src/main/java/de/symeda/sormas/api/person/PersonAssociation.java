package de.symeda.sormas.api.person;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum PersonAssociation {

	CASE,
	CONTACT,
	EVENT_PARTICIPANT,
	IMMUNIZATION,
	TRAVEL_ENTRY;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
