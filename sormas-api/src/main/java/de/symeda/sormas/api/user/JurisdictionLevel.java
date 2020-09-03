package de.symeda.sormas.api.user;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum JurisdictionLevel {

	NONE,
	NATION,
	REGION,
	DISTRICT,
	COMMUNITY,
	HEALTH_FACILITY,
	LABORATORY,
	EXTERNAL_LABORATORY,
	POINT_OF_ENTRY;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
