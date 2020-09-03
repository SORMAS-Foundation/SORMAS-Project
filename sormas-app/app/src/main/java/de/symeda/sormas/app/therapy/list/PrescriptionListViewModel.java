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
import de.symeda.sormas.app.backend.therapy.Prescription;
import de.symeda.sormas.app.backend.therapy.PrescriptionCriteria;
import de.symeda.sormas.app.backend.therapy.Therapy;

public class PrescriptionListViewModel extends ViewModel {

	private LiveData<PagedList<Prescription>> prescriptions;
	private PrescriptionDataFactory prescriptionDataFactory;

	public void initializeViewModel(Therapy therapy) {
		prescriptionDataFactory = new PrescriptionDataFactory();
		PrescriptionCriteria prescriptionCriteria = new PrescriptionCriteria();
		prescriptionCriteria.therapy(therapy);
		prescriptionDataFactory.setPrescriptionCriteria(prescriptionCriteria);

		PagedList.Config config = new PagedList.Config.Builder().setEnablePlaceholders(true).setInitialLoadSizeHint(16).setPageSize(8).build();

		LivePagedListBuilder prescriptionListBuilder = new LivePagedListBuilder(prescriptionDataFactory, config);
		prescriptions = prescriptionListBuilder.build();
	}

	public LiveData<PagedList<Prescription>> getPrescriptions() {
		return prescriptions;
	}

	public static class PrescriptionDataSource extends PositionalDataSource<Prescription> {

		private PrescriptionCriteria prescriptionCriteria;

		PrescriptionDataSource(PrescriptionCriteria prescriptionCriteria) {
			this.prescriptionCriteria = prescriptionCriteria;
		}

		@Override
		public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Prescription> callback) {
			long totalCount = DatabaseHelper.getPrescriptionDao().countByCriteria(prescriptionCriteria);
			int offset = params.requestedStartPosition;
			int count = params.requestedLoadSize;
			if (offset + count > totalCount) {
				offset = (int) Math.max(0, totalCount - count);
			}
			List<Prescription> prescriptions = DatabaseHelper.getPrescriptionDao().queryByCriteria(prescriptionCriteria, offset, count);
			callback.onResult(prescriptions, offset, (int) totalCount);
		}

		@Override
		public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Prescription> callback) {
			List<Prescription> prescriptions =
				DatabaseHelper.getPrescriptionDao().queryByCriteria(prescriptionCriteria, params.startPosition, params.loadSize);
			callback.onResult(prescriptions);
		}
	}

	public static class PrescriptionDataFactory extends DataSource.Factory {

		private MutableLiveData<PrescriptionDataSource> mutableDataSource;
		private PrescriptionDataSource prescriptionDataSource;
		private PrescriptionCriteria prescriptionCriteria;

		PrescriptionDataFactory() {
			this.mutableDataSource = new MutableLiveData<>();
		}

		@NonNull
		@Override
		public DataSource create() {
			prescriptionDataSource = new PrescriptionDataSource(prescriptionCriteria);
			mutableDataSource.postValue(prescriptionDataSource);
			return prescriptionDataSource;
		}

		void setPrescriptionCriteria(PrescriptionCriteria prescriptionCriteria) {
			this.prescriptionCriteria = prescriptionCriteria;
		}

		PrescriptionCriteria getPrescriptionCriteria() {
			return prescriptionCriteria;
		}
	}
}
