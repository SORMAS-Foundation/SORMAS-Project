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

import java.io.Serializable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;

/**
 * One row of the Climate dashboard export (decision 128): a region or district and one epi week, with the week's
 * climate and each disease's cases. Never shifted by the lag. The screen turns it into CSV columns, one group per
 * disease.
 */
@AuditedClass
public class ClimateDashboardExportDto implements Serializable {

	private static final long serialVersionUID = 6830744013186359121L;

	private final RegionReferenceDto region;
	private final DistrictReferenceDto district;
	private final ClimateWeekDto climate;
	private final boolean openWeatherData;
	private final Integer population;
	private final Map<Disease, ClimateCaseWeekDto> cases = new EnumMap<>(Disease.class);

	public ClimateDashboardExportDto(
		RegionReferenceDto region,
		DistrictReferenceDto district,
		ClimateWeekDto climate,
		boolean openWeatherData,
		Integer population) {
		this.region = region;
		this.district = district;
		this.climate = climate;
		this.openWeatherData = openWeatherData;
		this.population = population;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	/**
	 * @return the district, or null for the region's row
	 */
	public DistrictReferenceDto getDistrict() {
		return district;
	}

	/**
	 * @return the week's climate: values, normal ranges, last year's values, unusual flags and days (or districts) with
	 *         data
	 */
	public ClimateWeekDto getClimate() {
		return climate;
	}

	/**
	 * @return true when the place's climate in the period includes OpenWeather days, so the row carries the credit
	 */
	public boolean isOpenWeatherData() {
		return openWeatherData;
	}

	/**
	 * @return the projected population used for incidence, or null without population data or without a disease
	 */
	public Integer getPopulation() {
		return population;
	}

	/**
	 * @return each exported disease's figures for the week
	 */
	public Map<Disease, ClimateCaseWeekDto> getCases() {
		return Collections.unmodifiableMap(cases);
	}

	public void putCases(Disease disease, ClimateCaseWeekDto week) {
		cases.put(disease, week);
	}
}
