package de.symeda.sormas.api.infrastructure;

import de.symeda.sormas.api.i18n.I18nProperties;

public class InfrastructureHelper {
	
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
