package de.symeda.sormas.app.event.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.event.EventSection;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

public class EventNewActivity extends BaseEditActivity<Event> {

    public static final String TAG = EventNewActivity.class.getSimpleName();

    private AsyncTask saveTask;

    public static void startActivity(Context fromActivity) {
        BaseEditActivity.startActivity(fromActivity, EventNewActivity.class, buildBundle(null));
    }

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
        return null;
    }

    @Override
    protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Event activityRootData) {
        return EventEditFragment.newInstance(activityRootData);
    }

    @Override
    public void replaceFragment(BaseEditFragment f, boolean allowBackNavigation) {
        super.replaceFragment(f, allowBackNavigation);
        getActiveFragment().setLiveValidationDisabled(true);
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_event_new;
    }

    @Override
    public void saveData() {
        final Event eventToSave = (Event) getActiveFragment().getPrimaryData();
        EventEditFragment fragment = (EventEditFragment) getActiveFragment();

        if (fragment.isLiveValidationDisabled()) {
            fragment.disableLiveValidation(false);
        }

        try {
            FragmentValidator.validate(getContext(), fragment.getContentBinding());
        } catch (ValidationException e) {
            NotificationHelper.showNotification(this, ERROR, e.getMessage());
            return;
        }

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
                    finish();
                    EventEditActivity.startActivity(getContext(), eventToSave.getUuid(), EventSection.EVENT_PARTICIPANTS);
                }
            }
        }.executeOnThreadPool();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }
}
