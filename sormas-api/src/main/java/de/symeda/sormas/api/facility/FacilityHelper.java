package de.symeda.sormas.api.facility;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;

public class FacilityHelper {

	public static String buildFacilityString(String facilityUuid, String facilityName, String facilityDetails) {
		StringBuilder result = new StringBuilder();
		result.append(buildFacilityString(facilityUuid, facilityName));

		if (!DataHelper.isNullOrEmpty(facilityDetails)) {
			if (result.length() > 0) {
				result.append(" - ");			
			}
			result.append(facilityDetails);
		}		
		return result.toString();
	}

	public static String buildFacilityString(String facilityUuid, String facilityName) {
		if (facilityUuid != null) {
			if (facilityUuid.equals(FacilityDto.OTHER_FACILITY_UUID)) {
				return I18nProperties.getPrefixFieldCaption(FacilityDto.I18N_PREFIX, FacilityDto.OTHER_FACILITY);
			}
			if (facilityUuid.equals(FacilityDto.NONE_FACILITY_UUID)) {
				return I18nProperties.getPrefixFieldCaption(FacilityDto.I18N_PREFIX, FacilityDto.NO_FACILITY);
			}
			if (facilityUuid.equals(FacilityDto.OTHER_LABORATORY_UUID)) {
				return I18nProperties.getPrefixFieldCaption(FacilityDto.I18N_PREFIX, FacilityDto.OTHER_LABORATORY);
			}
		}

		StringBuilder caption = new StringBuilder();
		caption.append(facilityName);

		return caption.toString();
	}

	public static boolean isOtherOrNoneHealthFacility(String facilityUuid) {
		return FacilityDto.OTHER_FACILITY_UUID.equals(facilityUuid)
				|| FacilityDto.NONE_FACILITY_UUID.equals(facilityUuid);
	}
}
