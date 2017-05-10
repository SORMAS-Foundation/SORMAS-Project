package de.symeda.sormas.app.person;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoGetInterface;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoPostInterface;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.person.PersonDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import retrofit2.Call;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncPersonsTask extends AsyncTask<Void, Void, Void> {

    private final Context context;
    private boolean hasThrownError;

    public SyncPersonsTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
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
        } catch (DaoException | SQLException | IOException e) {
            Log.e(getClass().getName(), "Error while synchronizing persons", e);
            SormasApplication application = (SormasApplication) context.getApplicationContext();
            Tracker tracker = application.getDefaultTracker();
            ErrorReportingHelper.sendCaughtException(tracker, this.getClass().getSimpleName(), e, null, true);
        }
        return null;
    }

    public static void syncPersons(Context context, final Callback callback) {
        new SyncPersonsTask(context) {
            @Override
            protected void onPostExecute(Void aVoid) {
                if (callback != null) {
                    callback.call();
                }
            }
        }.execute();
    }
}