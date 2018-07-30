package de.symeda.sormas.app.sample.list;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.symeda.sormas.app.BaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.sample.ShipmentStatus;
import de.symeda.sormas.app.sample.read.SampleReadActivity;
import de.symeda.sormas.app.searchstrategy.ISearchExecutor;
import de.symeda.sormas.app.searchstrategy.ISearchResultCallback;
import de.symeda.sormas.app.searchstrategy.SearchStrategyFor;
import de.symeda.sormas.app.util.SubheadingHelper;

public class SampleListFragment extends BaseListFragment<SampleListAdapter> implements OnListItemClickListener {

    private AsyncTask searchTask;
    private List<Sample> samples;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerViewForList;

    public static SampleListFragment newInstance(ShipmentStatus listFilter) {
        return newInstance(SampleListFragment.class, null, listFilter);
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
        return new SampleListAdapter(R.layout.row_sample_list_item_layout, this, samples);
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Sample sample = (Sample) item;
        SampleReadActivity.startActivity(getContext(), sample.getUuid());
    }

    @Override
    protected int getEmptyListEntityResId() {
        return R.string.entity_sample;
    }

    @Override
    public void onResume() {
        super.onResume();

        getSubHeadingHandler().updateSubHeadingTitle(SubheadingHelper.getSubHeading(getResources(), SearchBy.BY_FILTER_STATUS, getListFilter(), "Sample"));

        ISearchExecutor<Sample> executor = SearchStrategyFor.SAMPLE.selector(SearchBy.BY_FILTER_STATUS, getListFilter(), null);
        searchTask = executor.search(new ISearchResultCallback<Sample>() {
            @Override
            public void preExecute() {
                getBaseActivity().showPreloader();
            }

            @Override
            public void searchResult(List<Sample> result, BoolResult resultStatus) {
                getBaseActivity().hidePreloader();

                if (!resultStatus.isSuccess()) {
                    String message = String.format(getResources().getString(R.string.notification_records_not_retrieved), "Samples");
                    NotificationHelper.showNotification((NotificationContext) getActivity(), NotificationType.ERROR, message);

                    return;
                }

                samples = result;

                if (SampleListFragment.this.isResumed()) {
                    SampleListFragment.this.getListAdapter().replaceAll(samples);
                    SampleListFragment.this.getListAdapter().notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
