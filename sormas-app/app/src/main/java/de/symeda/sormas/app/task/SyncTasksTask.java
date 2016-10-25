package de.symeda.sormas.app.task;

import android.os.AsyncTask;

import java.util.List;

import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoGetInterface;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoPostInterface;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.task.TaskDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncTasksTask extends AsyncTask<Void, Void, Void> {

    public SyncTasksTask() {
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
}