package de.symeda.sormas.app.sample;

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

import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.sample.SampleDtoHelper;
import de.symeda.sormas.app.backend.sample.SampleTestDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.SyncCallback;
import retrofit2.Call;

/**
 * Created by Mate Strysewske on 06.02.2017.
 */

public class SyncSamplesTask extends AsyncTask<Void, Void, Void> {

    /**
     * Should be set to true when the synchronization fails and reset to false as soon
     * as the last callback is called (i.e. the synchronization has been completed/cancelled).
     */
    private static boolean hasThrownError;
    private final Context context;

    public SyncSamplesTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            new SampleDtoHelper().pullEntities(new AdoDtoHelper.DtoGetInterface<SampleDto>() {
                @Override
                public Call<List<SampleDto>> getAll(long since) {

                    User user = ConfigProvider.getUser();
                    if (user != null) {
                        Call<List<SampleDto>> all = RetroProvider.getSampleFacade().getAll(user.getUuid(), since);
                        return all;
                    }
                    return null;
                }
            }, DatabaseHelper.getSampleDao());

            new SampleTestDtoHelper().pullEntities(new AdoDtoHelper.DtoGetInterface<SampleTestDto>() {
                @Override
                public Call<List<SampleTestDto>> getAll(long since) {

                    User user = ConfigProvider.getUser();
                    if (user != null) {
                        Call<List<SampleTestDto>> all = RetroProvider.getSampleTestFacade().getAll(user.getUuid(), since);
                        return all;
                    }
                    return null;
                }
            }, DatabaseHelper.getSampleTestDao());

            new SampleDtoHelper().pushEntities(new AdoDtoHelper.DtoPostInterface<SampleDto>() {
                @Override
                public Call<Long> postAll(List<SampleDto> dtos) {
                    return RetroProvider.getSampleFacade().postAll(dtos);
                }
            }, DatabaseHelper.getSampleDao());
        } catch (DaoException | SQLException | IOException e) {
            hasThrownError = true;
            Log.e(getClass().getName(), "Error while synchronizing samples", e);
            SormasApplication application = (SormasApplication) context.getApplicationContext();
            Tracker tracker = application.getDefaultTracker();
            ErrorReportingHelper.sendCaughtException(tracker, e, null, true);
        }
        return null;
    }

    public static void syncSamplesWithoutCallback(Context context, final FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            createSyncSamplesTask(context, new SyncCallback() {
                @Override
                public void call(boolean syncSuccessful) {
                    if (fragmentManager.getFragments() != null) {
                        for (Fragment fragment : fragmentManager.getFragments()) {
                            if (fragment instanceof SamplesListFragment) {
                                fragment.onResume();
                            }
                        }
                    }
                    hasThrownError = false;
                }
            });
        } else {
            createSyncSamplesTask(context, null);
            hasThrownError = false;
        }
    }

    public static void syncSamplesWithCallback(Context context, final FragmentManager fragmentManager, final SyncCallback callback) {
        if (fragmentManager != null) {
            createSyncSamplesTask(context, new SyncCallback() {
                @Override
                public void call(boolean syncFailed) {
                    if (fragmentManager.getFragments() != null) {
                        for (Fragment fragment : fragmentManager.getFragments()) {
                            if (fragment instanceof SamplesListFragment) {
                                fragment.onResume();
                            }
                        }
                    }
                    callback.call(syncFailed);
                    hasThrownError = false;
                }
            });
        } else {
            createSyncSamplesTask(context, callback);
            hasThrownError = false;
        }
    }

    /**
     * Synchronizes the contacts, displays a progress dialog and an error message when the synchronization fails.
     * Should only be called when the user has manually triggered the synchronization.
     *
     * @param context
     * @param callback
     */
    public static void syncSamplesWithProgressDialog(Context context, final SyncCallback callback) {
        final ProgressDialog progressDialog = ProgressDialog.show(context, "Sample synchronization",
                "Samples are being synchronized...", true);

        createSyncSamplesTask(context, new SyncCallback() {
            @Override
            public void call(boolean syncFailed) {
                progressDialog.dismiss();
                callback.call(syncFailed);
                hasThrownError = false;
            }
        });
    }

    public static void createSyncSamplesTask(final Context context, final SyncCallback callback) {
        new SyncSamplesTask(context) {
            @Override
            protected void onPostExecute(Void aVoid) {
                callback.call(hasThrownError);
                hasThrownError = false;
            }
        }.execute();
    }

}
