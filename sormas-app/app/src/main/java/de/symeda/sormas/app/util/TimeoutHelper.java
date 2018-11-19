/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
            public void doInBackground(TaskResultHolder resultHolder) {
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
