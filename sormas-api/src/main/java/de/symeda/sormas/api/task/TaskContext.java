package de.symeda.sormas.api.task;

import de.symeda.sormas.api.I18nProperties;

public enum TaskContext {

	CASE,
	CONTACT,
	EVENT,
	GENERAL
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
