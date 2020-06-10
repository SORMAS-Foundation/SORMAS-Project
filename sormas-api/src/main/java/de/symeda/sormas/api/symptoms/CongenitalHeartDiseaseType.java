package de.symeda.sormas.api.symptoms;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum CongenitalHeartDiseaseType {

	PDA,
	PPS,
	VSD,
	OTHER;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
