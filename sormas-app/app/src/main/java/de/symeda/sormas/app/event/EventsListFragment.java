package de.symeda.sormas.app.event;


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
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;

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
            events = DatabaseHelper.getEventDao().queryForEq(Event.EVENT_STATUS, filterStatus);
        } else {
            events = DatabaseHelper.getEventDao().queryForAll();
        }

        ArrayAdapter<Event> listAdapter = (ArrayAdapter<Event>)getListAdapter();
        listAdapter.clear();
        listAdapter.addAll(events);

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout)getView().findViewById(R.id.swiperefresh);
        if(refreshLayout != null) {
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    SyncEventsTask.syncEvents(getActivity().getSupportFragmentManager(), refreshLayout);
                }
            });
        }
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
//        Intent intent = new Intent(getActivity(), EventEditActivity.class);
//        intent.putExtra(EventEditActivity.KEY_CASE_UUID, event.getUuid());
//        startActivity(intent);
    }
}
