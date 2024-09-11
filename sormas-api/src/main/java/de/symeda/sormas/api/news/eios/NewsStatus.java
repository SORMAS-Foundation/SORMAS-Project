package de.symeda.sormas.api.news.eios;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum NewsStatus {

	PENDING,
	UNUSEFUL,
	APPROVED;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
