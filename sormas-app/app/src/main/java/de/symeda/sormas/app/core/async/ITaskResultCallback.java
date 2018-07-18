package de.symeda.sormas.app.core.async;

import de.symeda.sormas.app.core.BoolResult;

/**
 * Use DefaultAsyncTask.onPostExecute instead
 */
@Deprecated
public interface ITaskResultCallback {

    void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder);
}