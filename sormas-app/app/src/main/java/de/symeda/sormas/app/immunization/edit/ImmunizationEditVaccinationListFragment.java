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

package de.symeda.sormas.app.immunization.edit;

import java.util.List;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.immunization.Immunization;
import de.symeda.sormas.app.backend.vaccination.Vaccination;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.immunization.vaccination.VaccinationEditActivity;
import de.symeda.sormas.app.immunization.vaccination.VaccinationListAdapter;
import de.symeda.sormas.app.immunization.vaccination.VaccinationListViewModel;

public class ImmunizationEditVaccinationListFragment extends BaseEditFragment<FragmentFormListLayoutBinding, List<Vaccination>, Immunization>
	implements OnListItemClickListener {

	private VaccinationListAdapter adapter;

	public static ImmunizationEditVaccinationListFragment newInstance(Immunization activityRootData) {
		return newInstance(ImmunizationEditVaccinationListFragment.class, null, activityRootData);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((BaseActivity) getActivity()).showPreloader();
		adapter = new VaccinationListAdapter();
		VaccinationListViewModel model = ViewModelProviders.of(this).get(VaccinationListViewModel.class);
		model.initializeViewModel(getActivityRootData());
		model.getVaccinations().observe(this, vaccination -> {
			((ImmunizationEditActivity) getActivity()).hidePreloader();
			adapter.submitList(vaccination);
			updateEmptyListHint(vaccination);
		});
	}

	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
		adapter.setOnListItemClickListener(this);

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected String getSubHeadingTitle() {
		Resources r = getResources();
		return r.getString(R.string.caption_immunization_vaccinations);
	}

	@Override
	public List<Vaccination> getPrimaryData() {
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
		Vaccination Vaccination = (Vaccination) item;
		getBaseEditActivity().discardStoredRootEntity();
		VaccinationEditActivity.startActivity(getActivity(), Vaccination.getUuid());
	}

	@Override
	public boolean isShowSaveAction() {
		return false;
	}

	@Override
	public boolean isShowNewAction() {
		return true;
	}
}
