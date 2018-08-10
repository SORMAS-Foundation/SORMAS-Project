package de.symeda.sormas.app.event.list;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.BaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.event.read.EventReadActivity;

public class EventListFragment extends BaseListFragment<EventListAdapter> implements OnListItemClickListener {

    private List<Event> events;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerViewForList;

    public static EventListFragment newInstance(EventStatus listFilter) {
        return newInstance(EventListFragment.class, null, listFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewForList = (RecyclerView) view.findViewById(R.id.recyclerViewForList);

        return view;
    }

    @Override
    protected void prepareFragmentData() {
        events = DatabaseHelper.getEventDao().queryForEq(Event.EVENT_STATUS, getListFilter(), Event.REPORT_DATE_TIME, false);
        getListAdapter().replaceAll(events);
    }

    @Override
    public EventListAdapter getNewListAdapter() {
        return new EventListAdapter(R.layout.row_event_list_item_layout, this, this.events);
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Event e = (Event) item;
        EventReadActivity.startActivity(getContext(), e.getUuid(), false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerViewForList.setLayoutManager(linearLayoutManager);
        recyclerViewForList.setAdapter(getListAdapter());
    }
}

