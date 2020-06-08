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

package de.symeda.sormas.app.event.read;

import java.util.List;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.event.eventparticipant.list.EventParticipantListAdapter;
import de.symeda.sormas.app.event.eventparticipant.list.EventParticipantListViewModel;
import de.symeda.sormas.app.event.eventparticipant.read.EventParticipantReadActivity;

public class EventReadPersonsInvolvedListFragment extends BaseReadFragment<FragmentFormListLayoutBinding, List<EventParticipant>, Event>
	implements OnListItemClickListener {

	private EventParticipantListAdapter adapter;
	private EventParticipantListViewModel model;
	private LinearLayoutManager linearLayoutManager;

	public static EventReadPersonsInvolvedListFragment newInstance(Event activityRootData) {
		return newInstance(EventReadPersonsInvolvedListFragment.class, null, activityRootData);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((EventReadActivity) getActivity()).showPreloader();
		adapter = new EventParticipantListAdapter(R.layout.row_read_persons_involved_list_item_layout, this, null);
		model = ViewModelProviders.of(this).get(EventParticipantListViewModel.class);
		model.getEventParticipants(getActivityRootData()).observe(this, eventParticipants -> {
			adapter.replaceAll(eventParticipants);
			adapter.notifyDataSetChanged();
			updateEmptyListHint(eventParticipants);
			((EventReadActivity) getActivity()).hidePreloader();
		});
	}

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {

	}

	@Override
	public void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
		linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
		contentBinding.recyclerViewForList.setAdapter(adapter);
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
	public int getRootReadLayout() {
		return R.layout.fragment_root_list_form_layout;
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_form_list_layout;
	}

	@Override
	public void onListItemClick(View view, int position, Object item) {
		EventParticipant participant = (EventParticipant) item;
		EventParticipantReadActivity.startActivity(getContext(), participant.getUuid(), getActivityRootData().getUuid());
	}
}
