package de.symeda.sormas.ui.utils;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum EpiWeekFilterOption {

	LAST_WEEK,
	THIS_WEEK,
	SPECIFY_WEEK;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
