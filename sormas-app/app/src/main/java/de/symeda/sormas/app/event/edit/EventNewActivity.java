package de.symeda.sormas.app.event.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.event.EventSection;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;

public class EventNewActivity extends BaseEditActivity<Event> {

    public static final String TAG = EventNewActivity.class.getSimpleName();

    private AsyncTask saveTask;

    @Override
    protected Event queryRootEntity(String recordUuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Event buildRootEntity() {
        Event event = DatabaseHelper.getEventDao().build();
        return event;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_event);
        return result;
    }

    @Override
    public EventStatus getPageStatus() {
        return (EventStatus) super.getPageStatus();
    }

    @Override
    protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Event activityRootData) {
        EventFormNavigationCapsule dataCapsule = new EventFormNavigationCapsule(EventNewActivity.this,
                getRootEntityUuid(), getPageStatus());
        return EventEditFragment.newInstance(dataCapsule, activityRootData);
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_event_new;
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
                    EventEditActivity.goToActivity(EventNewActivity.this,
                            new EventFormNavigationCapsule(EventNewActivity.this, eventToSave.getUuid(), eventToSave.getEventStatus())
                            .setActiveMenu(EventSection.EVENT_PERSONS.ordinal()));
                }
            }
        }.executeOnThreadPool();
    }

    public static <TActivity extends BaseActivity> void goToActivity(Context fromActivity) {
        BaseEditActivity.goToActivity(fromActivity, EventNewActivity.class, new EventFormNavigationCapsule(fromActivity, null));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }
}
