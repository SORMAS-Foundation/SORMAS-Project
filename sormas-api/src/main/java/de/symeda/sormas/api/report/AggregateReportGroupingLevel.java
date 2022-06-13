package de.symeda.sormas.api.report;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.JurisdictionLevel;

public enum AggregateReportGroupingLevel {

	REGION,
	DISTRICT,
	HEALTH_FACILITY,
	POINT_OF_ENTRY;

	public static AggregateReportGroupingLevel getByJurisdictionLevel(JurisdictionLevel jurisdictionLevel) {
		switch (jurisdictionLevel) {

		case NATION:
		case REGION:
			return REGION;
		case DISTRICT:
			return DISTRICT;
		case HEALTH_FACILITY:
			return HEALTH_FACILITY;
		case POINT_OF_ENTRY:
			return POINT_OF_ENTRY;
		default:
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
