package de.symeda.sormas.app.contact;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.List;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoGetInterface;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoPostInterface;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.ContactDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.caze.SyncCasesTask;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.util.Callback;
import retrofit2.Call;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncContactsTask extends AsyncTask<Void, Void, Void> {

    public SyncContactsTask() {
    }

    @Override
    protected Void doInBackground(Void... params) {

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

        return null;
    }

    public static void syncContactsWithProgressDialog(Context context, final Callback callback) {

        final ProgressDialog progressDialog = ProgressDialog.show(context, "Contact synchronization",
                "Contacts are being synchronized...", true);

        syncContacts(new Callback() {
            @Override
            public void call() {
                progressDialog.dismiss();
                callback.call();
            }
        });
    }

    public static void syncContacts(final FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            syncContacts(new Callback() {
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
            syncContacts((Callback)null);
        }
    }

    public static void syncContacts(final FragmentManager fragmentManager, SwipeRefreshLayout refreshLayout) {
        syncContacts(fragmentManager);
        refreshLayout.setRefreshing(false);
    }

    public static void syncContacts(final Callback callback) {
        SyncCasesTask.syncCases(new Callback() {
            @Override
            public void call() {
                syncContactsWithoutDependencies(callback);
            }
        });
    }

    public static void syncContactsWithoutDependencies(final Callback callback) {
        new SyncContactsTask() {
            @Override
            protected void onPostExecute(Void aVoid) {
                if (callback != null) {
                    callback.call();
                }
            }
        }.execute();
    }
}