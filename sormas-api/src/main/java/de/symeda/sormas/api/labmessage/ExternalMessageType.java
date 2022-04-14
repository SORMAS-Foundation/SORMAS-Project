package de.symeda.sormas.api.labmessage;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum ExternalMessageType {

	LAB_MESSAGE,
	PHYSICIANS_REPORT;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

}
