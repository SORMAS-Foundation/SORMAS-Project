package de.symeda.sormas.api.person;

import de.symeda.sormas.api.I18nProperties;

public enum PresentCondition {
	ALIVE, 
	DEAD, 
	BURRIED;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
