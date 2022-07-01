package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum CaseReferenceDefinition {

	FULFILLED,
	NOT_FULFILLED;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
