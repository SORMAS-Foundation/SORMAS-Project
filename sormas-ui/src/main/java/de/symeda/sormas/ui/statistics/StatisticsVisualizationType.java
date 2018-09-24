package de.symeda.sormas.ui.statistics;

import de.symeda.sormas.api.I18nProperties;

public enum StatisticsVisualizationType {

	TABLE,
	MAP,
	CHART;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
	
	public enum StatisticsVisualizationMapType {
		REGIONS,
		DISTRICTS;

		public String toString() {
			return I18nProperties.getEnumCaption(this);
		};
	}

	public enum StatisticsVisualizationChartType {
		COLUMN,
		STACKED_COLUMN,
		LINE,
		PIE;

		public String toString() {
			return I18nProperties.getEnumCaption(this);
		};
	}
}


