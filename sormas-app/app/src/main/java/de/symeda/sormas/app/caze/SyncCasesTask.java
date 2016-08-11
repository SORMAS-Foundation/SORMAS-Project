package de.symeda.sormas.app.caze;

/**
 * Created by Stefan Szczesny on 21.07.2016.
 */

import android.os.AsyncTask;

import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.rest.DtoFacadeRetro;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncCasesTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = SyncCasesTask.class.getSimpleName();
    protected static Logger logger = LoggerFactory.getLogger(SyncCasesTask.class);


    @Override
    protected Void doInBackground(Void... params) {

        new CaseDtoHelper().syncEntities(new DtoFacadeRetro<CaseDataDto>() {
            @Override
            public Call<List<CaseDataDto>> getAll(long since) {
                User user = ConfigProvider.getUser();
                return RetroProvider.getCaseFacade().getAll(user.getUuid(), since);
            }
        }, DatabaseHelper.getCaseDao());
        return null;
    }

    protected void onPostExecute(Integer result) {

    }

}