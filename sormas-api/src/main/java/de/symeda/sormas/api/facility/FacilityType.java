package de.symeda.sormas.api.facility;

import de.symeda.sormas.api.I18nProperties;

public enum FacilityType {
	PRIMARY,
	SECONDARY,
	TERTIARY
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
