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

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

/**
 * The place, epi weeks, diseases and classification of the Climate dashboard's case series. Only case-based data is
 * counted in phase 1 (decision 73).
 */
@AuditedClass(includeAllFields = true)
public class ClimateCaseCriteria extends BaseCriteria {

	private static final long serialVersionUID = -3172606474934451905L;

	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private EpiWeek fromWeek;
	private EpiWeek toWeek;
	private List<Disease> diseases = new ArrayList<>();
	// confirmed cases by default, as on the dashboard (decision 145)
	private ClimateCaseClassificationFilter classification = ClimateCaseClassificationFilter.CONFIRMED;
	private boolean includeDistricts;

	@IgnoreForUrl
	public RegionReferenceDto getRegion() {
		return region;
	}

	public ClimateCaseCriteria region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	@IgnoreForUrl
	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public ClimateCaseCriteria district(DistrictReferenceDto district) {
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

	public ClimateCaseCriteria weeks(EpiWeek fromWeek, EpiWeek toWeek) {
		this.fromWeek = fromWeek;
		this.toWeek = toWeek;
		return this;
	}

	@IgnoreForUrl
	public List<Disease> getDiseases() {
		return diseases;
	}

	public ClimateCaseCriteria diseases(List<Disease> diseases) {
		this.diseases = diseases;
		return this;
	}

	@IgnoreForUrl
	public ClimateCaseClassificationFilter getClassification() {
		return classification;
	}

	public ClimateCaseCriteria classification(ClimateCaseClassificationFilter classification) {
		this.classification = classification;
		return this;
	}

	/**
	 * @return true to add the series of each of the region's districts, for the district table (only without a district)
	 */
	@IgnoreForUrl
	public boolean isIncludeDistricts() {
		return includeDistricts;
	}

	public ClimateCaseCriteria includeDistricts(boolean includeDistricts) {
		this.includeDistricts = includeDistricts;
		return this;
	}
}
