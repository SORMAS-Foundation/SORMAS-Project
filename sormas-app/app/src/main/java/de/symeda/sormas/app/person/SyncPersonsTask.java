package de.symeda.sormas.app.person;

import android.os.AsyncTask;

import java.util.List;

import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoGetInterface;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoPostInterface;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.person.PersonDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.util.Callback;
import retrofit2.Call;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncPersonsTask extends AsyncTask<Void, Void, Void> {

    public SyncPersonsTask() {
    }

    @Override
    protected Void doInBackground(Void... params) {

        new PersonDtoHelper().pullEntities(new DtoGetInterface<PersonDto>() {
            @Override
            public Call<List<PersonDto>> getAll(long since) {
                User user = ConfigProvider.getUser();
                if (user != null) {
                    return RetroProvider.getPersonFacade().getAll(user.getUuid(), since);
                }
                return null;
            }
        }, DatabaseHelper.getPersonDao());

        new PersonDtoHelper().pushEntities(new DtoPostInterface<PersonDto>() {
            @Override
            public Call<Long> postAll(List<PersonDto> dtos) {
                return RetroProvider.getPersonFacade().postAll(dtos);
            }
        }, DatabaseHelper.getPersonDao());

        return null;
    }

    public static void syncPersons(final Callback callback) {
        new SyncPersonsTask() {
            @Override
            protected void onPostExecute(Void aVoid) {
                if (callback != null) {
                    callback.call();
                }
            }
        }.execute();
    }
}