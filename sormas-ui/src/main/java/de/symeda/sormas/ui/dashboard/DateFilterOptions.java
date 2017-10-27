package de.symeda.sormas.ui.dashboard;

import de.symeda.sormas.api.I18nProperties;

public enum DateFilterOptions {
	DATE,
	EPI_WEEK;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
}
