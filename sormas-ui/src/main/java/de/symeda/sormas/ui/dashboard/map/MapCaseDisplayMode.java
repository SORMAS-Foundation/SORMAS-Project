package de.symeda.sormas.ui.dashboard.map;

import de.symeda.sormas.api.I18nProperties;

public enum MapCaseDisplayMode {
	
	CASES,
	HEALTH_FACILITIES_OR_ADDRESS,
	HEALTH_FACILITIES;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};

}
