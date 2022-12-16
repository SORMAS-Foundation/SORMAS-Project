package de.symeda.sormas.api.dashboard;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Whether to group data in epidemiological curves by DAY, WEEK or MONTH")
public enum EpiCurveGrouping {

	DAY,
	WEEK,
	MONTH;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
