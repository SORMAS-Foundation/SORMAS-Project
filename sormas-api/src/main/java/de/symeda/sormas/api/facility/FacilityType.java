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
import java.util.List;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum FacilityType {

	AFTERSCHOOL_CENTER(false),
	CHILDRENS_DAY_CARE(false),
	CHILDRENS_HOME(true),
	CORRECTIONAL_FACILITY(true),
	DOCTORS_OFFICE(false),
	ELDERLY_CARE_FACILITY(true),
	KINDERGARTEN(false),
	HOMELESS_SHELTER(true),
	HOSPITAL(true),
	LABORATORY(false),
	MOBILE_NURSING_SERVICE(false),
	OTHER_COLLECTIVE_ACCOMMODATION(true),
	OTHER_EDUCATIONAL_FACILITY(false),
	REFUGEE_HOSTEL(true),
	SCHOOL(false),
	TREATMENT_CENTER(true),
	VACATION_CAMP(true);

	private final boolean permanentResidencePossible;

	FacilityType(boolean permanentResidencePossible) {
		this.permanentResidencePossible = permanentResidencePossible;
	}

	public List<FacilityType> getPermanentResidenceFacilityTypes() {
		List<FacilityType> facilityTypes = new ArrayList<>();
		for (FacilityType facilityType : values()) {
			if (facilityType.isPermanentResidencePossible()) {
				facilityTypes.add(facilityType);
			}
		}
		return facilityTypes;
	}

	public boolean isPermanentResidencePossible() {
		return permanentResidencePossible;
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
