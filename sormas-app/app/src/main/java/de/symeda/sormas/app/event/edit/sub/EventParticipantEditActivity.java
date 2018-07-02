package de.symeda.sormas.app.event.edit.sub;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.shared.EventParticipantFormNavigationCapsule;
import de.symeda.sormas.app.util.MenuOptionsHelper;


public class EventParticipantEditActivity extends BaseEditActivity<EventParticipant> {

    private AsyncTask saveTask;

    @Override
    public EventStatus getPageStatus() {
        return (EventStatus) super.getPageStatus();
    }

    @Override
    protected EventParticipant queryRootEntity(String recordUuid) {
        return DatabaseHelper.getEventParticipantDao().queryUuid(recordUuid);
    }

    @Override
    protected EventParticipant buildRootEntity() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_participant);
        return result;
    }

    @Override
    protected BaseEditFragment buildEditFragment(LandingPageMenuItem menuItem, EventParticipant activityRootData) {
        EventParticipantFormNavigationCapsule dataCapsule = new EventParticipantFormNavigationCapsule(
                EventParticipantEditActivity.this, getRootEntityUuid());
        return EventParticipantEditFragment.newInstance(dataCapsule, activityRootData);
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level4_event_edit;
    }

    @Override
    public void saveData() {

        final EventParticipant eventParticipant = (EventParticipant) getActiveFragment().getPrimaryData();

        //TODO: Validation
        /*EventParticipantFragmentLayoutBinding eventParticipantBinding = eventParticipantTab.getBinding();
        PersonEditFragmentLayoutBinding personBinding = personEditForm.getBinding();

        EventParticipantValidator.clearErrorsForEventParticipantData(eventParticipantBinding);
        PersonValidator.clearErrors(personBinding);

        boolean validationError = false;

        if (!PersonValidator.validatePersonData(person, personBinding)) {
            validationError = true;
        }
        if (!EventParticipantValidator.validateEventParticipantData(eventParticipant, eventParticipantBinding)) {
            validationError = true;
        }

        if (validationError) {
            return true;
        }*/

        saveTask = new DefaultAsyncTask(getContext(), eventParticipant) {

            @Override
            public void doInBackground(TaskResultHolder resultHolder) throws Exception {
                DatabaseHelper.getPersonDao().saveAndSnapshot(eventParticipant.getPerson());
                DatabaseHelper.getEventParticipantDao().saveAndSnapshot(eventParticipant);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {

                if (taskResult.getResultStatus().isFailed()) {
                    NotificationHelper.showNotification(EventParticipantEditActivity.this, NotificationType.ERROR,
                            String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_event_participant)));
                } else {
                    NotificationHelper.showNotification(EventParticipantEditActivity.this, NotificationType.SUCCESS,
                            String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_event_participant)));

                    finish();
                }
            }
        }.executeOnThreadPool();
    }

    public static void goToActivity(Context fromActivity, EventParticipantFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, EventParticipantEditActivity.class, dataCapsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }
}