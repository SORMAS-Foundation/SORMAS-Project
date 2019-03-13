/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.sample;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class PathogenTestDtoHelper extends AdoDtoHelper<PathogenTest, PathogenTestDto> {

    @Override
    protected Class<PathogenTest> getAdoClass() {
        return PathogenTest.class;
    }

    @Override
    protected Class<PathogenTestDto> getDtoClass() {
        return PathogenTestDto.class;
    }

    @Override
    protected Call<List<PathogenTestDto>> pullAllSince(long since) {
        return RetroProvider.getSampleTestFacade().pullAllSince(since);
    }

    @Override
    protected Call<List<PathogenTestDto>> pullByUuids(List<String> uuids) {
        return RetroProvider.getSampleTestFacade().pullByUuids(uuids);
    }

    @Override
    protected Call<List<PushResult>> pushAll(List<PathogenTestDto> PathogenTestDtos) {
        throw new UnsupportedOperationException("Can't change sample tests in app");
    }

    @Override
    protected void fillInnerFromDto(PathogenTest target, PathogenTestDto source) {

        target.setSample(DatabaseHelper.getSampleDao().getByReferenceDto(source.getSample()));
        target.setTestDateTime(source.getTestDateTime());
        target.setTestResult(source.getTestResult());
        target.setTestType(source.getTestType());
    }

    @Override
    protected void fillInnerFromAdo(PathogenTestDto dto, PathogenTest ado) {
        if(ado.getSample() != null) {
            Sample sample = DatabaseHelper.getSampleDao().queryForId(ado.getSample().getId());
            dto.setSample(SampleDtoHelper.toReferenceDto(sample));
        } else {
            dto.setSample(null);
        }

        dto.setTestDateTime(ado.getTestDateTime());
        dto.setTestResult(ado.getTestResult());
        dto.setTestType(ado.getTestType());
    }
}
