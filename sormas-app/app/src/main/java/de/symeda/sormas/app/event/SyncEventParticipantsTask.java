package de.symeda.sormas.app.event;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.List;

import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoGetInterface;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoPostInterface;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.event.EventParticipantDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.person.SyncPersonsTask;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.util.Callback;
import retrofit2.Call;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncEventParticipantsTask extends AsyncTask<Void, Void, Void> {

    public SyncEventParticipantsTask() {
    }

    @Override
    protected Void doInBackground(Void... params) {

        new EventParticipantDtoHelper().pullEntities(new DtoGetInterface<EventParticipantDto>() {
            @Override
            public Call<List<EventParticipantDto>> getAll(long since) {

                User user = ConfigProvider.getUser();
                if (user != null) {
                    Call<List<EventParticipantDto>> all = RetroProvider.getEventParticipantFacade().getAll(user.getUuid(), since);
                    return all;
                }
                return null;
            }
        }, DatabaseHelper.getEventParticipantDao());

        new EventParticipantDtoHelper().pushEntities(new DtoPostInterface<EventParticipantDto>() {
            @Override
            public Call<Long> postAll(List<EventParticipantDto> dtos) {
                // TODO postAll should return the date&time the server used as modifiedDate
                return RetroProvider.getEventParticipantFacade().postAll(dtos);
            }
        }, DatabaseHelper.getEventParticipantDao());

        return null;
    }

    public static void syncEventParticipants(final FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            syncEventParticipants(new Callback() {
                @Override
                public void call() {
                    if (fragmentManager.getFragments() != null) {
                        for (Fragment fragement : fragmentManager.getFragments()) {
                            if (fragement instanceof EventParticipantsListFragment) {
                                fragement.onResume();
                            }
                        }
                    }
                }
            });
        } else {
            syncEventParticipants((Callback)null);
        }
    }

    public static void syncEventParticipants(final FragmentManager fragmentManager, SwipeRefreshLayout refreshLayout) {
        syncEventParticipants(fragmentManager);
        refreshLayout.setRefreshing(false);
    }

    public static void syncEventParticipants(final Callback callback, final SwipeRefreshLayout refreshLayout) {
        syncEventParticipants(callback);
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
    }

    public static void syncEventParticipants(final Callback callback) {
        SyncPersonsTask.syncPersons(new Callback(){
            @Override
            public void call(){
                new SyncEventParticipantsTask(){
                    @Override
                    protected void onPostExecute(Void aVoid){
                        if(callback!=null){
                            callback.call();
                        }
                    }
                }.execute();
             }
        });
    }
}