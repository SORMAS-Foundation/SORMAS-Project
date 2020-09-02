package de.symeda.sormas.api.visit;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum VisitResult {

	NOT_SYMPTOMATIC,
	SYMPTOMATIC,
	UNAVAILABLE,
	UNCOOPERATIVE,
	NOT_PERFORMED;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
