package de.symeda.sormas.app.contact;

import android.os.AsyncTask;

import java.util.List;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoGetInterface;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoPostInterface;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.ContactDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.util.Callback;
import retrofit2.Call;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncContactsTask extends AsyncTask<Void, Void, Void> {

    public SyncContactsTask() {
    }

    @Override
    protected Void doInBackground(Void... params) {

        new ContactDtoHelper().pullEntities(new DtoGetInterface<ContactDto>() {
            @Override
            public Call<List<ContactDto>> getAll(long since) {

                User user = ConfigProvider.getUser();
                if (user != null) {
                    Call<List<ContactDto>> all = RetroProvider.getContactFacade().getAll(user.getUuid(), since);
                    return all;
                }
                return null;
            }
        }, DatabaseHelper.getContactDao());

        new ContactDtoHelper().pushEntities(new DtoPostInterface<ContactDto>() {
            @Override
            public Call<Integer> postAll(List<ContactDto> dtos) {
                // TODO postAll should return the date&time the server used as modifiedDate
                return RetroProvider.getContactFacade().postAll(dtos);
            }
        }, DatabaseHelper.getContactDao());

        return null;
    }

    public static void syncContacts(final Callback callback) {
        new SyncContactsTask() {
            @Override
            protected void onPostExecute(Void aVoid) {
                if (callback != null) {
                    callback.call();
                }
            }
        }.execute();
    }
}