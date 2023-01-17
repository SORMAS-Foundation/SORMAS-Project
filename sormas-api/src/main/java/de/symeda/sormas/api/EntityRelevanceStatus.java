package de.symeda.sormas.api;

import java.util.Arrays;
import java.util.stream.Collectors;

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

	public static EntityRelevanceStatus[] getAllExceptDeleted() {
		return Arrays.stream(EntityRelevanceStatus.values())
			.filter(val -> val != EntityRelevanceStatus.DELETED)
			.collect(Collectors.toList())
			.toArray(new EntityRelevanceStatus[] {});
	}
}
