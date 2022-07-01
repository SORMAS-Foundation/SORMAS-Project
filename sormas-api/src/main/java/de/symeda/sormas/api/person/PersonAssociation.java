package de.symeda.sormas.api.person;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum PersonAssociation {

	ALL,
	CASE,
	CONTACT,
	EVENT_PARTICIPANT,
	IMMUNIZATION,
	TRAVEL_ENTRY;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	/**
	 * @return All values that map to a single association coming from {@link Person}.
	 */
	public static PersonAssociation[] getSingleAssociations() {

		return new PersonAssociation[] {
			CASE,
			CONTACT,
			EVENT_PARTICIPANT,
			IMMUNIZATION,
			TRAVEL_ENTRY };
	}
}
