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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum FacilityType {

	ASSOCIATION(FacilityTypeGroup.LEISURE_FACILITY, false, false),
	BUSINESS(FacilityTypeGroup.WORKING_PLACE, false, false),
	BAR(FacilityTypeGroup.CATERING_OUTLET, false, false),
	CAMPSITE(FacilityTypeGroup.ACCOMMODATION, true, false),
	CANTINE(FacilityTypeGroup.CATERING_OUTLET, false, false),
	CHILDRENS_DAY_CARE(FacilityTypeGroup.CARE_FACILITY, false, false),
	CHILDRENS_HOME(FacilityTypeGroup.RESIDENCE, true, false),
	CORRECTIONAL_FACILITY(FacilityTypeGroup.RESIDENCE, true, false),
	CRUISE_SHIP(FacilityTypeGroup.ACCOMMODATION, true, false),
	ELDERLY_DAY_CARE(FacilityTypeGroup.CARE_FACILITY, false, false),
	EVENT_VENUE(FacilityTypeGroup.LEISURE_FACILITY, false, false),
	FOOD_STALL(FacilityTypeGroup.CATERING_OUTLET, false, false),
	HOLIDAY_CAMP(FacilityTypeGroup.CARE_FACILITY, false, false),
	HOMELESS_SHELTER(FacilityTypeGroup.RESIDENCE, true, false),
	HOSPITAL(FacilityTypeGroup.MEDICAL_FACILITY, true, true),
	HOSTEL(FacilityTypeGroup.RESIDENCE, true, false),
	HOTEL(FacilityTypeGroup.ACCOMMODATION, true, false),
	KINDERGARTEN(FacilityTypeGroup.CARE_FACILITY, false, false),
	LABORATORY(FacilityTypeGroup.MEDICAL_FACILITY, false, false),
	MASS_ACCOMMODATION(FacilityTypeGroup.ACCOMMODATION, true, false),
	MILITARY_BARRACKS(FacilityTypeGroup.RESIDENCE, true, false),
	MOBILE_NURSING_SERVICE(FacilityTypeGroup.CARE_FACILITY, false, false),
	NIGHT_CLUB(FacilityTypeGroup.LEISURE_FACILITY, false, false),
	OTHER_ACCOMMODATION(FacilityTypeGroup.ACCOMMODATION, true, false),
	OTHER_CARE_FACILITY(FacilityTypeGroup.CARE_FACILITY, false, false),
	OTHER_CATERING_OUTLET(FacilityTypeGroup.CATERING_OUTLET, false, false),
	OTHER_EDUCATIONAL_FACILITY(FacilityTypeGroup.EDUCATIONAL_FACILITY, false, false),
	OTHER_LEISURE_FACILITY(FacilityTypeGroup.LEISURE_FACILITY, false, false),
	OTHER_MEDICAL_FACILITY(FacilityTypeGroup.MEDICAL_FACILITY, true, true),
	OTHER_RESIDENCE(FacilityTypeGroup.RESIDENCE, true, false),
	OTHER_WORKING_PLACE(FacilityTypeGroup.WORKING_PLACE, false, false),
	OTHER_COMMERCE(FacilityTypeGroup.COMMERCE, false, false),
	OUTPATIENT_TREATMENT_FACILITY(FacilityTypeGroup.MEDICAL_FACILITY, false, true),
	PLACE_OF_WORSHIP(FacilityTypeGroup.LEISURE_FACILITY, false, false),
	PUBLIC_PLACE(FacilityTypeGroup.LEISURE_FACILITY, false, false),
	REFUGEE_ACCOMMODATION(FacilityTypeGroup.RESIDENCE, true, false),
	REHAB_FACILITY(FacilityTypeGroup.MEDICAL_FACILITY, true, false),
	RESTAURANT(FacilityTypeGroup.CATERING_OUTLET, false, false),
	RETIREMENT_HOME(FacilityTypeGroup.RESIDENCE, true, false),
	RETAIL(FacilityTypeGroup.COMMERCE, false, false),
	WHOLESALE(FacilityTypeGroup.COMMERCE, false, false),
	SCHOOL(FacilityTypeGroup.EDUCATIONAL_FACILITY, false, false),
	SWIMMING_POOL(FacilityTypeGroup.LEISURE_FACILITY, false, false),
	THEATER(FacilityTypeGroup.LEISURE_FACILITY, false, false),
	UNIVERSITY(FacilityTypeGroup.EDUCATIONAL_FACILITY, false, false),
	ZOO(FacilityTypeGroup.LEISURE_FACILITY, false, false),
	AMBULATORY_SURGERY_FACILITY(FacilityTypeGroup.MEDICAL_FACILITY, false, false),
	DIALYSIS_FACILITY(FacilityTypeGroup.MEDICAL_FACILITY, false, false),
	DAY_HOSPITAL(FacilityTypeGroup.MEDICAL_FACILITY, false, false),
	MATERNITY_FACILITY(FacilityTypeGroup.MEDICAL_FACILITY, true, true),
	MEDICAL_PRACTICE(FacilityTypeGroup.MEDICAL_FACILITY, false, false),
	DENTAL_PRACTICE(FacilityTypeGroup.MEDICAL_FACILITY, false, false),
	OTHER_MEDICAL_PRACTICE(FacilityTypeGroup.MEDICAL_FACILITY, false, false),
	DIAGNOSTIC_PREVENTATIVE_THERAPEUTIC_FACILITY(FacilityTypeGroup.MEDICAL_FACILITY, true, false),
	EMERGENCY_MEDICAL_SERVICES(FacilityTypeGroup.MEDICAL_FACILITY, false, false),
	ELDERLY_CARE_FACILITY(FacilityTypeGroup.CARE_FACILITY, true, false),
	DISABLED_PERSON_HABITATION(FacilityTypeGroup.CARE_FACILITY, true, false),
	CARE_RECIPIENT_HABITATION(FacilityTypeGroup.CARE_FACILITY, true, false),
	VISITING_AMBULATORY_AID(FacilityTypeGroup.CARE_FACILITY, false, false),
	AFTER_SCHOOL(FacilityTypeGroup.EDUCATIONAL_FACILITY, false, false),;

	private static final Map<FacilityTypeGroup, List<FacilityType>> typesByGroup = new HashMap<FacilityTypeGroup, List<FacilityType>>();
	private static final Map<FacilityTypeGroup, List<FacilityType>> accomodationTypesByGroup = new HashMap<FacilityTypeGroup, List<FacilityType>>();
	private static List<FacilityType> placeOfBirthTypes;

	private final FacilityTypeGroup facilityTypeGroup;
	private final boolean accommodation;
	private final boolean placeOfBirth;

	FacilityType(FacilityTypeGroup group, boolean accommodation, boolean placeOfBirth) {
		this.facilityTypeGroup = group;
		this.accommodation = accommodation;
		this.placeOfBirth = placeOfBirth;
	}

	public FacilityTypeGroup getFacilityTypeGroup() {
		return facilityTypeGroup;
	}

	public boolean isAccommodation() {
		return accommodation;
	}

	public boolean isPlaceOfBirth() {
		return placeOfBirth;
	}

	public static List<FacilityType> getTypes(FacilityTypeGroup group) {
		if (group == null) {
			return null;
		}
		if (!typesByGroup.containsKey(group)) {
			List<FacilityType> facilityTypes = new ArrayList<FacilityType>();
			for (FacilityType facilityType : values()) {
				if (group.equals(facilityType.getFacilityTypeGroup())) {
					facilityTypes.add(facilityType);
				}
			}
			typesByGroup.put(group, facilityTypes);
		}
		return typesByGroup.get(group);
	}

	public static List<FacilityType> getPlaceOfBirthTypes() {
		if (placeOfBirthTypes == null) {
			placeOfBirthTypes = new ArrayList<FacilityType>();
			for (FacilityType facilityType : values()) {
				if (facilityType.isPlaceOfBirth())
					placeOfBirthTypes.add(facilityType);
			}
		}
		return placeOfBirthTypes;
	}

	public static List<FacilityType> getAccommodationTypes(FacilityTypeGroup group) {
		if (group == null) {
			return null;
		}
		if (!accomodationTypesByGroup.containsKey(group)) {
			List<FacilityType> facilityTypes = new ArrayList<FacilityType>();
			for (FacilityType facilityType : values()) {
				if (group.equals(facilityType.getFacilityTypeGroup()) && facilityType.isAccommodation()) {
					facilityTypes.add(facilityType);
				}
			}
			accomodationTypesByGroup.put(group, facilityTypes);
		}
		return accomodationTypesByGroup.get(group);
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
