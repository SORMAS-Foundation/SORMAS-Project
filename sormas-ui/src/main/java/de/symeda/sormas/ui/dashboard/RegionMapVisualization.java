package de.symeda.sormas.ui.dashboard;

import de.symeda.sormas.api.I18nProperties;

public enum RegionMapVisualization {

	CASE_COUNT,
	CASE_INCIDENCE
	;	
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
