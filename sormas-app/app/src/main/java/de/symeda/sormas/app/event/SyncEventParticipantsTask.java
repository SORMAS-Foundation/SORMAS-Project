package de.symeda.sormas.app.event;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.google.android.gms.analytics.Tracker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoGetInterface;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoPostInterface;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.event.EventParticipantDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.person.SyncPersonsTask;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.SyncCallback;
import retrofit2.Call;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncEventParticipantsTask extends AsyncTask<Void, Void, Void> {

    /**
     * Should be set to true when the synchronization fails and reset to false as soon
     * as the last callback is called (i.e. the synchronization has been completed/cancelled).
     */
    private static boolean hasThrownError;
    private final Context context;

    public SyncEventParticipantsTask(final Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            new EventParticipantDtoHelper().pullEntities(new DtoGetInterface<EventParticipantDto>() {
                @Override
                public Call<List<EventParticipantDto>> getAll(long since) {

                    User user = ConfigProvider.getUser();
                    if (user != null) {
                        Call<List<EventParticipantDto>> all = RetroProvider.getEventParticipantFacade().getAll(user.getUuid(), since);
                        return all;
                    }
                    return null;
                }
            }, DatabaseHelper.getEventParticipantDao());

            new EventParticipantDtoHelper().pushEntities(new DtoPostInterface<EventParticipantDto>() {
                @Override
                public Call<Long> postAll(List<EventParticipantDto> dtos) {
                    // TODO postAll should return the date&time the server used as modifiedDate
                    return RetroProvider.getEventParticipantFacade().postAll(dtos);
                }
            }, DatabaseHelper.getEventParticipantDao());
        } catch (DaoException | SQLException | IOException e) {
            hasThrownError = true;
            Log.e(getClass().getName(), "Error while synchronizing alert persons", e);
            SormasApplication application = (SormasApplication) context.getApplicationContext();
            Tracker tracker = application.getDefaultTracker();
            ErrorReportingHelper.sendCaughtException(tracker, e, null, true);
        }

        return null;
    }

    public static void syncEventParticipantsWithoutCallback(Context context, final FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            syncEventParticipants(context, new SyncCallback() {
                @Override
                public void call(boolean syncFailed) {
                    if (fragmentManager.getFragments() != null) {
                        for (Fragment fragment : fragmentManager.getFragments()) {
                            if (fragment instanceof EventParticipantsListFragment) {
                                fragment.onResume();
                            }
                        }
                    }
                    hasThrownError = false;
                }
            });
        } else {
            syncEventParticipants(context, null);
            hasThrownError = false;
        }
    }

    public static void syncEventParticipantsWithCallback(Context context, final FragmentManager fragmentManager, final SyncCallback callback) {
        if (fragmentManager != null) {
            syncEventParticipants(context, new SyncCallback() {
                @Override
                public void call(boolean syncFailed) {
                    if (fragmentManager.getFragments() != null) {
                        for (Fragment fragment : fragmentManager.getFragments()) {
                            if (fragment instanceof EventsListFragment) {
                                fragment.onResume();
                            }
                        }
                    }
                    callback.call(syncFailed);
                    hasThrownError = false;
                }
            });
        } else {
            syncEventParticipants(context, callback);
            hasThrownError = false;
        }
    }

    public static void syncEventParticipants(final Context context, final SyncCallback callback) {
        SyncPersonsTask.syncPersons(context, new SyncCallback(){
            @Override
            public void call(boolean syncFailed){
                new SyncEventParticipantsTask(context) {
                    @Override
                    protected void onPostExecute(Void aVoid){
                        callback.call(hasThrownError);
                        hasThrownError = false;
                    }
                }.execute();
            }
        });
    }
}