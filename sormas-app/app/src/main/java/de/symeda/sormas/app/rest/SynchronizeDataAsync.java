package de.symeda.sormas.app.rest;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.analytics.Tracker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.ContactDtoHelper;
import de.symeda.sormas.app.backend.event.EventDtoHelper;
import de.symeda.sormas.app.backend.event.EventParticipantDtoHelper;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.person.PersonDtoHelper;
import de.symeda.sormas.app.backend.region.CommunityDtoHelper;
import de.symeda.sormas.app.backend.region.DistrictDtoHelper;
import de.symeda.sormas.app.backend.region.RegionDtoHelper;
import de.symeda.sormas.app.backend.sample.SampleDtoHelper;
import de.symeda.sormas.app.backend.sample.SampleTestDtoHelper;
import de.symeda.sormas.app.backend.task.TaskDtoHelper;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.backend.visit.VisitDtoHelper;
import de.symeda.sormas.app.task.TaskNotificationService;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.SyncCallback;

public class SynchronizeDataAsync extends AsyncTask<Void, Void, Void> {

    /**
     * Should be set to true when the synchronization fails and reset to false as soon
     * as the last callback is called (i.e. the synchronization has been completed/cancelled).
     */
    protected boolean syncFailed;
    protected boolean secondTry;

    private final SyncMode syncMode;
    private final Context context;


    private SynchronizeDataAsync(SyncMode syncMode, Context context) {
        this.syncMode = syncMode;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (!RetroProvider.isConnected()) {
            return null;
        }

        try {

            switch (syncMode) {
                case ChangesOnly:
                    if (secondTry) {
                        pullUuidsAndDeleteInvalid();
                    }
                    synchronizeChangedData();
                    break;
                case ChangesAndInfrastructure:
                    pullInfrastructure();
                    if (secondTry) {
                        pullUuidsAndDeleteInvalid();
                    }
                    synchronizeChangedData();
                    break;
                case Complete:
                    pullInfrastructure();
                    pullUuidsAndDeleteInvalid();
                    synchronizeChangedData();
                    break;
                default:
                    throw new IllegalArgumentException(syncMode.toString());
            }

        } catch (Exception e) {

            Log.e(getClass().getName(), "Error trying to synchronize data", e);
            SormasApplication application = (SormasApplication) context.getApplicationContext();
            Tracker tracker = application.getDefaultTracker();
            ErrorReportingHelper.sendCaughtException(tracker, e, null, true);

            if (!secondTry) {
                secondTry = true;
                doInBackground(params);
            } else {
                syncFailed = true;
                RetroProvider.disconnect();
            }
        }
        return null;
    }

    private void synchronizeChangedData() throws DaoException, SQLException, IOException {

        PersonDtoHelper personDtoHelper = new PersonDtoHelper();
        EventDtoHelper eventDtoHelper = new EventDtoHelper();
        EventParticipantDtoHelper eventParticipantDtoHelper = new EventParticipantDtoHelper();
        CaseDtoHelper caseDtoHelper = new CaseDtoHelper();
        SampleDtoHelper sampleDtoHelper = new SampleDtoHelper();
        SampleTestDtoHelper sampleTestDtoHelper = new SampleTestDtoHelper();
        ContactDtoHelper contactDtoHelper = new ContactDtoHelper();
        VisitDtoHelper visitDtoHelper = new VisitDtoHelper();
        TaskDtoHelper taskDtoHelper = new TaskDtoHelper();

        boolean personsNeedPull = personDtoHelper.pullAndPushEntities();
        boolean eventsNeedPull = eventDtoHelper.pullAndPushEntities();
        boolean eventParticipantsNeedPull = eventParticipantDtoHelper.pullAndPushEntities();
        boolean casesNeedPull = caseDtoHelper.pullAndPushEntities();
        boolean samplesNeedPull = sampleDtoHelper.pullAndPushEntities();
        boolean sampleTestsNeedPull = sampleTestDtoHelper.pullAndPushEntities();
        boolean contactsNeedPull = contactDtoHelper.pullAndPushEntities();
        boolean visitsNeedPull = visitDtoHelper.pullAndPushEntities();
        boolean tasksNeedPull = taskDtoHelper.pullAndPushEntities();

        if (personsNeedPull)
            personDtoHelper.pullEntities(true);
        if (eventsNeedPull)
            eventDtoHelper.pullEntities(true);
        if (eventParticipantsNeedPull)
            eventParticipantDtoHelper.pullEntities(true);
        if (casesNeedPull)
            caseDtoHelper.pullEntities(true);
        if (samplesNeedPull)
            sampleDtoHelper.pullEntities(true);
        if (sampleTestsNeedPull)
            sampleTestDtoHelper.pullEntities(true);
        if (contactsNeedPull)
            contactDtoHelper.pullEntities(true);
        if (visitsNeedPull)
            visitDtoHelper.pullEntities(true);
        if (tasksNeedPull)
            taskDtoHelper.pullEntities(true);
    }

    private void pullInfrastructure() throws DaoException, SQLException, IOException {

        new RegionDtoHelper().pullEntities(false);
        new DistrictDtoHelper().pullEntities(false);
        new CommunityDtoHelper().pullEntities(false);
        new FacilityDtoHelper().pullEntities(false);
        new UserDtoHelper().pullEntities(false);
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
        uuids = RetroProvider.getCaseFacade().pullUuids().execute().body();
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
    public static void callWithProgressDialog(SyncMode syncMode, final Context context, final SyncCallback callback) {
        final ProgressDialog progressDialog = ProgressDialog.show(context, context.getString(R.string.headline_synchronization),
                context.getString(R.string.hint_synchronization), true);

        call(syncMode, context, new SyncCallback() {
            @Override
            public void call(boolean syncFailed) {
                progressDialog.dismiss();
                if (callback != null) {
                    callback.call(syncFailed);
                }
            }
        });
    }


    public static void call(SyncMode syncMode, final Context context, final SyncCallback callback) {
        new SynchronizeDataAsync(syncMode, context) {
            @Override
            protected void onPostExecute(Void aVoid) {
                if (callback != null) {
                    callback.call(syncFailed);
                }
                if (context != null) {
                    TaskNotificationService.doTaskNotification(context);
                }
            }
        }.execute();
    }

    public enum SyncMode {
        ChangesOnly,
        ChangesAndInfrastructure,
        Complete,
    }
}