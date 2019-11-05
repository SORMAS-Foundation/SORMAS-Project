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

package de.symeda.sormas.app.pathogentest.list;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.paging.PositionalDataSource;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.PathogenTest;
import de.symeda.sormas.app.backend.sample.Sample;

public class PathogenTestListViewModel extends ViewModel {

    private LiveData<PagedList<PathogenTest>> pathogenTests;
    private PathogenTestDataFactory pathogenTestDataFactory;

    private Sample sample;
    public void initializeViewModel(Sample sample) {
        this.sample  = sample;
        pathogenTestDataFactory = new PathogenTestDataFactory();
        pathogenTestDataFactory.setPathogenTestSample(sample);
        initializeList();
    }

    public void initializeViewModel() {
        pathogenTestDataFactory = new PathogenTestDataFactory();
        pathogenTestDataFactory.setPathogenTestSample(sample);
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

        private Sample sample;

        PathogenTestDataSource(Sample sample) {
            this.sample = sample;
        }

        @Override
        public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<PathogenTest> callback) {

            int offset = params.requestedStartPosition;
            int count = params.requestedLoadSize;
            int totalCount = 0;
            List<PathogenTest> pathogenTests = new ArrayList<PathogenTest>();
            if(offset==0){
                pathogenTests = DatabaseHelper.getSampleTestDao().queryBySample(sample);
                totalCount = pathogenTests.size();
            }else{

            }

            callback.onResult(pathogenTests, offset, totalCount);
        }

        @Override
        public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<PathogenTest> callback) {
            List<PathogenTest> pathogenTests = DatabaseHelper.getSampleTestDao().queryBySample(sample);
            callback.onResult(pathogenTests);
        }

    }

    public static class PathogenTestDataFactory extends DataSource.Factory {

        private MutableLiveData<PathogenTestDataSource> mutableDataSource;
        private PathogenTestDataSource pathogenTestDataSource;
        private Sample sample;

        PathogenTestDataFactory() {
            this.mutableDataSource = new MutableLiveData<>();
        }

        @NonNull
        @Override
        public DataSource create() {
            pathogenTestDataSource = new PathogenTestDataSource(sample);
            mutableDataSource.postValue(pathogenTestDataSource);
            return pathogenTestDataSource;
        }

        void setPathogenTestSample(Sample sample) {
            this.sample = sample;
        }

        Sample getPathogenTestSample() {
            return sample;
        }

    }

    private void initializeList() {
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(16)
                .setPageSize(8).build();

        LivePagedListBuilder pathogenTestListBuilder = new LivePagedListBuilder(pathogenTestDataFactory, config);
        pathogenTests = pathogenTestListBuilder.build();
    }

}
