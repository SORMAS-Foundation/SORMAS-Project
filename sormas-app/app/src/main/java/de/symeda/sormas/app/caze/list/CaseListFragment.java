package de.symeda.sormas.app.caze.list;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.BaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.searchstrategy.ISearchExecutor;
import de.symeda.sormas.app.searchstrategy.ISearchResultCallback;
import de.symeda.sormas.app.searchstrategy.SearchStrategyFor;
import de.symeda.sormas.app.util.SubheadingHelper;

public class CaseListFragment extends BaseListFragment<CaseListAdapter> implements OnListItemClickListener {

    private AsyncTask searchTask;
    private List<Case> cases;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerViewForList;

    public static CaseListFragment newInstance(InvestigationStatus listFilter) {
        return newInstance(CaseListFragment.class, null, listFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewForList = (RecyclerView) view.findViewById(R.id.recyclerViewForList);

        return view;
    }

    @Override
    public CaseListAdapter getNewListAdapter() {
        return new CaseListAdapter(this.getActivity(), R.layout.row_case_list_item_layout, this, this.cases);
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Case caze = (Case) item;
        CaseReadActivity.startActivity(getContext(), caze.getUuid());
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

        getSubHeadingHandler().updateSubHeadingTitle(SubheadingHelper.getSubHeading(getResources(), SearchBy.BY_FILTER_STATUS, getListFilter(), "Case"));

        ISearchExecutor<Case> executor = SearchStrategyFor.CASE.selector(SearchBy.BY_FILTER_STATUS, getListFilter(), null);
        searchTask = executor.search(new ISearchResultCallback<Case>() {
            @Override
            public void preExecute() {
                getBaseActivity().showPreloader();
            }

            @Override
            public void searchResult(List<Case> result, BoolResult resultStatus) {
                getBaseActivity().hidePreloader();

                if (!resultStatus.isSuccess()) {
                    String message = String.format(getResources().getString(R.string.notification_records_not_retrieved), "Cases");
                    NotificationHelper.showNotification((NotificationContext) getActivity(), NotificationType.ERROR, message);

                    return;
                } else {
                    cases = result;

                    if (CaseListFragment.this.isResumed()) {
                        CaseListFragment.this.getListAdapter().replaceAll(cases);
                        CaseListFragment.this.getListAdapter().notifyDataSetChanged();
                    }
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
