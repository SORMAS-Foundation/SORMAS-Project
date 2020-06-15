package de.symeda.sormas.api.infrastructure;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum PointOfEntryType {

	AIRPORT,
	SEAPORT,
	GROUND_CROSSING,
	OTHER;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
