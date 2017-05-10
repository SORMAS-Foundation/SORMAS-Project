package de.symeda.sormas.app.contact;

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

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoGetInterface;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoPostInterface;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.ContactDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.caze.SyncCasesTask;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import retrofit2.Call;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncContactsTask extends AsyncTask<Void, Void, Void> {

    private final Context context;

    public SyncContactsTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            new ContactDtoHelper().pullEntities(new DtoGetInterface<ContactDto>() {
                @Override
                public Call<List<ContactDto>> getAll(long since) {

                    User user = ConfigProvider.getUser();
                    if (user != null) {
                        Call<List<ContactDto>> all = RetroProvider.getContactFacade().getAll(user.getUuid(), since);
                        return all;
                    }
                    return null;
                }
            }, DatabaseHelper.getContactDao());

            boolean anotherPullNeeded = new ContactDtoHelper().pushEntities(new DtoPostInterface<ContactDto>() {
                @Override
                public Call<Long> postAll(List<ContactDto> dtos) {
                    return RetroProvider.getContactFacade().postAll(dtos);
                }
            }, DatabaseHelper.getContactDao());

            if (anotherPullNeeded) {
                new ContactDtoHelper().pullEntities(new DtoGetInterface<ContactDto>() {
                    @Override
                    public Call<List<ContactDto>> getAll(long since) {

                        User user = ConfigProvider.getUser();
                        if (user != null) {
                            Call<List<ContactDto>> all = RetroProvider.getContactFacade().getAll(user.getUuid(), since);
                            return all;
                        }
                        return null;
                    }
                }, DatabaseHelper.getContactDao());
            }
        } catch (DaoException | SQLException | IOException e) {
            Log.e(getClass().getName(), "Error while synchronizing contacts", e);
            SormasApplication application = (SormasApplication) context.getApplicationContext();
            Tracker tracker = application.getDefaultTracker();
            ErrorReportingHelper.sendCaughtException(tracker, this.getClass().getSimpleName(), e, null, true);
        }

        return null;
    }

    public static void syncContactsWithProgressDialog(Context context, final Callback callback) {
        final ProgressDialog progressDialog = ProgressDialog.show(context, "Contact synchronization",
                "Contacts are being synchronized...", true);

        syncContacts(context, new Callback() {
            @Override
            public void call() {
                progressDialog.dismiss();
                callback.call();
            }
        });
    }

    public static void syncContacts(Context context, final FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            syncContacts(context, new Callback() {
                @Override
                public void call() {
                    if (fragmentManager.getFragments() != null) {
                        for (Fragment fragement : fragmentManager.getFragments()) {
                            if (fragement instanceof ContactsListFragment) {
                                fragement.onResume();
                            }
                        }
                    }
                }
            });
        } else {
            syncContacts(context, (Callback)null);
        }
    }

    public static void syncContacts(final FragmentManager fragmentManager, Context context, SwipeRefreshLayout refreshLayout) {
        syncContacts(context, fragmentManager);
        refreshLayout.setRefreshing(false);
    }

    public static void syncContacts(final Context context, final Callback callback) {
        SyncCasesTask.syncCases(context, new Callback() {
            @Override
            public void call() {
                syncContactsWithoutDependencies(context, callback);
            }
        });
    }

    public static void syncContactsWithoutDependencies(Context context, final Callback callback) {
        new SyncContactsTask(context) {
            @Override
            protected void onPostExecute(Void aVoid) {
                if (callback != null) {
                    callback.call();
                }
            }
        }.execute();
    }
}