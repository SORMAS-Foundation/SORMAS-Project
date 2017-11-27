package de.symeda.sormas.app.backend.region;

import android.util.Log;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.common.ServerConnectionException;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDao;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class CommunityDtoHelper extends AdoDtoHelper<Community, CommunityDto> {

    @Override
    protected Class<Community> getAdoClass() {
        return Community.class;
    }

    @Override
    protected Class<CommunityDto> getDtoClass() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Call<List<CommunityDto>> pullAllSince(long since) {
        return RetroProvider.getCommunityFacade().pullAllSince(since);
    }

    @Override
    protected Call<List<CommunityDto>> pullByUuids(List<String> uuids) {
        throw new UnsupportedOperationException("Entity is infrastructure");
    }

    @Override
    protected Call<Integer> pushAll(List<CommunityDto> communityDtos) {
        throw new UnsupportedOperationException("Entity is infrastructure");
    }

    // cache of last queried entities
    private District lastDistrict = null;

    @Override
    public void fillInnerFromDto(Community target, CommunityDto source) {
        target.setName(source.getName());

        if (lastDistrict == null || !lastDistrict.getUuid().equals(source.getDistrict().getUuid())) {
            lastDistrict = DatabaseHelper.getDistrictDao().getByReferenceDto(source.getDistrict());
        }
        target.setDistrict(lastDistrict);
    }

    @Override
    public void pullEntities(final boolean markAsRead) throws DaoException, ServerConnectionException {
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
    protected int handlePullResponse(final boolean markAsRead, final AbstractAdoDao<Community> dao, Response<List<CommunityDto>> response) throws ServerConnectionException {
        if (!response.isSuccessful()) {
            String responseErrorBodyString;
            try {
                responseErrorBodyString = response.errorBody().string();
            } catch (IOException e) {
                responseErrorBodyString = "Exception accessing error body: " + e.getMessage();
            }
            throw new ServerConnectionException(responseErrorBodyString);
        }

        final CommunityDao communityDao = (CommunityDao) dao;

        final List<CommunityDto> result = response.body();
        if (result != null && result.size() > 0) {
            preparePulledResult(result);
            dao.callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {
                    for (CommunityDto dto : result) {

                        Community existing = null;
                        if (!databaseWasEmpty) {
                            existing = communityDao.queryUuid(dto.getUuid());
                        }
                        Community existingOrNew = fillOrCreateFromDto(existing, dto);
                        if (markAsRead) {
                            existingOrNew.setLastOpenedDate(DateHelper.addSeconds(new Date(), 5));
                        }
                        communityDao.updateOrCreate(existingOrNew);
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
    public void fillInnerFromAdo(CommunityDto communityDto, Community community) {
        throw new UnsupportedOperationException("Entity is infrastructure");
    }

    public static CommunityReferenceDto toReferenceDto(Community ado) {
        if (ado == null) {
            return null;
        }
        CommunityReferenceDto dto = new CommunityReferenceDto();
        fillReferenceDto(dto, ado);

        return dto;
    }
}
