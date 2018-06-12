package de.symeda.sormas.app.core.async;

import android.os.AsyncTask;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.core.BoolResult;

/**
 * Created by Orson on 08/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TaskExecutor<ADO1 extends AbstractDomainObject, ADO2 extends AbstractDomainObject, ADO3 extends AbstractDomainObject> implements ITaskExecutor<ADO1, ADO2, ADO3> {
    private AsyncTask<Void, Void, AsyncTaskResult<TaskResultHolder>> task;
    private IJobDefinition jobDefinition;

    public TaskExecutor(IJobDefinition jobDefinition) {
        this.jobDefinition = jobDefinition;
    }

    @Override
    public AsyncTask execute(ITaskResultCallback resultCallback) {
        try {
            if (this.jobDefinition == null){
                resultCallback.taskResult(new BoolResult(false, "No job execute to execute"), TaskResultHolder.EMPTY);
                return null;
            }

            task = new GenericTask(this.jobDefinition, resultCallback);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            resultCallback.taskResult(new BoolResult(false, e.getMessage()), TaskResultHolder.EMPTY);
        }

        return task;
    }



    /*@Override
    public AsyncTask execute(TaskResultCallback<ADO1, ADO2, ADO3> resultCallback) {
    }*/
}
