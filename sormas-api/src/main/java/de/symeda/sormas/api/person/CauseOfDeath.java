package de.symeda.sormas.api.person;

import de.symeda.sormas.api.I18nProperties;

public enum CauseOfDeath {
	
	EPIDEMIC_DISEASE,
	OTHER_CAUSE;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

}
