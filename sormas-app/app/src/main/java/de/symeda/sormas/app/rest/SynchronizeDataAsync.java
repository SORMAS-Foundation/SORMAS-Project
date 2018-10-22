package de.symeda.sormas.app.rest;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.analytics.Tracker;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import de.symeda.sormas.api.caze.classification.DiseaseClassificationCriteria;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.classification.DiseaseClassificationAppHelper;
import de.symeda.sormas.app.backend.classification.DiseaseClassificationDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.ContactDtoHelper;
import de.symeda.sormas.app.backend.event.EventDtoHelper;
import de.symeda.sormas.app.backend.event.EventParticipantDtoHelper;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.outbreak.OutbreakDtoHelper;
import de.symeda.sormas.app.backend.person.PersonDtoHelper;
import de.symeda.sormas.app.backend.region.CommunityDtoHelper;
import de.symeda.sormas.app.backend.region.DistrictDtoHelper;
import de.symeda.sormas.app.backend.region.RegionDtoHelper;
import de.symeda.sormas.app.backend.report.WeeklyReportDtoHelper;
import de.symeda.sormas.app.backend.report.WeeklyReportEntryDtoHelper;
import de.symeda.sormas.app.backend.sample.SampleDtoHelper;
import de.symeda.sormas.app.backend.sample.SampleTestDtoHelper;
import de.symeda.sormas.app.backend.task.TaskDtoHelper;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.backend.visit.VisitDtoHelper;
import de.symeda.sormas.app.core.TaskNotificationService;
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

    private SyncMode syncMode;
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
                case Changes:
                    // infrastructure always has to be pulled - otherwise referenced data may be lost (e.g. #586)
                    pullInfrastructure();
                    synchronizeChangedData();
                    break;
                case Complete:
                    pullMissingAndDeleteInvalidInfrastructure();
                    pullInfrastructure();
                    pullMissingAndDeleteInvalidData();
                    synchronizeChangedData();
                    break;
                case CompleteAndRepull:
                    pullMissingAndDeleteInvalidInfrastructure();
                    pullInfrastructure();
                    repullData();
                    pullMissingAndDeleteInvalidData();
                    synchronizeChangedData();
                    break;
                default:
                    throw new IllegalArgumentException(syncMode.toString());
            }

        } catch (ServerConnectionException e) {
            syncFailed = true;
            syncFailedMessage = e.getMessage(context);
            RetroProvider.disconnect();

        } catch (Exception e) {

            SyncMode newSyncMode = null;
            switch (syncMode) {
                case Changes:
                    newSyncMode = SyncMode.Complete;
                    break;
                case Complete:
                    newSyncMode = SyncMode.CompleteAndRepull;
                    break;
                case CompleteAndRepull:
                    break;
                default:
                    throw new IllegalArgumentException(syncMode.toString());
            }

            if (newSyncMode != null) {

                Log.w(getClass().getName(), "Error trying to synchronizing data in mode '" + syncMode + "'", e);

                syncMode = newSyncMode;
                doInBackground(params);

            } else {

                Log.e(getClass().getName(), "Error trying to synchronizing data in mode '" + syncMode + "'", e);

                SormasApplication application = (SormasApplication) context.getApplicationContext();
                Tracker tracker = application.getDefaultTracker();
                ErrorReportingHelper.sendCaughtException(tracker, e, null, true);

                syncFailed = true;
                syncFailedMessage = DatabaseHelper.getContext().getString(R.string.sync_error);
                RetroProvider.disconnect();
            }
        }

        return null;
    }

    public static boolean hasAnyUnsynchronizedData() {
        return DatabaseHelper.getCaseDao().isAnyModified() ||
                DatabaseHelper.getContactDao().isAnyModified() ||
                DatabaseHelper.getPersonDao().isAnyModified() ||
                DatabaseHelper.getEventDao().isAnyModified() ||
                DatabaseHelper.getEventParticipantDao().isAnyModified() ||
                DatabaseHelper.getSampleDao().isAnyModified() ||
                DatabaseHelper.getSampleTestDao().isAnyModified() ||
                DatabaseHelper.getTaskDao().isAnyModified() ||
                DatabaseHelper.getVisitDao().isAnyModified() ||
                DatabaseHelper.getWeeklyReportDao().isAnyModified() ||
                DatabaseHelper.getWeeklyReportEntryDao().isAnyModified();
    }

    private void synchronizeChangedData() throws DaoException, ServerConnectionException, ServerCommunicationException {
        PersonDtoHelper personDtoHelper = new PersonDtoHelper();
        CaseDtoHelper caseDtoHelper = new CaseDtoHelper();
        EventDtoHelper eventDtoHelper = new EventDtoHelper();
        EventParticipantDtoHelper eventParticipantDtoHelper = new EventParticipantDtoHelper();
        SampleDtoHelper sampleDtoHelper = new SampleDtoHelper();
        SampleTestDtoHelper sampleTestDtoHelper = new SampleTestDtoHelper();
        ContactDtoHelper contactDtoHelper = new ContactDtoHelper();
        VisitDtoHelper visitDtoHelper = new VisitDtoHelper();
        TaskDtoHelper taskDtoHelper = new TaskDtoHelper();
        WeeklyReportDtoHelper weeklyReportDtoHelper = new WeeklyReportDtoHelper();
        WeeklyReportEntryDtoHelper weeklyReportEntryDtoHelper = new WeeklyReportEntryDtoHelper();

        // order is important, due to dependencies (e.g. case & person)

        new OutbreakDtoHelper().pullEntities(false);

        boolean personsNeedPull = personDtoHelper.pullAndPushEntities();
        boolean casesNeedPull = caseDtoHelper.pullAndPushEntities();
        boolean eventsNeedPull = eventDtoHelper.pullAndPushEntities();
        boolean eventParticipantsNeedPull = eventParticipantDtoHelper.pullAndPushEntities();
        boolean samplesNeedPull = sampleDtoHelper.pullAndPushEntities();
        boolean sampleTestsNeedPull = sampleTestDtoHelper.pullAndPushEntities();
        boolean contactsNeedPull = contactDtoHelper.pullAndPushEntities();
        boolean visitsNeedPull = visitDtoHelper.pullAndPushEntities();
        boolean tasksNeedPull = taskDtoHelper.pullAndPushEntities();
        boolean weeklyReportsNeedPull = weeklyReportDtoHelper.pullAndPushEntities();
        boolean weeklyReportEntriesNeedPull = weeklyReportEntryDtoHelper.pullAndPushEntities();

        if (personsNeedPull)
            personDtoHelper.pullEntities(true);
        if (casesNeedPull)
            caseDtoHelper.pullEntities(true);
        if (eventsNeedPull)
            eventDtoHelper.pullEntities(true);
        if (eventParticipantsNeedPull)
            eventParticipantDtoHelper.pullEntities(true);
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
        if (weeklyReportsNeedPull)
            weeklyReportDtoHelper.pullEntities(true);
        if (weeklyReportEntriesNeedPull)
            weeklyReportEntryDtoHelper.pullEntities(true);

        // Synchronize disease classification if the table is empty
//        if (DatabaseHelper.getDiseaseClassificationDao().isEmpty()) {
//            pullDiseaseClassification();
//        }
    }

    private void repullData() throws DaoException, ServerConnectionException, ServerCommunicationException {
        PersonDtoHelper personDtoHelper = new PersonDtoHelper();
        CaseDtoHelper caseDtoHelper = new CaseDtoHelper();
        EventDtoHelper eventDtoHelper = new EventDtoHelper();
        EventParticipantDtoHelper eventParticipantDtoHelper = new EventParticipantDtoHelper();
        SampleDtoHelper sampleDtoHelper = new SampleDtoHelper();
        SampleTestDtoHelper sampleTestDtoHelper = new SampleTestDtoHelper();
        ContactDtoHelper contactDtoHelper = new ContactDtoHelper();
        VisitDtoHelper visitDtoHelper = new VisitDtoHelper();
        TaskDtoHelper taskDtoHelper = new TaskDtoHelper();
        WeeklyReportDtoHelper weeklyReportDtoHelper = new WeeklyReportDtoHelper();
        WeeklyReportEntryDtoHelper weeklyReportEntryDtoHelper = new WeeklyReportEntryDtoHelper();

        // order is important, due to dependencies (e.g. case & person)

        new UserDtoHelper().repullEntities();
        new OutbreakDtoHelper().repullEntities();
        personDtoHelper.repullEntities();
        caseDtoHelper.repullEntities();
        eventDtoHelper.repullEntities();
        eventParticipantDtoHelper.repullEntities();
        sampleDtoHelper.repullEntities();
        sampleTestDtoHelper.repullEntities();
        contactDtoHelper.repullEntities();
        visitDtoHelper.repullEntities();
        taskDtoHelper.repullEntities();
        weeklyReportDtoHelper.repullEntities();
        weeklyReportEntryDtoHelper.repullEntities();
    }

    private void pullInfrastructure() throws DaoException, ServerConnectionException, ServerCommunicationException {

        new RegionDtoHelper().pullEntities(false);
        new DistrictDtoHelper().pullEntities(false);
        new CommunityDtoHelper().pullEntities(false);
        new FacilityDtoHelper().pullEntities(false);
        new UserDtoHelper().pullEntities(false);


    }

    private void pullMissingAndDeleteInvalidData() throws ServerConnectionException, ServerCommunicationException, DaoException {
        // ATTENTION: Since we are working with UUID lists we have no type safety. Look for typos!

        Log.d(SynchronizeDataAsync.class.getSimpleName(), "pullMissingAndDeleteInvalidData");

        // order is important, due to dependencies (e.g. case & person)

        // weekly reports and entries
        List<String> weeklyReportUuids = executeUuidCall(RetroProvider.getWeeklyReportFacade().pullUuids());
        DatabaseHelper.getWeeklyReportDao().deleteInvalid(weeklyReportUuids);
        List<String> weeklyReportEntryUuids = executeUuidCall(RetroProvider.getWeeklyReportEntryFacade().pullUuids());
        DatabaseHelper.getWeeklyReportEntryDao().deleteInvalid(weeklyReportEntryUuids);
        // tasks
        List<String> taskUuids = executeUuidCall(RetroProvider.getTaskFacade().pullUuids());
        DatabaseHelper.getTaskDao().deleteInvalid(taskUuids);
        // visits
        List<String> visitUuids = executeUuidCall(RetroProvider.getVisitFacade().pullUuids());
        DatabaseHelper.getVisitDao().deleteInvalid(visitUuids);
        // contacts
        List<String> contactUuids = executeUuidCall(RetroProvider.getContactFacade().pullUuids());
        DatabaseHelper.getContactDao().deleteInvalid(contactUuids);
        // sample tests
        List<String> sampleTestUuids = executeUuidCall(RetroProvider.getSampleTestFacade().pullUuids());
        DatabaseHelper.getSampleTestDao().deleteInvalid(sampleTestUuids);
        // samples
        List<String> sampleUuids = executeUuidCall(RetroProvider.getSampleFacade().pullUuids());
        DatabaseHelper.getSampleDao().deleteInvalid(sampleUuids);
        // event participants
        List<String> eventParticipantUuids = executeUuidCall(RetroProvider.getEventParticipantFacade().pullUuids());
        DatabaseHelper.getEventParticipantDao().deleteInvalid(eventParticipantUuids);
        // events
        List<String> eventUuids = executeUuidCall(RetroProvider.getEventFacade().pullUuids());
        DatabaseHelper.getEventDao().deleteInvalid(eventUuids);
        // cases
        List<String> caseUuids = executeUuidCall(RetroProvider.getCaseFacade().pullUuids());
        DatabaseHelper.getCaseDao().deleteInvalid(caseUuids);
        // persons
        List<String> personUuids = executeUuidCall(RetroProvider.getPersonFacade().pullUuids());
        DatabaseHelper.getPersonDao().deleteInvalid(personUuids);
        // outbreak
        List<String> outbreakUuids = executeUuidCall(RetroProvider.getOutbreakFacade().pullUuids());
        DatabaseHelper.getOutbreakDao().deleteInvalid(outbreakUuids);

        // order is important, due to dependencies (e.g. case & person)

        new PersonDtoHelper().pullMissing(personUuids);
        new CaseDtoHelper().pullMissing(caseUuids);
        new EventDtoHelper().pullMissing(eventUuids);
        new EventParticipantDtoHelper().pullMissing(eventParticipantUuids);
        new SampleDtoHelper().pullMissing(sampleUuids);
        new SampleTestDtoHelper().pullMissing(sampleTestUuids);
        new ContactDtoHelper().pullMissing(contactUuids);
        new VisitDtoHelper().pullMissing(visitUuids);
        new TaskDtoHelper().pullMissing(taskUuids);
        new WeeklyReportDtoHelper().pullMissing(weeklyReportUuids);
        new WeeklyReportEntryDtoHelper().pullMissing(weeklyReportEntryUuids);
    }

    private void pullMissingAndDeleteInvalidInfrastructure() throws ServerConnectionException, ServerCommunicationException, DaoException {
        // ATTENTION: Since we are working with UUID lists we have no type safety. Look for typos!

        Log.d(SynchronizeDataAsync.class.getSimpleName(), "pullMissingAndDeleteInvalidInfrastructure");

        // users
        List<String> userUuids = executeUuidCall(RetroProvider.getUserFacade().pullUuids());
        DatabaseHelper.getUserDao().deleteInvalid(userUuids);
        // facilities
        List<String> facilityUuids = executeUuidCall(RetroProvider.getFacilityFacade().pullUuids());
        DatabaseHelper.getFacilityDao().deleteInvalid(facilityUuids);
        // communities
        List<String> communityUuids = executeUuidCall(RetroProvider.getCommunityFacade().pullUuids());
        DatabaseHelper.getCommunityDao().deleteInvalid(communityUuids);
        // districts
        List<String> districtUuids = executeUuidCall(RetroProvider.getDistrictFacade().pullUuids());
        DatabaseHelper.getDistrictDao().deleteInvalid(districtUuids);
        // regions
        List<String> regionUuids = executeUuidCall(RetroProvider.getRegionFacade().pullUuids());
        DatabaseHelper.getRegionDao().deleteInvalid(regionUuids);

        // order is important, due to dependencies

        new RegionDtoHelper().pullMissing(regionUuids);
        new DistrictDtoHelper().pullMissing(districtUuids);
        new CommunityDtoHelper().pullMissing(communityUuids);
        // facilities need special handling
        FacilityDtoHelper facilityDtoHelper = new FacilityDtoHelper();
        if (facilityDtoHelper.isAnyMissing(facilityUuids)) {
            facilityDtoHelper.repullEntities();
        }
        new UserDtoHelper().pullMissing(userUuids);
    }

    private List<String> executeUuidCall(Call<List<String>> call) throws ServerConnectionException, ServerCommunicationException {
        Response<List<String>> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
        if (!response.isSuccessful()) {
            RetroProvider.throwException(response);
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
    public static ProgressDialog callWithProgressDialog(SyncMode syncMode, final Context context, final SyncCallback callback) {
        final ProgressDialog progressDialog = ProgressDialog.show(context, context.getString(R.string.headline_synchronization),
                context.getString(R.string.hint_synchronization), true);

        call(syncMode, context, new SyncCallback() {
            @Override
            public void call(boolean syncFailed, String syncFailedMessage) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (callback != null) {
                    callback.call(syncFailed, syncFailedMessage);
                }
            }
        });

        return progressDialog;
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

    private void pullDiseaseClassification() throws DaoException, ServerConnectionException, ServerCommunicationException {
        Call<List<DiseaseClassificationCriteria>> classificationCriteriaCall = RetroProvider.getClassificationFacade().pullAllClassificationCriteria();

        if (classificationCriteriaCall != null) {
            Response<List<DiseaseClassificationCriteria>> response;
            try {
                response = classificationCriteriaCall.execute();
            } catch (IOException e) {
                throw new ServerCommunicationException(e);
            }

            if (!response.isSuccessful()) {
                RetroProvider.throwException(response);
            }

            DiseaseClassificationDao dao = DatabaseHelper.getDiseaseClassificationDao();
            final List<DiseaseClassificationCriteria> result = response.body();
            if (result != null && result.size() > 0) {
                dao.callBatchTasks(new Callable<Void>() {
                    public Void call() throws Exception {
                        for (DiseaseClassificationCriteria criteria : result) {
                            DiseaseClassificationAppHelper.saveClassificationToDatabase(criteria);
                        }
                        return null;
                    }
                });

                Log.d(dao.getTableName(), "Pulled and saved " + result.size());
            }
        }
    }

    public enum SyncMode {
        Changes,
        Complete,
        /**
         * Also repulls all non-infrastructure data and users
         * Use to handle conflict states resulting out of bugs
         */
        CompleteAndRepull,
    }
}