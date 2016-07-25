package de.symeda.sormas.app.person;

import android.os.AsyncTask;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.rest.PersonFacadeRetro;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncPersonsTask extends AsyncTask<Void, Void, Integer> {

    public SyncPersonsTask() {
    }

    @Override
    protected Integer doInBackground(Void... params) {

        List<PersonDto> result = null;

        PersonDao personDao = DatabaseHelper.getPersonDao();
        Date maxModifiedDate = personDao.getLatestChangeDate();

        // 10.0.2.2 points to localhost from emulator
        // SSL not working because of missing certificate
        Retrofit retrofit = new Retrofit.Builder()
                //.baseUrl("http://wahnschaffe.symeda:8080/sormas-rest/")
                .baseUrl("http://szczesny.symeda:8080/sormas-rest/")
                //.baseUrl("http://10.0.2.2:8080/sormas-rest/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PersonFacadeRetro personFacade = retrofit.create(PersonFacadeRetro.class);
        Call<List<PersonDto>> personsCall = personFacade.getAllPersons(maxModifiedDate != null ? maxModifiedDate.getTime() : 0);

        try {
            result = personsCall.execute().body();

        } catch (IOException e) {
            // TODO proper exception handling/logging
            e.printStackTrace();
        }

        if (result != null) {

            for (PersonDto dto : result) {

                Person person = personDao.queryUuid(dto.getUuid());
                if (person == null) {
                    person = new Person();
                    person.setCreationDate(dto.getCreationDate());
                    person.setUuid(dto.getUuid());
                }

                person.setChangeDate(dto.getChangeDate());

                // todo copy data with some helper
                person.setFirstName(dto.getFirstName());
                person.setLastName(dto.getLastName());
                personDao.createOrUpdate(person);
            }

            return result.size();
        }
        return null;
    }

    protected void onPostExecute(Integer result) {

    }
}