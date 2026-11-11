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

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

/**
 * The place and epi weeks of a weekly climate series: a region, or one of its districts. The period is the climate's own,
 * so the dashboard can ask for weeks before the case period to shift climate by a lag.
 */
@AuditedClass(includeAllFields = true)
public class ClimateSeriesCriteria extends BaseCriteria {

	private static final long serialVersionUID = 2370583187429683251L;

	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private EpiWeek fromWeek;
	private EpiWeek toWeek;
	private boolean includeDistricts;
	private int leadWeeks;

	@IgnoreForUrl
	public RegionReferenceDto getRegion() {
		return region;
	}

	public ClimateSeriesCriteria region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	@IgnoreForUrl
	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public ClimateSeriesCriteria district(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}

	@IgnoreForUrl
	public EpiWeek getFromWeek() {
		return fromWeek;
	}

	@IgnoreForUrl
	public EpiWeek getToWeek() {
		return toWeek;
	}

	public ClimateSeriesCriteria weeks(EpiWeek fromWeek, EpiWeek toWeek) {
		this.fromWeek = fromWeek;
		this.toWeek = toWeek;
		return this;
	}

	/**
	 * @return true to add the series of each of the region's districts, for the district table (only without a district)
	 */
	@IgnoreForUrl
	public boolean isIncludeDistricts() {
		return includeDistricts;
	}

	public ClimateSeriesCriteria includeDistricts(boolean includeDistricts) {
		this.includeDistricts = includeDistricts;
		return this;
	}

	/**
	 * @return how many weeks before {@link #getFromWeek()} to return as well, for the dashboard's lag (decision 126); they
	 *         are left out of completeness and the range figures
	 */
	@IgnoreForUrl
	public int getLeadWeeks() {
		return leadWeeks;
	}

	public ClimateSeriesCriteria leadWeeks(int leadWeeks) {
		this.leadWeeks = leadWeeks;
		return this;
	}
}
