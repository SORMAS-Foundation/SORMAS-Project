package de.symeda.sormas.app.util;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import java.util.List;

import de.symeda.sormas.app.AbstractSormasActivity;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.caze.list.CaseListFragment;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.ICallback;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;

/**
 * Created by Orson on 28/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class MarkAllAsReadHelper {

    public static void markCases(final AbstractSormasActivity activity, final ICallback<AsyncTask> callback) {
        try {
            AsyncTask markCasesAsync = null;
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {

                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    CaseDao caseDao = DatabaseHelper.getCaseDao();
                    List<Case> cases = caseDao.queryForAll();
                    for (Case caseToMark : cases) {
                        caseDao.markAsRead(caseToMark);
                    }
                }
            });
            final AsyncTask finalMarkCasesAsync = markCasesAsync;
            markCasesAsync = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    if (resultHolder == null){
                        return;
                    }

                    for (Fragment fragment : activity.getSupportFragmentManager().getFragments()) {
                        if (fragment instanceof CaseListFragment) {
                            fragment.onResume();
                        }
                    }

                    callback.result(finalMarkCasesAsync);
                }
            });
        } catch (Exception ex) {

        }
    }
}
