/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import de.symeda.sormas.api.PostResponse;
import de.symeda.sormas.api.infrastructure.area.AreaDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class AreaDtoHelper extends AdoDtoHelper<Area, AreaDto> {

	@Override
	protected Class<Area> getAdoClass() {
		return Area.class;
	}

	@Override
	protected Class<AreaDto> getDtoClass() {
		return AreaDto.class;
	}

	@Override
	protected Call<List<AreaDto>> pullAllSince(long since, Integer size, String lastSynchronizedUuid) throws NoConnectionException {
		return RetroProvider.getAreaFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<AreaDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getAreaFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PostResponse>> pushAll(List<AreaDto> areaDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is infrastructure");
	}

	@Override
	protected void fillInnerFromDto(Area area, AreaDto dto) {
		area.setName(dto.getName());
		area.setExternalId(dto.getExternalId());
		area.setArchived(dto.isArchived());
	}

	@Override
	protected void fillInnerFromAdo(AreaDto dto, Area area) {
		throw new UnsupportedOperationException("Entity is infrastructure");
	}

	@Override
	protected long getApproximateJsonSizeInBytes() {
		return 0;
	}
}
