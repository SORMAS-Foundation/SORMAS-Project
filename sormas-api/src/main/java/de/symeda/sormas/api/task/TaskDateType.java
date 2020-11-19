package de.symeda.sormas.api.task;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum TaskDateType {

	SUGGESTED_START_DATE,
	DUE_DATE;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
