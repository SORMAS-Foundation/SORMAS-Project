package de.symeda.sormas.api.person;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status of the symptoms journal maintained by the contact person")
public enum SymptomJournalStatus {

	UNREGISTERED,
	REGISTERED,
	ACCEPTED,
	REJECTED,
	DELETED;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
