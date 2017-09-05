package de.symeda.sormas.app.backend.facility;

import android.util.Log;

import java.io.IOException;
import java.net.ConnectException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.common.ServerConnectionException;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class FacilityDtoHelper extends AdoDtoHelper<Facility, FacilityDto> {

    @Override
    protected Class<Facility> getAdoClass() {
        return Facility.class;
    }

    @Override
    protected Class<FacilityDto> getDtoClass() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Call<List<FacilityDto>> pullAllSince(long since) {
        throw new UnsupportedOperationException("Use pullAllByRegionSince");
    }

    @Override
    protected Call<List<FacilityDto>> pullByUuids(List<String> uuids) {
        throw new UnsupportedOperationException("Entity is infrastructure");
    }

    protected Call<List<FacilityDto>> pullAllByRegionSince(Region region, long since) {
        return RetroProvider.getFacilityFacade().pullAllByRegionSince(region.getUuid(), since);
    }

    protected Call<List<FacilityDto>> pullAllWithoutRegionSince(long since) {
        return RetroProvider.getFacilityFacade().pullAllWithoutRegionSince(since);
    }

    @Override
    protected Call<Integer> pushAll(List<FacilityDto> facilityDtos) {
        throw new UnsupportedOperationException("Entity is infrastructure");
    }

    /**
     * Pulls the data chunkwise per region
     * @param markAsRead
     * @throws DaoException
     * @throws SQLException
     * @throws IOException
     */
    @Override
    public void pullEntities(final boolean markAsRead) throws DaoException, ServerConnectionException {
        try {
            final AbstractAdoDao<Facility> dao = DatabaseHelper.getAdoDao(getAdoClass());

            Date maxModifiedDate = dao.getLatestChangeDate();
            long maxModifiedTime = maxModifiedDate != null ? maxModifiedDate.getTime() + 1 : 0;

            List<Region> regions = DatabaseHelper.getRegionDao().queryForAll();
            for (Region region : regions) {
                Call<List<FacilityDto>> dtoCall = pullAllByRegionSince(region, maxModifiedTime);
                if (dtoCall == null) {
                    return;
                }
                handlePullResponse(markAsRead, dao, dtoCall.execute());
            }

            // Pull 'Other' health facility which has no region set
            Call<List<FacilityDto>> dtoCall = pullAllWithoutRegionSince(maxModifiedTime);
            if (dtoCall == null) {
                return;
            }
            handlePullResponse(markAsRead, dao, dtoCall.execute());

        } catch (RuntimeException e) {
            Log.e(getClass().getName(), "Exception thrown when trying to pull entities");
            throw new DaoException(e);
        } catch (IOException e) {
            throw new ServerConnectionException(e);
        }
    }

    @Override
    protected void handlePullResponse(final boolean markAsRead, final AbstractAdoDao<Facility> dao, Response<List<FacilityDto>> response) {
        if (!response.isSuccessful()) {
            String responseErrorBodyString;
            try {
                responseErrorBodyString = response.errorBody().string();
            } catch (IOException e) {
                responseErrorBodyString = "Exception accessing error body: " + e.getMessage();
            }
            Log.e(getClass().getName(), "Pulling changes from server did not work: " + responseErrorBodyString);
        }

        final List<FacilityDto> result = response.body();
        if (result != null && result.size() > 0) {
            preparePulledResult(result);
            dao.callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {
                    for (FacilityDto dto : result) {
                        // TODO find a performance friendly way for merging
                        Facility source = fillOrCreateFromDto(null, dto);
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

    private Community lastCommunity = null;
    private District lastDistrict = null;
    private Region lastRegion = null;

    @Override
    public void fillInnerFromDto(Facility target, FacilityDto source) {

        target.setName(source.getName());

        if (source.getCommunity() != null) {
            // keep a cache to improve performance
            if (lastCommunity == null || !lastCommunity.getUuid().equals(source.getCommunity().getUuid())) {
                lastCommunity = DatabaseHelper.getCommunityDao().getByReferenceDto(source.getCommunity());
            }
            target.setCommunity(lastCommunity);
            if (lastDistrict == null || !lastDistrict.getId().equals(lastCommunity.getDistrict().getId())) {
                lastDistrict = DatabaseHelper.getDistrictDao().queryForId(lastCommunity.getDistrict().getId());
            }
            target.setDistrict(lastDistrict);
            if (lastRegion == null || !lastRegion.getId().equals(lastDistrict.getRegion().getId())) {
                lastRegion = DatabaseHelper.getRegionDao().queryForId(lastDistrict.getRegion().getId());
            }
            target.setRegion(lastRegion);
        } else {
            target.setCommunity(null);
            target.setDistrict(null);
            target.setRegion(null);
        }

        target.setCity(source.getCity());
        target.setLatitude(source.getLatitude());
        target.setLongitude(source.getLongitude());
        target.setPublicOwnership(source.isPublicOwnership());
        target.setType(source.getType());
    }

    @Override
    public void fillInnerFromAdo(FacilityDto facilityDto, Facility facility) {
        throw new UnsupportedOperationException();
    }

    public static FacilityReferenceDto toReferenceDto(Facility ado) {
        if (ado == null) {
            return null;
        }
        FacilityReferenceDto dto = new FacilityReferenceDto();
        fillReferenceDto(dto, ado);

        return dto;
    }
}
