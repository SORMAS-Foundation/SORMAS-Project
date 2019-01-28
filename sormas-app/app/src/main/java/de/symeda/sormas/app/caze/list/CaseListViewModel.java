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

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.paging.PositionalDataSource;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

public class CaseListViewModel extends ViewModel {

    private LiveData<PagedList<Case>> casesList;
    private CaseDataFactory caseDataFactory;

    public CaseListViewModel() {

        caseDataFactory = new CaseDataFactory();
        caseDataFactory.setInvestigationStatus(InvestigationStatus.PENDING);
        PagedList.Config config = new PagedList.Config.Builder()
                        .setEnablePlaceholders(true)
                        .setInitialLoadSizeHint(16)
                        .setPageSize(8).build();

        LivePagedListBuilder casesListBuilder = new LivePagedListBuilder(caseDataFactory, config);
        casesList = casesListBuilder.build();
    }

    public LiveData<PagedList<Case>> getCases() {
        return casesList;
    }

    public void setInvestigationStatus(InvestigationStatus investigationStatus) {
        caseDataFactory.setInvestigationStatus(investigationStatus);
        if (casesList.getValue() != null
            && casesList.getValue().getDataSource() != null) {
            casesList.getValue().getDataSource().invalidate();
        }
    }

    public InvestigationStatus getInvestigationStatus() {
        return caseDataFactory.getInvestigationStatus();
    }

    public static class CaseDataSource extends PositionalDataSource<Case> {

        private InvestigationStatus investigationStatus;

        public CaseDataSource(InvestigationStatus investigationStatus) {
            this.investigationStatus = investigationStatus;
        }

        @Override
        public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Case> callback) {
            long totalCount = DatabaseHelper.getCaseDao().countOfEq(Case.INVESTIGATION_STATUS, investigationStatus);
            int offset = params.requestedStartPosition;
            int count = params.requestedLoadSize;
            if (offset + count > totalCount) {
                offset = (int)Math.max(0, totalCount - count);
            }
            List<Case> cases = DatabaseHelper.getCaseDao().queryForEq(Case.INVESTIGATION_STATUS, investigationStatus, Case.REPORT_DATE, false, offset, count);
            callback.onResult(cases, offset, (int)totalCount);
        }

        @Override
        public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Case> callback) {
            List<Case> cases = DatabaseHelper.getCaseDao().queryForEq(Case.INVESTIGATION_STATUS, investigationStatus, Case.REPORT_DATE, false, params.startPosition, params.loadSize);
            callback.onResult(cases);
        }
    }

    public static class CaseDataFactory extends DataSource.Factory {

        private MutableLiveData<CaseDataSource> mutableDataSource;
        private CaseDataSource caseDataSource;
        private InvestigationStatus investigationStatus;

        public CaseDataFactory() {
            this.mutableDataSource = new MutableLiveData<>();
        }

        @Override
        public DataSource create() {
            caseDataSource = new CaseDataSource(investigationStatus);
            mutableDataSource.postValue(caseDataSource);
            return caseDataSource;
        }

        public void setInvestigationStatus(InvestigationStatus investigationStatus) {
            this.investigationStatus = investigationStatus;
        }

        public InvestigationStatus getInvestigationStatus() {
            return investigationStatus;
        }
    }
}
