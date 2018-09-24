package de.symeda.sormas.api.task;

import de.symeda.sormas.api.I18nProperties;

public enum TaskStatus {
	PENDING,
	DONE,
	REMOVED,
	NOT_EXECUTABLE,
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
