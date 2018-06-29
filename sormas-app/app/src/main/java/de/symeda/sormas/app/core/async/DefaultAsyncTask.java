package de.symeda.sormas.app.core.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.util.ErrorReportingHelper;

public abstract class DefaultAsyncTask extends AsyncTask<Void, Void, AsyncTaskResult<TaskResultHolder>> {

    private final Context context;
    private ITaskResultCallback resultCallback;

    public DefaultAsyncTask(Context context) {
        this.context = context;
    }

    protected abstract void execute(TaskResultHolder resultHolder) throws Exception;

    @Override
    protected AsyncTaskResult<TaskResultHolder> doInBackground(Void... voids) {

        TaskResultHolder resultHolder = new TaskResultHolder();

        try {
            execute(resultHolder);
            return new AsyncTaskResult<>(resultHolder.getResultStatus(), resultHolder);
        } catch (Exception e) {
            return handleException(e, null);
        }
    }

    /**
     * Override onPostExecute instead
     */
    @Deprecated
    protected void postExecute(BoolResult resultStatus, TaskResultHolder resultHolder) { }


    @Override
    protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
        postExecute(taskResult.getResultStatus(), taskResult.getResult());
        if (resultCallback != null) {
            resultCallback.taskResult(taskResult.getResultStatus(), taskResult.getResult());
        }
    }

    protected AsyncTaskResult handleException(Exception e, AbstractDomainObject relatedEntity) {
        Log.e(getClass().getName(), "Error executing an async task", e);
        Log.e(getClass().getName(), "- root cause: ", ErrorReportingHelper.getRootCause(e));

        SormasApplication application = (SormasApplication) context.getApplicationContext();
        Tracker tracker = application.getDefaultTracker();
        ErrorReportingHelper.sendCaughtException(tracker, e, relatedEntity, true);

        return new AsyncTaskResult<>(e);
    }

    public static AsyncTask execute(DefaultAsyncTask jobDefinition) {
        return jobDefinition.execute((ITaskResultCallback)null);
    }

    public AsyncTask execute(ITaskResultCallback resultCallback) {
        this.resultCallback = resultCallback;
        return executeOnThreadPool();
    }

    public AsyncTask executeOnThreadPool() {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return this;
    }

}
