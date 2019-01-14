/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.event.edit;

import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import java.util.List;

import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.event.edit.eventparticipant.EventParticipantEditActivity;

public class EventEditPersonsInvolvedListFragment extends BaseEditFragment<FragmentFormListLayoutBinding, List<EventParticipant>, Event> implements OnListItemClickListener {

    private List<EventParticipant> record;
    private EventEditPersonsInvolvedListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    public static EventEditPersonsInvolvedListFragment newInstance(Event activityRootData) {
        return newInstance(EventEditPersonsInvolvedListFragment.class, null, activityRootData);
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_event_participants);
    }

    @Override
    public List<EventParticipant> getPrimaryData() {
        return null;
    }

    @Override
    protected void prepareFragmentData() {
        Event event = getActivityRootData();
        record = DatabaseHelper.getEventParticipantDao().getByEvent(event);
    }

    @Override
    public void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
        updateEmptyListHint(record);
        adapter = new EventEditPersonsInvolvedListAdapter(R.layout.row_read_persons_involved_list_item_layout, this, record);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        contentBinding.recyclerViewForList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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

