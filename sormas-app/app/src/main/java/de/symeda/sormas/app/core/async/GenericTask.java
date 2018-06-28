package de.symeda.sormas.app.core.async;

import android.os.AsyncTask;
import android.util.Log;

import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.core.BoolResult;

/**
 * Created by Orson on 09/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class GenericTask extends AsyncTask<Void, Void, AsyncTaskResult<TaskResultHolder>> {

    private static final String TAG = GenericTask.class.getSimpleName();

    private ITaskResultCallback callback;
    private IJobDefinition jobDefinition;
    private BoolResult mResultStatus = BoolResult.TRUE;
    private TaskResultHolder resultHolder;
    //private TaskResultHolder resultHolder;

    public GenericTask(IJobDefinition jobDefinition, ITaskResultCallback callback) {
        this.jobDefinition = jobDefinition;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        resultHolder = new TaskResultHolder();
        jobDefinition.preExecute(mResultStatus, resultHolder);
    }

    @Override
    protected AsyncTaskResult<TaskResultHolder> doInBackground(Void... voids) {
        BoolResult _resultStatus = resultHolder.getResultStatus();

        if (!_resultStatus.isSuccess())
            return new AsyncTaskResult<TaskResultHolder>(_resultStatus, resultHolder);

        try {
            jobDefinition.execute(_resultStatus, resultHolder);

            _resultStatus = resultHolder.getResultStatus();

            return new AsyncTaskResult<TaskResultHolder>(_resultStatus, resultHolder);
        } catch (Exception e) {
            //TODO: Errors seams to be swallowed; resultHolder is null
            Log.w(TAG, (e != null)? e.getMessage() : "Error executing a job definition!");
            return new AsyncTaskResult<TaskResultHolder>(mResultStatus, e);
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<TaskResultHolder> result) {
        BoolResult resultStatus = result.getResultStatus();
        TaskResultHolder taskResultHolder = result.getResult();

        if (!resultStatus.isSuccess()) {
            callback.taskResult(resultStatus, taskResultHolder);
        } else if (result.getError() != null) {
            callback.taskResult(new BoolResult(false, result.getError().getMessage()), taskResultHolder);
        }  else if ( isCancelled()) {
            callback.taskResult(new BoolResult(false, "Listing search has been cancelled"), taskResultHolder);
        } else {
            if (taskResultHolder != null)
                resultStatus = BoolResult.TRUE;

            callback.taskResult(resultStatus, taskResultHolder);
        }
    }

}
