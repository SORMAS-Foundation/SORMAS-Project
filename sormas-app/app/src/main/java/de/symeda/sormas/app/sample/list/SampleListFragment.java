package de.symeda.sormas.app.sample.list;

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

import de.symeda.sormas.app.BaseListActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.IListNavigationCapsule;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.sample.read.SampleReadActivity;
import de.symeda.sormas.app.searchstrategy.ISearchExecutor;
import de.symeda.sormas.app.searchstrategy.ISearchResultCallback;
import de.symeda.sormas.app.searchstrategy.SearchStrategyFor;
import de.symeda.sormas.app.shared.SampleFormNavigationCapsule;
import de.symeda.sormas.app.shared.ShipmentStatus;
import de.symeda.sormas.app.util.SubheadingHelper;

/**
 * Created by Orson on 07/12/2017.
 */

public class SampleListFragment extends BaseListActivityFragment<SampleListAdapter> implements OnListItemClickListener {

    private boolean dataLoaded = false;
    private AsyncTask searchTask;
    private List<Sample> samples;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerViewForList;
    private ShipmentStatus filterStatus = null;
    private SearchBy searchBy = null;
    private String recordUuid = null;

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

        filterStatus = (ShipmentStatus) getFilterStatusArg(arguments);
        searchBy = (SearchBy) getSearchStrategyArg(arguments);
        recordUuid = (String) getRecordUuidArg(arguments);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewForList = (RecyclerView) view.findViewById(R.id.recyclerViewForList);

        return view;
    }

    @Override
    public SampleListAdapter getNewListAdapter() {
        return new SampleListAdapter(this.getActivity(), R.layout.row_sample_list_item_layout, this, samples);
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Sample s = (Sample)item;
        SampleFormNavigationCapsule dataCapsule = new SampleFormNavigationCapsule(getContext(), s.getUuid(), filterStatus);
        SampleReadActivity.goToActivity(getActivity(), dataCapsule);
    }

    @Override
    public void cancelTaskExec() {
        if (searchTask != null && !searchTask.isCancelled())
            searchTask.cancel(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        //TODO: Orson - reverse this relationship
        getSubHeadingHandler().updateSubHeadingTitle(SubheadingHelper.getSubHeading(getResources(), searchBy, filterStatus, "Sample"));

        try {
            dataLoaded = false;
            if (!dataLoaded) {
                ISearchExecutor<Sample> executor = SearchStrategyFor.SAMPLE.selector(searchBy, filterStatus, recordUuid);
                searchTask = executor.search(new ISearchResultCallback<Sample>() {
                    @Override
                    public void preExecute() {
                        getActivityCommunicator().showPreloader();
                        getActivityCommunicator().hideFragmentView();
                    }

                    @Override
                    public void searchResult(List<Sample> result, BoolResult resultStatus) {
                        getActivityCommunicator().hidePreloader();

                        if (!resultStatus.isSuccess()) {
                            String message = String.format(getResources().getString(R.string.notification_records_not_retrieved), "Samples");
                            NotificationHelper.showNotification((INotificationContext) getActivity(), NotificationType.ERROR, message);

                            return;
                        }

                        samples = result;

                        SampleListFragment.this.getListAdapter().replaceAll(samples);
                        SampleListFragment.this.getListAdapter().notifyDataSetChanged();

                        dataLoaded = true;

                        getActivityCommunicator().hidePreloader();
                        getActivityCommunicator().showFragmentView();
                    }

                    private ISearchResultCallback<Sample> init() {
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
                    getActivityCommunicator().synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly, true, false, true, swiperefresh, null);
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

    public static SampleListFragment newInstance(IActivityCommunicator communicator, IListNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(communicator, SampleListFragment.class, capsule);
    }

    /*@Override
    public void onDestroy() {
        super.onDestroy();

        if (searchTask != null && !searchTask.isCancelled())
            searchTask.cancel(true);
    }*/
}
