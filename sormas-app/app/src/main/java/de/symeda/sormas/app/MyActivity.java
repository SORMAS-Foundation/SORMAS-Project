package de.symeda.sormas.app;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.rest.CaseFacadeRetro;
import de.symeda.sormas.app.rest.PersonFacadeRetro;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyActivity extends OrmLiteBaseActivity<DatabaseHelper> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        try {
            // todo asynchronous calls: Cases have to wait for Persons
            Integer syncedPersons = new SyncPersonsTask().execute().get();
            Integer syncedCases = new SyncCasesTask().execute().get();
            new AlertDialog.Builder(this).setTitle("synced: " + syncedCases).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
     */
    private class SyncPersonsTask extends AsyncTask<Void, Void, Integer> {
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

            RuntimeExceptionDao<Person, Integer> dao = getHelper().getSimplePersonDao();

            // temp: delete old persons
            List<Person> oldEntities = dao.queryForAll();
            dao.delete(oldEntities);

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

    /**
     * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
     */
    private class SyncCasesTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {

            // 10.0.2.2 points to localhost from emulator
            // SSL not working because of missing certificate
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080/sormas-rest/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            List<CaseDataDto> result = null;

            CaseFacadeRetro caseFacade = retrofit.create(CaseFacadeRetro.class);
            Call<List<CaseDataDto>> cazesCall = caseFacade.getAllCases();
            try {
                result = cazesCall.execute().body();

            } catch (IOException e) {
                // TODO proper exception handling/logging
                e.printStackTrace();
            }

            RuntimeExceptionDao<Case, Integer> caseDao = getHelper().getSimpleCaseDao();
            RuntimeExceptionDao<Person, Integer> personDao = getHelper().getSimplePersonDao();

            // temp: delete old cases
            List<Case> oldEntities = caseDao.queryForAll();
            caseDao.delete(oldEntities);

            for (CaseDataDto dto : result) {
                Case caze = new Case();
                caze.setUuid(dto.getUuid());

                List<Person> matchingPersons = personDao.queryForEq(Person.UUID, dto.getPerson().getUuid());
                caze.setPerson(matchingPersons.get(0));

                caze.setDisease(dto.getDisease());
                caze.setCaseStatus(dto.getCaseStatus());

                caseDao.createOrUpdate(caze);
            }

            if (result != null)
                return result.size();
            return null;
        }

        protected void onPostExecute(Integer result) {

        }
    }
}
