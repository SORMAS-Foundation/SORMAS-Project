package de.symeda.sormas.ui.statistics;

import de.symeda.sormas.api.I18nProperties;

public enum StatisticsVisualizationType {

	TABLE,
	REGIONS_MAP,
	DISTRICTS_MAP,
	BAR_CHART,
	PIE_CHART,
	// TODO
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
