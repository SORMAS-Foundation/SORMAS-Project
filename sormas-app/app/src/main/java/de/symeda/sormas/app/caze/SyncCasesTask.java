package de.symeda.sormas.app.caze;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;

import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoGetInterface;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.person.SyncPersonsTask;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.sample.SyncSamplesTask;
import de.symeda.sormas.app.util.Callback;
import retrofit2.Call;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncCasesTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = SyncCasesTask.class.getSimpleName();
    protected static Logger logger = LoggerFactory.getLogger(SyncCasesTask.class);

    private SyncCasesTask() {

    }

    @Override
    protected Void doInBackground(Void... params) {

        new CaseDtoHelper().pullEntities(new DtoGetInterface<CaseDataDto>() {
            @Override
            public Call<List<CaseDataDto>> getAll(long since) {
                User user = ConfigProvider.getUser();
                if (user != null) {
                    return RetroProvider.getCaseFacade().getAll(user.getUuid(), since);
                }
                return null;
            }
        }, DatabaseHelper.getCaseDao());

        new CaseDtoHelper().pushEntities(new AdoDtoHelper.DtoPostInterface<CaseDataDto>() {
            @Override
            public Call<Long> postAll(List<CaseDataDto> dtos) {
                return RetroProvider.getCaseFacade().postAll(dtos);
            }
        }, DatabaseHelper.getCaseDao());

        return null;
    }

    public static void syncCases(final FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            syncCases(new Callback() {
                @Override
                public void call() {
                    if (fragmentManager.getFragments() != null) {
                        for (Fragment fragement : fragmentManager.getFragments()) {
                            if (fragement instanceof CasesListFragment) {
                                fragement.onResume();
                            }
                        }
                    }
                }
            });
        } else {
            syncCases((Callback)null);
        }
    }

    public static void syncCases(final FragmentManager fragmentManager, SwipeRefreshLayout refreshLayout) {
        syncCases(fragmentManager);
        refreshLayout.setRefreshing(false);
    }

    public static void syncCases(final Callback callback) {
        SyncPersonsTask.syncPersons(new Callback() {
                                        @Override
                                        public void call() {
                                            syncCasesWithoutDependencies(callback);
                                        }
                                    });
    }

    public static AsyncTask<Void, Void, Void> syncCasesWithoutDependencies(final Callback callback) {
        return new SyncCasesTask() {
            @Override
            protected void onPostExecute(Void aVoid) {
                SyncSamplesTask.syncSamples(callback);
            }
        }.execute();
    }
}