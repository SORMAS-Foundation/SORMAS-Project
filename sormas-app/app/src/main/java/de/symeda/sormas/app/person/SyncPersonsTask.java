package de.symeda.sormas.app.person;

import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.person.PersonDtoHelper;
import de.symeda.sormas.app.rest.DtoFacadeRetro;
import de.symeda.sormas.app.rest.PersonFacadeRetro;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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