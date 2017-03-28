package de.symeda.sormas.api;

public enum Month {

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
	};
	
}
