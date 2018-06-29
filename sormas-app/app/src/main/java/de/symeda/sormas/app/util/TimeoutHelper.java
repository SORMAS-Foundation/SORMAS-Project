package de.symeda.sormas.app.util;

import android.os.AsyncTask;

import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.TaskResultHolder;

public class TimeoutHelper {

    public static final String TAG = TimeoutHelper.class.getSimpleName();

    public static void executeIn5Seconds(final Callback.IAction<AsyncTask> callback) {
        executeIn(5000, callback);
    }

    public static AsyncTask executeIn(final int milliSeconds, final Callback.IAction<AsyncTask> callback) {
        DefaultAsyncTask executor = new DefaultAsyncTask(null) {

            @Override
            public void execute(TaskResultHolder resultHolder) {
                long endTimeMillis = System.currentTimeMillis() + milliSeconds;
                while (true) {
                    // method logic
                    if (System.currentTimeMillis() > endTimeMillis) {
                        // do some clean-up
                        return;
                    }
                }
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                callback.call(null);
            }
        };
        return executor.executeOnThreadPool();
    }
}
