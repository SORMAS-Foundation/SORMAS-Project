package de.symeda.sormas.api.immunization;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Enum type for meaning of immunization date")
public enum ImmunizationDateType {

	REPORT_DATE,
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
