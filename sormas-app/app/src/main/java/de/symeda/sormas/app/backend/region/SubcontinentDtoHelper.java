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
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class SubcontinentDtoHelper extends AdoDtoHelper<Subcontinent, SubcontinentDto> {

	@Override
	protected Class<Subcontinent> getAdoClass() {
		return Subcontinent.class;
	}

	@Override
	protected Class<SubcontinentDto> getDtoClass() {
		return SubcontinentDto.class;
	}

	@Override
	protected Call<List<SubcontinentDto>> pullAllSince(long since, Integer size, String lastSynchronizedUuid) throws NoConnectionException {
		return RetroProvider.getSubcontinentFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<SubcontinentDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getSubcontinentFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PostResponse>> pushAll(List<SubcontinentDto> subcontinentDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is infrastructure");
	}

	@Override
	protected void fillInnerFromDto(Subcontinent subcontinent, SubcontinentDto dto) {
		subcontinent.setDefaultName(dto.getDefaultName());
		subcontinent.setArchived(dto.isArchived());
		subcontinent.setContinent(DatabaseHelper.getContinentDao().queryUuid(dto.getContinent().getUuid()));
	}

	@Override
	protected void fillInnerFromAdo(SubcontinentDto dto, Subcontinent subcontinent) {
		throw new UnsupportedOperationException("Entity is infrastructure");
	}

	@Override
	protected long getApproximateJsonSizeInBytes() {
		return 0;
	}

	public static SubcontinentReferenceDto toReferenceDto(Subcontinent ado) {
		if (ado == null) {
			return null;
		}

		return new SubcontinentReferenceDto(ado.getUuid(), ado.getDefaultName(), null);
	}
}
