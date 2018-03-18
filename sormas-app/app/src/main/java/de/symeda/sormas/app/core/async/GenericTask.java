package de.symeda.sormas.app.core.async;

import android.os.AsyncTask;
import android.util.Log;

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
    //private TaskResultHolder resultHolder;

    public GenericTask(IJobDefinition jobDefinition, ITaskResultCallback callback) {
        this.jobDefinition = jobDefinition;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //resultHolder = new TaskResultHolder();

        jobDefinition.preExecute();
    }

    @Override
    protected AsyncTaskResult<TaskResultHolder> doInBackground(Void... voids) {
        try {
            TaskResultHolder resultHolder = new TaskResultHolder();
            jobDefinition.execute(resultHolder);
            return new AsyncTaskResult<TaskResultHolder>(resultHolder);
        } catch (Exception e) {
            Log.w(TAG, (e != null)? e.getMessage() : "Error executing a job definition!");
            return new AsyncTaskResult<TaskResultHolder>(e);
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<TaskResultHolder> result) {
        BoolResult resultStatus = BoolResult.FALSE;
        TaskResultHolder taskResultHolder = result.getResult();

        if (result.getError() != null) {
            callback.searchResult(new BoolResult(false, result.getError().getMessage()), taskResultHolder);
        }  else if ( isCancelled()) {
            callback.searchResult(new BoolResult(false, "Listing search has been cancelled"), taskResultHolder);
        } else {
            if (taskResultHolder != null)
                resultStatus = BoolResult.TRUE;

            callback.searchResult(resultStatus, taskResultHolder);
        }
    }

}
