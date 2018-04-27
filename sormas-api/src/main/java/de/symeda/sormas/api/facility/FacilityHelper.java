package de.symeda.sormas.api.facility;

import de.symeda.sormas.api.utils.DataHelper;

public class FacilityHelper {

	public static String buildFacilityString(String facilityName, String facilityDetails) {
		StringBuilder result = new StringBuilder();
		if (!DataHelper.isNullOrEmpty(facilityName)) {
			result.append(facilityName);
		}
		if (!DataHelper.isNullOrEmpty(facilityDetails)) {
			if (result.length() > 0) {
				result.append(" - ");			
			}
			result.append(facilityDetails);
		}		
		return result.toString();
	}
}
