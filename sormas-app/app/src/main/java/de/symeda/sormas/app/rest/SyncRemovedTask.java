package de.symeda.sormas.app.rest;

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

import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoGetInterface;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoPostInterface;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.task.TaskDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.contact.SyncContactsTask;
import de.symeda.sormas.app.event.SyncEventsTask;
import de.symeda.sormas.app.task.TaskNotificationService;
import de.symeda.sormas.app.task.TasksListFragment;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.SyncCallback;
import retrofit2.Call;

/**
 * Querries for uuids of all non-embedded entities and deletes local entities that are not in the whitelist
 * TODO find a better name
 */
public class SyncRemovedTask extends AsyncTask<Void, Void, Void> {

    /**
     * Should be set to true when the synchronization fails and reset to false as soon
     * as the last callback is called (i.e. the synchronization has been completed/cancelled).
     */
    protected boolean hasThrownError;
    private final Context context;

    private SyncRemovedTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {

            // ATTENTION: Since we are working with UUID lists we have no type safety. Look for typos!

            // tasks
            List<String> uuids = RetroProvider.getTaskFacade().getUuids().execute().body();
            DatabaseHelper.getTaskDao().deleteNotInList(uuids);

            // visits
            uuids = RetroProvider.getVisitFacade().getUuids().execute().body();
            DatabaseHelper.getVisitDao().deleteNotInList(uuids);

            // contacts
            uuids = RetroProvider.getContactFacade().getUuids().execute().body();
            DatabaseHelper.getContactDao().deleteNotInList(uuids);

            // sample tests
            uuids = RetroProvider.getSampleTestFacade().getUuids().execute().body();
            DatabaseHelper.getSampleTestDao().deleteNotInList(uuids);

            // samples
            uuids = RetroProvider.getSampleFacade().getUuids().execute().body();
            DatabaseHelper.getSampleDao().deleteNotInList(uuids);

            // cases
            uuids = RetroProvider.getCaseFacade().getUuids().execute().body();
            DatabaseHelper.getCaseDao().deleteNotInList(uuids);

            // event participants
            uuids = RetroProvider.getEventParticipantFacade().getUuids().execute().body();
            DatabaseHelper.getEventParticipantDao().deleteNotInList(uuids);

            // events
            uuids = RetroProvider.getEventFacade().getUuids().execute().body();
            DatabaseHelper.getEventDao().deleteNotInList(uuids);

        } catch (IOException e) {
            hasThrownError = true;
            Log.e(getClass().getName(), "Error while synchronizing removed entities", e);
            SormasApplication application = (SormasApplication) context.getApplicationContext();
            Tracker tracker = application.getDefaultTracker();
            ErrorReportingHelper.sendCaughtException(tracker, e, null, true);
        }
        return null;
    }

    /**
     * Does the call and meanwhile displays a progress dialog.
     * Should only be called when the user has manually triggered the synchronization.
     *
     * @param context
     * @param callback
     */
    public static void callWithProgressDialog(final Context context, final SyncCallback callback, final Context notificationContext) {
        final ProgressDialog progressDialog = ProgressDialog.show(context, context.getString(R.string.headline_task_synchronization),
                context.getString(R.string.hint_task_synchronization), true);

        call(context, new SyncCallback() {
            @Override
            public void call(boolean syncFailed) {
                progressDialog.dismiss();
                callback.call(syncFailed);
            }
        }, notificationContext);
    }


    public static void call(Context context, final SyncCallback callback, final Context notificationContext) {
        new SyncRemovedTask(context) {
            @Override
            protected void onPostExecute(Void aVoid) {
                if (callback != null) {
                    callback.call(this.hasThrownError);
                }
                this.hasThrownError = false;
                if (notificationContext != null) {
                    TaskNotificationService.doTaskNotification(notificationContext);
                }
            }
        }.execute();
    }
}