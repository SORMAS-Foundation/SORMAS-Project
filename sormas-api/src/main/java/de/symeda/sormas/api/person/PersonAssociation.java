package de.symeda.sormas.api.person;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Enum to show ALL or filter according to: \n\n" + "only persons associated with a CASE,\n\n"
	+ "only persons associated with a CONTACT,\n\n" + "only persons associated with at least one event, so they are a EVENT_PARTICIPANT,\n\n"
	+ "only perons associated with a established TRAVEL_ENTRY.")
public enum PersonAssociation {

	/**
	 * Persons that are associated to <strong>all permitted</strong> association types.
	 * 
	 * @see #getSingleAssociations()
	 */
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
