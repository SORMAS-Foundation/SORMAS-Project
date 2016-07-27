package de.symeda.sormas.app.util;

import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.region.CommunityDtoHelper;
import de.symeda.sormas.app.backend.region.DistrictDtoHelper;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.region.RegionDao;
import de.symeda.sormas.app.backend.region.RegionDtoHelper;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.rest.DtoFacadeRetro;
import de.symeda.sormas.app.rest.PersonFacadeRetro;
import de.symeda.sormas.app.rest.RegionFacadeRetro;
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

        new RegionDtoHelper().syncEntities(new DtoFacadeRetro<RegionDto>() {
            @Override
            public Call<List<RegionDto>> getAll(long since) {
                return RetroProvider.getRegionFacade().getAll(since);
            }
        }, DatabaseHelper.getRegionDao());

        new DistrictDtoHelper().syncEntities(new DtoFacadeRetro<DistrictDto>() {
            @Override
            public Call<List<DistrictDto>> getAll(long since) {
                return RetroProvider.getDistrictFacade().getAll(since);
            }
        }, DatabaseHelper.getDistrictDao());

        new CommunityDtoHelper().syncEntities(new DtoFacadeRetro<CommunityDto>() {
            @Override
            public Call<List<CommunityDto>> getAll(long since) {
                return RetroProvider.getCommunityFacade().getAll(since);
            }
        }, DatabaseHelper.getCommunityDao());

        new FacilityDtoHelper().syncEntities(new DtoFacadeRetro<FacilityDto>() {
            @Override
            public Call<List<FacilityDto>> getAll(long since) {
                return RetroProvider.getFacilityFacade().getAll(since);
            }
        }, DatabaseHelper.getFacilityDao());

        new UserDtoHelper().syncEntities(new DtoFacadeRetro<UserDto>() {
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