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

package de.symeda.sormas.api.climate;

import java.time.LocalDate;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;

/**
 * Case counts beside climate: weekly case-based counts, incidence and the usual range for the Climate dashboard. Counts
 * follow the user's jurisdiction.
 */
@Remote
public interface ClimateCaseFacade {

	/**
	 * @return one series per disease of the criteria, in the same order: the criteria's district, or its region when no
	 *         district is given, with the series of each of the region's districts added when the criteria asks for them
	 */
	List<ClimateCaseSeriesDto> getCaseSeries(ClimateCaseCriteria criteria);

	/**
	 * @return the weekly cases near the environment from {@code from} to {@code to}: those whose home address, else
	 *         report point, lies within {@code radiusKm}, and those without a point in the environment's community; by
	 *         community only when the environment has no usable GPS
	 * @param disease
	 *            the disease to count, or null for all diseases
	 */
	ClimateNearbyCasesDto getNearbyCases(EnvironmentReferenceDto environment, Disease disease, double radiusKm, LocalDate from, LocalDate to);
}
