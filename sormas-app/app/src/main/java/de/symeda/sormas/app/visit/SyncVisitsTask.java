package de.symeda.sormas.app.visit;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.google.android.gms.analytics.Tracker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoGetInterface;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoPostInterface;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.visit.VisitDtoHelper;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.SyncCallback;
import retrofit2.Call;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncVisitsTask extends AsyncTask<Void, Void, Void> {

    /**
     * Should be set to true when the synchronization fails and reset to false as soon
     * as the last callback is called (i.e. the synchronization has been completed/cancelled).
     */
    private static boolean hasThrownError;
    private final Context context;

    public SyncVisitsTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            new VisitDtoHelper().pullEntities(new DtoGetInterface<VisitDto>() {
                @Override
                public Call<List<VisitDto>> getAll(long since) {

                    User user = ConfigProvider.getUser();
                    if (user != null) {
                        Call<List<VisitDto>> all = RetroProvider.getVisitFacade().getAll(user.getUuid(), since);
                        return all;
                    }
                    return null;
                }
            }, DatabaseHelper.getVisitDao());

            new VisitDtoHelper().pushEntities(new DtoPostInterface<VisitDto>() {
                @Override
                public Call<Long> postAll(List<VisitDto> dtos) {
                    // TODO postAll should return the date&time the server used as modifiedDate
                    return RetroProvider.getVisitFacade().postAll(dtos);
                }
            }, DatabaseHelper.getVisitDao());
        } catch (DaoException | SQLException | IOException e) {
            hasThrownError = true;
            Log.e(getClass().getName(), "Error while synchronizing visits", e);
            SormasApplication application = (SormasApplication) context.getApplicationContext();
            Tracker tracker = application.getDefaultTracker();
            ErrorReportingHelper.sendCaughtException(tracker, this.getClass().getSimpleName(), e, null, true);
        }
        return null;
    }

    public static void syncVisitsWithCallback(Context context, final FragmentManager fragmentManager, final SyncCallback callback) {
        if (fragmentManager != null) {
            createSyncVisitsTask(context, new SyncCallback() {
                @Override
                public void call(boolean syncFailed) {
                    if (fragmentManager.getFragments() != null) {
                        for (Fragment fragment : fragmentManager.getFragments()) {
                            if (fragment instanceof VisitsListFragment) {
                                fragment.onResume();
                            }
                        }
                    }
                    callback.call(syncFailed);
                    hasThrownError = false;
                }
            });
        } else {
            createSyncVisitsTask(context, callback);
            hasThrownError = false;
        }
    }

    public static void syncVisitsWithProgressDialog(Context context, final SyncCallback callback) {
        final ProgressDialog progressDialog = ProgressDialog.show(context, "Visit synchronization",
                "Visits are being synchronized...", true);

        createSyncVisitsTask(context, new SyncCallback() {
            @Override
            public void call(boolean syncFailed) {
                progressDialog.dismiss();
                callback.call(syncFailed);
                hasThrownError = false;
            }
        });
    }

    public static void createSyncVisitsTask(Context context, final SyncCallback callback) {
        new SyncVisitsTask(context) {
            @Override
            protected void onPostExecute(Void aVoid) {
                callback.call(hasThrownError);
                hasThrownError = false;
            }
        }.execute();
    }
}