package de.symeda.sormas.app.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.List;

import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoGetInterface;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoPostInterface;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.task.TaskDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.caze.CasesListFragment;
import de.symeda.sormas.app.caze.SyncCasesTask;
import de.symeda.sormas.app.contact.SyncContactsTask;
import de.symeda.sormas.app.person.SyncPersonsTask;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.util.Callback;
import retrofit2.Call;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncTasksTask extends AsyncTask<Void, Void, Void> {

    private SyncTasksTask() {
    }

    @Override
    protected Void doInBackground(Void... params) {

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
            public Call<Integer> postAll(List<TaskDto> dtos) {
                // TODO postAll should return the date&time the server used as modifiedDate
                return RetroProvider.getTaskFacade().postAll(dtos);
            }
        }, DatabaseHelper.getTaskDao());

        return null;
    }

    public static void syncTasks(final FragmentManager fragmentManager, final Context notificationContext) {
        if (fragmentManager != null) {
            syncTasks(new Callback() {
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
            syncTasks((Callback)null, notificationContext);
        }
    }

    public static void syncTasks(final FragmentManager fragmentManager, final Context notificationContext, SwipeRefreshLayout refreshLayout) {
        syncTasks(fragmentManager, notificationContext);
        refreshLayout.setRefreshing(false);
    }

    public static void syncTasks(final Callback callback, final Context notificationContext) {
        SyncCasesTask.syncCases(new Callback() {
            @Override
            public void call() {
                SyncContactsTask.syncContacts(new Callback() {
                    @Override
                    public void call() {
                        syncTasksWithoutDependencies(callback, notificationContext);
                    }
                });
            }
        });
    }

    public static void syncTasksWithoutDependencies(final Callback callback, final Context notificationContext) {
        new SyncTasksTask() {
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