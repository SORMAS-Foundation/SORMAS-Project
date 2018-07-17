package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

public enum CaseClassification implements StatisticsGroupingKey {
	
	NOT_CLASSIFIED, 
	SUSPECT, 
	PROBABLE,
	CONFIRMED, 
	NO_CASE;

	public String getName() {
		return this.name();
	}
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
	public String toShortString() {
		return I18nProperties.getEnumCaption(this, "Short");
	}
	
	@Override
	public int keyCompareTo(StatisticsGroupingKey o) {
		if (o == null) {
			throw new NullPointerException("Can't compare to null.");
		}
		if (o.getClass() != this.getClass()) {
			throw new UnsupportedOperationException("Can't compare to class " + o.getClass().getName() + " that differs from " + this.getClass().getName());
		}
		
		return this.toString().compareTo(o.toString());
	}
	
}
