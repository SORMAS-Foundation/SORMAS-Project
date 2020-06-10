/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.facility;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;

public final class FacilityHelper {

	private FacilityHelper() {
		// Hide Utility Class Constructor
	}

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
				return I18nProperties.getPrefixCaption(FacilityDto.I18N_PREFIX, FacilityDto.OTHER_FACILITY);
			}
			if (facilityUuid.equals(FacilityDto.NONE_FACILITY_UUID)) {
				return I18nProperties.getPrefixCaption(FacilityDto.I18N_PREFIX, FacilityDto.NO_FACILITY);
			}
			if (facilityUuid.equals(FacilityDto.OTHER_LABORATORY_UUID)) {
				return I18nProperties.getPrefixCaption(FacilityDto.I18N_PREFIX, FacilityDto.OTHER_LABORATORY);
			}
		}

		StringBuilder caption = new StringBuilder();
		if (!DataHelper.isNullOrEmpty(facilityName)) {
			caption.append(facilityName);
		}

		return caption.toString();
	}

	public static boolean isOtherOrNoneHealthFacility(String facilityUuid) {
		return FacilityDto.OTHER_FACILITY_UUID.equals(facilityUuid) || FacilityDto.NONE_FACILITY_UUID.equals(facilityUuid);
	}
}
