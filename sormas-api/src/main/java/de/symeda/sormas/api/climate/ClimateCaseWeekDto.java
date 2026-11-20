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

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.utils.EpiWeek;

/**
 * The cases of one disease in one district or region and epi week, with the usual range for that week.
 */
@AuditedClass
public class ClimateCaseWeekDto implements Serializable {

	private static final long serialVersionUID = 7316020566310457930L;

	private final EpiWeek week;
	private final int count;
	private final int reportDateCount;
	private final Double incidence;
	private final ClimateRangeDto usualRange;
	private final ClimateRangeDto usualRangeIncidence;
	private final ClimateUsualRangeMissing usualRangeMissing;
	private final boolean reported;

	public ClimateCaseWeekDto(
		EpiWeek week,
		int count,
		int reportDateCount,
		Double incidence,
		ClimateRangeDto usualRange,
		ClimateRangeDto usualRangeIncidence,
		ClimateUsualRangeMissing usualRangeMissing,
		boolean reported) {

		this.week = week;
		this.count = count;
		this.reportDateCount = reportDateCount;
		this.incidence = incidence;
		this.usualRange = usualRange;
		this.usualRangeIncidence = usualRangeIncidence;
		this.usualRangeMissing = usualRangeMissing;
		this.reported = reported;
	}

	public EpiWeek getWeek() {
		return week;
	}

	/**
	 * @return the cases whose onset date, or report date when onset is missing, falls in the week, 0 when there were none;
	 *         for a disease counted from aggregate reports the week's lab-confirmed count, 0 when it has no report (see
	 *         {@link #isReported()})
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @return the cases of {@link #getCount()} placed by report date because their onset date is missing
	 */
	public int getReportDateCount() {
		return reportDateCount;
	}

	/**
	 * @return cases per 100,000 people, or null when a district of the selection has no population data
	 */
	public Double getIncidence() {
		return incidence;
	}

	/**
	 * @return the usual range of weekly counts, or null with the reason in {@link #getUsualRangeMissing()}
	 */
	public ClimateRangeDto getUsualRange() {
		return usualRange;
	}

	/**
	 * @return the usual range as incidence: the count range scaled by the same population
	 */
	public ClimateRangeDto getUsualRangeIncidence() {
		return usualRangeIncidence;
	}

	public ClimateUsualRangeMissing getUsualRangeMissing() {
		return usualRangeMissing;
	}

	/**
	 * @return false for a week of aggregate reports without any report: it has no value and is drawn as a gap, not as 0
	 *         (decision 147); always true for cases
	 */
	public boolean isReported() {
		return reported;
	}

	/**
	 * @return true when the week is above its usual range; the same for counts and incidence
	 */
	public boolean isAboveUsualRange() {
		return reported && usualRange != null && usualRange.isAbove(count);
	}
}
