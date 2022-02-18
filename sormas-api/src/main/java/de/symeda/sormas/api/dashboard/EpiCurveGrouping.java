package de.symeda.sormas.api.dashboard;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum EpiCurveGrouping {

	DAY,
	WEEK,
	MONTH;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

}
