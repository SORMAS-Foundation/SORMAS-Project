package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.I18nProperties;

public enum DengueFeverType {

	DENGUE_FEVER,
	DENGUE_HEMORRHAGIC_FEVER,
	DENUGE_SHOCK_SYNDROME;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
