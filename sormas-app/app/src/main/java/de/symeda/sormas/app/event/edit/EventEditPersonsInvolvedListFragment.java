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

package de.symeda.sormas.app.event.edit;

import java.util.List;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.event.eventparticipant.edit.EventParticipantEditActivity;
import de.symeda.sormas.app.event.eventparticipant.list.EventParticipantListAdapter;
import de.symeda.sormas.app.event.eventparticipant.list.EventParticipantListViewModel;

public class EventEditPersonsInvolvedListFragment extends BaseEditFragment<FragmentFormListLayoutBinding, List<EventParticipant>, Event>
	implements OnListItemClickListener {

	private EventParticipantListAdapter adapter;
	private EventParticipantListViewModel model;
	private LinearLayoutManager linearLayoutManager;

	public static EventEditPersonsInvolvedListFragment newInstance(Event activityRootData) {
		return newInstance(EventEditPersonsInvolvedListFragment.class, null, activityRootData);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((EventEditActivity) getActivity()).showPreloader();
		adapter = new EventParticipantListAdapter(R.layout.row_read_persons_involved_list_item_layout, this, null);
		model = ViewModelProviders.of(this).get(EventParticipantListViewModel.class);
		model.getEventParticipants(getActivityRootData()).observe(this, eventParticipants -> {
			adapter.replaceAll(eventParticipants);
			adapter.notifyDataSetChanged();
			updateEmptyListHint(eventParticipants);
			((EventEditActivity) getActivity()).hidePreloader();
		});
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_event_participants);
	}

	@Override
	public List<EventParticipant> getPrimaryData() {
		throw new UnsupportedOperationException("Sub list fragments don't hold their data");
	}

	@Override
	protected void prepareFragmentData() {

	}

	@Override
	public void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
		linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
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
	public boolean isShowSaveAction() {
		return false;
	}

	@Override
	public boolean isShowNewAction() {
		return true;
	}

	@Override
	public void onListItemClick(View view, int position, Object item) {
		EventParticipant o = (EventParticipant) item;
		EventParticipantEditActivity.startActivity(getContext(), o.getUuid(), getActivityRootData().getUuid());
	}
}
