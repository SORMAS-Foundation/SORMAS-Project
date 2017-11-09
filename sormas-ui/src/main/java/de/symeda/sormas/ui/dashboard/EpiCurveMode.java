package de.symeda.sormas.ui.dashboard;

import de.symeda.sormas.api.I18nProperties;

public enum EpiCurveMode {
	
	CASE_STATUS,
	ALIVE_OR_DEAD;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
	
}