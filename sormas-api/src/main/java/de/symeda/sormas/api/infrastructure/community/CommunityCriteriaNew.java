/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.infrastructure.community;

import java.io.Serializable;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class CommunityCriteriaNew extends BaseCriteria implements Serializable, Cloneable {

	private static final long serialVersionUID = 78151805085134182L;

	private CountryReferenceDto country;
	private AreaReferenceDto area;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private String nameLike;
	private EntityRelevanceStatus relevanceStatus;

	public CountryReferenceDto getCountry() {
		return country;
	}

	public CommunityCriteriaNew country(CountryReferenceDto country) {
		this.country = country;

		return this;
	}

	public CommunityCriteriaNew area(AreaReferenceDto area) {
		this.area = area;
		return this;
	}

	public AreaReferenceDto getArea() {
		return area;
	}
	
	public CommunityCriteriaNew region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public CommunityCriteriaNew district(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	@IgnoreForUrl
	public String getNameLike() {
		return nameLike;
	}

	public CommunityCriteriaNew nameLike(String nameLike) {
		this.nameLike = nameLike;
		return this;
	}

	public CommunityCriteriaNew relevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
		return this;
	}

	@IgnoreForUrl
	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}
}
