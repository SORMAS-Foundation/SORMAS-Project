package de.symeda.sormas.api;

import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

public enum Month implements StatisticsGroupingKey {
	
	JANUARY,
	FEBRUARY,
	MARCH,
	APRIL,
	MAY,
	JUNE,
	JULY,
	AUGUST,
	SEPTEMBER,
	OCTOBER,
	NOVEMBER,
	DECEMBER;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
	@Override
	public int keyCompareTo(StatisticsGroupingKey o) {
		if (o == null) {
			throw new NullPointerException("Can't compare to null.");
		}
		if (o.getClass() != this.getClass()) {
			throw new UnsupportedOperationException("Can't compare to class " + o.getClass().getName() + " that differs from " + this.getClass().getName());
		}
		
		return compareTo((Month) o);
	}
	
}