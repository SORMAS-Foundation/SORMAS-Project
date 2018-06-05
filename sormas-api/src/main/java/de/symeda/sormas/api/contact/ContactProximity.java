package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.I18nProperties;

public enum ContactProximity {
	TOUCHED_FLUID,
	PHYSICAL_CONTACT,
	CLOTHES_OR_OTHER,
	CLOSE_CONTACT,
	SAME_ROOM
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
