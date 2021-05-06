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

package de.symeda.sormas.api.utils.criteria;

import java.util.ArrayList;
import java.util.Arrays;

public class CriteriaDateTypeHelper {

	private CriteriaDateTypeHelper() {
	}

	public static CriteriaDateType[] getTypes(Class<? extends CriteriaDateType> defaultDateType, boolean isExternalSurvToolShareEnbaled) {
		ArrayList<CriteriaDateType> types = new ArrayList<>(Arrays.asList(defaultDateType.getEnumConstants()));

		if (isExternalSurvToolShareEnbaled) {
			types.addAll(Arrays.asList(ExternalShareDateType.values()));
		}

		return types.toArray(new CriteriaDateType[] {});
	}

	public static CriteriaDateType valueOf(Class<? extends CriteriaDateType> defaultDateType, String string) {
		if (string == null) {
			return null;
		}

		for (CriteriaDateType type : getTypes(defaultDateType, true)) {
			if (type.name().equals(string)) {
				return type;
			}
		}

		return null;
	}

	public static String toUrlString(CriteriaDateType type) {
		return type.name();
	}
}
