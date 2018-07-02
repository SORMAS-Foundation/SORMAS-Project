package de.symeda.sormas.app.event.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.event.EventSection;
import de.symeda.sormas.app.event.edit.sub.EventParticipantNewActivity;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;
import de.symeda.sormas.app.util.MenuOptionsHelper;

public class EventEditActivity extends BaseEditActivity<Event> {

    public static final String TAG = EventEditActivity.class.getSimpleName();

    private AsyncTask saveTask;

    @Override
    protected Event queryActivityRootEntity(String recordUuid) {
        return DatabaseHelper.getEventDao().queryUuid(recordUuid);
    }

    @Override
    protected Event buildActivityRootEntity() {
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
    protected BaseEditActivityFragment buildEditFragment(LandingPageMenuItem menuItem, Event activityRootData) {
        EventFormNavigationCapsule dataCapsule = new EventFormNavigationCapsule(EventEditActivity.this,
                getRootEntityUuid(), getPageStatus());

        EventSection section = EventSection.fromMenuKey(menuItem.getKey());
        BaseEditActivityFragment fragment;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!MenuOptionsHelper.handleEditModuleOptionsItemSelected(this, item))
            return super.onOptionsItemSelected(item);
        return true;
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

        // TODO validation
//        EventValidator.clearErrorsForEventData(getContentBinding());
//        if (!EventValidator.validateEventData(nContext, eventToSave, getContentBinding())) {
//            return;
//        }

        saveTask = new DefaultAsyncTask(getContext(), eventToSave) {

            @Override
            public void doInBackground(TaskResultHolder resultHolder) throws DaoException {
                DatabaseHelper.getEventDao().saveAndSnapshot(eventToSave);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {

                if (taskResult.getResultStatus().isFailed()) {
                    NotificationHelper.showNotification(EventEditActivity.this, NotificationType.ERROR,
                            String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_event)));
                } else {
                    NotificationHelper.showNotification(EventEditActivity.this, NotificationType.SUCCESS,
                            String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_event)));

                    goToNextMenu();
                }

            }
        }.executeOnThreadPool();
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