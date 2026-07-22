/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
package de.symeda.sormas.api.epidata;

import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * Type of clinical service where the patient was first seen.
 * Used in the Conclusion section of the exposure investigation for Syphilis cases in Luxembourg.
 */
public enum TypeOfClinicalService {

	INFECTIOUS_DISEASE_CLINIC,
	GENERAL_PRACTITIONER,
	DERMATOLOGY_VENEREOLOGY_CLINIC,
	GYNAECOLOGY_CLINIC,
	DEDICATED_STI_CLINIC,
	UROLOGY,
	ANTENATAL_CARE,
	FAMILY_PLANNING_CLINIC,
	HOSPITAL_EMERGENCY_DEPARTMENT,
	YOUTH_CLINIC,
	COMBINED_SERVICE,
	OTHER_PRIMARY_CARE,
	OTHER;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
