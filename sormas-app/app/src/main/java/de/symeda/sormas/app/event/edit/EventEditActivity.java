package de.symeda.sormas.app.event.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.event.EventSection;
import de.symeda.sormas.app.event.edit.sub.EventParticipantNewActivity;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;

public class EventEditActivity extends BaseEditActivity<Event> {

    public static final String TAG = EventEditActivity.class.getSimpleName();

    private AsyncTask saveTask;

    @Override
    protected Event queryRootEntity(String recordUuid) {
        return DatabaseHelper.getEventDao().queryUuidWithEmbedded(recordUuid);
    }

    @Override
    protected Event buildRootEntity() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getPageMenuData() {
        return R.xml.data_form_page_alert_menu;
    }

    @Override
    public EventStatus getPageStatus() {
        return (EventStatus) super.getPageStatus();
    }

    @Override
    protected BaseEditFragment buildEditFragment(LandingPageMenuItem menuItem, Event activityRootData) {
        EventFormNavigationCapsule dataCapsule = new EventFormNavigationCapsule(EventEditActivity.this,
                getRootEntityUuid(), getPageStatus());

        EventSection section = EventSection.fromMenuKey(menuItem.getKey());
        BaseEditFragment fragment;
        switch (section) {
            case EVENT_INFO:
                fragment = EventEditFragment.newInstance(dataCapsule, activityRootData);
                break;
            case EVENT_PERSONS:
                fragment = EventEditPersonsInvolvedListFragment.newInstance(dataCapsule, activityRootData);
                break;
            case TASKS:
                fragment = EventEditTaskListFragement.newInstance(dataCapsule, activityRootData);
                break;
            default:
                throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
        }
        return fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_event);
        return result;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level4_event_edit;
    }

    @Override
    public void goToNewView() {

        EventSection section = EventSection.fromMenuKey(getActiveMenuItem().getKey());
        switch (section) {
            case EVENT_PERSONS:

                EventFormNavigationCapsule dataCapsule = new EventFormNavigationCapsule(getContext(), getPageStatus())
                        .setEventUuid(getRootEntityUuid());
                EventParticipantNewActivity.goToActivity(this, dataCapsule);
                break;
            default:
                throw new IllegalArgumentException(DataHelper.toStringNullable(section));
        }
    }

    @Override
    public void saveData() {

        final Event eventToSave = (Event) getActiveFragment().getPrimaryData();

        saveTask = new SavingAsyncTask(getRootView(), eventToSave) {

            @Override
            protected void onPreExecute() {
                showPreloader();
            }

            @Override
            public void doInBackground(TaskResultHolder resultHolder) throws DaoException {
                DatabaseHelper.getEventDao().saveAndSnapshot(eventToSave);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                hidePreloader();
                super.onPostExecute(taskResult);
                if (taskResult.getResultStatus().isSuccess()) {
                    goToNextMenu();
                }

            }
        }.executeOnThreadPool();
    }

    private void validateData(Event data) throws ValidationException {
        // TODO validation
//        EventValidator.clearErrorsForEventData(getContentBinding());
//        if (!EventValidator.validateEventData(nContext, eventToSave, getContentBinding())) {
//            return;
//        }
    }

    public static <TActivity extends BaseActivity> void goToActivity(Context fromActivity, EventFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, EventEditActivity.class, dataCapsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }

}