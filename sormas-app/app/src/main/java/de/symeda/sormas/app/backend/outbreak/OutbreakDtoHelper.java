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

import android.util.Log;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import de.symeda.sormas.api.outbreak.OutbreakDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.rest.ServerConnectionException;
import de.symeda.sormas.app.rest.ServerCommunicationException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
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
        target.setReportingUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getReportingUser()));
        target.setReportDate(source.getReportDate());
    }

    @Override
    public void pullEntities(final boolean markAsRead) throws DaoException, ServerCommunicationException, ServerConnectionException {
        databaseWasEmpty = DatabaseHelper.getCommunityDao().countOf() == 0;
        try {
            super.pullEntities(markAsRead);
        } finally {
            databaseWasEmpty = false;
        }
    }

    // performance tweak: only query for existing during pull, when database was not empty
    private boolean databaseWasEmpty = false;

    /**
     * Overriden for performance reasons. No merge needed when database was empty.
     */
    @Override
    protected int handlePullResponse(final boolean markAsRead, final AbstractAdoDao<Outbreak> dao, Response<List<OutbreakDto>> response) throws ServerCommunicationException, DaoException, ServerConnectionException {
        if (!response.isSuccessful()) {
            RetroProvider.throwException(response);
        }

        final OutbreakDao outbreakDao = (OutbreakDao) dao;

        final List<OutbreakDto> result = response.body();
        if (result != null && result.size() > 0) {
            preparePulledResult(result);
            dao.callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {
                    for (OutbreakDto dto : result) {

                        Outbreak existing = null;
                        if (!databaseWasEmpty) {
                            existing = outbreakDao.queryUuid(dto.getUuid());
                        }
                        Outbreak existingOrNew = fillOrCreateFromDto(existing, dto);
                        if (markAsRead) {
                            existingOrNew.setLastOpenedDate(DateHelper.addSeconds(new Date(), 5));
                        }
                        outbreakDao.updateOrCreate(existingOrNew);
                    }
                    return null;
                }
            });

            Log.d(dao.getTableName(), "Pulled: " + result.size());
            return result.size();
        }
        return 0;
    }

    @Override
    public void fillInnerFromAdo(OutbreakDto outbreakDto, Outbreak outbreak) {
        throw new UnsupportedOperationException("Entity is read-only");
    }
}
