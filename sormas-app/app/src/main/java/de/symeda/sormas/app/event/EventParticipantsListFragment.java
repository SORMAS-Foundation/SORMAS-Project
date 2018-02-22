package de.symeda.sormas.app.event;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.List;

import de.symeda.sormas.app.AbstractTabActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.SyncCallback;

public class EventParticipantsListFragment extends ListFragment {

    private String eventUuid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_layout, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateArrayAdapter();
    }

    public void updateArrayAdapter() {
        eventUuid = getArguments().getString(Event.UUID);
        if (eventUuid != null) {
            final Event event = DatabaseHelper.getEventDao().queryUuid(eventUuid);
            updateEventParticipants(event);

            final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swiperefresh);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    ((AbstractTabActivity)getActivity()).synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly, true, false, true, refreshLayout, null);
                }
            });
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        EventParticipantsListArrayAdapter adapter = new EventParticipantsListArrayAdapter(
                this.getActivity(),             // Context for the activity.
                R.layout.event_participants_list_item);     // Layout to use (build)

        setListAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent,
                    View viewClicked,
                    int position, long id) {
                EventParticipant eventParticipant = (EventParticipant) getListAdapter().getItem(position);
                showEventParticipantEditView(eventParticipant);
            }
        });
    }

    public void showEventParticipantEditView(EventParticipant eventParticipant) {
        Intent intent = new Intent(getActivity(), EventParticipantEditActivity.class);
        intent.putExtra(EventParticipant.UUID, eventParticipant.getUuid());
        startActivity(intent);
    }

    private void updateEventParticipants(final Event event) {
        List<EventParticipant> eventParticipants = DatabaseHelper.getEventParticipantDao().getByEvent(event);
        ArrayAdapter<EventParticipant> listAdapter = (ArrayAdapter<EventParticipant>) getListAdapter();
        listAdapter.clear();
        listAdapter.addAll(eventParticipants);

        if (listAdapter.getCount() == 0) {
            getView().findViewById(R.id.empty_list_hint).setVisibility(View.VISIBLE);
            getView().findViewById(android.R.id.list).setVisibility(View.GONE);
        } else {
            getView().findViewById(R.id.empty_list_hint).setVisibility(View.GONE);
            getView().findViewById(android.R.id.list).setVisibility(View.VISIBLE);
        }
    }

}
