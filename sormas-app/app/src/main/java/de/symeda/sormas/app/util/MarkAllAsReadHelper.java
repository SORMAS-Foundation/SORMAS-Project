package de.symeda.sormas.app.util;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import java.util.List;

import de.symeda.sormas.app.AbstractSormasActivity;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.caze.list.CaseListFragment;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.TaskResultHolder;

public class MarkAllAsReadHelper {

    public static AsyncTask markCasesAsRead(final AbstractSormasActivity activity, final Callback.IAction callback) {
        DefaultAsyncTask executor = new DefaultAsyncTask(activity.getContext()) {
            @Override
            public void execute(TaskResultHolder resultHolder) {
                CaseDao caseDao = DatabaseHelper.getCaseDao();
                List<Case> cases = caseDao.queryForAll();
                for (Case caseToMark : cases) {
                    caseDao.markAsRead(caseToMark);
                }
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                for (Fragment fragment : activity.getSupportFragmentManager().getFragments()) {
                    if (fragment instanceof CaseListFragment) {
                        fragment.onResume();
                    }
                }

                if (callback != null)
                    callback.call(null);
            }
        };
        return executor.executeOnThreadPool();
    }
}
