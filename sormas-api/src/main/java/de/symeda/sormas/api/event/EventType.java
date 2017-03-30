package de.symeda.sormas.api.event;

import de.symeda.sormas.api.I18nProperties;

public enum EventType {
	RUMOR,
	OUTBREAK;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}