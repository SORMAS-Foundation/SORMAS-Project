/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.facility;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum FacilityTypeGroup {

	ACCOMMODATION(true),
	CARE_FACILITY(false),
	CATERING_OUTLET(false),
	EDUCATIONAL_FACILITY(false),
	LEISURE_FACILITY(false),
	MEDICAL_FACILITY(true),
	WORKING_PLACE(false),
	RESIDENCE(true);

	private final boolean suitableForLongerStay;

	private static List<FacilityTypeGroup> groupsWithOvernightAccomodation = null;

	FacilityTypeGroup(boolean suitableForLongerStay) {
		this.suitableForLongerStay = suitableForLongerStay;
	}

	public static List<FacilityTypeGroup> getTypeGroupsSuitableForLongerStay() {
		if (groupsWithOvernightAccomodation == null) {
			groupsWithOvernightAccomodation = new ArrayList<FacilityTypeGroup>();
			for (FacilityTypeGroup facilityTypeGroup : values()) {
				if (facilityTypeGroup.isSuitableForLongerStay()) {
					groupsWithOvernightAccomodation.add(facilityTypeGroup);
				}
			}
		}
		return groupsWithOvernightAccomodation;
	}

	public boolean isSuitableForLongerStay() {
		return suitableForLongerStay;
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
