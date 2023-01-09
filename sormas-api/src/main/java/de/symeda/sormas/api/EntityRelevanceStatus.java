package de.symeda.sormas.api;

import java.util.Arrays;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum EntityRelevanceStatus {

	ACTIVE,
	ARCHIVED,
	ACTIVE_AND_ARCHIVED,
	DELETED;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public static Object[] getAllExceptDeleted() {
		return Arrays.stream(EntityRelevanceStatus.values()).filter(val -> val != EntityRelevanceStatus.DELETED).toArray();
	};
}
