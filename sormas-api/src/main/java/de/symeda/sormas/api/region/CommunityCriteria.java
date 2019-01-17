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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.region;

import java.io.Serializable;

public class CommunityCriteria implements Serializable, Cloneable {

	private static final long serialVersionUID = 7815180508529134182L;

	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private String[] nameLike;
	
	public CommunityCriteria regionEquals(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}
	
	public CommunityCriteria districtEquals(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}
	
	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public String[] getNameLike() {
		return nameLike;
	}

	public CommunityCriteria nameLike(String[] nameLike) {
		this.nameLike = nameLike;
		return this;
	}	
	
}
