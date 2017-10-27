package de.symeda.sormas.api.epidata;

import de.symeda.sormas.api.I18nProperties;

public enum AnimalCondition {
	
	ALIVE,
	DEAD,
	PROCESSED,
	UNKNOWN;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

}
