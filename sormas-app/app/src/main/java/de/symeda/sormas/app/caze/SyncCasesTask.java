package de.symeda.sormas.app.caze;

/**
 * Created by Stefan Szczesny on 21.07.2016.
 */

import android.os.AsyncTask;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.io.IOException;
import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.rest.CaseFacadeRetro;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncCasesTask extends AsyncTask<Void, Void, List<CaseDataDto>> {

    private OrmLiteBaseActivity context;

    public SyncCasesTask(OrmLiteBaseActivity context) {
        this.context = context;
    }

    @Override
    protected List<CaseDataDto> doInBackground(Void... params) {

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

        RuntimeExceptionDao<Case, Long> caseDao = ((DatabaseHelper)context.getHelper()).getSimpleCaseDao();
        RuntimeExceptionDao<Person, Long> personDao = ((DatabaseHelper)context.getHelper()).getSimplePersonDao();

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
            return result;
        return null;
    }

    protected void onPostExecute(Integer result) {

    }

}