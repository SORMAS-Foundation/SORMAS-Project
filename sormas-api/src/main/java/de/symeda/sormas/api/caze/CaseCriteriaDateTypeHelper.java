/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.caze;

import java.util.ArrayList;
import java.util.Arrays;

public class CaseCriteriaDateTypeHelper {

	private CaseCriteriaDateTypeHelper() {
	}

	public static CaseCriteriaDateType[] getTypes(boolean isExternalSurvToolShareEnbaled) {
		ArrayList<CaseCriteriaDateType> types = new ArrayList<>();
		types.addAll(Arrays.asList(NewCaseDateType.values()));

		if (isExternalSurvToolShareEnbaled) {
			types.addAll(Arrays.asList(ExternalShareDateType.values()));
		}

		return types.toArray(new CaseCriteriaDateType[] {});
	}

	public static CaseCriteriaDateType valueOf(String string) {
		if (string == null) {
			return null;
		}

		for (CaseCriteriaDateType type : getTypes(true)) {
			if (type.name().equals(string)) {
				return type;
			}
		}

		return null;
	}

	public static String toUrlString(CaseCriteriaDateType type) {
		return type.name();
	}
}
