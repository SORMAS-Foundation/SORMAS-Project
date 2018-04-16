package de.symeda.sormas.app.util;

import android.os.AsyncTask;
import android.util.Log;

import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;

/**
 * Created by Orson on 27/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TimeoutHelper {

    public static final String TAG = TimeoutHelper.class.getSimpleName();

    public static void executeIn5Seconds(final Callback.IAction<AsyncTask> callback) {
        executeIn(5000, callback);
    }

    public static void executeIn(final int milliSeconds, final Callback.IAction<AsyncTask> callback) {
        try {
            AsyncTask timeoutTask = null;
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                private PersonDao personDao;
                private String saveUnsuccessful;

                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    long endTimeMillis = System.currentTimeMillis() + milliSeconds;
                    while (true) {
                        // method logic
                        if (System.currentTimeMillis() > endTimeMillis) {
                            // do some clean-up
                            return;
                        }
                    }
                }
            });
            timeoutTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    callback.call(null);
                }
            });
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }
}
