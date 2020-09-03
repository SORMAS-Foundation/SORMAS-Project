/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.core.async;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.util.ErrorReportingHelper;

public abstract class DefaultAsyncTask extends AsyncTask<Void, Void, AsyncTaskResult<TaskResultHolder>> {

	// for error reporting
	private final WeakReference<SormasApplication> applicationReference;
	AbstractDomainObject relatedEntity;

	private ITaskResultCallback resultCallback;

	public DefaultAsyncTask(Context context) {
		this(context, null);
	}

	public DefaultAsyncTask(Context context, AbstractDomainObject relatedEntity) {
		this.applicationReference = new WeakReference<>((SormasApplication) context.getApplicationContext());
		this.relatedEntity = relatedEntity;
	}

	protected abstract void doInBackground(TaskResultHolder resultHolder) throws Exception;

	@Override
	protected AsyncTaskResult<TaskResultHolder> doInBackground(Void... voids) {

		TaskResultHolder resultHolder = new TaskResultHolder();

		try {
			doInBackground(resultHolder);
			return new AsyncTaskResult<>(resultHolder.getResultStatus(), resultHolder);
		} catch (ValidationException e) {
			return new AsyncTaskResult<>(e);
		} catch (Exception e) {
			return handleException(e);
		}
	}

	@Override
	protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
		if (resultCallback != null) {
			resultCallback.taskResult(taskResult.getResultStatus(), taskResult.getResult());
		}
	}

	protected AsyncTaskResult handleException(Exception e) {
		Log.e(getClass().getName(), "Error executing an async task", e);

		SormasApplication application = applicationReference.get();
		if (application != null) {
			ErrorReportingHelper.sendCaughtException(e, relatedEntity);
		}

		return new AsyncTaskResult<>(e);
	}

	protected WeakReference<SormasApplication> getApplicationReference() {
		return applicationReference;
	}

	public AsyncTask executeOnThreadPool() {
		executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		return this;
	}
}
