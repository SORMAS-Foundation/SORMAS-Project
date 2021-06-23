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

package de.symeda.sormas.app.backend.region;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class RegionDtoHelper extends AdoDtoHelper<Region, RegionDto> {

	@Override
	protected Class<Region> getAdoClass() {
		return Region.class;
	}

	@Override
	protected Class<RegionDto> getDtoClass() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Call<List<RegionDto>> pullAllSince(long since) throws NoConnectionException {
		return RetroProvider.getRegionFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<RegionDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getRegionFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<RegionDto> regionDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is infrastructure");
	}

	@Override
	public void fillInnerFromDto(Region ado, RegionDto dto) {
		ado.setName(dto.getName());
		ado.setEpidCode(dto.getEpidCode());
		ado.setCountry(DatabaseHelper.getCountryDao().getByReferenceDto(dto.getCountry()));
		ado.setArea(DatabaseHelper.getAreaDao().getByReferenceDto(dto.getArea()));
		ado.setArchived(dto.isArchived());
	}

	@Override
	public void fillInnerFromAdo(RegionDto regionDto, Region region) {
		throw new UnsupportedOperationException("Entity is infrastructure");
	}

	public static RegionReferenceDto toReferenceDto(Region ado) {
		if (ado == null) {
			return null;
		}
		RegionReferenceDto dto = new RegionReferenceDto(ado.getUuid());

		return dto;
	}
}
