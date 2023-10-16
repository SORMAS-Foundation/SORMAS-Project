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
import de.symeda.sormas.api.infrastructure.continent.ContinentDto;
import de.symeda.sormas.api.infrastructure.continent.ContinentReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class ContinentDtoHelper extends AdoDtoHelper<Continent, ContinentDto> {

	@Override
	protected Class<Continent> getAdoClass() {
		return Continent.class;
	}

	@Override
	protected Class<ContinentDto> getDtoClass() {
		return ContinentDto.class;
	}

	@Override
	protected Call<List<ContinentDto>> pullAllSince(long since, Integer size, String lastSynchronizedUuid) throws NoConnectionException {
		return RetroProvider.getContinentFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<ContinentDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getContinentFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PostResponse>> pushAll(List<ContinentDto> continentDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is infrastructure");
	}

	@Override
	protected void fillInnerFromDto(Continent continent, ContinentDto dto) {
		continent.setDefaultName(dto.getDefaultName());
		continent.setArchived(dto.isArchived());
	}

	@Override
	protected void fillInnerFromAdo(ContinentDto dto, Continent continent) {
		throw new UnsupportedOperationException("Entity is infrastructure");
	}

	@Override
	protected long getApproximateJsonSizeInBytes() {
		return 0;
	}

	public static ContinentReferenceDto toReferenceDto(Continent ado) {
		if (ado == null) {
			return null;
		}

		return new ContinentReferenceDto(ado.getUuid(), ado.getDefaultName(), null);
	}
}
