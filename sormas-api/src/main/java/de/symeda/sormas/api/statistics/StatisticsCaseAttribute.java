package de.symeda.sormas.api.statistics;

import de.symeda.sormas.api.I18nProperties;

public enum StatisticsCaseAttribute {

	ONSET_TIME(StatisticsCaseAttributeGroup.TIME, false, StatisticsCaseSubAttribute.YEAR,StatisticsCaseSubAttribute.QUARTER,StatisticsCaseSubAttribute.MONTH,StatisticsCaseSubAttribute.EPI_WEEK,
			StatisticsCaseSubAttribute.QUARTER_OF_YEAR,StatisticsCaseSubAttribute.MONTH_OF_YEAR,StatisticsCaseSubAttribute.EPI_WEEK_OF_YEAR,StatisticsCaseSubAttribute.DATE_RANGE),
	RECEPTION_TIME(StatisticsCaseAttributeGroup.TIME, false, StatisticsCaseSubAttribute.YEAR,StatisticsCaseSubAttribute.QUARTER,StatisticsCaseSubAttribute.MONTH,StatisticsCaseSubAttribute.EPI_WEEK,
			StatisticsCaseSubAttribute.QUARTER_OF_YEAR,StatisticsCaseSubAttribute.MONTH_OF_YEAR,StatisticsCaseSubAttribute.EPI_WEEK_OF_YEAR,StatisticsCaseSubAttribute.DATE_RANGE),
	REPORT_TIME(StatisticsCaseAttributeGroup.TIME, false, StatisticsCaseSubAttribute.YEAR,StatisticsCaseSubAttribute.QUARTER,StatisticsCaseSubAttribute.MONTH,StatisticsCaseSubAttribute.EPI_WEEK,
			StatisticsCaseSubAttribute.QUARTER_OF_YEAR,StatisticsCaseSubAttribute.MONTH_OF_YEAR,StatisticsCaseSubAttribute.EPI_WEEK_OF_YEAR,StatisticsCaseSubAttribute.DATE_RANGE),
	REGION_DISTRICT(StatisticsCaseAttributeGroup.PLACE, true, StatisticsCaseSubAttribute.REGION,StatisticsCaseSubAttribute.DISTRICT),
	SEX(StatisticsCaseAttributeGroup.PERSON, true),
	AGE_INTERVAL_1_YEAR(StatisticsCaseAttributeGroup.PERSON, false),
	AGE_INTERVAL_5_YEARS(StatisticsCaseAttributeGroup.PERSON, false),
	AGE_INTERVAL_CHILDREN_COARSE(StatisticsCaseAttributeGroup.PERSON, false),
	AGE_INTERVAL_CHILDREN_FINE(StatisticsCaseAttributeGroup.PERSON, false),
	AGE_INTERVAL_CHILDREN_MEDIUM(StatisticsCaseAttributeGroup.PERSON, false),
	AGE_INTERVAL_BASIC(StatisticsCaseAttributeGroup.PERSON, false),
	DISEASE(StatisticsCaseAttributeGroup.CASE, true),
	CLASSIFICATION(StatisticsCaseAttributeGroup.CASE, true),
	OUTCOME(StatisticsCaseAttributeGroup.CASE, true);
	
	private final StatisticsCaseAttributeGroup attributeGroup;
	private final boolean sortByCaption;
	private final StatisticsCaseSubAttribute[] subAttributes;

	StatisticsCaseAttribute(StatisticsCaseAttributeGroup attributeGroup, boolean sortByCaption, StatisticsCaseSubAttribute ...subAttributes) {
		this.attributeGroup = attributeGroup;
		this.sortByCaption = sortByCaption;
		this.subAttributes = subAttributes;
	}
	
	public StatisticsCaseAttributeGroup getAttributeGroup() {
		return attributeGroup;
	}
	
	public boolean isSortByCaption() {
		return sortByCaption;
	}

	public StatisticsCaseSubAttribute[] getSubAttributes() {
		return subAttributes;
	}
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
}
