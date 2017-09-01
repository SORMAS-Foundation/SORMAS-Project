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
    protected Call<Integer> pushAll(List<CommunityDto> communityDtos) {
        throw new UnsupportedOperationException("Entity is infrastructure");
    }

    @Override
    public void fillInnerFromDto(Community ado, CommunityDto dto) {
        ado.setName(dto.getName());
        ado.setDistrict(DatabaseHelper.getDistrictDao().queryUuid(dto.getDistrict().getUuid()));
    }

    @Override
    protected void handlePullResponse(final boolean markAsRead, final AbstractAdoDao<Community> dao, Response<List<CommunityDto>> response) throws ServerConnectionException {
        if (!response.isSuccessful()) {
            String responseErrorBodyString;
            try {
                responseErrorBodyString = response.errorBody().string();
            } catch (IOException e) {
                responseErrorBodyString = "Exception accessing error body: " + e.getMessage();
            }
            throw new ServerConnectionException(responseErrorBodyString);
        }

        final List<CommunityDto> result = response.body();
        if (result != null && result.size() > 0) {
            preparePulledResult(result);
            dao.callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {
                    for (CommunityDto dto : result) {
                        // TODO find a performance friendly way for merging
                        Community source = fillOrCreateFromDto(null, dto);
                        if (markAsRead) {
                            source.setLastOpenedDate(DateHelper.addSeconds(new Date(), 5));
                        }
                        dao.create(source);
                    }
                    return null;
                }
            });

            Log.d(dao.getTableName(), "Pulled: " + result.size());
        }
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
