package de.symeda.sormas.app.rest;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.analytics.Tracker;

import java.io.IOException;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.common.ServerConnectionException;
import de.symeda.sormas.app.backend.common.SynchronizationException;
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
import retrofit2.Call;
import retrofit2.Response;

public class SynchronizeDataAsync extends AsyncTask<Void, Void, Void> {

    /**
     * Should be set to true when the synchronization fails and reset to false as soon
     * as the last callback is called (i.e. the synchronization has been completed/cancelled).
     */
    protected boolean syncFailed;
    protected String syncFailedMessage;
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

        } catch (ServerConnectionException e) {
            syncFailed = true;
            syncFailedMessage = DatabaseHelper.getContext().getString(R.string.server_connection_error);
            RetroProvider.disconnect();

        } catch (Exception e) {

            Log.e(getClass().getName(), "Error trying to synchronize data", e);
            SormasApplication application = (SormasApplication) context.getApplicationContext();
            Tracker tracker = application.getDefaultTracker();
            ErrorReportingHelper.sendCaughtException(tracker, e, null, true);

            if (!secondTry) {
                secondTry = true;
                syncFailedMessage = DatabaseHelper.getContext().getString(R.string.sync_error);
                doInBackground(params);
            } else {
                syncFailed = true;
                RetroProvider.disconnect();
            }
        }
        return null;
    }

    private void synchronizeChangedData() throws DaoException, ServerConnectionException, SynchronizationException {

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

    private void pullInfrastructure() throws DaoException, ServerConnectionException, SynchronizationException {

        new RegionDtoHelper().pullEntities(false);
        new DistrictDtoHelper().pullEntities(false);
        new CommunityDtoHelper().pullEntities(false);
        new FacilityDtoHelper().pullEntities(false);
        new UserDtoHelper().pullEntities(false);
    }

    private void pullUuidsAndDeleteInvalid() throws ServerConnectionException {
        // ATTENTION: Since we are working with UUID lists we have no type safety. Look for typos!

        // tasks
        Call<List<String>> call = RetroProvider.getTaskFacade().pullUuids();
        DatabaseHelper.getTaskDao().deleteInvalid(executeUuidCall(call));

        // visits
        call = RetroProvider.getVisitFacade().pullUuids();
        DatabaseHelper.getVisitDao().deleteInvalid(executeUuidCall(call));

        // contacts
        call = RetroProvider.getContactFacade().pullUuids();
        DatabaseHelper.getContactDao().deleteInvalid(executeUuidCall(call));

        // sample tests
        call = RetroProvider.getSampleTestFacade().pullUuids();
        DatabaseHelper.getSampleTestDao().deleteInvalid(executeUuidCall(call));

        // samples
        call = RetroProvider.getSampleFacade().pullUuids();
        DatabaseHelper.getSampleDao().deleteInvalid(executeUuidCall(call));

        // cases
        call = RetroProvider.getCaseFacade().pullUuids();
        DatabaseHelper.getCaseDao().deleteInvalid(executeUuidCall(call));

        // event participants
        call = RetroProvider.getEventParticipantFacade().pullUuids();
        DatabaseHelper.getEventParticipantDao().deleteInvalid(executeUuidCall(call));

        // events
        call = RetroProvider.getEventFacade().pullUuids();
        DatabaseHelper.getEventDao().deleteInvalid(executeUuidCall(call));

        // persons
        call = RetroProvider.getPersonFacade().pullUuids();
        DatabaseHelper.getPersonDao().deleteInvalid(executeUuidCall(call));
    }

    private List<String> executeUuidCall(Call<List<String>> call) throws ServerConnectionException {
        Response<List<String>> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new ServerConnectionException(e);
        }
        if (!response.isSuccessful()) {
            throw ServerConnectionException.fromResponse(response);
        }
        return response.body();
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
            public void call(boolean syncFailed, String syncFailedMessage) {
                progressDialog.dismiss();
                if (callback != null) {
                    callback.call(syncFailed, syncFailedMessage);
                }
            }
        });
    }


    public static void call(SyncMode syncMode, final Context context, final SyncCallback callback) {
        new SynchronizeDataAsync(syncMode, context) {
            @Override
            protected void onPostExecute(Void aVoid) {
                if (callback != null) {
                    callback.call(syncFailed, syncFailedMessage);
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