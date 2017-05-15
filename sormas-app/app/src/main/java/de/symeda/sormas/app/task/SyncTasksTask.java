package de.symeda.sormas.app.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.google.android.gms.analytics.Tracker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoGetInterface;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoPostInterface;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.task.TaskDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.contact.SyncContactsTask;
import de.symeda.sormas.app.event.SyncEventsTask;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.SyncCallback;
import retrofit2.Call;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncTasksTask extends AsyncTask<Void, Void, Void> {

    /**
     * Should be set to true when the synchronization fails and reset to false as soon
     * as the last callback is called (i.e. the synchronization has been completed/cancelled).
     */
    protected boolean hasThrownError;
    private final Context context;

    private SyncTasksTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            new TaskDtoHelper().pullEntities(new DtoGetInterface<TaskDto>() {
                @Override
                public Call<List<TaskDto>> getAll(long since) {

                    User user = ConfigProvider.getUser();
                    if (user != null) {
                        Call<List<TaskDto>> all = RetroProvider.getTaskFacade().getAll(user.getUuid(), since);
                        return all;
                    }
                    return null;
                }
            }, DatabaseHelper.getTaskDao());

            new TaskDtoHelper().pushEntities(new DtoPostInterface<TaskDto>() {
                @Override
                public Call<Long> postAll(List<TaskDto> dtos) {
                    // TODO postAll should return the date&time the server used as modifiedDate
                    return RetroProvider.getTaskFacade().postAll(dtos);
                }
            }, DatabaseHelper.getTaskDao());
        } catch (DaoException | SQLException | IOException e) {
            hasThrownError = true;
            Log.e(getClass().getName(), "Error while synchronizing tasks", e);
            SormasApplication application = (SormasApplication) context.getApplicationContext();
            Tracker tracker = application.getDefaultTracker();
            ErrorReportingHelper.sendCaughtException(tracker, e, null, true);
        }
        return null;
    }

    public static void syncTasksWithoutCallback(final FragmentManager fragmentManager, Context context, final Context notificationContext) {
        if (fragmentManager != null) {
            syncTasks(context, new SyncCallback() {
                @Override
                public void call(boolean syncFailed) {
                    if (fragmentManager.getFragments() != null) {
                        for (Fragment fragment : fragmentManager.getFragments()) {
                            if (fragment instanceof TasksListFragment) {
                                fragment.onResume();
                            }
                        }
                    }
                }
            }, notificationContext);
        } else {
            syncTasks(context, null, notificationContext);
        }
    }

    public static void syncTasksWithCallback(final FragmentManager fragmentManager, Context context, final Context notificationContext, final SyncCallback callback) {
        if (fragmentManager != null) {
            syncTasks(context, new SyncCallback() {
                @Override
                public void call(boolean syncFailed) {
                    if (fragmentManager.getFragments() != null) {
                        for (Fragment fragment : fragmentManager.getFragments()) {
                            if (fragment instanceof TasksListFragment) {
                                fragment.onResume();
                            }
                        }
                    }
                    callback.call(syncFailed);
                }
            }, notificationContext);
        } else {
            syncTasks(context, callback, notificationContext);
        }
    }

    /**
     * Synchronizes the tasks, displays a progress dialog and an error message when the synchronization fails.
     * Should only be called when the user has manually triggered the synchronization.
     *
     * @param context
     * @param callback
     */
    public static void syncTasksWithProgressDialog(final Context context, final SyncCallback callback, final Context notificationContext) {
        final ProgressDialog progressDialog = ProgressDialog.show(context, "Case synchronization",
                "Cases are being synchronized...", true);

        syncTasks(context, new SyncCallback() {
            @Override
            public void call(boolean syncFailed) {
                progressDialog.dismiss();
                callback.call(syncFailed);
            }
        }, notificationContext);
    }

    public static void syncTasks(final Context context, final SyncCallback callback, final Context notificationContext) {
        // syncing contacts also syncs cases
        SyncContactsTask.syncContacts(context, new SyncCallback() {
            @Override
            public void call(boolean syncFailed) {
                if (!syncFailed) {
                    SyncEventsTask.createSyncEventsTask(context, new SyncCallback() {
                        @Override
                        public void call(boolean syncFailed) {
                            if (!syncFailed) {
                                createSyncTasksTask(context, callback, notificationContext);
                            } else {
                                if (callback != null) {
                                    callback.call(true);
                                }
                            }
                        }
                    });
                } else {
                    if (callback != null) {
                        callback.call(true);
                    }
                }
            }
        });
    }

    public static void createSyncTasksTask(Context context, final SyncCallback callback, final Context notificationContext) {
        new SyncTasksTask(context) {
            @Override
            protected void onPostExecute(Void aVoid) {
                if (callback != null) {
                    callback.call(this.hasThrownError);
                }
                this.hasThrownError = false;
                if (notificationContext != null) {
                    TaskNotificationService.doTaskNotification(notificationContext);
                }
            }
        }.execute();
    }
}