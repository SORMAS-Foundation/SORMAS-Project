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

package de.symeda.sormas.api.epidata;

import java.util.Date;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;

public final class EpiDataHelper {

	private EpiDataHelper() {

	}

	public static String buildDetailedTravelString(
		String travelLocation,
		String travelDetails,
		Date travelDateFrom,
		Date travelDateTo,
		Language language) {
		StringBuilder resultString = new StringBuilder();

		if (!DataHelper.isNullOrEmpty(travelDetails)) {
			resultString.append(travelDetails);
		}

		if (!DataHelper.isNullOrEmpty(travelLocation)) {
			if (resultString.length() > 0) {
				resultString.append(", ");
			}
			resultString.append(travelLocation);
		}

		if (travelDateFrom != null) {
			if (resultString.length() > 0) {
				resultString.append(", ");
			}
			resultString.append(DateHelper.formatLocalDate(travelDateFrom, language));
		}

		if (travelDateTo != null) {
			if (travelDateFrom != null) {
				resultString.append(" - ");
			} else if (resultString.length() > 0) {
				resultString.append(", ");
			}
			resultString.append(DateHelper.formatLocalDate(travelDateTo, language));
		}

		return resultString.toString();
	}

}
