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

package de.symeda.sormas.app.backend.outbreak;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.api.outbreak.OutbreakDto;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class OutbreakDtoHelper extends AdoDtoHelper<Outbreak, OutbreakDto> {

    @Override
    protected Class<Outbreak> getAdoClass() {
        return Outbreak.class;
    }

    @Override
    protected Class<OutbreakDto> getDtoClass() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Call<List<OutbreakDto>> pullAllSince(long since) {
        return RetroProvider.getOutbreakFacade().pullAllSince(since);
    }

    @Override
    protected Call<List<OutbreakDto>> pullByUuids(List<String> uuids) {
        throw new UnsupportedOperationException("Entity is read-only");
    }

    @Override
    protected Call<Integer> pushAll(List<OutbreakDto> communityDtos) {
        throw new UnsupportedOperationException("Entity is read-onl");
    }

    @Override
    public void fillInnerFromDto(Outbreak target, OutbreakDto source) {
        target.setDisease(source.getDisease());
        target.setDistrict(DatabaseHelper.getDistrictDao().getByReferenceDto(source.getDistrict()));
        target.setStartDate(source.getStartDate());
        target.setEndDate(source.getEndDate());
        target.setReportingUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getReportingUser()));
        target.setReportDate(source.getReportDate());
    }

    @Override
    public void fillInnerFromAdo(OutbreakDto target, Outbreak source) {
        throw new UnsupportedOperationException("Entity is read-only");
    }

    @Override
    protected Outbreak handlePulledDto(AbstractAdoDao<Outbreak> dao, OutbreakDto dto) throws DaoException, SQLException {

        if (dto.getEndDate() != null) {
            // outbreaks that already have an end are not relevant
            Outbreak existing = dao.queryUuid(dto.getUuid());
            if (existing != null) {
                dao.deleteCascade(existing);
            }
            return null;
        } else {
            Outbreak source = fillOrCreateFromDto(null, dto);
            return dao.mergeOrCreate(source);
        }
    }
}
