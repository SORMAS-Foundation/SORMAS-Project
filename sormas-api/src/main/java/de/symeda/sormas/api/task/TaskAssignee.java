package de.symeda.sormas.api.task;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum TaskAssignee {

	ALL,
	CURRENT_USER,
	OTHER_USERS;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
