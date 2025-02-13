package de.symeda.sormas.app.news;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import de.symeda.sormas.api.event.RiskLevel;
import de.symeda.sormas.app.PagedBaseListActivity;
import de.symeda.sormas.app.PagedBaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.databinding.FilterNewsListLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.InfrastructureDaoHelper;
import de.symeda.sormas.app.util.InfrastructureFieldsDependencyHandler;

public class NewsListActivity extends PagedBaseListActivity {

	NewsListViewModel viewModel;
	FilterNewsListLayoutBinding filterBinding;
	Boolean isFirstCreate = true;

	@Override
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		adapter = new NewsListAdapter(getContext());
		viewModel = new ViewModelProvider(this).get(NewsListViewModel.class);
		viewModel.setContext(this);
		viewModel.getNewsList().observe(this, news -> {
			adapter.submitList(news);
			hidePreloader();
		});
		filterBinding.setCriteria(viewModel.getNewsFilterCriteria());
	}

	@Override
	public void onResume() {
		super.onResume();
		if (isFirstCreate) {
			isFirstCreate = false;
			showPreloader();
		}
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_activityNews;
	}

	@Override
	public void addFiltersToPageMenu() {
		View newsListFilterView = getLayoutInflater().inflate(R.layout.filter_news_list_layout, null);
		filterBinding = DataBindingUtil.bind(newsListFilterView);
		pageMenu.addFilter(newsListFilterView);

		runOnUiThread(() -> {
			List<Item> initialRegions = InfrastructureDaoHelper.loadRegionsByServerCountry();
			InfrastructureFieldsDependencyHandler.instance.initializeRegionFields(
				filterBinding.regionFilter,
				initialRegions,
				null,
				filterBinding.districtFilter,
				List.of(),
				null,
				filterBinding.communityFilter,
				List.of(),
				null);
		});
		filterBinding.priorityFilter.initializeSpinner(DataUtils.getEnumItems(RiskLevel.class));
		filterBinding.startDateFilter.initializeDateField(getSupportFragmentManager());
		filterBinding.endDateFilter.initializeDateField(getSupportFragmentManager());
		filterBinding.applyFilters.setOnClickListener(e -> {
			showPreloader();
			pageMenu.hideAll();
			viewModel.notifyCriteriaUpdated();
		});
		filterBinding.resetFilters.setOnClickListener(e -> resetFilter());
	}

	private void resetFilter() {
		showPreloader();
		pageMenu.hideAll();
		viewModel.getNewsFilterCriteria().setRegion(null);
		viewModel.getNewsFilterCriteria().setDistrict(null);
		viewModel.getNewsFilterCriteria().setCommunity(null);
		viewModel.getNewsFilterCriteria().setRiskLevel(null);
		viewModel.getNewsFilterCriteria().setStartDate(null);
		viewModel.getNewsFilterCriteria().setEndDate(null);
		filterBinding.invalidateAll();
		viewModel.notifyCriteriaUpdated();
	}

	@Override
	protected PagedBaseListFragment buildListFragment(PageMenuItem menuItem) {
		NewsListFragment newsListFragment = NewsListFragment.newInstance();
		newsListFragment.setResetFilterCallBack(this::resetFilter);
		return newsListFragment;
	}
}
