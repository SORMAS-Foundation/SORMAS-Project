package de.symeda.sormas.app.task;

import android.os.AsyncTask;

import java.util.List;

import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoGetInterface;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoPostInterface;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.visit.VisitDtoHelper;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.util.Callback;
import retrofit2.Call;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncVisitsTask extends AsyncTask<Void, Void, Void> {

    public SyncVisitsTask() {
    }

    @Override
    protected Void doInBackground(Void... params) {

        new VisitDtoHelper().pullEntities(new DtoGetInterface<VisitDto>() {
            @Override
            public Call<List<VisitDto>> getAll(long since) {

                User user = ConfigProvider.getUser();
                if (user != null) {
                    Call<List<VisitDto>> all = RetroProvider.getVisitFacade().getAll(user.getUuid(), since);
                    return all;
                }
                return null;
            }
        }, DatabaseHelper.getVisitDao());

        new VisitDtoHelper().pushEntities(new DtoPostInterface<VisitDto>() {
            @Override
            public Call<Integer> postAll(List<VisitDto> dtos) {
                // TODO postAll should return the date&time the server used as modifiedDate
                return RetroProvider.getVisitFacade().postAll(dtos);
            }
        }, DatabaseHelper.getVisitDao());

        return null;
    }

    public static void syncVisits(final Callback callback) {
        new SyncVisitsTask() {
            @Override
            protected void onPostExecute(Void aVoid) {
                if (callback != null) {
                    callback.call();
                }
            }
        }.execute();
    }
}