package de.symeda.sormas.ui.statistics;

import de.symeda.sormas.api.I18nProperties;

public enum StatisticsVisualizationElementType {

	ROWS,
	COLUMNS;

	public String toString(StatisticsVisualizationType visualizationType) {
		if (visualizationType == StatisticsVisualizationType.CHART) {
			return I18nProperties.getEnumCaption(this, "Chart");
		} else {
			return I18nProperties.getEnumCaption(this);
		}
	}

	public String getEmptySelectionString(StatisticsVisualizationType visualizationType) {
		switch (this) {
		case ROWS:
			if (visualizationType == StatisticsVisualizationType.CHART) {
				return "Don't group series";
			} else {
				return "Don't group rows";
			}
		case COLUMNS:
			if (visualizationType == StatisticsVisualizationType.CHART) {
				return "Don't group x-axis";
			} else {
				return "Don't group columns";
			}
		default:
			return null;
		}
	}

}
