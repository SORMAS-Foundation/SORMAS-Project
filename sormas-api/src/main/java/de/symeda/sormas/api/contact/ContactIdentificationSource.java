package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;
@Schema(description = "Source that identified a contact has occured.")
public enum ContactIdentificationSource {

	CASE_PERSON,
	CONTACT_PERSON,
	TRACING_APP,
	OTHER,
	UNKNOWN;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
