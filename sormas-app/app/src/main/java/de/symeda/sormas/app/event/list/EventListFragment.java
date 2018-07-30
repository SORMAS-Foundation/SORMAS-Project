package de.symeda.sormas.app.event.list;

import android.os.AsyncTask;
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
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.event.read.EventReadActivity;
import de.symeda.sormas.app.searchstrategy.ISearchExecutor;
import de.symeda.sormas.app.searchstrategy.ISearchResultCallback;
import de.symeda.sormas.app.searchstrategy.SearchStrategyFor;
import de.symeda.sormas.app.util.SubheadingHelper;

public class EventListFragment extends BaseListFragment<EventListAdapter> implements OnListItemClickListener {

    private AsyncTask searchTask;
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
    public EventListAdapter getNewListAdapter() {
        return new EventListAdapter(R.layout.row_event_list_item_layout, this, this.events);
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Event e = (Event) item;
        EventReadActivity.startActivity(getContext(), e.getUuid());
    }

    @Override
    protected int getEmptyListEntityResId() {
        return R.string.entity_event;
    }

    @Override
    public void onResume() {
        super.onResume();

        getSubHeadingHandler().updateSubHeadingTitle(SubheadingHelper.getSubHeading(getResources(), SearchBy.BY_FILTER_STATUS, getListFilter(), "Event"));

        ISearchExecutor<Event> executor = SearchStrategyFor.EVENT.selector(SearchBy.BY_FILTER_STATUS, getListFilter(), null);
        searchTask = executor.search(new ISearchResultCallback<Event>() {
            @Override
            public void preExecute() {
                getBaseActivity().showPreloader();
            }

            @Override
            public void searchResult(List<Event> result, BoolResult resultStatus) {
                getBaseActivity().hidePreloader();

                if (!resultStatus.isSuccess()) {
                    String message = String.format(getResources().getString(R.string.notification_records_not_retrieved), "Events");
                    NotificationHelper.showNotification((NotificationContext) getActivity(), NotificationType.ERROR, message);
                    return;
                }

                events = result;

                if (EventListFragment.this.isResumed()) {
                    EventListFragment.this.getListAdapter().replaceAll(events);
                    EventListFragment.this.getListAdapter().notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //recyclerViewForList.setHasFixedSize(true);
        recyclerViewForList.setLayoutManager(linearLayoutManager);
        recyclerViewForList.setAdapter(getListAdapter());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (searchTask != null && !searchTask.isCancelled())
            searchTask.cancel(true);
    }

}

