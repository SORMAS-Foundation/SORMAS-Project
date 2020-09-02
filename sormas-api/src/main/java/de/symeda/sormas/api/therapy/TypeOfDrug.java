package de.symeda.sormas.api.therapy;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum TypeOfDrug {

	ANTIMICROBIAL,
	ANTIVIRAL,
	OTHER;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
