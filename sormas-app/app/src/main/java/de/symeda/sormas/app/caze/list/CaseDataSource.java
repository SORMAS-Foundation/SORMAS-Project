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

package de.symeda.sormas.app.caze.list;

import android.os.AsyncTask;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.paging.ItemKeyedDataSource;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

public class CaseDataSource extends ItemKeyedDataSource<Long, Case> {

    private static final String TAG = CaseDataSource.class.getSimpleName();

    public static final long STEP_SIZE = 10;

    private InvestigationStatus investigationStatus;
    private Long offset = 0L;

    public CaseDataSource(InvestigationStatus investigationStatus) {
        this.investigationStatus = investigationStatus;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<Case> callback) {
        try {
            List<Case> cases = new LoadCasesTask(investigationStatus, 0L, STEP_SIZE).execute().get();
            offset += (STEP_SIZE + 1);
            callback.onResult(cases);
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Case> callback) {
        try {
            List<Case> cases = new LoadCasesTask(investigationStatus, offset, STEP_SIZE).execute().get();
            offset += (STEP_SIZE + 1);
            callback.onResult(cases);
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Case> callback) {

    }

    @NonNull
    @Override
    public Long getKey(@NonNull Case item) {
        return null;
    }

    private static class LoadCasesTask extends AsyncTask<Void, Void, List<Case>> {
        private InvestigationStatus investigationStatus;
        private long offset;
        private long limit;

        LoadCasesTask(InvestigationStatus investigationStatus, long offset, long limit) {
            this.investigationStatus = investigationStatus;
            this.offset = offset;
            this.limit = limit;
        }

        @Override
        protected List<Case> doInBackground(Void... args) {
            return DatabaseHelper.getCaseDao().queryForEq(Case.INVESTIGATION_STATUS, investigationStatus, Case.REPORT_DATE, false, offset, limit);
        }
    }
}
