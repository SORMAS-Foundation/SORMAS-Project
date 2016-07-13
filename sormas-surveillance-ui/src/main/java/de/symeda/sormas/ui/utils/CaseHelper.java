package de.symeda.sormas.ui.utils;

import de.symeda.sormas.api.DataTransferObject;

public final class CaseHelper {

	public static final String getShortUuid(DataTransferObject domainObject) {
		return getShortUuid(domainObject.getUuid());
	}
	
	public static final String getShortUuid(String uuid) {
		if (uuid == null)
			return null;
		return uuid.substring(0, 6).toUpperCase();
	}
}
