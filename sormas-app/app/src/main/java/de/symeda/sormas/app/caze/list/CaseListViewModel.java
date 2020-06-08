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

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseCriteria;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

public class CaseListViewModel extends ViewModel {

	private LiveData<PagedList<Case>> casesList;
	private CaseDataFactory caseDataFactory;

	public CaseListViewModel() {
		caseDataFactory = new CaseDataFactory();
		CaseCriteria caseCriteria = new CaseCriteria();
		caseCriteria.setInvestigationStatus(null);
		caseDataFactory.setCaseCriteria(caseCriteria);
		PagedList.Config config = new PagedList.Config.Builder().setEnablePlaceholders(true).setInitialLoadSizeHint(32).setPageSize(16).build();

		LivePagedListBuilder casesListBuilder = new LivePagedListBuilder(caseDataFactory, config);
		casesList = casesListBuilder.build();
	}

	public LiveData<PagedList<Case>> getCases() {
		return casesList;
	}

	void notifyCriteriaUpdated() {
		if (casesList.getValue() != null) {
			casesList.getValue().getDataSource().invalidate();
			if (!casesList.getValue().isEmpty()) {
				casesList.getValue().loadAround(0);
			}
		}
	}

	public CaseCriteria getCaseCriteria() {
		return caseDataFactory.getCaseCriteria();
	}

	public static class CaseDataSource extends PositionalDataSource<Case> {

		private CaseCriteria caseCriteria;

		CaseDataSource(CaseCriteria caseCriteria) {
			this.caseCriteria = caseCriteria;
		}

		@Override
		public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Case> callback) {
			long totalCount = DatabaseHelper.getCaseDao().countByCriteria(caseCriteria);
			int offset = params.requestedStartPosition;
			int count = params.requestedLoadSize;
			if (offset + count > totalCount) {
				offset = (int) Math.max(0, totalCount - count);
			}
			List<Case> cases = DatabaseHelper.getCaseDao().queryByCriteria(caseCriteria, offset, count);
			callback.onResult(cases, offset, (int) totalCount);
		}

		@Override
		public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Case> callback) {
			List<Case> cases = DatabaseHelper.getCaseDao().queryByCriteria(caseCriteria, params.startPosition, params.loadSize);
			callback.onResult(cases);
		}
	}

	public static class CaseDataFactory extends DataSource.Factory {

		private MutableLiveData<CaseDataSource> mutableDataSource;
		private CaseDataSource caseDataSource;
		private CaseCriteria caseCriteria;

		CaseDataFactory() {
			this.mutableDataSource = new MutableLiveData<>();
		}

		@NonNull
		@Override
		public DataSource create() {
			caseDataSource = new CaseDataSource(caseCriteria);
			mutableDataSource.postValue(caseDataSource);
			return caseDataSource;
		}

		void setCaseCriteria(CaseCriteria caseCriteria) {
			this.caseCriteria = caseCriteria;
		}

		CaseCriteria getCaseCriteria() {
			return caseCriteria;
		}
	}
}
