package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.I18nProperties;

public enum SampleTestType {

	PCR,
	RT_PCR,
	ELISA,
	CULTURE,
	MICROSCOPY,
	VIRUS_ISOLATION,
	RAPID_TEST,
	OTHER,
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
	
}
