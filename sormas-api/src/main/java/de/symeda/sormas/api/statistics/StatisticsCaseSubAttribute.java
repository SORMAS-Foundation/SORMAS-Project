package de.symeda.sormas.api.statistics;

import de.symeda.sormas.api.I18nProperties;

public enum StatisticsCaseSubAttribute {
	
	YEAR(true, true),
	QUARTER(true, true),
	MONTH(true, true),
	EPI_WEEK(true, true),
	QUARTER_OF_YEAR(true, true),
	MONTH_OF_YEAR(true, true),
	EPI_WEEK_OF_YEAR(true, true),
	DATE_RANGE(true, false),
	REGION(false, true),
	DISTRICT(false, true);
	
	private boolean usedForFilters;
	private boolean usedForGrouping;
	
	StatisticsCaseSubAttribute(boolean usedForFilters, boolean usedForGrouping) {
		this.usedForFilters = usedForFilters;
		this.usedForGrouping = usedForGrouping;
	}
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public boolean isUsedForFilters() {
		return usedForFilters;
	}	
	
	public boolean isUsedForGrouping() {
		return usedForGrouping;
	}
	
}