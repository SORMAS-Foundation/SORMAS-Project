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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum FacilityType {

	ASSOCIATION(FacilityTypeGroup.LEISURE_FACILITY),
	BUSINESS(FacilityTypeGroup.WORKING_PLACE),
	CAMPSITE(FacilityTypeGroup.ACCOMMODATION),
	CANTINE(FacilityTypeGroup.CATERING_OUTLET),
	CHILDRENS_DAY_CARE(FacilityTypeGroup.CARE_FACILITY),
	CHILDRENS_HOME(FacilityTypeGroup.RESIDENCE),
	CORRECTIONAL_FACILITY(FacilityTypeGroup.RESIDENCE),
	CRUISE_SHIP(FacilityTypeGroup.ACCOMMODATION),
	ELDERLY_CARE(FacilityTypeGroup.CARE_FACILITY),
	EVENT_VENUE(FacilityTypeGroup.LEISURE_FACILITY),
	FOOD_STALL(FacilityTypeGroup.CATERING_OUTLET),
	HOLIDAY_CAMP(FacilityTypeGroup.CARE_FACILITY),
	HOMELESS_SHELTER(FacilityTypeGroup.RESIDENCE),
	HOSPITAL(FacilityTypeGroup.MEDICAL_FACILITY),
	HOSTEL(FacilityTypeGroup.RESIDENCE),
	HOTEL(FacilityTypeGroup.ACCOMMODATION),
	KINDERGARTEN(FacilityTypeGroup.CARE_FACILITY),
	LABORATORY(FacilityTypeGroup.MEDICAL_FACILITY),
	MASS_ACCOMMODATION(FacilityTypeGroup.ACCOMMODATION),
	MILITARY_BARRACKS(FacilityTypeGroup.RESIDENCE),
	MOBILE_NURSING_SERVICE(FacilityTypeGroup.CARE_FACILITY),
	OTHER_ACCOMMODATION(FacilityTypeGroup.ACCOMMODATION),
	OTHER_CARE_FACILITY(FacilityTypeGroup.CARE_FACILITY),
	OTHER_CATERING_OUTLET(FacilityTypeGroup.CATERING_OUTLET),
	OTHER_EDUCATIONAL_FACILITY(FacilityTypeGroup.EDUCATIONAL_FACILITY),
	OTHER_LEISURE_FACILITY(FacilityTypeGroup.LEISURE_FACILITY),
	OTHER_MEDICAL_FACILITY(FacilityTypeGroup.MEDICAL_FACILITY),
	OTHER_RESIDENCE(FacilityTypeGroup.RESIDENCE),
	OTHER_WORKING_PLACE(FacilityTypeGroup.WORKING_PLACE),
	OUTPATIENT_TREATMENT_FACILITY(FacilityTypeGroup.MEDICAL_FACILITY),
	PLACE_OF_WORSHIP(FacilityTypeGroup.LEISURE_FACILITY),
	PUBLIC_PLACE(FacilityTypeGroup.LEISURE_FACILITY),
	REFUGEE_ACCOMMODATION(FacilityTypeGroup.RESIDENCE),
	REHAB_FACILITY(FacilityTypeGroup.MEDICAL_FACILITY),
	RESTAURANT(FacilityTypeGroup.CATERING_OUTLET),
	RETIREMENT_HOME(FacilityTypeGroup.RESIDENCE),
	SCHOOL(FacilityTypeGroup.EDUCATIONAL_FACILITY),
	SWIMMING_POOL(FacilityTypeGroup.LEISURE_FACILITY),
	THEATER(FacilityTypeGroup.LEISURE_FACILITY),
	UNIVERSITY(FacilityTypeGroup.EDUCATIONAL_FACILITY),
	ZOO(FacilityTypeGroup.LEISURE_FACILITY);

	private final FacilityTypeGroup facilityTypeGroup;

	FacilityType(FacilityTypeGroup group) {
		this.facilityTypeGroup = group;
	}

	public FacilityTypeGroup getFacilityTypeGroup() {
		return facilityTypeGroup;
	}

	public static List<FacilityType> getFacilityTypesByGroup(FacilityTypeGroup group, boolean withoutLaboratory) {
		if (group == null) {
			return null;
		}
		List<FacilityType> list =
			Arrays.stream(FacilityType.values()).filter(e -> group.equals(e.getFacilityTypeGroup())).collect(Collectors.toList());

		if (withoutLaboratory && FacilityTypeGroup.MEDICAL_FACILITY.equals(group)) {
			list.remove(FacilityType.LABORATORY);
		}
		return list;
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
