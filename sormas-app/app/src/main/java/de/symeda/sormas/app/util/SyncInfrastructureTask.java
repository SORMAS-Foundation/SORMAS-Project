package de.symeda.sormas.app.util;

import android.os.AsyncTask;

import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import java.util.List;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoGetInterface;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.region.CommunityDtoHelper;
import de.symeda.sormas.app.backend.region.DistrictDtoHelper;
import de.symeda.sormas.app.backend.region.RegionDtoHelper;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncInfrastructureTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = SyncInfrastructureTask.class.getSimpleName();
    protected static Logger logger = LoggerFactory.getLogger(SyncInfrastructureTask.class);

    public SyncInfrastructureTask() {
    }

    @Override
    protected Void doInBackground(Void... params) {

        new RegionDtoHelper().pullEntities(new DtoGetInterface<RegionDto>() {
            @Override
            public Call<List<RegionDto>> getAll(long since) {
                return RetroProvider.getRegionFacade().getAll(since);
            }
        }, DatabaseHelper.getRegionDao());

        new DistrictDtoHelper().pullEntities(new DtoGetInterface<DistrictDto>() {
            @Override
            public Call<List<DistrictDto>> getAll(long since) {
                return RetroProvider.getDistrictFacade().getAll(since);
            }
        }, DatabaseHelper.getDistrictDao());

        new CommunityDtoHelper().pullEntities(new DtoGetInterface<CommunityDto>() {
            @Override
            public Call<List<CommunityDto>> getAll(long since) {
                return RetroProvider.getCommunityFacade().getAll(since);
            }
        }, DatabaseHelper.getCommunityDao());

        new FacilityDtoHelper().pullEntities(new DtoGetInterface<FacilityDto>() {
            @Override
            public Call<List<FacilityDto>> getAll(long since) {
                return RetroProvider.getFacilityFacade().getAll(since);
            }
        }, DatabaseHelper.getFacilityDao());

        new UserDtoHelper().pullEntities(new DtoGetInterface<UserDto>() {
            @Override
            public Call<List<UserDto>> getAll(long since) {
                return RetroProvider.getUserFacade().getAll(since);
            }
        }, DatabaseHelper.getUserDao());

        return null;
    }


    protected void onPostExecute(Integer result) {

    }
}