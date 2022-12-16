package de.symeda.sormas.api.externalmessage;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Type of the external message.")
public enum ExternalMessageType {

	LAB_MESSAGE,
	PHYSICIANS_REPORT;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
