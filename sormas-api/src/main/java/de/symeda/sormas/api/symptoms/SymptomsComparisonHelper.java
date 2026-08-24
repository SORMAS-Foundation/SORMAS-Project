/*******************************************************************************
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
 *******************************************************************************/
package de.symeda.sormas.api.symptoms;

import java.util.List;

import de.symeda.sormas.api.utils.DtoUserDefinedValuesHelper;

/**
 * Helper class for comparing SymptomsDto objects.
 * Provides methods to extract comparable symptom values and check for mismatches between two SymptomsDto instances.
 */
public final class SymptomsComparisonHelper {

	private static final List<String> NON_COMPARABLE_SYMPTOM_PROPERTIES = List.of(SymptomsDto.SYMPTOMATIC);

	private SymptomsComparisonHelper() {
		// Utility class
	}

	/**
	 * Checks whether a SymptomsDto contains any user-defined symptom data.
	 * Derived or technical properties such as pseudonymization flags and the calculated symptomatic flag are ignored.
	 *
	 * @param symptoms
	 *            The SymptomsDto to inspect
	 * @return true when at least one user-defined symptom field contains a value, false otherwise
	 */
	public static boolean hasAnyUserDefinedSymptoms(SymptomsDto symptoms) {
		return DtoUserDefinedValuesHelper.hasAnyUserDefinedValuesIgnoringUnknown(symptoms, SymptomsDto.class, NON_COMPARABLE_SYMPTOM_PROPERTIES);
	}
}
