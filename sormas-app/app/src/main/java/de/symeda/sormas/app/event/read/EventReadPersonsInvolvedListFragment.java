package de.symeda.sormas.app.event.read;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.List;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.event.read.eventparticipant.EventParticipantReadActivity;

public class EventReadPersonsInvolvedListFragment extends BaseReadFragment<FragmentFormListLayoutBinding, List<EventParticipant>, Event> implements OnListItemClickListener {

    private List<EventParticipant> record;

    private EventReadPersonsInvolvedAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    public static EventReadPersonsInvolvedListFragment newInstance(Event activityRootData) {
        return newInstance(EventReadPersonsInvolvedListFragment.class, null, activityRootData);
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        Event event = getActivityRootData();
        record = DatabaseHelper.getEventParticipantDao().getByEvent(event);
    }

    @Override
    public void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
        updateEmptyListHint(record);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new EventReadPersonsInvolvedAdapter(
                R.layout.row_read_persons_involved_list_item_layout, EventReadPersonsInvolvedListFragment.this, record);

        contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        contentBinding.recyclerViewForList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
