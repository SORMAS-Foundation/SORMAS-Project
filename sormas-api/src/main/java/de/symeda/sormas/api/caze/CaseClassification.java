package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.I18nProperties;

public enum CaseClassification {
	POSSIBLE, 
	SUSPECT, 
	PROBABLE,
	CONFIRMED, 
	NO_CASE 
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
