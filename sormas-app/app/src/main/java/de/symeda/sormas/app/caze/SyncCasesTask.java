package de.symeda.sormas.app.caze;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoGetInterface;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.person.SyncPersonsTask;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.sample.SyncSamplesTask;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import retrofit2.Call;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncCasesTask extends AsyncTask<Void, Void, Void> {

    private final Context context;

    private SyncCasesTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            new CaseDtoHelper().pullEntities(new DtoGetInterface<CaseDataDto>() {
                @Override
                public Call<List<CaseDataDto>> getAll(long since) {
                    User user = ConfigProvider.getUser();
                    if (user != null) {
                        return RetroProvider.getCaseFacade().getAll(user.getUuid(), since);
                    }
                    return null;
                }
            }, DatabaseHelper.getCaseDao());

            new CaseDtoHelper().pushEntities(new AdoDtoHelper.DtoPostInterface<CaseDataDto>() {
                @Override
                public Call<Long> postAll(List<CaseDataDto> dtos) {
                    return RetroProvider.getCaseFacade().postAll(dtos);
                }
            }, DatabaseHelper.getCaseDao());
        } catch (DaoException | SQLException | IOException e) {
            Log.e(getClass().getName(), "Error while synchronizing cases", e);
            Toast.makeText(context, "Synchronization of cases failed. Please try again.", Toast.LENGTH_LONG).show();
            SormasApplication application = (SormasApplication) context.getApplicationContext();
            Tracker tracker = application.getDefaultTracker();
            ErrorReportingHelper.sendCaughtException(tracker, this.getClass().getSimpleName(), e, null, true);
        }

        return null;
    }

    public static void syncCases(Context context, final FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            syncCases(context, new Callback() {
                @Override
                public void call() {
                    if (fragmentManager.getFragments() != null) {
                        for (Fragment fragment : fragmentManager.getFragments()) {
                            if (fragment instanceof CasesListFragment) {
                                fragment.onResume();
                            }
                        }
                    }
                }
            });
        } else {
            syncCases(context, (Callback)null);
        }
    }

    public static void syncCases(final FragmentManager fragmentManager, Context context, SwipeRefreshLayout refreshLayout) {
        syncCases(context, fragmentManager);
        refreshLayout.setRefreshing(false);
    }

    public static void syncCasesWithProgressDialog(Context context, final Callback callback) {

        final ProgressDialog progressDialog = ProgressDialog.show(context, "Case synchronization",
                "Cases are being synchronized...", true);

        syncCases(context, new Callback() {
            @Override
            public void call() {
                progressDialog.dismiss();
                callback.call();
            }
        });
    }

    public static void syncCases(final Context context, final Callback callback) {
        SyncPersonsTask.syncPersons(context, new Callback() {
                                        @Override
                                        public void call() {
                                            syncCasesWithoutDependencies(context, callback);
                                        }
                                    });
    }

    public static AsyncTask<Void, Void, Void> syncCasesWithoutDependencies(final Context context, final Callback callback) {
        return new SyncCasesTask(context) {
            @Override
            protected void onPostExecute(Void aVoid) {
                SyncSamplesTask.syncSamples(context, callback);
            }
        }.execute();
    }
}