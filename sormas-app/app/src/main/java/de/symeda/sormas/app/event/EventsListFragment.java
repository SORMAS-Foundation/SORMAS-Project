package de.symeda.sormas.app.event;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.List;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.AbstractTabActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;

public class EventsListFragment extends ListFragment {

    public static final String ARG_FILTER_STATUS = "filterStatus";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_layout, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        List<Event> events;
        Bundle arguments = getArguments();
        if (arguments.containsKey(ARG_FILTER_STATUS)) {
            EventStatus filterStatus = (EventStatus)arguments.getSerializable(ARG_FILTER_STATUS);
            events = DatabaseHelper.getEventDao().queryForEq(Event.EVENT_STATUS, filterStatus, Event.EVENT_DATE, false);
        } else {
            events = DatabaseHelper.getEventDao().queryForAll(Event.EVENT_DATE, false);
        }

        ArrayAdapter<Event> listAdapter = (ArrayAdapter<Event>)getListAdapter();
        listAdapter.clear();
        listAdapter.addAll(events);

        if (listAdapter.getCount() == 0) {
            this.getView().findViewById(R.id.empty_list_hint).setVisibility(View.VISIBLE);
            this.getView().findViewById(android.R.id.list).setVisibility(View.GONE);
        } else {
            this.getView().findViewById(R.id.empty_list_hint).setVisibility(View.GONE);
            this.getView().findViewById(android.R.id.list).setVisibility(View.VISIBLE);
        }

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout)getView().findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((AbstractTabActivity)getActivity()).synchronizeData(SynchronizeDataAsync.SyncMode.Changes, true, false, true, refreshLayout, null);
            }
        });

        ((EventsListArrayAdapter)getListAdapter()).updateUnreadIndicator();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        EventsListArrayAdapter adapter = new EventsListArrayAdapter(
                this.getActivity(),
                R.layout.events_list_item);

        setListAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent,
                    View viewClicked,
                    int position, long id) {
                Event event = (Event)getListAdapter().getItem(position);
                showEditView(event);
            }
        });
    }

    public void showEditView(Event event) {
        Intent intent = new Intent(getActivity(), EventEditActivity.class);
        intent.putExtra(EventEditActivity.KEY_EVENT_UUID, event.getUuid());
        startActivity(intent);
    }
}
