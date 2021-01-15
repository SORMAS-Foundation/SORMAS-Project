package de.symeda.sormas.api.systemevents;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum SystemEventStatus {

	STARTED,
	SUCCESS,
	CANCELLED,
	ERROR;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
