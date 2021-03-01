package de.symeda.sormas.api.person;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum PersonAssociation {

	CASE,
	CONTACT,
	EVENT_PARTICIPANT;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
