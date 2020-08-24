/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.location;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.region.CommunityDtoHelper;
import de.symeda.sormas.app.backend.region.DistrictDtoHelper;
import de.symeda.sormas.app.backend.region.RegionDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import retrofit2.Call;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class LocationDtoHelper extends AdoDtoHelper<Location, LocationDto> {

	@Override
	protected Class<Location> getAdoClass() {
		return Location.class;
	}

	@Override
	protected Class<LocationDto> getDtoClass() {
		return LocationDto.class;
	}

	@Override
	protected Call<List<LocationDto>> pullAllSince(long since) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<LocationDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<LocationDto> locationDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	public void fillInnerFromDto(Location target, LocationDto source) {

		target.setAddress(source.getAddress());
		target.setCity(source.getCity());
		target.setAreaType(source.getAreaType());
		target.setDetails(source.getDetails());
		target.setLatitude(source.getLatitude());
		target.setLongitude(source.getLongitude());
		target.setLatLonAccuracy(source.getLatLonAccuracy());

		target.setRegion(DatabaseHelper.getRegionDao().getByReferenceDto(source.getRegion()));
		target.setDistrict(DatabaseHelper.getDistrictDao().getByReferenceDto(source.getDistrict()));
		target.setCommunity(DatabaseHelper.getCommunityDao().getByReferenceDto(source.getCommunity()));

		target.setPostalCode(source.getPostalCode());

		target.setPseudonymized(source.isPseudonymized());
	}

	@Override
	public void fillInnerFromAdo(LocationDto target, Location source) {

		target.setAddress(source.getAddress());
		target.setCity(source.getCity());
		target.setAreaType(source.getAreaType());
		target.setDetails(source.getDetails());
		target.setLatitude(source.getLatitude());
		target.setLongitude(source.getLongitude());
		target.setLatLonAccuracy(source.getLatLonAccuracy());

		if (source.getCommunity() != null) {
			target.setCommunity(CommunityDtoHelper.toReferenceDto(DatabaseHelper.getCommunityDao().queryForId(source.getCommunity().getId())));
		} else {
			target.setCommunity(null);
		}
		if (source.getDistrict() != null) {
			target.setDistrict(DistrictDtoHelper.toReferenceDto(DatabaseHelper.getDistrictDao().queryForId(source.getDistrict().getId())));
		} else {
			target.setDistrict(null);
		}
		if (source.getRegion() != null) {
			target.setRegion(RegionDtoHelper.toReferenceDto(DatabaseHelper.getRegionDao().queryForId(source.getRegion().getId())));
		} else {
			target.setRegion(null);
		}

		target.setPostalCode(source.getPostalCode());

		target.setPseudonymized(source.isPseudonymized());
	}
}
