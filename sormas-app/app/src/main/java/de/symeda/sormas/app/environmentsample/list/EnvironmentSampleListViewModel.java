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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.paging.PositionalDataSource;

import de.symeda.sormas.api.environment.environmentsample.Pathogen;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.common.HasLocalChangeDate;
import de.symeda.sormas.app.backend.environment.Environment;
import de.symeda.sormas.app.backend.environment.environmentsample.EnvironmentSample;
import de.symeda.sormas.app.backend.environment.environmentsample.EnvironmentSampleCriteria;
import de.symeda.sormas.app.backend.sample.PathogenTest;

public class EnvironmentSampleListViewModel extends ViewModel {

	private LiveData<PagedList<SampleWithTestedPathogens>> environmentSamples;
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

	public LiveData<PagedList<SampleWithTestedPathogens>> getEnvironmentSamples() {
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

	public static class EnvironmmentSampleDataSource extends PositionalDataSource<SampleWithTestedPathogens> {

		private EnvironmentSampleCriteria criteria;

		EnvironmmentSampleDataSource(EnvironmentSampleCriteria criteria) {
			this.criteria = criteria;
		}

		@Override
		public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<SampleWithTestedPathogens> callback) {
			long totalCount = DatabaseHelper.getEnvironmentSampleDao().countByCriteria(criteria);
			int offset = params.requestedStartPosition;
			int count = params.requestedLoadSize;
			if (offset + count > totalCount) {
				offset = (int) Math.max(0, totalCount - count);
			}

            callback.onResult(loadSampleWithTestedPathogens(offset, count), offset, (int) totalCount);
		}

        private List<SampleWithTestedPathogens> loadSampleWithTestedPathogens(int offset, int count) {
            List<EnvironmentSample> environmentSamples = DatabaseHelper.getEnvironmentSampleDao().queryByCriteria(criteria, offset, count);
            Map<Long, List<Pathogen>> pathogenTests = DatabaseHelper.getSampleTestDao().queryAllPositiveByEnvironmentSamples(environmentSamples)
                    .stream().collect(Collectors.groupingBy(t -> t.getEnvironmentSample().getId(), Collectors.mapping(PathogenTest::getTestedPathogen, Collectors.toList())));
            List<SampleWithTestedPathogens> samplesWithPathogens = environmentSamples.stream().map(s -> {
                String testedPathogens = pathogenTests.getOrDefault(s.getId(), Collections.emptyList()).stream().map(Pathogen::getCaption).collect(Collectors.joining(", "));
                return new SampleWithTestedPathogens(s, testedPathogens);
            }).collect(Collectors.toList());
            return samplesWithPathogens;
        }

        @Override
		public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<SampleWithTestedPathogens> callback) {
			callback.onResult(loadSampleWithTestedPathogens(params.startPosition, params.loadSize));
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

	public static class SampleWithTestedPathogens implements HasLocalChangeDate {
		private final EnvironmentSample sample;
		private String testedPathogens;

		public SampleWithTestedPathogens(EnvironmentSample sample, String testedPathogens) {
			this.sample = sample;
			this.testedPathogens = testedPathogens;
		}

		public EnvironmentSample getSample() {
			return sample;
		}

		public String getTestedPathogens() {
			return testedPathogens;
		}

		public void setTestedPathogens(String testedPathogens) {
			this.testedPathogens = testedPathogens;
		}

		@Override
		public Date getLocalChangeDate() {
			return sample.getLocalChangeDate();
		}
	}

}
