package de.symeda.sormas.app.person;

import android.os.AsyncTask;

import java.util.List;

import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.PersonDtoHelper;
import de.symeda.sormas.app.rest.DtoFacadeRetro;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncPersonsTask extends AsyncTask<Void, Void, Void> {

    public SyncPersonsTask() {
    }

    @Override
    protected Void doInBackground(Void... params) {

        new PersonDtoHelper().syncEntities(new DtoFacadeRetro<PersonDto>() {
            @Override
            public Call<List<PersonDto>> getAll(long since) {
                return RetroProvider.getPersonFacade().getAll(since);
            }
        }, DatabaseHelper.getPersonDao());

        return null;
    }

    protected void onPostExecute(Integer result) {

    }
}