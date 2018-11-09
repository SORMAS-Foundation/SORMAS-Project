package de.symeda.sormas.api;

import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

public enum Disease implements StatisticsGroupingKey {
	
	CHOLERA,
	CSM,
	DENGUE,
	EVD,
	LASSA,
	MEASLES,
	MONKEYPOX,
	NEW_INFLUENCA,
	PLAGUE,
	YELLOW_FEVER,
	OTHER;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
	public String toShortString() {
		return I18nProperties.getShortEnumCaption(this);
	}
	
	public String getName() {
		return this.name();
	}
	
	public boolean isSupportingOutbreakMode() {
		switch(this) {
		case CSM:
			return true;
		default:
			return false;		
		}
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
