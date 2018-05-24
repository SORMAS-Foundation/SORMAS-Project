package de.symeda.sormas.ui.statistics;

import de.symeda.sormas.api.I18nProperties;

public enum StatisticsVisualizationElementType {

	ROWS,
	COLUMNS;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public String getEmptySelectionString() {
		switch (this) {
		case ROWS:
			return "Don't group rows";
		case COLUMNS:
			return "Don't group columns";
		default:
			return null;
		}
	}

}
