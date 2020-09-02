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
package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;

public enum SampleMaterial {

	BLOOD,
	SERA,
	STOOL,
	NASAL_SWAB,
	THROAT_SWAB,
	NP_SWAB,
	RECTAL_SWAB,
	CEREBROSPINAL_FLUID,
	CRUST,
	TISSUE,
	URINE,
	CORNEA_PM,
	SALIVA,
	URINE_PM,
	NUCHAL_SKIN_BIOPSY,
	SPUTUM,
	ENDOTRACHEAL_ASPIRATE,
	BRONCHOALVEOLAR_LAVAGE,
	BRAIN_TISSUE,
	OTHER;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public static String toString(SampleMaterial value, String details) {

		if (value == null) {
			return "";
		}

		if (value == SampleMaterial.OTHER) {
			return DataHelper.toStringNullable(details);
		}

		return value.toString();
	}
}
