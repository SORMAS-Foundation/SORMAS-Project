package de.symeda.sormas.app.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.analytics.Tracker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoGetInterface;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.region.CommunityDtoHelper;
import de.symeda.sormas.app.backend.region.DistrictDtoHelper;
import de.symeda.sormas.app.backend.region.RegionDtoHelper;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.task.SyncTasksTask;
import retrofit2.Call;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncInfrastructureTask extends AsyncTask<Void, Void, Void> {

    /**
     * Should be set to true when the synchronization fails and reset to false as soon
     * as the last callback is called (i.e. the synchronization has been completed/cancelled).
     */
    protected boolean hasThrownError;
    private final Context notificationContext;

    private SyncInfrastructureTask(final Context notificationContext) {
        this.notificationContext = notificationContext;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            new RegionDtoHelper().pullEntities(new DtoGetInterface<RegionDto>() {
                @Override
                public Call<List<RegionDto>> getAll(long since) {
                    return RetroProvider.getRegionFacade().pullAllSince(since);
                }
            }, DatabaseHelper.getRegionDao(), notificationContext);

            new DistrictDtoHelper().pullEntities(new DtoGetInterface<DistrictDto>() {
                @Override
                public Call<List<DistrictDto>> getAll(long since) {
                    return RetroProvider.getDistrictFacade().pullAllSince(since);
                }
            }, DatabaseHelper.getDistrictDao(), notificationContext);

            new CommunityDtoHelper().pullEntities(new DtoGetInterface<CommunityDto>() {
                @Override
                public Call<List<CommunityDto>> getAll(long since) {
                    return RetroProvider.getCommunityFacade().pullAllSince(since);
                }
            }, DatabaseHelper.getCommunityDao(), notificationContext);

            new FacilityDtoHelper().pullEntities(new DtoGetInterface<FacilityDto>() {
                @Override
                public Call<List<FacilityDto>> getAll(long since) {
                    return RetroProvider.getFacilityFacade().pullAllSince(since);
                }
            }, DatabaseHelper.getFacilityDao(), notificationContext);

            new UserDtoHelper().pullEntities(new DtoGetInterface<UserDto>() {
                @Override
                public Call<List<UserDto>> getAll(long since) {
                    return RetroProvider.getUserFacade().pullAllSince(since);
                }
            }, DatabaseHelper.getUserDao(), notificationContext);
        } catch(DaoException | SQLException | IOException e) {
            hasThrownError = true;
            Log.e(getClass().getName(), "Error while synchronizing cases", e);
            SormasApplication application = (SormasApplication) notificationContext.getApplicationContext();
            Tracker tracker = application.getDefaultTracker();
            ErrorReportingHelper.sendCaughtException(tracker, e, null, true);

//            hasThrownError = true;
//            Log.e(this.getClass().getName(), "Exception on executing background synchronization task", e);
//            if (notificationContext != null) {
//                if (RetroProvider.isConnected()) {
//                    SormasApplication application = (SormasApplication) notificationContext.getApplicationContext();
//                    Tracker tracker = application.getDefaultTracker();
//                    ErrorReportingHelper.sendCaughtException(tracker, e, null, true);
//                }
//            }
        }

        return null;
    }

    public static void syncAll(final SyncCallback callback, final Context notificationContext) {
        syncInfrastructure(notificationContext, new SyncCallback() {
            @Override
            public void call(boolean syncFailed) {
                if (!syncFailed) {
                    // this also syncs cases, contacts and events
                    SyncTasksTask.syncTasks(notificationContext, callback, notificationContext);
                }
            }
        });

        // TODO sync samples
    }

    public static void syncInfrastructure(final Context notificationContext, final SyncCallback callback) {
        new SyncInfrastructureTask(notificationContext) {
            @Override
            protected void onPostExecute(Void aVoid) {
                if (callback != null) {
                    callback.call(this.hasThrownError);
                }
                this.hasThrownError = false;
            }
        }.execute();
    }

}