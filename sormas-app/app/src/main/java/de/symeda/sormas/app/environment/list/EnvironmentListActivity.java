package de.symeda.sormas.app.environment.list;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.PagedBaseListActivity;
import de.symeda.sormas.app.PagedBaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.environment.Environment;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.databinding.FilterEnvironmentListLayoutBinding;
import de.symeda.sormas.app.environment.edit.EnvironmentNewActivity;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.DataUtils;

public class EnvironmentListActivity extends PagedBaseListActivity {

	private EnvironmentListViewModel model;

	private FilterEnvironmentListLayoutBinding filterBinding;

	public static void startActivity(Context context) {
		List<Environment> environments = DatabaseHelper.getEnvironmentDao().getAll();
		int pageMenuPosition = environments.size() > 0 ? 1 : 0;
		BaseListActivity.startActivity(context, EnvironmentListActivity.class, buildBundle(pageMenuPosition));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		showPreloader();
		adapter = new EnvironmentListAdapter();
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				if (positionStart == 0) {
					RecyclerView recyclerView = findViewById(R.id.recyclerViewForList);
					if (recyclerView != null) {
						recyclerView.scrollToPosition(0);
					}
				}
			}

			@Override
			public void onItemRangeMoved(int positionStart, int toPosition, int itemCount) {
				RecyclerView recyclerView = findViewById(R.id.recyclerViewForList);
				if (recyclerView != null) {
					recyclerView.scrollToPosition(0);
				}
			}

		});

		model = new ViewModelProvider(this).get(EnvironmentListViewModel.class);
		model.initializeViewModel();
		model.getEnvironmentList().observe(this, environments -> {
			adapter.submitList(environments);
			hidePreloader();
		});

		filterBinding.setCriteria(model.getEnvironmentCriteria());

		setOpenPageCallback(pageMenuItem -> {
			showPreloader();
			model.notifyCriteriaUpdated();
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		getIntent().putExtra("refreshOnResume", true);
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_environment_list;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getIntent().getBooleanExtra("refreshOnResume", false)) {
			showPreloader();
			if (model.getEnvironmentList().getValue() != null) {
				model.getEnvironmentList().getValue().getDataSource().invalidate();
			}
		}
	}

	@Override
	public void addFiltersToPageMenu() {
		View environmentListFilterView = getLayoutInflater().inflate(R.layout.filter_environment_list_layout, null);
		filterBinding = DataBindingUtil.bind(environmentListFilterView);

		List<Item> environementInvestigationStatuses = DataUtils.getEnumItems(InvestigationStatus.class);
		filterBinding.investigationStatusFilter.initializeSpinner(environementInvestigationStatuses);

		pageMenu.addFilter(environmentListFilterView);

		filterBinding.applyFilters.setOnClickListener(e -> {
			showPreloader();
			pageMenu.hideAll();
			model.notifyCriteriaUpdated();
		});

		filterBinding.resetFilters.setOnClickListener(e -> {
			showPreloader();
			pageMenu.hideAll();
			model.getEnvironmentCriteria().setInvestigationStatus(null);
			filterBinding.invalidateAll();
			model.notifyCriteriaUpdated();
		});
	}

	@Override
	protected Callback getSynchronizeResultCallback() {
		return () -> {
			showPreloader();
			model.getEnvironmentList().getValue().getDataSource().invalidate();
		};
	}

	@Override
	protected PagedBaseListFragment buildListFragment(PageMenuItem menuItem) {
		return EnvironmentListFragment.newInstance();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getNewMenu().setTitle(R.string.action_new_environment);
		return true;
	}

	@Override
	public void goToNewView() {
		EnvironmentNewActivity.startActivity(getContext());
	}

	@Override
	public boolean isEntryCreateAllowed() {
		return ConfigProvider.hasUserRight(UserRight.ENVIRONMENT_CREATE);
	}
}
