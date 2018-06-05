package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.I18nProperties;

public enum SampleTestType {

	ANTIGEN_DETECTION,
	RAPID_TEST,
	CULTURE,
	DENGUE_FEVER_IGM,
	DENGUE_FEVER_ANTIBODIES,
	HISTOPATHOLOGY,
	IGM_SERUM_ANTIBODY,
	IGG_SERUM_ANTIBODY,
	MICROSCOPY,
	PCR_RT_PCR,
	VIRUS_ISOLATION,
	WEST_NILE_FEVER_IGM,
	WEST_NILE_FEVER_ANTIBODIES,
	YELLOW_FEVER_IGM,
	YELLOW_FEVER_ANTIBODIES,
	YERSINIA_PESTIS_ANTIGEN,
	OTHER,
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
}
