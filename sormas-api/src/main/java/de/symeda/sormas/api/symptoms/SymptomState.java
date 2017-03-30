package de.symeda.sormas.api.symptoms;

import de.symeda.sormas.api.I18nProperties;

public enum SymptomState {
	YES, 
	NO, 
	UNKNOWN;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
