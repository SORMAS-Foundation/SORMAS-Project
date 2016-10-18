package de.symeda.sormas.api.task;

import de.symeda.sormas.api.I18nProperties;

public enum TaskPriority {

	HIGH,
	NORMAL,
	LOW	
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
