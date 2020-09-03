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

package de.symeda.sormas.app.therapy.list;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.paging.PositionalDataSource;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.therapy.Therapy;
import de.symeda.sormas.app.backend.therapy.Treatment;
import de.symeda.sormas.app.backend.therapy.TreatmentCriteria;

public class TreatmentListViewModel extends ViewModel {

	private LiveData<PagedList<Treatment>> treatments;
	private TreatmentDataFactory treatmentDataFactory;

	public void initializeViewModel(Therapy therapy) {
		treatmentDataFactory = new TreatmentDataFactory();
		TreatmentCriteria treatmentCriteria = new TreatmentCriteria();
		treatmentCriteria.therapy(therapy);
		treatmentDataFactory.setTreatmentCriteria(treatmentCriteria);

		PagedList.Config config = new PagedList.Config.Builder().setEnablePlaceholders(true).setInitialLoadSizeHint(16).setPageSize(8).build();

		LivePagedListBuilder treatmentListBuilder = new LivePagedListBuilder(treatmentDataFactory, config);
		treatments = treatmentListBuilder.build();
	}

	public LiveData<PagedList<Treatment>> getTreatments() {
		return treatments;
	}

	public static class TreatmentDataSource extends PositionalDataSource<Treatment> {

		private TreatmentCriteria treatmentCriteria;

		TreatmentDataSource(TreatmentCriteria treatmentCriteria) {
			this.treatmentCriteria = treatmentCriteria;
		}

		@Override
		public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Treatment> callback) {
			long totalCount = DatabaseHelper.getTreatmentDao().countByCriteria(treatmentCriteria);
			int offset = params.requestedStartPosition;
			int count = params.requestedLoadSize;
			if (offset + count > totalCount) {
				offset = (int) Math.max(0, totalCount - count);
			}
			List<Treatment> treatments = DatabaseHelper.getTreatmentDao().queryByCriteria(treatmentCriteria, offset, count);
			callback.onResult(treatments, offset, (int) totalCount);
		}

		@Override
		public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Treatment> callback) {
			List<Treatment> treatments = DatabaseHelper.getTreatmentDao().queryByCriteria(treatmentCriteria, params.startPosition, params.loadSize);
			callback.onResult(treatments);
		}
	}

	public static class TreatmentDataFactory extends DataSource.Factory {

		private MutableLiveData<TreatmentDataSource> mutableDataSource;
		private TreatmentDataSource treatmentDataSource;
		private TreatmentCriteria treatmentCriteria;

		TreatmentDataFactory() {
			this.mutableDataSource = new MutableLiveData<>();
		}

		@NonNull
		@Override
		public DataSource create() {
			treatmentDataSource = new TreatmentDataSource(treatmentCriteria);
			mutableDataSource.postValue(treatmentDataSource);
			return treatmentDataSource;
		}

		void setTreatmentCriteria(TreatmentCriteria treatmentCriteria) {
			this.treatmentCriteria = treatmentCriteria;
		}

		TreatmentCriteria getTreatmentCriteria() {
			return treatmentCriteria;
		}
	}
}
