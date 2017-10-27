package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.I18nProperties;

public enum SampleMaterial {

	BLOOD,
	SERA,
	STOOL,
	NASAL_SWAB,
	THROAT_SWAB,
	NP_SWAB,
	CEREBROSPINAL_FLUID,
	CRUST,
	OTHER,
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
}
