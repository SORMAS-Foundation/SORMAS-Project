package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Types of screenings that can be conducted for a disease")
public enum ScreeningType {

	ON_HOSPITAL_ADMISSION,
	ON_CARE_HOME_ADMISSION,
	ON_ASYLUM_ADMISSION,
	ON_ENTRY_FROM_RISK_AREA,
	HEALTH_SECTOR_EMPLOYEE,
	EDUCATIONAL_INSTITUTIONS,
	SELF_ARRANGED_TEST,
	SELF_CONDUCTED_TEST,
	OTHER;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
