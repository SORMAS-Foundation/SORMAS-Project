package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;
@Schema(description = "App used to trace contacts of patients infected with the disease.")
public enum TracingApp {

	CORONA_WARN_APP,
	OTHER,
	UNKNOWN;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
