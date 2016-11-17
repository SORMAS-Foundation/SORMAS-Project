package de.symeda.sormas.app.user;

import android.os.AsyncTask;

import java.util.List;

import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoGetInterface;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncUsersTask extends AsyncTask<Void, Void, Void> {

    public SyncUsersTask() {
    }

    @Override
    protected Void doInBackground(Void... params) {

        new UserDtoHelper().pullEntities(new DtoGetInterface<UserDto>() {
            @Override
            public Call<List<UserDto>> getAll(long since) {
                return RetroProvider.getUserFacade().getAll(since);
            }
        }, DatabaseHelper.getUserDao());

        return null;
    }

    protected void onPostExecute(Integer result) {

    }
}