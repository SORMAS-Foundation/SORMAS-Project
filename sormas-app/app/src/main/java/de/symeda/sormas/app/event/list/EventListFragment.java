package de.symeda.sormas.app.event.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.BaseListActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.SearchStrategy;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.event.EventFormNavigationCapsule;
import de.symeda.sormas.app.event.read.EventReadActivity;

import java.util.List;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.backend.event.Event;

/**
 * Created by Orson on 07/12/2017.
 */

public class EventListFragment extends BaseListActivityFragment<EventListAdapter> implements OnListItemClickListener {

    private List<Event> events;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerViewForList;
    private EventStatus filterStatus = null;
    private SearchStrategy searchStrategy = null;
    String recordUuid = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveFilterStatusState(outState, filterStatus);
        SaveSearchStrategyState(outState, searchStrategy);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        filterStatus = (EventStatus) getFilterStatusArg(arguments);
        searchStrategy = (SearchStrategy) getSearchStrategyArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewForList = (RecyclerView) view.findViewById(R.id.recyclerViewForList);

        return view;
    }

    @Override
    public EventListAdapter getNewListAdapter() {
        return new EventListAdapter(this.getActivity(), R.layout.row_event_list_item_layout, this, this.events);
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Event e = (Event) item;
        EventFormNavigationCapsule dataCapsule = new EventFormNavigationCapsule(getContext(),
                e.getUuid(), e.getEventStatus());//, e.getEventType());
        EventReadActivity.goToActivity(getActivity(), dataCapsule);
    }

    @Override
    public void onResume() {
        super.onResume();

        IEventsSearchStrategy searchStrategy;

        //TODO: Orson remove
        if (filterStatus == null)
            filterStatus = EventStatus.POSSIBLE;

        getCommunicator().updateSubHeadingTitle(filterStatus != null ? filterStatus.toString() : "");

        searchStrategy = new EventsSearchByEventStatusStrategy(filterStatus);
        events = searchStrategy.search();

        this.getListAdapter().replaceAll(events);
        this.getListAdapter().notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //recyclerViewForList.setHasFixedSize(true);
        recyclerViewForList.setLayoutManager(linearLayoutManager);
        recyclerViewForList.setAdapter(getListAdapter());
    }

    public static EventListFragment newInstance(EventListCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(EventListFragment.class, capsule);
    }

}

