package de.symeda.sormas.api.immunization;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum ImmunizationDateType {

	REPORT_DATE,
	/**
	 * @deprecated The immunization end date was superseded by the validity period, use {@link #VALID_UNTIL}. For immunizations without a
	 *             start or end date, filtering by this type falls back to valid until.
	 */
	@Deprecated
	IMMUNIZATION_END,
	VALID_UNTIL,
	RECOVERY_DATE,
	LAST_VACCINATION_DATE,
	FIRST_VACCINATION_DATE;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
