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

package de.symeda.sormas.app.sample.list;

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
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleCriteria;

public class SampleListViewModel extends ViewModel {

	private LiveData<PagedList<Sample>> samples;
	private SampleDataFactory sampleDataFactory;

	public void initializeViewModel(Case caze) {
		sampleDataFactory = new SampleDataFactory();
		SampleCriteria sampleCriteria = new SampleCriteria();
		sampleCriteria.caze(caze);
		sampleDataFactory.setSampleCriteria(sampleCriteria);
		initializeList();
	}

	public void initializeViewModel() {
		sampleDataFactory = new SampleDataFactory();
		SampleCriteria sampleCriteria = new SampleCriteria();
		sampleCriteria.shipmentStatus(null);
		sampleDataFactory.setSampleCriteria(sampleCriteria);
		initializeList();
	}

	public LiveData<PagedList<Sample>> getSamples() {
		return samples;
	}

	void notifyCriteriaUpdated() {
		if (samples.getValue() != null) {
			samples.getValue().getDataSource().invalidate();
			if (!samples.getValue().isEmpty()) {
				samples.getValue().loadAround(0);
			}
		}
	}

	public SampleCriteria getSampleCriteria() {
		return sampleDataFactory.getSampleCriteria();
	}

	public static class SampleDataSource extends PositionalDataSource<Sample> {

		private SampleCriteria sampleCriteria;

		SampleDataSource(SampleCriteria sampleCriteria) {
			this.sampleCriteria = sampleCriteria;
		}

		@Override
		public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Sample> callback) {
			long totalCount = DatabaseHelper.getSampleDao().countByCriteria(sampleCriteria);
			int offset = params.requestedStartPosition;
			int count = params.requestedLoadSize;
			if (offset + count > totalCount) {
				offset = (int) Math.max(0, totalCount - count);
			}
			List<Sample> samples = DatabaseHelper.getSampleDao().queryByCriteria(sampleCriteria, offset, count);
			callback.onResult(samples, offset, (int) totalCount);
		}

		@Override
		public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Sample> callback) {
			List<Sample> samples = DatabaseHelper.getSampleDao().queryByCriteria(sampleCriteria, params.startPosition, params.loadSize);
			callback.onResult(samples);
		}
	}

	public static class SampleDataFactory extends DataSource.Factory {

		private MutableLiveData<SampleDataSource> mutableDataSource;
		private SampleDataSource sampleDataSource;
		private SampleCriteria sampleCriteria;

		SampleDataFactory() {
			this.mutableDataSource = new MutableLiveData<>();
		}

		@NonNull
		@Override
		public DataSource create() {
			sampleDataSource = new SampleDataSource(sampleCriteria);
			mutableDataSource.postValue(sampleDataSource);
			return sampleDataSource;
		}

		void setSampleCriteria(SampleCriteria sampleCriteria) {
			this.sampleCriteria = sampleCriteria;
		}

		SampleCriteria getSampleCriteria() {
			return sampleCriteria;
		}
	}

	private void initializeList() {
		PagedList.Config config = new PagedList.Config.Builder().setEnablePlaceholders(true).setInitialLoadSizeHint(16).setPageSize(8).build();

		LivePagedListBuilder sampleListBuilder = new LivePagedListBuilder(sampleDataFactory, config);
		samples = sampleListBuilder.build();
	}
}
