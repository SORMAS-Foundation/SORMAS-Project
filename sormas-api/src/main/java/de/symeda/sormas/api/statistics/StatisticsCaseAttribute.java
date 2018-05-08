package de.symeda.sormas.api.statistics;

import de.symeda.sormas.api.I18nProperties;

public enum StatisticsCaseAttribute {

	ONSET_TIME(false, StatisticsCaseSubAttribute.YEAR,StatisticsCaseSubAttribute.QUARTER,StatisticsCaseSubAttribute.MONTH,StatisticsCaseSubAttribute.EPI_WEEK,
			StatisticsCaseSubAttribute.QUARTER_OF_YEAR,StatisticsCaseSubAttribute.MONTH_OF_YEAR,StatisticsCaseSubAttribute.EPI_WEEK_OF_YEAR,StatisticsCaseSubAttribute.DATE_RANGE),
	RECEPTION_TIME(false, StatisticsCaseSubAttribute.YEAR,StatisticsCaseSubAttribute.QUARTER,StatisticsCaseSubAttribute.MONTH,StatisticsCaseSubAttribute.EPI_WEEK,
			StatisticsCaseSubAttribute.QUARTER_OF_YEAR,StatisticsCaseSubAttribute.MONTH_OF_YEAR,StatisticsCaseSubAttribute.EPI_WEEK_OF_YEAR,StatisticsCaseSubAttribute.DATE_RANGE),
	REPORT_TIME(false, StatisticsCaseSubAttribute.YEAR,StatisticsCaseSubAttribute.QUARTER,StatisticsCaseSubAttribute.MONTH,StatisticsCaseSubAttribute.EPI_WEEK,
			StatisticsCaseSubAttribute.QUARTER_OF_YEAR,StatisticsCaseSubAttribute.MONTH_OF_YEAR,StatisticsCaseSubAttribute.EPI_WEEK_OF_YEAR,StatisticsCaseSubAttribute.DATE_RANGE),
	REGION_DISTRICT(true, StatisticsCaseSubAttribute.REGION,StatisticsCaseSubAttribute.DISTRICT),
	SEX(true),
	AGE_INTERVAL_1_YEAR(false),
	AGE_INTERVAL_5_YEARS(false),
	AGE_INTERVAL_CHILDREN_COARSE(false),
	AGE_INTERVAL_CHILDREN_FINE(false),
	AGE_INTERVAL_CHILDREN_MEDIUM(false),
	DISEASE(true),
	CLASSIFICATION(true),
	OUTCOME(true);
	
	private final boolean needsSorting;
	private final StatisticsCaseSubAttribute[] subAttributes;

	StatisticsCaseAttribute(boolean needsSorting, StatisticsCaseSubAttribute ...subAttributes) {
		this.needsSorting = needsSorting;
		this.subAttributes = subAttributes;
	}
	
	public boolean isNeedsSorting() {
		return needsSorting;
	}

	public StatisticsCaseSubAttribute[] getSubAttributes() {
		return subAttributes;
	}
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
}
