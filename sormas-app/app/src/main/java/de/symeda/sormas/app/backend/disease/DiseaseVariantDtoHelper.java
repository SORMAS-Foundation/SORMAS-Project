/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.disease;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.disease.DiseaseVariantDto;
import de.symeda.sormas.api.disease.DiseaseVariantReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class DiseaseVariantDtoHelper extends AdoDtoHelper<DiseaseVariant, DiseaseVariantDto> {

    @Override
    protected Class<DiseaseVariant> getAdoClass() {
        return DiseaseVariant.class;
    }

    @Override
    protected Class<DiseaseVariantDto> getDtoClass() {
        return DiseaseVariantDto.class;
    }

    @Override
    protected Call<List<DiseaseVariantDto>> pullAllSince(long since) throws NoConnectionException {
        return RetroProvider.getDiseaseVariantFacade().pullAllSince(since);
    }

    @Override
    protected Call<List<DiseaseVariantDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
        return RetroProvider.getDiseaseVariantFacade().pullByUuids(uuids);
    }

    @Override
    protected Call<List<PushResult>> pushAll(List<DiseaseVariantDto> dtos) throws NoConnectionException {
        throw new UnsupportedOperationException("Entity is read-only");
    }

    @Override
    public void fillInnerFromDto(DiseaseVariant target, DiseaseVariantDto source) {
        target.setDisease(source.getDisease());
        target.setName(source.getName());
    }

    @Override
    public void fillInnerFromAdo(DiseaseVariantDto target, DiseaseVariant source) {
        target.setDisease(source.getDisease());
        target.setName(source.getName());
    }

    public static DiseaseVariantReferenceDto toReferenceDto(DiseaseVariant ado) {
        if (ado == null) {
            return null;
        }
        return new DiseaseVariantReferenceDto(ado.getUuid(), ado.getName());
    }
}
