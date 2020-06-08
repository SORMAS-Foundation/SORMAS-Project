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

package de.symeda.sormas.app.pathogentest.list;

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
import de.symeda.sormas.app.backend.sample.PathogenTest;
import de.symeda.sormas.app.backend.sample.PathogenTestCriteria;
import de.symeda.sormas.app.backend.sample.Sample;

public class PathogenTestListViewModel extends ViewModel {

	private LiveData<PagedList<PathogenTest>> pathogenTests;
	private PathogenTestDataFactory pathogenTestDataFactory;

	public void initializeViewModel(Sample sample) {
		pathogenTestDataFactory = new PathogenTestDataFactory();
		PathogenTestCriteria pathogenTestCriteria = new PathogenTestCriteria();
		pathogenTestCriteria.sample(sample);
		pathogenTestDataFactory.setPathogenTestCriteria(pathogenTestCriteria);
		initializeList();
	}

	public LiveData<PagedList<PathogenTest>> getPathogenTests() {
		return pathogenTests;
	}

	void notifyCriteriaUpdated() {
		if (pathogenTests.getValue() != null) {
			pathogenTests.getValue().getDataSource().invalidate();
			if (!pathogenTests.getValue().isEmpty()) {
				pathogenTests.getValue().loadAround(0);
			}
		}
	}

	public static class PathogenTestDataSource extends PositionalDataSource<PathogenTest> {

		private PathogenTestCriteria pathogenTestCriteria;

		PathogenTestDataSource(PathogenTestCriteria pathogenTestCriteria) {
			this.pathogenTestCriteria = pathogenTestCriteria;
		}

		@Override
		public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<PathogenTest> callback) {
			long totalCount = DatabaseHelper.getSampleTestDao().countByCriteria(pathogenTestCriteria);
			int offset = params.requestedStartPosition;
			int count = params.requestedLoadSize;
			if (offset + count > totalCount) {
				offset = (int) Math.max(0, totalCount - count);
			}
			List<PathogenTest> pathogenTests = DatabaseHelper.getSampleTestDao().queryByCriteria(pathogenTestCriteria, offset, count);
			callback.onResult(pathogenTests, offset, (int) totalCount);
		}

		@Override
		public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<PathogenTest> callback) {
			List<PathogenTest> pathogenTests =
				DatabaseHelper.getSampleTestDao().queryByCriteria(pathogenTestCriteria, params.startPosition, params.loadSize);
			callback.onResult(pathogenTests);
		}
	}

	public static class PathogenTestDataFactory extends DataSource.Factory {

		private MutableLiveData<PathogenTestDataSource> mutableDataSource;
		private PathogenTestDataSource pathogenTestDataSource;
		private PathogenTestCriteria pathogenTestCriteria;

		PathogenTestDataFactory() {
			this.mutableDataSource = new MutableLiveData<>();
		}

		@NonNull
		@Override
		public DataSource create() {
			pathogenTestDataSource = new PathogenTestDataSource(pathogenTestCriteria);
			mutableDataSource.postValue(pathogenTestDataSource);
			return pathogenTestDataSource;
		}

		void setPathogenTestCriteria(PathogenTestCriteria pathogenTestCriteria) {
			this.pathogenTestCriteria = pathogenTestCriteria;
		}

		PathogenTestCriteria getPathogenTestCriteria() {
			return pathogenTestCriteria;
		}
	}

	private void initializeList() {
		PagedList.Config config = new PagedList.Config.Builder().setEnablePlaceholders(true).setInitialLoadSizeHint(16).setPageSize(8).build();

		LivePagedListBuilder pathogenTestListBuilder = new LivePagedListBuilder(pathogenTestDataFactory, config);
		pathogenTests = pathogenTestListBuilder.build();
	}
}
