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

import java.util.Date;

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

/**
 * Filters for the observations on the Climate data screen. Region and district select the locations of their districts,
 * of their environments, or both, as {@link #getPlaceType()} says; an environment selects its own location only.
 */
@AuditedClass(includeAllFields = true)
public class ClimateObservationCriteria extends BaseCriteria {

	private static final long serialVersionUID = -2361847046731262409L;

	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String PLACE_TYPE = "placeType";
	public static final String ENVIRONMENT = "environment";
	public static final String DATE_FROM = "dateFrom";
	public static final String DATE_TO = "dateTo";
	public static final String SOURCE = "source";

	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private ClimatePlaceType placeType = ClimatePlaceType.DISTRICTS;
	private EnvironmentReferenceDto environment;
	private Date dateFrom;
	private Date dateTo;
	private ClimateDataSource source;

	@IgnoreForUrl
	public RegionReferenceDto getRegion() {
		return region;
	}

	public ClimateObservationCriteria region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	@IgnoreForUrl
	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public ClimateObservationCriteria district(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}

	@IgnoreForUrl
	public ClimatePlaceType getPlaceType() {
		return placeType;
	}

	public ClimateObservationCriteria placeType(ClimatePlaceType placeType) {
		this.placeType = placeType;
		return this;
	}

	@IgnoreForUrl
	public EnvironmentReferenceDto getEnvironment() {
		return environment;
	}

	public ClimateObservationCriteria environment(EnvironmentReferenceDto environment) {
		this.environment = environment;
		return this;
	}

	@IgnoreForUrl
	public Date getDateFrom() {
		return dateFrom;
	}

	@IgnoreForUrl
	public Date getDateTo() {
		return dateTo;
	}

	/**
	 * @param dateFrom
	 *            first day, inclusive; null for no lower limit
	 * @param dateTo
	 *            last day, inclusive; null for no upper limit
	 */
	public ClimateObservationCriteria dateBetween(Date dateFrom, Date dateTo) {
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		return this;
	}

	@IgnoreForUrl
	public ClimateDataSource getSource() {
		return source;
	}

	public ClimateObservationCriteria source(ClimateDataSource source) {
		this.source = source;
		return this;
	}
}
