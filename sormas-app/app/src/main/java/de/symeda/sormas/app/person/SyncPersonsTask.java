package de.symeda.sormas.app.person;

import android.os.AsyncTask;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.io.IOException;
import java.util.List;

import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.rest.PersonFacadeRetro;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncPersonsTask extends AsyncTask<Void, Void, Integer> {

    private OrmLiteBaseActivity context;

    public SyncPersonsTask(OrmLiteBaseActivity context) {
        this.context = context;
    }

    @Override
    protected Integer doInBackground(Void... params) {

        // 10.0.2.2 points to localhost from emulator
        // SSL not working because of missing certificate
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/sormas-rest/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        List<PersonDto> result = null;

        PersonFacadeRetro personFacade = retrofit.create(PersonFacadeRetro.class);
        Call<List<PersonDto>> personsCall = personFacade.getAllPersons();
        try {
            result = personsCall.execute().body();

        } catch (IOException e) {
            // TODO proper exception handling/logging
            e.printStackTrace();
        }

        RuntimeExceptionDao<Person, Long> dao = ((DatabaseHelper)context.getHelper()).getSimplePersonDao();

        for (PersonDto dto : result) {
            Person person = new Person();
            // todo copy data with helper
            person.setUuid(dto.getUuid());
            person.setFirstName(dto.getFirstName());
            person.setLastName(dto.getLastName());
            dao.createOrUpdate(person);
        }

        if (result != null)
            return result.size();

        return null;
    }

    protected void onPostExecute(Integer result) {

    }
}