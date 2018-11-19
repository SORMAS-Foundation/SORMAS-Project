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
package de.symeda.sormas.api.facility;

import java.io.Serializable;

import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public class FacilityCriteria implements Serializable, Cloneable {

	private static final long serialVersionUID = 3958619224286048978L;

	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community;

	private FacilityType type;
	private Boolean excludeStaticFacilities;

	public FacilityCriteria regionEquals(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public CommunityReferenceDto getCommunity() {
		return community;
	}

	public FacilityType getType() {
		return type;
	}
	
	public Boolean isExcludeStaticFacilities() {
		return excludeStaticFacilities;
	}

	public FacilityCriteria districtEquals(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}

	public FacilityCriteria communityEquals(CommunityReferenceDto community) {
		this.community = community;
		return this;
	}

	public FacilityCriteria typeEquals(FacilityType type) {
		this.type = type;
		return this;
	}
	
	public FacilityCriteria excludeStaticFacilitesEquals(boolean excludeStaticFacilities) {
		this.excludeStaticFacilities = excludeStaticFacilities;
		return this;
	}
}
