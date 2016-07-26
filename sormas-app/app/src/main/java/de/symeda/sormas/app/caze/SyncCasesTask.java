package de.symeda.sormas.app.caze;

/**
 * Created by Stefan Szczesny on 21.07.2016.
 */

import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.rest.CaseFacadeRetro;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncCasesTask extends AsyncTask<Void, Void, List<CaseDataDto>> {

    private static final String TAG = SyncCasesTask.class.getSimpleName();

    @Override
    protected List<CaseDataDto> doInBackground(Void... params) {

        List<CaseDataDto> result = null;

        CaseDao caseDao = DatabaseHelper.getCaseDao();
        Date maxModifiedDate = caseDao.getLatestChangeDate();

        Log.v(TAG, "maxModifiedDate=" + maxModifiedDate);
        Log.v(TAG, "maxModifiedDate=" + maxModifiedDate.getTime());

        // 10.0.2.2 points to localhost from emulator
        // SSL not working because of missing certificate
        Retrofit retrofit = new Retrofit.Builder()
                //.baseUrl("http://wahnschaffe.symeda:8080/sormas-rest/")
                .baseUrl("http://szczesny.symeda:8080/sormas-rest/")
                //.baseUrl("http://10.0.2.2:8080/sormas-rest/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CaseFacadeRetro caseFacade = retrofit.create(CaseFacadeRetro.class);
        Call<List<CaseDataDto>> cazesCall = caseFacade.getAllCases(maxModifiedDate != null ? maxModifiedDate.getTime() : 0);
        try {
            result = cazesCall.execute().body();
        } catch (IOException e) {
            // TODO proper exception handling/logging
            e.printStackTrace();
        }

        Log.v(TAG, "cases=" + result);

        if (result != null) {
            PersonDao personDao = DatabaseHelper.getPersonDao();

            for (CaseDataDto dto : result) {

                Case caze = caseDao.queryUuid(dto.getUuid());
                if (caze == null) {
                    caze = new Case();
                    caze.setCreationDate(dto.getCreationDate());
                    caze.setUuid(dto.getUuid());

                    Person person = personDao.queryUuid(dto.getPerson().getUuid());
                    caze.setPerson(person);
                }

                caze.setChangeDate(dto.getChangeDate());

                caze.setDisease(dto.getDisease());
                caze.setCaseStatus(dto.getCaseStatus());

                caseDao.createOrUpdate(caze);
            }

            return result;
        }
        return null;
    }

    protected void onPostExecute(Integer result) {

    }

}