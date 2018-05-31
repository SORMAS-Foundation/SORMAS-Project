package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.I18nProperties;

public enum PlagueType {

	BUBONIC,
	PNEUMONIC,
	SEPTICAEMIC;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
	
}
