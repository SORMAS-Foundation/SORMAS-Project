package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.I18nProperties;

public enum ShipmentStatus {

	NOT_SHIPPED,
	SHIPPED,
	RECEIVED,
	REFERRED_OTHER_LAB,
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
}
