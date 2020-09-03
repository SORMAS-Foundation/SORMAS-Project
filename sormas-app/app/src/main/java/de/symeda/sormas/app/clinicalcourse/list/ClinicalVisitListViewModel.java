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

package de.symeda.sormas.app.clinicalcourse.list;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.paging.PositionalDataSource;
import de.symeda.sormas.app.backend.clinicalcourse.ClinicalCourse;
import de.symeda.sormas.app.backend.clinicalcourse.ClinicalVisit;
import de.symeda.sormas.app.backend.clinicalcourse.ClinicalVisitCriteria;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

public class ClinicalVisitListViewModel extends ViewModel {

	private LiveData<PagedList<ClinicalVisit>> clinicalVisits;
	private ClinicalVisitDataFactory clinicalVisitDataFactory;

	public void initializeViewModel(ClinicalCourse clinicalCourse) {
		clinicalVisitDataFactory = new ClinicalVisitDataFactory();
		ClinicalVisitCriteria clinicalVisitCriteria = new ClinicalVisitCriteria();
		clinicalVisitCriteria.clinicalCourse(clinicalCourse);
		clinicalVisitDataFactory.setClinicalVisitCriteria(clinicalVisitCriteria);

		PagedList.Config config = new PagedList.Config.Builder().setEnablePlaceholders(true).setInitialLoadSizeHint(16).setPageSize(8).build();

		LivePagedListBuilder clinicalVisitListBuilder = new LivePagedListBuilder(clinicalVisitDataFactory, config);
		clinicalVisits = clinicalVisitListBuilder.build();
	}

	public LiveData<PagedList<ClinicalVisit>> getClinicalVisits() {
		return clinicalVisits;
	}

	public static class ClinicalVisitDataSource extends PositionalDataSource<ClinicalVisit> {

		private ClinicalVisitCriteria clinicalVisitCriteria;

		ClinicalVisitDataSource(ClinicalVisitCriteria clinicalVisitCriteria) {
			this.clinicalVisitCriteria = clinicalVisitCriteria;
		}

		@Override
		public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<ClinicalVisit> callback) {
			long totalCount = DatabaseHelper.getClinicalVisitDao().countByCriteria(clinicalVisitCriteria);
			int offset = params.requestedStartPosition;
			int count = params.requestedLoadSize;
			if (offset + count > totalCount) {
				offset = (int) Math.max(0, totalCount - count);
			}
			List<ClinicalVisit> clinicalVisits = DatabaseHelper.getClinicalVisitDao().queryByCriteria(clinicalVisitCriteria, offset, count);
			callback.onResult(clinicalVisits, offset, (int) totalCount);
		}

		@Override
		public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<ClinicalVisit> callback) {
			List<ClinicalVisit> clinicalVisits =
				DatabaseHelper.getClinicalVisitDao().queryByCriteria(clinicalVisitCriteria, params.startPosition, params.loadSize);
			callback.onResult(clinicalVisits);
		}
	}

	public static class ClinicalVisitDataFactory extends DataSource.Factory {

		private MutableLiveData<ClinicalVisitDataSource> mutableDataSource;
		private ClinicalVisitDataSource clinicalVisitDataSource;
		private ClinicalVisitCriteria clinicalVisitCriteria;

		ClinicalVisitDataFactory() {
			this.mutableDataSource = new MutableLiveData<>();
		}

		@NonNull
		@Override
		public DataSource create() {
			clinicalVisitDataSource = new ClinicalVisitDataSource(clinicalVisitCriteria);
			mutableDataSource.postValue(clinicalVisitDataSource);
			return clinicalVisitDataSource;
		}

		void setClinicalVisitCriteria(ClinicalVisitCriteria clinicalVisitCriteria) {
			this.clinicalVisitCriteria = clinicalVisitCriteria;
		}

		ClinicalVisitCriteria getClinicalVisitCriteria() {
			return clinicalVisitCriteria;
		}
	}
}
