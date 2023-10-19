/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.location;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.common.QueryJoins;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;

public class LocationJoins extends QueryJoins<Location> {

	private Join<Location, Country> country;
	private Join<Location, Region> region;
	private Join<Location, District> district;
	private Join<Location, Community> community;
	private Join<Location, Facility> facility;

	public LocationJoins(From<?, Location> root) {
		super(root);
	}

	public Join<Location, Country> getCountry() {
		return getOrCreate(country, Location.COUNTRY, JoinType.LEFT, this::setCountry);
	}

	private void setCountry(Join<Location, Country> country) {
		this.country = country;
	}

	public Join<Location, Region> getRegion() {
		return getOrCreate(region, Location.REGION, JoinType.LEFT, this::setRegion);
	}

	private void setRegion(Join<Location, Region> region) {
		this.region = region;
	}

	public Join<Location, District> getDistrict() {
		return getOrCreate(district, Location.DISTRICT, JoinType.LEFT, this::setDistrict);
	}

	private void setDistrict(Join<Location, District> district) {
		this.district = district;
	}

	public Join<Location, Community> getCommunity() {
		return getOrCreate(community, Location.COMMUNITY, JoinType.LEFT, this::setCommunity);
	}

	private void setCommunity(Join<Location, Community> community) {
		this.community = community;
	}

	public Join<Location, Facility> getFacility() {
		return getOrCreate(facility, Location.FACILITY, JoinType.LEFT, this::setFacility);
	}

	private void setFacility(Join<Location, Facility> facility) {
		this.facility = facility;
	}

}
