package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.I18nProperties;

public enum VaccinationInfoSource {

	VACCINATION_CARD,
	ORAL_COMMUNICATION;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
}
