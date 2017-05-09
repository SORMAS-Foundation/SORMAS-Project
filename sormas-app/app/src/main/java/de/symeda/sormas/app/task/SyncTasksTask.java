package de.symeda.sormas.app.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

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
import retrofit2.Call;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncTasksTask extends AsyncTask<Void, Void, Void> {

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
            Log.e(getClass().getName(), "Error while synchronizing tasks", e);
            Toast.makeText(context, "Synchronization of tasks failed. Please try again.", Toast.LENGTH_LONG).show();
            SormasApplication application = (SormasApplication) context.getApplicationContext();
            Tracker tracker = application.getDefaultTracker();
            ErrorReportingHelper.sendCaughtException(tracker, this.getClass().getSimpleName(), e, null, true);
        }
        return null;
    }

    public static void syncTasks(final FragmentManager fragmentManager, Context context, final Context notificationContext) {
        if (fragmentManager != null) {
            syncTasks(context, new Callback() {
                @Override
                public void call() {
                    if (fragmentManager.getFragments() != null) {
                        for (Fragment fragement : fragmentManager.getFragments()) {
                            if (fragement instanceof TasksListFragment) {
                                fragement.onResume();
                            }
                        }
                    }
                }
            }, notificationContext);
        } else {
            syncTasks(context, (Callback)null, notificationContext);
        }
    }

    public static void syncTasks(final FragmentManager fragmentManager, Context context, final Context notificationContext, SwipeRefreshLayout refreshLayout) {
        syncTasks(fragmentManager, context, notificationContext);
        refreshLayout.setRefreshing(false);
    }

    public static void syncTasks(final Context context, final Callback callback, final Context notificationContext) {
        // syncing contacts also syncs cases
        SyncContactsTask.syncContacts(context, new Callback() {
            @Override
            public void call() {
                SyncEventsTask.syncEvents(context, new Callback() {
                    @Override
                    public void call() {
                        syncTasksWithoutDependencies(context, callback, notificationContext);
                    }
                });
            }
        });
    }

    public static void syncTasksWithoutDependencies(Context context, final Callback callback, final Context notificationContext) {
        new SyncTasksTask(context) {
            @Override
            protected void onPostExecute(Void aVoid) {
                if (callback != null) {
                    callback.call();
                }
                if (notificationContext != null) {
                    TaskNotificationService.doTaskNotification(notificationContext);
                }
            }
        }.execute();
    }
}