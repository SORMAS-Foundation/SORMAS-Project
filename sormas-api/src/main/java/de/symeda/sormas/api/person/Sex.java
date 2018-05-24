package de.symeda.sormas.api.person;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

public enum Sex implements StatisticsGroupingKey {
	
	MALE,
	FEMALE;
	
	public String getName() {
		return this.name();
	}
	
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
		
		return this.toString().compareTo(o.toString());
	}
	
}
