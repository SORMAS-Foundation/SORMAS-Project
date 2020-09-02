package de.symeda.sormas.ui.statistics;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum CaseCountOrIncidence {

	CASE_COUNT,
	CASE_INCIDENCE;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
