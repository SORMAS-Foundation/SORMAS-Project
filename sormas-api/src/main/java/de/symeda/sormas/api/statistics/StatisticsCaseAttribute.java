package de.symeda.sormas.api.statistics;

import de.symeda.sormas.api.I18nProperties;

public enum StatisticsCaseAttribute {

	ONSET_TIME(StatisticsCaseSubAttribute.YEAR,StatisticsCaseSubAttribute.QUARTER,StatisticsCaseSubAttribute.MONTH,StatisticsCaseSubAttribute.EPI_WEEK,
			StatisticsCaseSubAttribute.QUARTER_OF_YEAR,StatisticsCaseSubAttribute.MONTH_OF_YEAR,StatisticsCaseSubAttribute.EPI_WEEK_OF_YEAR,StatisticsCaseSubAttribute.DATE_RANGE),
	RECEPTION_TIME(StatisticsCaseSubAttribute.YEAR,StatisticsCaseSubAttribute.QUARTER,StatisticsCaseSubAttribute.MONTH,StatisticsCaseSubAttribute.EPI_WEEK,
			StatisticsCaseSubAttribute.QUARTER_OF_YEAR,StatisticsCaseSubAttribute.MONTH_OF_YEAR,StatisticsCaseSubAttribute.EPI_WEEK_OF_YEAR,StatisticsCaseSubAttribute.DATE_RANGE),
	REPORT_TIME(StatisticsCaseSubAttribute.YEAR,StatisticsCaseSubAttribute.QUARTER,StatisticsCaseSubAttribute.MONTH,StatisticsCaseSubAttribute.EPI_WEEK,
			StatisticsCaseSubAttribute.QUARTER_OF_YEAR,StatisticsCaseSubAttribute.MONTH_OF_YEAR,StatisticsCaseSubAttribute.EPI_WEEK_OF_YEAR,StatisticsCaseSubAttribute.DATE_RANGE),
	REGION_DISTRICT(StatisticsCaseSubAttribute.REGION,StatisticsCaseSubAttribute.DISTRICT),
	SEX,
	AGE_INTERVAL_1_YEAR,
	AGE_INTERVAL_5_YEARS,
	AGE_INTERVAL_CHILDREN_COARSE,
	AGE_INTERVAL_CHILDREN_FINE,
	AGE_INTERVAL_CHILDREN_MEDIUM,
	DISEASE,
	CLASSIFICATION,
	OUTCOME;
	
	private final StatisticsCaseSubAttribute[] subAttributes;

	StatisticsCaseAttribute(StatisticsCaseSubAttribute ...subAttributes) {
		this.subAttributes = subAttributes;
	}
	
	public StatisticsCaseSubAttribute[] getSubAttributes() {
		return subAttributes;
	}
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
}
