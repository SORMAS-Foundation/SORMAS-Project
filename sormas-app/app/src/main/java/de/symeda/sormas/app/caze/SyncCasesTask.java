package de.symeda.sormas.app.caze;

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
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.SyncCallback;
import retrofit2.Call;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncCasesTask extends AsyncTask<Void, Void, Void> {

    /**
     * Should be set to true when the synchronization fails and reset to false as soon
     * as the last callback is called (i.e. the synchronization has been completed/cancelled).
     */
    protected boolean hasThrownError;
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
            hasThrownError = true;
            Log.e(getClass().getName(), "Error while synchronizing cases", e);
            SormasApplication application = (SormasApplication) context.getApplicationContext();
            Tracker tracker = application.getDefaultTracker();
            ErrorReportingHelper.sendCaughtException(tracker, e, null, true);
        }

        return null;
    }

    public static void syncCasesWithoutCallback(Context context, final FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            syncCases(context, new SyncCallback() {
                @Override
                public void call(boolean syncFailed) {
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
            syncCases(context, null);
        }
    }

    public static void syncCasesWithCallback(Context context, final FragmentManager fragmentManager, final SyncCallback callback) {
        if (fragmentManager != null) {
            syncCases(context, new SyncCallback() {
                @Override
                public void call(boolean syncFailed) {
                    if (fragmentManager.getFragments() != null) {
                        for (Fragment fragment : fragmentManager.getFragments()) {
                            if (fragment instanceof CasesListFragment) {
                                fragment.onResume();
                            }
                        }
                    }
                    callback.call(syncFailed);
                }
            });
        } else {
            syncCases(context, callback);
        }
    }

    /**
     * Synchronizes the cases, displays a progress dialog and an error message when the synchronization fails.
     * Should only be called when the user has manually triggered the synchronization.
     *
     * @param context
     * @param callback
     */
    public static void syncCasesWithProgressDialog(final Context context, final SyncCallback callback) {
        final ProgressDialog progressDialog = ProgressDialog.show(context, "Case synchronization",
                "Cases are being synchronized...", true);

        syncCases(context, new SyncCallback() {
            @Override
            public void call(boolean syncFailed) {
                progressDialog.dismiss();
                callback.call(syncFailed);
            }
        });
    }

    public static void syncCases(final Context context, final SyncCallback callback) {
        SyncPersonsTask.syncPersons(context, new SyncCallback() {
            @Override
            public void call(boolean syncFailed) {
                if (!syncFailed) {
                    createSyncCasesTask(context, callback);
                } else {
                    callback.call(true);
                }
            }
        });
    }

    public static AsyncTask<Void, Void, Void> createSyncCasesTask(final Context context, final SyncCallback callback) {
        return new SyncCasesTask(context) {
            @Override
            protected void onPostExecute(Void aVoid) {
                if (this.hasThrownError) {
                    callback.call(true);
                } else {
                    SyncSamplesTask.createSyncSamplesTask(context, callback);
                }
                this.hasThrownError = false;
            }
        }.execute();
    }

}