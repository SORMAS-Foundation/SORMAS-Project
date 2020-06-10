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
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class DistrictDtoHelper extends AdoDtoHelper<District, DistrictDto> {

	@Override
	protected Class<District> getAdoClass() {
		return District.class;
	}

	@Override
	protected Class<DistrictDto> getDtoClass() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Call<List<DistrictDto>> pullAllSince(long since) throws NoConnectionException {
		return RetroProvider.getDistrictFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<DistrictDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getDistrictFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<DistrictDto> districtDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is infrastructure");
	}

	@Override
	public void fillInnerFromDto(District ado, DistrictDto dto) {
		ado.setName(dto.getName());
		ado.setEpidCode(dto.getEpidCode());
		ado.setRegion(DatabaseHelper.getRegionDao().queryUuid(dto.getRegion().getUuid()));
		ado.setArchived(dto.isArchived());
	}

	@Override
	public void fillInnerFromAdo(DistrictDto districtDto, District district) {
		throw new UnsupportedOperationException("Entity is infrastructure");
	}

	public static DistrictReferenceDto toReferenceDto(District ado) {
		if (ado == null) {
			return null;
		}
		DistrictReferenceDto dto = new DistrictReferenceDto(ado.getUuid());

		return dto;
	}
}
