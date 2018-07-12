package de.symeda.sormas.app.event.edit.sub;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.dialog.SelectOrCreatePersonDialog;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;
import de.symeda.sormas.app.shared.EventParticipantFormNavigationCapsule;
import de.symeda.sormas.app.util.Consumer;

public class EventParticipantNewActivity extends BaseEditActivity<EventParticipant> {

    public static final String TAG = EventParticipantNewActivity.class.getSimpleName();

    private AsyncTask saveTask;

    private String eventUuid = null;

    @Override
    protected void onCreateInner(Bundle savedInstanceState) {
        super.onCreateInner(savedInstanceState);
        eventUuid = getEventUuidArg(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveEventUuidState(outState, eventUuid);
    }

    @Override
    protected EventParticipant queryRootEntity(String recordUuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected EventParticipant buildRootEntity() {
        Person person = DatabaseHelper.getPersonDao().build();
        EventParticipant eventParticipant = DatabaseHelper.getEventParticipantDao().build();
        eventParticipant.setPerson(person);
        return eventParticipant;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_event);
        return result;
    }

    @Override
    protected BaseEditFragment buildEditFragment(LandingPageMenuItem menuItem, EventParticipant activityRootData) {
        EventParticipantFormNavigationCapsule dataCapsule = new EventParticipantFormNavigationCapsule(EventParticipantNewActivity.this,
                getRootEntityUuid()).setEventUuid(eventUuid);
        return EventParticipantNewFragment.newInstance(dataCapsule, activityRootData);
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_person_involved_new;
    }

    @Override
    public EventStatus getPageStatus() {
        return (EventStatus) super.getPageStatus();
    }

    @Override
    public void saveData() {

        final EventParticipant eventParticipantToSave = (EventParticipant) getActiveFragment().getPrimaryData();

        SelectOrCreatePersonDialog.selectOrCreatePerson(eventParticipantToSave.getPerson(), new Consumer<Person>() {
            @Override
            public void accept(Person person) {
                eventParticipantToSave.setPerson(person);

                saveTask = new SavingAsyncTask(getRootView(), eventParticipantToSave) {
                    @Override
                    protected void onPreExecute() {
                        showPreloader();
                    }

                    @Override
                    protected void doInBackground(TaskResultHolder resultHolder) throws Exception {
                        DatabaseHelper.getPersonDao().saveAndSnapshot(eventParticipantToSave.getPerson());
                        final Event event = DatabaseHelper.getEventDao().queryUuid(eventUuid);
                        eventParticipantToSave.setEvent(event);
                        DatabaseHelper.getEventParticipantDao().saveAndSnapshot(eventParticipantToSave);
                    }

                    @Override
                    protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                        hidePreloader();
                        super.onPostExecute(taskResult);
                        if (taskResult.getResultStatus().isSuccess()) {
                            goToEventParticipantEditActivity();
                        }
                    }
                }.executeOnThreadPool();
            }
        });
    }

    private void goToEventParticipantEditActivity() {
        EventParticipantFormNavigationCapsule dataCapsule = new EventParticipantFormNavigationCapsule(this, getRootEntityUuid());
        EventParticipantEditActivity.goToActivity(this, dataCapsule);
    }

    public static <TActivity extends BaseActivity> void goToActivity(Context fromActivity, EventFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, EventParticipantNewActivity.class, dataCapsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }
}
