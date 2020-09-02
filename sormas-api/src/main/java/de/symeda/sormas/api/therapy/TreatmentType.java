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
package de.symeda.sormas.api.therapy;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum TreatmentType {

	DRUG_INTAKE,
	ORAL_REHYDRATION_SALTS,
	BLOOD_TRANSFUSION,
	RENAL_REPLACEMENT_THERAPY,
	IV_FLUID_THERAPY,
	OXYGEN_THERAPY,
	INVASIVE_MECHANICAL_VENTILATION,
	VASOPRESSORS_INOTROPES,
	OTHER;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public static String buildCaption(TreatmentType treatmentType, String treatmentDetails, TypeOfDrug typeOfDrug) {

		StringBuilder captionBuilder = new StringBuilder();
		captionBuilder.append(treatmentType.toString());
		if (!StringUtils.isEmpty(treatmentDetails)) {
			captionBuilder.append(" - ").append(treatmentDetails);
		}
		if (typeOfDrug != null && typeOfDrug != TypeOfDrug.OTHER) {
			captionBuilder.append(" - ").append(typeOfDrug.toString());
		}
		return captionBuilder.toString();
	}
}
