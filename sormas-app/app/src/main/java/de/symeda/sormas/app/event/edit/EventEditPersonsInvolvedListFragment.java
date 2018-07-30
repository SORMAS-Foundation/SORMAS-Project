package de.symeda.sormas.app.event.edit;

import android.support.v7.widget.LinearLayoutManager;
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
        return getResources().getString(R.string.caption_persons_involved);
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
        showEmptyListHintWithAdd(record, R.string.entity_event_participant);

        //Create adapter and set data
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
    public boolean isShowAddAction() {
        return true;
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        EventParticipant o = (EventParticipant) item;
        EventParticipantEditActivity.startActivity(getContext(), o.getUuid(), getActivityRootData().getUuid());
    }
}

