package de.symeda.sormas.app.contact;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.google.android.gms.analytics.Tracker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.app.R;
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
import de.symeda.sormas.app.util.SyncCallback;
import retrofit2.Call;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncContactsTask extends AsyncTask<Void, Void, Void> {

    /**
     * Should be set to true when the synchronization fails and reset to false as soon
     * as the last callback is called (i.e. the synchronization has been completed/cancelled).
     */
    protected boolean hasThrownError;
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
                        Call<List<ContactDto>> all = RetroProvider.getContactFacade().getAll(since);
                        return all;
                    }
                    return null;
                }
            }, DatabaseHelper.getContactDao(), context);

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
                            Call<List<ContactDto>> all = RetroProvider.getContactFacade().getAll(since);
                            return all;
                        }
                        return null;
                    }
                }, DatabaseHelper.getContactDao(), context);
            }
        } catch (DaoException | SQLException | IOException e) {
            hasThrownError = true;
            Log.e(getClass().getName(), "Error while synchronizing contacts", e);
            SormasApplication application = (SormasApplication) context.getApplicationContext();
            Tracker tracker = application.getDefaultTracker();
            ErrorReportingHelper.sendCaughtException(tracker, e, null, true);
        }

        return null;
    }

    public static void syncContactsWithoutCallback(Context context, final FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            syncContacts(context, new SyncCallback() {
                @Override
                public void call(boolean syncFailed) {
                    if (fragmentManager.getFragments() != null) {
                        for (Fragment fragment : fragmentManager.getFragments()) {
                            if (fragment instanceof ContactsListFragment) {
                                fragment.onResume();
                            }
                        }
                    }
                }
            });
        } else {
            syncContacts(context, null);
        }
    }

    public static void syncContactsWithCallback(Context context, final FragmentManager fragmentManager, final SyncCallback callback) {
        if (fragmentManager != null) {
            syncContacts(context, new SyncCallback() {
                @Override
                public void call(boolean syncFailed) {
                    if (fragmentManager.getFragments() != null) {
                        for (Fragment fragment : fragmentManager.getFragments()) {
                            if (fragment instanceof ContactsListFragment) {
                                fragment.onResume();
                            }
                        }
                    }
                    callback.call(syncFailed);
                }
            });
        } else {
            syncContacts(context, callback);
        }
    }

    /**
     * Synchronizes the contacts, displays a progress dialog and an error message when the synchronization fails.
     * Should only be called when the user has manually triggered the synchronization.
     *
     * @param context
     * @param callback
     */
    public static void syncContactsWithProgressDialog(Context context, final SyncCallback callback) {
        final ProgressDialog progressDialog = ProgressDialog.show(context, context.getString(R.string.headline_contact_synchronization),
                context.getString(R.string.hint_contact_synchronization), true);

        syncContacts(context, new SyncCallback() {
            @Override
            public void call(boolean syncFailed) {
                progressDialog.dismiss();
                callback.call(syncFailed);
            }
        });
    }

    public static void syncContacts(final Context context, final SyncCallback callback) {
        SyncCasesTask.syncCases(context, new SyncCallback() {
            @Override
            public void call(boolean syncFailed) {
                if (!syncFailed) {
                    createSyncContactsTask(context, callback);
                } else {
                    callback.call(true);
                }
            }
        });
    }

    public static AsyncTask<Void, Void, Void> createSyncContactsTask(Context context, final SyncCallback callback) {
        return new SyncContactsTask(context) {
            @Override
            protected void onPostExecute(Void aVoid) {
                callback.call(this.hasThrownError);
                this.hasThrownError = false;
            }
        }.execute();
    }
}