package de.symeda.sormas.app.news;

import java.util.function.Consumer;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.paging.PositionalDataSource;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.news.NewsCriteria;
import de.symeda.sormas.api.news.eios.NewsStatus;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;
import retrofit2.Response;

public class NewsListViewModel extends ViewModel {

	private LiveData<PagedList<News>> newsList;
	private NewsDataFactory newsDataFactory;
	private static Context context;

	private static final Integer NEWS_PAGE_SIZE = 16;

	public NewsListViewModel() {
		newsDataFactory = new NewsDataFactory();
		newsDataFactory.setCriteria(new NewsFilterCriteria());
		PagedList.Config config =
			new PagedList.Config.Builder().setEnablePlaceholders(true).setInitialLoadSizeHint(NEWS_PAGE_SIZE).setPageSize(NEWS_PAGE_SIZE).build();
		LivePagedListBuilder newsPageListBuilder = new LivePagedListBuilder(newsDataFactory, config);
		newsList = newsPageListBuilder.build();
	}

	void notifyCriteriaUpdated() {
		if (newsList.getValue() != null) {
			newsList.getValue().getDataSource().invalidate();
			if (!newsList.getValue().isEmpty()) {
				newsList.getValue().loadAround(0);
			}
		}
	}

	public void setContext(Context context) {
		NewsListViewModel.context = context;
	}

	public NewsFilterCriteria getNewsFilterCriteria() {
		return newsDataFactory.getCriteria();
	}

	public LiveData<PagedList<News>> getNewsList() {
		return newsList;
	}

	public static class NewsDataSource extends PositionalDataSource<News> {

		NewsFilterCriteria criteria;

		public NewsDataSource(NewsFilterCriteria criteria) {
			this.criteria = criteria;
		}

		@Override
		public void loadInitial(@NonNull LoadInitialParams loadInitialParams, @NonNull LoadInitialCallback<News> loadInitialCallback) {
			loadNewsList(0, criteria, page -> loadInitialCallback.onResult(page.getElements(), 0, page.getTotalElementCount().intValue()));

		}

		@Override
		public void loadRange(@NonNull LoadRangeParams loadRangeParams, @NonNull LoadRangeCallback<News> loadRangeCallback) {
			loadNewsList(loadRangeParams.startPosition, criteria, page -> loadRangeCallback.onResult(page.getElements()));
		}

		private void loadNewsList(int offset, NewsFilterCriteria criteria, Consumer<Page<News>> consumer) {

			DefaultAsyncTask task = new DefaultAsyncTask(context) {

				@Override
				protected void doInBackground(TaskResultHolder resultHolder) throws Exception {
					CriteriaWithSorting<NewsCriteria> criteriaWithSorting = new CriteriaWithSorting<>();
					criteriaWithSorting.setCaseCriteria(toNewCriteria(criteria));
					RetroProvider.connect(context);
					Call<Page<News>> pageCall = RetroProvider.getNewsFacade().pullNewsIndexList(criteriaWithSorting, offset, NEWS_PAGE_SIZE);
					Response<Page<News>> response = pageCall.execute();
					if (response.isSuccessful()) {
						Page<News> newsListPage = response.body();
						Log.i("NewsDataSource", "loadNewsList: " + newsListPage);
						consumer.accept(newsListPage);
					} else {
						Log.e("NewsDataSource", "loadNewsList: " + response.errorBody().string());
					}
				}

				@Override
				protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
					super.onPostExecute(taskResult);
					RetroProvider.disconnect();
				}

			};
			task.executeOnThreadPool();
		}

		private NewsCriteria toNewCriteria(NewsFilterCriteria criteria) {
			NewsCriteria newCriteria = new NewsCriteria();
			// Only receiving approved news
			newCriteria.setStatus(NewsStatus.APPROVED);
			if (criteria.getRegion() != null)
				newCriteria.setRegion(new RegionReferenceDto(criteria.getRegion().getUuid()));
			if (criteria.getDistrict() != null)
				newCriteria.setDistrict(new DistrictReferenceDto(criteria.getDistrict().getUuid()));
			if (criteria.getCommunity() != null)
				newCriteria.setCommunity(new CommunityReferenceDto(criteria.getCommunity().getUuid()));
			newCriteria.setRiskLevel(criteria.getRiskLevel());
			newCriteria.setStartDate(criteria.getStartDate());
			newCriteria.setEndDate(criteria.getEndDate());
			return newCriteria;
		}

	}

	public static class NewsDataFactory extends DataSource.Factory {

		private MutableLiveData<NewsDataSource> mutableLiveData;
		private NewsFilterCriteria criteria;

		public NewsDataFactory() {
			this.mutableLiveData = new MutableLiveData<>();
		}

		@NonNull
		@Override
		public DataSource create() {
			NewsDataSource dataSource = new NewsDataSource(criteria);
			mutableLiveData.postValue(dataSource);
			return dataSource;
		}

		public NewsFilterCriteria getCriteria() {
			return criteria;
		}

		public void setCriteria(NewsFilterCriteria criteria) {
			this.criteria = criteria;
		}
	}

}
