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

package de.symeda.sormas.app.caze.edit;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.clinicalcourse.ClinicalVisit;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.clinicalcourse.ClinicalVisitSection;
import de.symeda.sormas.app.clinicalcourse.edit.ClinicalVisitEditActivity;
import de.symeda.sormas.app.clinicalcourse.list.ClinicalVisitListAdapter;
import de.symeda.sormas.app.clinicalcourse.list.ClinicalVisitListViewModel;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;

public class CaseEditClinicalVisitListFragment extends BaseEditFragment<FragmentFormListLayoutBinding, List<ClinicalVisit>, Case>
	implements OnListItemClickListener {

	private ClinicalVisitListAdapter adapter;

	public static CaseEditClinicalVisitListFragment newInstance(Case activityRootData) {
		return newInstance(CaseEditClinicalVisitListFragment.class, null, activityRootData);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((BaseActivity) getActivity()).showPreloader();
		adapter = new ClinicalVisitListAdapter();
		ClinicalVisitListViewModel model = new ViewModelProvider(this).get(ClinicalVisitListViewModel.class);
		model.initializeViewModel(getActivityRootData().getClinicalCourse());
		model.getClinicalVisits().observe(this, clinicalVisits -> {
			((BaseActivity) getActivity()).hidePreloader();
			adapter.submitList(clinicalVisits);
			updateEmptyListHint(clinicalVisits);
		});
	}

	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
		adapter.setOnListItemClickListener(this);

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_case_clinical_visits);
	}

	@Override
	public List<ClinicalVisit> getPrimaryData() {
		throw new UnsupportedOperationException("Sub list fragments don't hold their data");
	}

	@Override
	protected void prepareFragmentData() {

	}

	@Override
	public void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
		contentBinding.recyclerViewForList.setAdapter(adapter);
	}

	@Override
	public int getRootEditLayout() {
		return R.layout.fragment_root_list_form_layout;
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_form_list_layout;
	}

	@Override
	public void onListItemClick(View view, int position, Object item) {
		ClinicalVisit clinicalVisit = (ClinicalVisit) item;
		ClinicalVisitEditActivity
			.startActivity(getActivity(), clinicalVisit.getUuid(), getActivityRootData().getUuid(), ClinicalVisitSection.VISIT_INFO);
	}

	@Override
	public boolean isShowSaveAction() {
		return false;
	}

	@Override
	public boolean isShowNewAction() {
		return ConfigProvider.hasUserRight(UserRight.CLINICAL_VISIT_CREATE);
	}
}
