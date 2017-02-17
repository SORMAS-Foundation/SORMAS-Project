package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.I18nProperties;

public enum SpecimenCondition {
	
	ADEQUATE,
	NOT_ADEQUATE,
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};

}
