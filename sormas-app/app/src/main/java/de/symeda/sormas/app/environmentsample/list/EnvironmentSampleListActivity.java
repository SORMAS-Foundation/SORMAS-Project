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

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.PagedBaseListActivity;
import de.symeda.sormas.app.PagedBaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.databinding.FilterEnvironmentSampleListLayoutBinding;
import de.symeda.sormas.app.sample.ShipmentStatus;
import de.symeda.sormas.app.util.Callback;

public class EnvironmentSampleListActivity extends PagedBaseListActivity {

	private static ShipmentStatus[] statusFilters = new ShipmentStatus[] {
		null,
		ShipmentStatus.NOT_SHIPPED,
		ShipmentStatus.SHIPPED,
		ShipmentStatus.RECEIVED };

	private EnvironmentSampleListViewModel model;
	private FilterEnvironmentSampleListLayoutBinding filterBinding;

	public static void startActivity(Context context, ShipmentStatus listFilter) {
		BaseListActivity.startActivity(context, EnvironmentSampleListActivity.class, buildBundle(getStatusFilterPosition(statusFilters, listFilter)));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		showPreloader();

		adapter = new EnvironmentSampleListAdapter();
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				// Scroll to the topmost position after environment samples have been inserted
				if (positionStart == 0) {
					RecyclerView recyclerView = findViewById(R.id.recyclerViewForList);
					if (recyclerView != null) {
						recyclerView.scrollToPosition(0);
					}
				}
			}

			@Override
			public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
				RecyclerView recyclerView = findViewById(R.id.recyclerViewForList);
				if (recyclerView != null) {
					recyclerView.scrollToPosition(0);
				}
			}
		});

		model = new ViewModelProvider(this).get(EnvironmentSampleListViewModel.class);
		model.initializeViewModel();
		model.getEnvironmentSamples().observe(this, es -> {
			adapter.submitList(es);
			hidePreloader();
		});
		setOpenPageCallback(p -> {
			showPreloader();
			model.getCriteria().shipmentStatus(statusFilters[((PageMenuItem) p).getPosition()]);
			model.notifyCriteriaUpdated();
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		getIntent().putExtra("refreshOnResume", true);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getIntent().getBooleanExtra("refreshOnResume", false)) {
			showPreloader();
			model.getEnvironmentSamples().getValue().getDataSource().invalidate();
		}
	}

	@Override
	public List<PageMenuItem> getPageMenuData() {
		return PageMenuItem.fromEnum(statusFilters, getContext());
	}

	@Override
	protected Callback getSynchronizeResultCallback() {
		// Reload the list after a synchronization has been done
		return () -> {
			showPreloader();
			model.getEnvironmentSamples().getValue().getDataSource().invalidate();
		};
	}

	@Override
	protected PagedBaseListFragment buildListFragment(PageMenuItem menuItem) {
		if (menuItem != null) {
			ShipmentStatus listFilter = statusFilters[menuItem.getPosition()];
			return EnvironmentSampleListFragment.newInstance(listFilter);
		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getNewMenu().setTitle(R.string.action_new_environment_sample);
		return true;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_environment_sample_list;
	}

	@Override
	public void addFiltersToPageMenu() {
		View sampleListFilterView = getLayoutInflater().inflate(R.layout.filter_environment_sample_list_layout, null);
		filterBinding = DataBindingUtil.bind(sampleListFilterView);
		pageMenu.addFilter(sampleListFilterView);
	}

}
