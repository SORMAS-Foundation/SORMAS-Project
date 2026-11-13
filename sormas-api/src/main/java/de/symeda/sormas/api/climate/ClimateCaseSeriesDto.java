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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;

/**
 * The weekly case-based counts of one disease in a district, or in a region (the sum of its districts), with incidence
 * and the usual range. Counts follow the user's jurisdiction.
 */
@AuditedClass
public class ClimateCaseSeriesDto implements Serializable {

	private static final long serialVersionUID = 1846071583905526130L;

	/** How many of the most recent complete weeks the district table's "Last 4 weeks" column looks at. */
	public static final int RECENT_WEEKS = 4;

	private final Disease disease;
	private final ClimateCaseCountSource countSource;
	private final RegionReferenceDto region;
	private final DistrictReferenceDto district;
	private final Integer population;
	private final List<DistrictReferenceDto> districtsWithoutPopulation;
	private final int firstReferenceYear;
	private final int lastReferenceYear;
	private final Set<Integer> excludedYears;
	private final List<Integer> yearsWithCases;
	private final List<ClimateCaseWeekDto> weeks;
	private int recentWeeks;
	private int recentWeeksAbove;
	private final List<ClimateCaseSeriesDto> districtSeries = new ArrayList<>();

	public ClimateCaseSeriesDto(
		Disease disease,
		ClimateCaseCountSource countSource,
		RegionReferenceDto region,
		DistrictReferenceDto district,
		Integer population,
		List<DistrictReferenceDto> districtsWithoutPopulation,
		int firstReferenceYear,
		int lastReferenceYear,
		Set<Integer> excludedYears,
		List<Integer> yearsWithCases,
		List<ClimateCaseWeekDto> weeks) {

		this.disease = disease;
		this.countSource = countSource;
		this.region = region;
		this.district = district;
		this.population = population;
		this.districtsWithoutPopulation = districtsWithoutPopulation;
		this.firstReferenceYear = firstReferenceYear;
		this.lastReferenceYear = lastReferenceYear;
		this.excludedYears = excludedYears;
		this.yearsWithCases = yearsWithCases;
		this.weeks = weeks;
	}

	public Disease getDisease() {
		return disease;
	}

	/**
	 * @return what is counted: cases, or lab-confirmed aggregate reports for a disease without case surveillance (decision 147)
	 */
	public ClimateCaseCountSource getCountSource() {
		return countSource;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	/**
	 * @return the district, or null for the region's series
	 */
	public DistrictReferenceDto getDistrict() {
		return district;
	}

	/**
	 * @return the projected population (for a region the sum of its districts'), or null when a district has none
	 */
	public Integer getPopulation() {
		return population;
	}

	/**
	 * @return true when incidence can be shown: every district of the selection has population data
	 */
	public boolean isIncidenceAvailable() {
		return population != null;
	}

	/**
	 * @return the districts without population data, for "Showing counts: no population data for …"
	 */
	public List<DistrictReferenceDto> getDistrictsWithoutPopulation() {
		return Collections.unmodifiableList(districtsWithoutPopulation);
	}

	/**
	 * @return the first epi year the usual range may come from
	 */
	public int getFirstReferenceYear() {
		return firstReferenceYear;
	}

	public int getLastReferenceYear() {
		return lastReferenceYear;
	}

	/**
	 * @return the epidemic years left out, for "excl. 2024"
	 */
	public Set<Integer> getExcludedYears() {
		return Collections.unmodifiableSet(excludedYears);
	}

	/**
	 * @return the reference years the usual range comes from: those with cases of the disease (or aggregate reports, for a
	 *         disease counted from them), excluded years left out
	 */
	public List<Integer> getYearsWithCases() {
		return Collections.unmodifiableList(yearsWithCases);
	}

	public List<ClimateCaseWeekDto> getWeeks() {
		return Collections.unmodifiableList(weeks);
	}

	/**
	 * @return the weeks shown that are above their usual range, for "7 of 52"
	 */
	public long countWeeksAboveUsualRange() {
		return weeks.stream().filter(ClimateCaseWeekDto::isAboveUsualRange).count();
	}

	/**
	 * @return the weeks shown that have a usual range
	 */
	public long countWeeksWithUsualRange() {
		return weeks.stream().filter(week -> week.getUsualRange() != null).count();
	}

	/**
	 * @return how many of the most recent complete weeks shown (up to {@link #RECENT_WEEKS}) were looked at
	 */
	public int getRecentWeeks() {
		return recentWeeks;
	}

	/**
	 * @return how many of {@link #getRecentWeeks()} were above their usual range, for "3 of 4 above"
	 */
	public int getRecentWeeksAbove() {
		return recentWeeksAbove;
	}

	public void setRecentWeeks(int recentWeeks, int recentWeeksAbove) {
		this.recentWeeks = recentWeeks;
		this.recentWeeksAbove = recentWeeksAbove;
	}

	/**
	 * @return the series of each of the region's districts, by name, when they were asked for
	 */
	public List<ClimateCaseSeriesDto> getDistrictSeries() {
		return districtSeries;
	}
}
