package de.symeda.sormas.app.event.list;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.BaseListActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.IListNavigationCapsule;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.event.read.EventReadActivity;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.searchstrategy.ISearchExecutor;
import de.symeda.sormas.app.searchstrategy.ISearchResultCallback;
import de.symeda.sormas.app.searchstrategy.SearchStrategyFor;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;
import de.symeda.sormas.app.util.SubheadingHelper;

/**
 * Created by Orson on 07/12/2017.
 */

public class EventListFragment extends BaseListActivityFragment<EventListAdapter> implements OnListItemClickListener {

    private boolean dataLoaded = false;
    private AsyncTask searchTask;
    private List<Event> events;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerViewForList;
    private EventStatus filterStatus = null;
    private SearchBy searchBy = null;
    String recordUuid = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveFilterStatusState(outState, filterStatus);
        SaveSearchStrategyState(outState, searchBy);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        filterStatus = (EventStatus) getFilterStatusArg(arguments);
        searchBy = (SearchBy) getSearchStrategyArg(arguments);
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
    public void cancelTaskExec() {
        if (searchTask != null && !searchTask.isCancelled())
            searchTask.cancel(true);
    }

    @Override
    protected int getEmptyListEntityResId() {
        return R.string.entity_event;
    }

    @Override
    public void onResume() {
        super.onResume();

        //TODO: Orson - reverse this relationship
        getSubHeadingHandler().updateSubHeadingTitle(SubheadingHelper.getSubHeading(getResources(), searchBy, filterStatus, "Event"));

        try {
            dataLoaded = false;
            if (!dataLoaded) {
                ISearchExecutor<Event> executor = SearchStrategyFor.EVENT.selector(searchBy, filterStatus, recordUuid);
                searchTask = executor.search(new ISearchResultCallback<Event>() {
                    @Override
                    public void preExecute() {
                        getActivityCommunicator().showPreloader();
                        getActivityCommunicator().hideFragmentView();
                    }

                    @Override
                    public void searchResult(List<Event> result, BoolResult resultStatus) {
                        getActivityCommunicator().hidePreloader();

                        if (!resultStatus.isSuccess()) {
                            String message = String.format(getResources().getString(R.string.notification_records_not_retrieved), "Events");
                            NotificationHelper.showNotification((INotificationContext) getActivity(), NotificationType.ERROR, message);

                            return;
                        }

                        events = result;

                        EventListFragment.this.getListAdapter().replaceAll(events);
                        EventListFragment.this.getListAdapter().notifyDataSetChanged();

                        dataLoaded = true;

                        getActivityCommunicator().hidePreloader();
                        getActivityCommunicator().showFragmentView();
                    }

                    private ISearchResultCallback<Event> init() {
                        getActivityCommunicator().showPreloader();

                        return this;
                    }
                }.init());
            }
        } catch (Exception ex) {
            getActivityCommunicator().hidePreloader();
            dataLoaded = false;
        }

        final SwipeRefreshLayout swiperefresh = (SwipeRefreshLayout)this.getView().findViewById(R.id.swiperefresh);
        if (swiperefresh != null) {
            swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getActivityCommunicator().synchronizeData(SynchronizeDataAsync.SyncMode.Changes, true, false, true, swiperefresh, null);
                }
            });
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //recyclerViewForList.setHasFixedSize(true);
        recyclerViewForList.setLayoutManager(linearLayoutManager);
        recyclerViewForList.setAdapter(getListAdapter());
    }

    public static EventListFragment newInstance(IActivityCommunicator communicator, IListNavigationCapsule capsule) {
        return newInstance(communicator, EventListFragment.class, capsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (searchTask != null && !searchTask.isCancelled())
            searchTask.cancel(true);
    }

}

