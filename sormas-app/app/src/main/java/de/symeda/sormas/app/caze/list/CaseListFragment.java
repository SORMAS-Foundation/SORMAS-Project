package de.symeda.sormas.app.caze.list;

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

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.BaseListActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.IListNavigationCapsule;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.searchstrategy.ISearchExecutor;
import de.symeda.sormas.app.searchstrategy.ISearchResultCallback;
import de.symeda.sormas.app.searchstrategy.SearchStrategyFor;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.util.SubheadingHelper;

/**
 * Created by Orson on 05/12/2017.
 */

public class CaseListFragment extends BaseListActivityFragment<CaseListAdapter> implements OnListItemClickListener {

    private boolean dataLoaded = false;
    private AsyncTask searchTask;
    private List<Case> cases;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerViewForList;
    private InvestigationStatus filterStatus = null;
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

        filterStatus = (InvestigationStatus) getFilterStatusArg(arguments);
        searchBy = (SearchBy) getSearchStrategyArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewForList = (RecyclerView)view.findViewById(R.id.recyclerViewForList);

        return view;
    }

    @Override
    public CaseListAdapter getNewListAdapter() {
        return new CaseListAdapter(this.getActivity(), R.layout.row_case_list_item_layout, this, this.cases);
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Case c = (Case)item;
        CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(getContext(),
                c.getUuid()).setReadPageStatus(c.getCaseClassification());
        CaseReadActivity.goToActivity(getActivity(), dataCapsule);
    }

    @Override
    public void cancelTaskExec() {
        if (searchTask != null && !searchTask.isCancelled())
            searchTask.cancel(true);
    }

    @Override
    protected int getEmptyListEntityResId() {
        return R.string.entity_case;
    }

    @Override
    protected boolean canAddToList() {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        //TODO: Orson - reverse this relationship
        getSubHeadingHandler().updateSubHeadingTitle(SubheadingHelper.getSubHeading(getResources(), searchBy, filterStatus, "Case"));

        try {
            dataLoaded = false;
            if (!dataLoaded) {
                ISearchExecutor<Case> executor = SearchStrategyFor.CASE.selector(searchBy, filterStatus, recordUuid);
                searchTask = executor.search(new ISearchResultCallback<Case>() {
                    @Override
                    public void preExecute() {
                        getActivityCommunicator().showPreloader();
                        getActivityCommunicator().hideFragmentView();
                    }

                    @Override
                    public void searchResult(List<Case> result, BoolResult resultStatus) {
                        getActivityCommunicator().hidePreloader();

                        if (!resultStatus.isSuccess()) {
                            String message = String.format(getResources().getString(R.string.notification_records_not_retrieved), "Cases");
                            NotificationHelper.showNotification((INotificationContext) getActivity(), NotificationType.ERROR, message);

                            return;
                        }

                        cases = result;

                        CaseListFragment.this.getListAdapter().replaceAll(cases);
                        CaseListFragment.this.getListAdapter().notifyDataSetChanged();
                        //CaseListFragment.this.getListAdapter().updateUnreadIndicator();

                        dataLoaded = true;

                        getActivityCommunicator().hidePreloader();
                        getActivityCommunicator().showFragmentView();
                    }

                    private ISearchResultCallback<Case> init() {
                        getActivityCommunicator().showPreloader();

                        return this;
                    }
                }.init());
            }
        } catch (Exception ex) {
            if (getActivityCommunicator() != null) {
                getActivityCommunicator().hidePreloader();
            }
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

    @Override
    public boolean showNewAction() {
        User user = ConfigProvider.getUser();
        return user.hasUserRight(UserRight.CASE_CREATE);
    }

    public static CaseListFragment newInstance(IActivityCommunicator communicator, IListNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(communicator, CaseListFragment.class, capsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (searchTask != null && !searchTask.isCancelled())
            searchTask.cancel(true);
    }
}
