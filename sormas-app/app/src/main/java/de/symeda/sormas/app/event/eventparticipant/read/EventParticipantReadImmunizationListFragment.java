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

package de.symeda.sormas.app.event.eventparticipant.read;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.immunization.Immunization;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.immunization.list.ImmunizationListAdapter;
import de.symeda.sormas.app.immunization.list.ImmunizationListViewModel;
import de.symeda.sormas.app.immunization.read.ImmunizationReadActivity;

public class EventParticipantReadImmunizationListFragment
	extends BaseReadFragment<FragmentFormListLayoutBinding, List<Immunization>, EventParticipant>
	implements OnListItemClickListener {

	private ImmunizationListAdapter immunizationListAdapter;

	public static EventParticipantReadImmunizationListFragment newInstance(EventParticipant activityRootData) {
		return newInstance(EventParticipantReadImmunizationListFragment.class, null, activityRootData);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((EventParticipantReadActivity) getActivity()).showPreloader();
		immunizationListAdapter = new ImmunizationListAdapter();
		ImmunizationListViewModel model = ViewModelProviders.of(this).get(ImmunizationListViewModel.class);
		model.initializeViewModel(getActivityRootData());
		model.getImmunizationList().observe(this, immunizations -> {
			immunizationListAdapter.submitList(immunizations);
			((EventParticipantReadActivity) getActivity()).hidePreloader();
			updateEmptyListHint(immunizations);
		});

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		immunizationListAdapter.setOnListItemClickListener(this);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {

	}

	@Override
	protected void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
		contentBinding.recyclerViewForList.setAdapter(immunizationListAdapter);
	}

	@Override
	protected String getSubHeadingTitle() {
		Resources r = getResources();
		return r.getString(R.string.caption_event_participant_immunizations);
	}

	@Override
	public List<Immunization> getPrimaryData() {
		throw new UnsupportedOperationException("Sub list fragments don't hold their data");
	}

	@Override
	public int getRootReadLayout() {
		return R.layout.fragment_root_list_form_layout;
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_form_list_layout;
	}

	@Override
	public void onListItemClick(View v, int position, Object item) {
		Immunization immunization = (Immunization) item;
		ImmunizationReadActivity.startActivity(getActivity(), immunization.getUuid());
	}
}
