package de.symeda.sormas.api.event;

import de.symeda.sormas.api.I18nProperties;

public enum EventStatus {
	POSSIBLE,
	CONFIRMED,
	NO_EVENT;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
