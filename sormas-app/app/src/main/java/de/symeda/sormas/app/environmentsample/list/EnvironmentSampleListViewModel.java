/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.environmentsample.list;

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
import de.symeda.sormas.app.backend.environment.Environment;
import de.symeda.sormas.app.backend.environment.environmentsample.EnvironmentSample;
import de.symeda.sormas.app.backend.environment.environmentsample.EnvironmentSampleCriteria;

public class EnvironmentSampleListViewModel extends ViewModel {

	private LiveData<PagedList<EnvironmentSample>> environmentSamples;
	private EnvironmentSampleDataFactory environmentSampleDataFactory;

	public void initializeViewModel(Environment environment) {
		environmentSampleDataFactory = new EnvironmentSampleListViewModel.EnvironmentSampleDataFactory();
		EnvironmentSampleCriteria criteria = new EnvironmentSampleCriteria();
		criteria.environment(environment);
		environmentSampleDataFactory.setCriteria(criteria);
		initializeList();
	}

	public void initializeViewModel() {
		environmentSampleDataFactory = new EnvironmentSampleListViewModel.EnvironmentSampleDataFactory();
		EnvironmentSampleCriteria criteria = new EnvironmentSampleCriteria();
		criteria.shipmentStatus(null);
		environmentSampleDataFactory.setCriteria(criteria);
		initializeList();
	}

	public LiveData<PagedList<EnvironmentSample>> getEnvironmentSamples() {
		return environmentSamples;
	}

	void notifyCriteriaUpdated() {
		if (environmentSamples.getValue() != null) {
			environmentSamples.getValue().getDataSource().invalidate();
			if (!environmentSamples.getValue().isEmpty()) {
				environmentSamples.getValue().loadAround(0);
			}
		}
	}

	public EnvironmentSampleCriteria getCriteria() {
		return environmentSampleDataFactory.getCriteria();
	}

	public static class EnvironmmentSampleDataSource extends PositionalDataSource<EnvironmentSample> {

		private EnvironmentSampleCriteria criteria;

		EnvironmmentSampleDataSource(EnvironmentSampleCriteria criteria) {
			this.criteria = criteria;
		}

		@Override
		public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<EnvironmentSample> callback) {
			long totalCount = DatabaseHelper.getEnvironmentSampleDao().countByCriteria(criteria);
			int offset = params.requestedStartPosition;
			int count = params.requestedLoadSize;
			if (offset + count > totalCount) {
				offset = (int) Math.max(0, totalCount - count);
			}
			List<EnvironmentSample> environmentSamples = DatabaseHelper.getEnvironmentSampleDao().queryByCriteria(criteria, offset, count);
			callback.onResult(environmentSamples, offset, (int) totalCount);
		}

		@Override
		public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<EnvironmentSample> callback) {
			List<EnvironmentSample> environmentSamples =
				DatabaseHelper.getEnvironmentSampleDao().queryByCriteria(criteria, params.startPosition, params.loadSize);
			callback.onResult(environmentSamples);
		}
	}

	public static class EnvironmentSampleDataFactory extends DataSource.Factory {

		private MutableLiveData<EnvironmentSampleListViewModel.EnvironmmentSampleDataSource> mutableDataSource;
		private EnvironmentSampleListViewModel.EnvironmmentSampleDataSource dataSource;
		private EnvironmentSampleCriteria criteria;

		EnvironmentSampleDataFactory() {
			this.mutableDataSource = new MutableLiveData<>();
		}

		@NonNull
		@Override
		public DataSource create() {
			dataSource = new EnvironmentSampleListViewModel.EnvironmmentSampleDataSource(criteria);
			mutableDataSource.postValue(dataSource);
			return dataSource;
		}

		void setCriteria(EnvironmentSampleCriteria criteria) {
			this.criteria = criteria;
		}

		EnvironmentSampleCriteria getCriteria() {
			return criteria;
		}
	}

	private void initializeList() {
		PagedList.Config config = new PagedList.Config.Builder().setEnablePlaceholders(true).setInitialLoadSizeHint(16).setPageSize(8).build();

		LivePagedListBuilder listBuilder = new LivePagedListBuilder(environmentSampleDataFactory, config);
		environmentSamples = listBuilder.build();
	}

}
