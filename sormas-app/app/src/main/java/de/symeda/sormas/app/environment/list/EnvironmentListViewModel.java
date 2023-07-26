package de.symeda.sormas.app.environment.list;

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
import de.symeda.sormas.app.backend.environment.EnvironmentCriteria;

public class EnvironmentListViewModel extends ViewModel {

	private LiveData<PagedList<Environment>> environmentList;

	private EnvironmentFactory environmentFactory;

	public void initializeViewModel() {
		initializeModel(new EnvironmentCriteria());
	}

	private void initializeModel(EnvironmentCriteria environmentCriteria) {
		environmentFactory = new EnvironmentFactory();
		environmentFactory.setEnvironmentCriteria(environmentCriteria);
		PagedList.Config config = new PagedList.Config.Builder().setEnablePlaceholders(true).setInitialLoadSizeHint(32).setPageSize(16).build();

		LivePagedListBuilder environmentsListBuilder = new LivePagedListBuilder(environmentFactory, config);
		environmentList = environmentsListBuilder.build();
	}

	public LiveData<PagedList<Environment>> getEnvironmentList() {
		return environmentList;
	}

	public EnvironmentCriteria getEnvironmentCriteria() {
		return environmentFactory.getEnvironmentCriteria();
	}

	void notifyCriteriaUpdated() {
		if (environmentList.getValue() != null) {
			environmentList.getValue().getDataSource().invalidate();
			if (!environmentList.getValue().isEmpty()) {
				environmentList.getValue().loadAround(0);
			}
		}
	}

	public static class EnvironmentDataSource extends PositionalDataSource<Environment> {

		private EnvironmentCriteria environmentCriteria;

		EnvironmentDataSource(EnvironmentCriteria environmentCriteria) {
			this.environmentCriteria = environmentCriteria;
		}

		@Override
		public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Environment> callback) {
			long totalCount = DatabaseHelper.getEnvironmentDao().countByCriteria(environmentCriteria);
			int offset = params.requestedStartPosition;
			int count = params.requestedLoadSize;
			if (offset + count > totalCount) {
				offset = (int) Math.max(0, totalCount - count);
			}
			List<Environment> formDataList = DatabaseHelper.getEnvironmentDao().queryByCriteria(environmentCriteria, offset, count);
			callback.onResult(formDataList, offset, (int) totalCount);
		}

		@Override
		public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Environment> callback) {
			List<Environment> environments =
				DatabaseHelper.getEnvironmentDao().queryByCriteria(environmentCriteria, params.startPosition, params.loadSize);
			callback.onResult(environments);
		}
	}

	public static class EnvironmentFactory extends DataSource.Factory {

		private MutableLiveData<EnvironmentDataSource> mutableDataSource;
		private EnvironmentDataSource environmentDataSource;
		private EnvironmentCriteria environmentCriteria;

		EnvironmentFactory() {
			this.mutableDataSource = new MutableLiveData<>();
		}

		@NonNull
		@Override
		public DataSource create() {
			environmentDataSource = new EnvironmentDataSource(environmentCriteria);
			mutableDataSource.postValue(environmentDataSource);
			return environmentDataSource;
		}

		public EnvironmentCriteria getEnvironmentCriteria() {
			return environmentCriteria;
		}

		public void setEnvironmentCriteria(EnvironmentCriteria environmentCriteria) {
			this.environmentCriteria = environmentCriteria;
		}
	}
}
