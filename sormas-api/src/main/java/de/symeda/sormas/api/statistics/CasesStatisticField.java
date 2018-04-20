package de.symeda.sormas.api.statistics;

public enum CasesStatisticField {

	ONSET_TIME(StatisticSubField.YEAR,StatisticSubField.QUARTER,StatisticSubField.MONTH,StatisticSubField.EPI_WEEK),
	RECEPTION_TIME(StatisticSubField.YEAR,StatisticSubField.QUARTER,StatisticSubField.MONTH,StatisticSubField.EPI_WEEK),
	REPORT_TIME(StatisticSubField.YEAR,StatisticSubField.QUARTER,StatisticSubField.MONTH,StatisticSubField.EPI_WEEK),
	PLACE(StatisticSubField.REGION,StatisticSubField.DISTRICT),
	PERSON_SEX,
	PERSON_AGE_GROUP,
	DISEASE,
	CLASSIFICATION,
	OUTCOME,
	;
	
	private final StatisticSubField[] subFields;

	CasesStatisticField(StatisticSubField ...subFields) {
		this.subFields = subFields;
	}
	
	public StatisticSubField[] getSubFields() {
		return subFields;
	}
}
