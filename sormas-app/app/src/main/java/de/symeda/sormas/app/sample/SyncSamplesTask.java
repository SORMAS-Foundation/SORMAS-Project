package de.symeda.sormas.app.sample;

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
import retrofit2.Call;

/**
 * Created by Mate Strysewske on 06.02.2017.
 */

public class SyncSamplesTask extends AsyncTask<Void, Void, Void> {

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
            Log.e(getClass().getName(), "Error while synchronizing samples", e);
            Toast.makeText(context, "Synchronization of samples failed. Please try again.", Toast.LENGTH_LONG).show();
            SormasApplication application = (SormasApplication) context.getApplicationContext();
            Tracker tracker = application.getDefaultTracker();
            ErrorReportingHelper.sendCaughtException(tracker, this.getClass().getSimpleName(), e, null, true);
        }
        return null;
    }

    public static void syncSamples(Context context, final FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            syncSamples(context, new Callback() {
                @Override
                public void call() {
                    if (fragmentManager.getFragments() != null) {
                        for (Fragment fragment : fragmentManager.getFragments()) {
                            if (fragment instanceof SamplesListFragment) {
                                fragment.onResume();
                            }
                        }
                    }
                }
            });
        }
    }

    public static void syncSamples(final FragmentManager fragmentManager, Context context, SwipeRefreshLayout refreshLayout) {
        syncSamples(context, fragmentManager);
        refreshLayout.setRefreshing(false);
    }

    public static void syncSamplesWithProgressDialog(Context context, final Callback callback) {

        final ProgressDialog progressDialog = ProgressDialog.show(context, "Sample synchronization",
                "Samples are being synchronized...", true);

        syncSamples(context, new Callback() {
            @Override
            public void call() {
                progressDialog.dismiss();
                callback.call();
            }
        });
    }

    public static void syncSamples(Context context, final Callback callback) {
        new SyncSamplesTask(context) {
            @Override
            protected void onPostExecute(Void aVoid) {
                if (callback != null) {
                    callback.call();
                }
            }
        }.execute();
    }

}
