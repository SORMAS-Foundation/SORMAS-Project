package de.symeda.sormas.api.infrastructure;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;

public class InfrastructureHelper {

	public static String buildPointOfEntryString(String pointOfEntryUuid, String pointOfEntryName, String pointOfEntryDetails) {
		StringBuilder result = new StringBuilder();
		result.append(buildPointOfEntryString(pointOfEntryUuid, pointOfEntryName));

		if (!DataHelper.isNullOrEmpty(pointOfEntryDetails)) {
			if (result.length() > 0) {
				result.append(" - ");			
			}
			result.append(pointOfEntryDetails);
		}		
		return result.toString();
	}
	
	public static String buildPointOfEntryString(String pointOfEntryUuid, String pointOfEntryName) {
		if (pointOfEntryUuid != null) {
			if (pointOfEntryUuid.equals(PointOfEntryDto.OTHER_AIRPORT_UUID)) {
				return I18nProperties.getPrefixCaption(PointOfEntryDto.I18N_PREFIX, PointOfEntryDto.OTHER_AIRPORT);
			}
			if (pointOfEntryUuid.equals(PointOfEntryDto.OTHER_SEAPORT_UUID)) {
				return I18nProperties.getPrefixCaption(PointOfEntryDto.I18N_PREFIX, PointOfEntryDto.OTHER_SEAPORT);
			}
			if (pointOfEntryUuid.equals(PointOfEntryDto.OTHER_GROUND_CROSSING_UUID)) {
				return I18nProperties.getPrefixCaption(PointOfEntryDto.I18N_PREFIX, PointOfEntryDto.OTHER_GROUND_CROSSING);
			}
			if (pointOfEntryUuid.equals(PointOfEntryDto.OTHER_POE_UUID)) {
				return I18nProperties.getPrefixCaption(PointOfEntryDto.I18N_PREFIX, PointOfEntryDto.OTHER_POE);
			}
		}

		return pointOfEntryName;
	}
	
}
