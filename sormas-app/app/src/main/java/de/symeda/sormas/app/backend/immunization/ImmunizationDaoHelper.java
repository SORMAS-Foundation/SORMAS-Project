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

package de.symeda.sormas.app.backend.immunization;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.utils.DateHelper;

public class ImmunizationDaoHelper {

	private ImmunizationDaoHelper() {
	}

	public static List<Immunization> overlappingDateRangeImmunizations(List<Immunization> immunizations, Date startDate, Date endDate) {
		return immunizations.stream().filter(immunization -> {
			final Date immunizationEndDate = immunization.getEndDate();
			final Date immunizationStartDate = immunization.getStartDate();
			if (startDate != null && endDate != null) {
				final boolean endDateNull =
					immunizationEndDate == null && immunizationStartDate != null && DateHelper.isBetween(immunizationStartDate, startDate, endDate);
				final boolean startDateNull =
					immunizationStartDate == null && immunizationEndDate != null && DateHelper.isBetween(immunizationEndDate, startDate, endDate);
				final boolean between = (immunizationEndDate == null || startDate.equals(immunizationEndDate) || immunizationEndDate.after(startDate))
					&& (immunizationStartDate == null || endDate.equals(immunizationStartDate) || immunizationStartDate.before(endDate));

				return endDateNull || startDateNull || between || (immunizationStartDate == null && immunizationEndDate == null);
			} else if (startDate != null && endDate == null) {
				return (immunizationEndDate == null)
					|| (immunizationEndDate != null && (immunizationEndDate.equals(startDate) || startDate.before(immunizationEndDate)))
					|| (immunizationStartDate == null && immunizationEndDate == null);
			} else if (endDate != null && startDate == null) {
				return (immunizationStartDate == null)
						|| (immunizationStartDate != null && (immunizationStartDate.equals(endDate) || endDate.after(immunizationStartDate)))
						|| (immunizationStartDate == null && immunizationEndDate == null);
			}
			return true;
		}).collect(Collectors.toList());
	}
}
