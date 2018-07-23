package de.symeda.sormas.app.event.edit.eventparticipant;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.shared.EventParticipantFormNavigationCapsule;
import de.symeda.sormas.app.validation.FragmentValidator;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;


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
    protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, EventParticipant activityRootData) {
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
        EventParticipantEditFragment fragment = (EventParticipantEditFragment) getActiveFragment();

        try {
            FragmentValidator.validate(getContext(), fragment.getContentBinding());
        } catch (ValidationException e) {
            NotificationHelper.showNotification((NotificationContext) getContext(), ERROR, e.getMessage());
            return;
        }

        saveTask = new SavingAsyncTask(getRootView(), eventParticipant) {

            @Override
            protected void onPreExecute() {
                showPreloader();
            }

            @Override
            public void doInBackground(TaskResultHolder resultHolder) throws Exception, ValidationException {
                validateData(eventParticipant);
                DatabaseHelper.getPersonDao().saveAndSnapshot(eventParticipant.getPerson());
                DatabaseHelper.getEventParticipantDao().saveAndSnapshot(eventParticipant);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                hidePreloader();
                super.onPostExecute(taskResult);
                if (taskResult.getResultStatus().isSuccess()) {
                    finish();
                }
            }
        }.executeOnThreadPool();
    }

    private void validateData(EventParticipant data) throws ValidationException {

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