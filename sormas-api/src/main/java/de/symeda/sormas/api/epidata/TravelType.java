package de.symeda.sormas.api.epidata;

import de.symeda.sormas.api.I18nProperties;

public enum TravelType {

	ABROAD,
	WITHIN_COUNTRY;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
}
