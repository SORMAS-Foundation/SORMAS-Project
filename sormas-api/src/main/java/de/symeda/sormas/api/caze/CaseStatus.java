package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.I18nProperties;

public enum CaseStatus {
	NEW,
	POSSIBLE, 
	INVESTIGATED, 
	SUSPECT, 
	PROBABLE,
	CONFIRMED, 
	NO_CASE, 
	RECOVERED, 
	DECEASED
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
