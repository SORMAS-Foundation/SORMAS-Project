package de.symeda.sormas.api.statistics;

import de.symeda.sormas.api.I18nProperties;

public enum StatisticsCaseAttributeGroup {
	
	TIME(StatisticsCaseAttribute.ONSET_TIME,StatisticsCaseAttribute.RECEPTION_TIME,StatisticsCaseAttribute.REPORT_TIME),
	PLACE(StatisticsCaseAttribute.REGION_DISTRICT),
	PERSON(StatisticsCaseAttribute.SEX,StatisticsCaseAttribute.AGE_INTERVAL_1_YEAR,StatisticsCaseAttribute.AGE_INTERVAL_5_YEARS,
			StatisticsCaseAttribute.AGE_INTERVAL_CHILDREN_COARSE,StatisticsCaseAttribute.AGE_INTERVAL_CHILDREN_FINE,
			StatisticsCaseAttribute.AGE_INTERVAL_CHILDREN_MEDIUM, StatisticsCaseAttribute.AGE_INTERVAL_BASIC),
	CASE(StatisticsCaseAttribute.DISEASE,StatisticsCaseAttribute.CLASSIFICATION,StatisticsCaseAttribute.OUTCOME);
	
	private final StatisticsCaseAttribute[] attributes;

	StatisticsCaseAttributeGroup(StatisticsCaseAttribute ...attributes) {
		this.attributes = attributes;
	}
	
	public StatisticsCaseAttribute[] getAttributes() {
		return attributes;
	}
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
}
