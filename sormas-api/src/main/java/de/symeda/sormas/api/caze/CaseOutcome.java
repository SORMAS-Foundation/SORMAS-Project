package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.I18nProperties;

public enum CaseOutcome {
	
	NO_OUTCOME,
	DECEASED,
	RECOVERED,
	UNKNOWN;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

}