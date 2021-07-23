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

import android.content.Context;
import android.os.Bundle;
import android.widget.AdapterView;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.PagedBaseListActivity;
import de.symeda.sormas.app.PagedBaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.immunization.Immunization;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.immunization.edit.ImmunizationNewActivity;

public class ImmunizationListActivity extends PagedBaseListActivity {

	private ImmunizationListViewModel model;

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
				// Scroll to the topmost position after cases have been inserted
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
		model.getImmunizationList().observe(this, immunizations -> {
			adapter.submitList(immunizations);
			hidePreloader();
		});

//		filterBinding.setCriteria(model.getCaseCriteria());

		setOpenPageCallback(p -> {
			showPreloader();
//			model.getCaseCriteria().setInvestigationStatus(statusFilters[((PageMenuItem) p).getPosition()]);
			model.notifyCriteriaUpdated();
		});
	}

	@Override
	public int onNotificationCountChangingAsync(AdapterView parent, PageMenuItem menuItem, int position) {
		return 0;
	}

	@Override
	public void goToNewView() {
		ImmunizationNewActivity.startActivity(getContext());
		finish();
	}

	@Override
	public boolean isEntryCreateAllowed() {
		return ConfigProvider.hasUserRight(UserRight.IMMUNIZATION_CREATE);
	}

	@Override
	public void addFiltersToPageMenu() {

	}

	@Override
	protected PagedBaseListFragment buildListFragment(PageMenuItem menuItem) {
		return ImmunizationListFragment.newInstance();
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_immunizations_list;
	}
}
