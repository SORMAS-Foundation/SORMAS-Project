/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.immunization.list;

import java.util.List;
import java.util.Random;

import org.joda.time.DateTime;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.PagedBaseListActivity;
import de.symeda.sormas.app.PagedBaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.immunization.Immunization;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.databinding.FilterImmunizationListLayoutBinding;
import de.symeda.sormas.app.immunization.edit.ImmunizationNewActivity;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;

public class ImmunizationListActivity extends PagedBaseListActivity {

	private ImmunizationListViewModel model;
	private FilterImmunizationListLayoutBinding filterBinding;

	public static void startActivity(Context context) {
		List<Immunization> immunizations = DatabaseHelper.getImmunizationDao().getAll();
		int pageMenuPosition = immunizations.size() > 0 ? 1 : 0;
		BaseListActivity.startActivity(context, ImmunizationListActivity.class, buildBundle(pageMenuPosition));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		showPreloader();
		adapter = new ImmunizationListAdapter();
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
		model = ViewModelProviders.of(this).get(ImmunizationListViewModel.class);
		model.initializeViewModel();
		model.getImmunizationList().observe(this, immunizations -> {
			adapter.submitList(immunizations);
			hidePreloader();
		});

		filterBinding.setCriteria(model.getImmunizationCriteria());

		setOpenPageCallback(p -> {
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
	public void onResume() {
		super.onResume();
		if (getIntent().getBooleanExtra("refreshOnResume", false)) {
			showPreloader();
			if (model.getImmunizationList().getValue() != null) {
				model.getImmunizationList().getValue().getDataSource().invalidate();
			}
		}
	}

	@Override
	protected Callback getSynchronizeResultCallback() {
		return () -> {
			showPreloader();
			model.getImmunizationList().getValue().getDataSource().invalidate();
		};
	}

	public int onNotificationCountChangingAsync(AdapterView parent, PageMenuItem menuItem, int position) {
		return (int) (new Random(DateTime.now().getMillis() * 1000).nextInt() / 10000000);
	}

	@Override
	public void goToNewView() {
		ImmunizationNewActivity.startActivity(getContext());
	}

	@Override
	public boolean isEntryCreateAllowed() {
		return ConfigProvider.hasUserRight(UserRight.IMMUNIZATION_CREATE);
	}

	@Override
	public void addFiltersToPageMenu() {
		View immunizationListFilterView = getLayoutInflater().inflate(R.layout.filter_immunization_list_layout, null);
		filterBinding = DataBindingUtil.bind(immunizationListFilterView);

		List<Item> diseases = DataUtils.toItems(DiseaseConfigurationCache.getInstance().getAllDiseases(true, true, true));
		filterBinding.diseaseFilter.initializeSpinner(diseases);

		List<Item> immunizationStatuses = DataUtils.getEnumItems(ImmunizationStatus.class);
		filterBinding.immunizationStatusFilter.initializeSpinner(immunizationStatuses);
		List<Item> immunizationManagementStatuses = DataUtils.getEnumItems(ImmunizationManagementStatus.class);
		filterBinding.immunizationManagementStatusFilter.initializeSpinner(immunizationManagementStatuses);
		List<Item> meansOfImmunizations = DataUtils.getEnumItems(MeansOfImmunization.class);
		filterBinding.meansOfImmunizationFilter.initializeSpinner(meansOfImmunizations);

		filterBinding.reportDateFromFilter.initializeDateField(getSupportFragmentManager());
		filterBinding.reportDateToFilter.initializeDateField(getSupportFragmentManager());

		filterBinding.startDateFromFilter.initializeDateField(getSupportFragmentManager());
		filterBinding.endDateToFilter.initializeDateField(getSupportFragmentManager());

		filterBinding.validFromFilter.initializeDateField(getSupportFragmentManager());
		filterBinding.validToFilter.initializeDateField(getSupportFragmentManager());

		filterBinding.positiveTestResultDateFromFilter.initializeDateField(getSupportFragmentManager());
		filterBinding.positiveTestResultDateToFilter.initializeDateField(getSupportFragmentManager());

		filterBinding.recoveryDateFromFilter.initializeDateField(getSupportFragmentManager());
		filterBinding.recoveryDateToFilter.initializeDateField(getSupportFragmentManager());

		pageMenu.addFilter(immunizationListFilterView);

		filterBinding.applyFilters.setOnClickListener(e -> {
			showPreloader();
			pageMenu.hideAll();
			model.notifyCriteriaUpdated();
		});

		filterBinding.resetFilters.setOnClickListener(e -> {
			showPreloader();
			pageMenu.hideAll();
			model.getImmunizationCriteria().setDisease(null);
			model.getImmunizationCriteria().setImmunizationStatus(null);
			model.getImmunizationCriteria().setImmunizationManagementStatus(null);
			model.getImmunizationCriteria().setMeansOfImmunization(null);
			model.getImmunizationCriteria().setReportDateFrom(null);
			model.getImmunizationCriteria().setReportDateTo(null);
			model.getImmunizationCriteria().setStartDateFrom(null);
			model.getImmunizationCriteria().setEndDateTo(null);
			model.getImmunizationCriteria().setValidFrom(null);
			model.getImmunizationCriteria().setValidUntil(null);
			model.getImmunizationCriteria().setRecoveryDateFrom(null);
			model.getImmunizationCriteria().setRecoveryDateTo(null);
			model.getImmunizationCriteria().setPositiveTestResultDateFrom(null);
			model.getImmunizationCriteria().setPositiveTestResultDateTo(null);
			filterBinding.invalidateAll();
			filterBinding.executePendingBindings();
			model.notifyCriteriaUpdated();
		});
	}

	@Override
	protected PagedBaseListFragment buildListFragment(PageMenuItem menuItem) {
		return ImmunizationListFragment.newInstance();
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_immunizations_list;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getNewMenu().setTitle(R.string.action_new_immunization);
		return true;
	}

}
