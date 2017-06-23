package de.symeda.sormas.app.rest;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.analytics.Tracker;

import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.task.TaskNotificationService;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.SyncCallback;

/**
 * Querries for uuids of all non-embedded entities and deletes local entities that are not in the whitelist
 * TODO find a better name
 */
public class SynchronizeDataAsync extends AsyncTask<Void, Void, Void> {

    /**
     * Should be set to true when the synchronization fails and reset to false as soon
     * as the last callback is called (i.e. the synchronization has been completed/cancelled).
     */
    protected boolean syncFailed;
    private final Context context;

    private SynchronizeDataAsync(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {

            pullUuidsAndDeleteInvalid();

        } catch (Exception e) {
            syncFailed = true;
            Log.e(getClass().getName(), "Error while synchronizing removed entities", e);
            SormasApplication application = (SormasApplication) context.getApplicationContext();
            Tracker tracker = application.getDefaultTracker();
            ErrorReportingHelper.sendCaughtException(tracker, e, null, true);
        }
        return null;
    }

    private void pullUuidsAndDeleteInvalid() throws java.io.IOException {
        // ATTENTION: Since we are working with UUID lists we have no type safety. Look for typos!

        // tasks
        List<String> uuids = RetroProvider.getTaskFacade().pullUuids().execute().body();
        DatabaseHelper.getTaskDao().deleteInvalid(uuids);

        // visits
        uuids = RetroProvider.getVisitFacade().pullUuids().execute().body();
        DatabaseHelper.getVisitDao().deleteInvalid(uuids);

        // contacts
        uuids = RetroProvider.getContactFacade().pullUuids().execute().body();
        DatabaseHelper.getContactDao().deleteInvalid(uuids);

        // sample tests
        uuids = RetroProvider.getSampleTestFacade().pullUuids().execute().body();
        DatabaseHelper.getSampleTestDao().deleteInvalid(uuids);

        // samples
        uuids = RetroProvider.getSampleFacade().pullUuids().execute().body();
        DatabaseHelper.getSampleDao().deleteInvalid(uuids);

        // cases
        uuids = RetroProvider.getCaseFacade().getUuids().execute().body();
        DatabaseHelper.getCaseDao().deleteInvalid(uuids);

        // event participants
        uuids = RetroProvider.getEventParticipantFacade().pullUuids().execute().body();
        DatabaseHelper.getEventParticipantDao().deleteInvalid(uuids);

        // events
        uuids = RetroProvider.getEventFacade().pullUuids().execute().body();
        DatabaseHelper.getEventDao().deleteInvalid(uuids);

        // persons
        uuids = RetroProvider.getPersonFacade().pullUuids().execute().body();
        DatabaseHelper.getPersonDao().deleteInvalid(uuids);
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
        new SynchronizeDataAsync(context) {
            @Override
            protected void onPostExecute(Void aVoid) {
                if (callback != null) {
                    callback.call(this.syncFailed);
                }
                this.syncFailed = false;
                if (notificationContext != null) {
                    TaskNotificationService.doTaskNotification(notificationContext);
                }
            }
        }.execute();
    }
}