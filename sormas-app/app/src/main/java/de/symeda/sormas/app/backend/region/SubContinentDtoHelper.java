/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.app.backend.region;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.region.SubContinentDto;
import de.symeda.sormas.api.region.SubContinentReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class SubContinentDtoHelper extends AdoDtoHelper<SubContinent, SubContinentDto> {
    @Override
    protected Class<SubContinent> getAdoClass() {
        return SubContinent.class;
    }

    @Override
    protected Class<SubContinentDto> getDtoClass() {
        return SubContinentDto.class;
    }

    @Override
    protected Call<List<SubContinentDto>> pullAllSince(long since) throws NoConnectionException {
        return RetroProvider.getSubContinentFacade().pullAllSince(since);
    }

    @Override
    protected Call<List<SubContinentDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
        return RetroProvider.getSubContinentFacade().pullByUuids(uuids);
    }

    @Override
    protected Call<List<PushResult>> pushAll(List<SubContinentDto> subContinentDtos) throws NoConnectionException {
        throw new UnsupportedOperationException("Entity is infrastructure");
    }

    @Override
    protected void fillInnerFromDto(SubContinent subContinent, SubContinentDto dto) {
        subContinent.setDefaultName(dto.getDefaultName());
        subContinent.setArchived(dto.isArchived());
    }

    @Override
    protected void fillInnerFromAdo(SubContinentDto dto, SubContinent subContinent) {
        throw new UnsupportedOperationException("Entity is infrastructure");
    }

    public static SubContinentReferenceDto toReferenceDto(SubContinent ado) {
        if (ado == null) {
            return null;
        }

        return new SubContinentReferenceDto(ado.getUuid(), ado.getDefaultName(), null);
    }
}
