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

package de.symeda.sormas.app.backend.infrastructure;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.DistrictDtoHelper;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.region.RegionDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class PointOfEntryDtoHelper extends AdoDtoHelper<PointOfEntry, PointOfEntryDto> {

	@Override
	protected Class<PointOfEntry> getAdoClass() {
		return PointOfEntry.class;
	}

	@Override
	protected Class<PointOfEntryDto> getDtoClass() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Call<List<PointOfEntryDto>> pullAllSince(long since) throws NoConnectionException {
		return RetroProvider.getPointOfEntryFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<PointOfEntryDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getPointOfEntryFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<PointOfEntryDto> dtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is infrastructure");
	}

	@Override
	public void fillInnerFromDto(PointOfEntry target, PointOfEntryDto source) {
		target.setName(source.getName());
		target.setPointOfEntryType(source.getPointOfEntryType());
		target.setLatitude(source.getLatitude());
		target.setLongitude(source.getLongitude());
		target.setActive(source.isActive());
		target.setArchived(source.isArchived());

		target.setRegion(DatabaseHelper.getRegionDao().getByReferenceDto(source.getRegion()));
		target.setDistrict(DatabaseHelper.getDistrictDao().getByReferenceDto(source.getDistrict()));
	}

	@Override
	public void fillInnerFromAdo(PointOfEntryDto target, PointOfEntry source) {
		target.setName(source.getName());
		target.setPointOfEntryType(source.getPointOfEntryType());
		target.setLatitude(source.getLatitude());
		target.setLongitude(source.getLongitude());
		target.setActive(source.isActive());
		target.setArchived(source.isArchived());

		if (source.getRegion() != null) {
			Region region = DatabaseHelper.getRegionDao().queryForId(source.getRegion().getId());
			target.setRegion(RegionDtoHelper.toReferenceDto(region));
		} else {
			target.setRegion(null);
		}

		if (source.getDistrict() != null) {
			District district = DatabaseHelper.getDistrictDao().queryForId(source.getDistrict().getId());
			target.setDistrict(DistrictDtoHelper.toReferenceDto(district));
		} else {
			target.setDistrict(null);
		}
	}

	public static PointOfEntryReferenceDto toReferenceDto(PointOfEntry ado) {
		if (ado == null) {
			return null;
		}
		return new PointOfEntryReferenceDto(ado.getUuid());
	}
}
