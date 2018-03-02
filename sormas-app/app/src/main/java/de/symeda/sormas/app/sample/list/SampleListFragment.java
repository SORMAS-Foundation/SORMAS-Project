package de.symeda.sormas.app.sample.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseListActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.core.SearchStrategy;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.sample.SampleFormNavigationCapsule;
import de.symeda.sormas.app.sample.ShipmentStatus;
import de.symeda.sormas.app.sample.read.SampleReadActivity;

/**
 * Created by Orson on 07/12/2017.
 */

public class SampleListFragment extends BaseListActivityFragment<SampleListAdapter> implements OnListItemClickListener {

    private List<Sample> samples;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerViewForList;
    private ShipmentStatus filterStatus = null;
    private SearchStrategy searchStrategy = null;
    private String recordUuid = null;

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

        filterStatus = (ShipmentStatus) getFilterStatusArg(arguments);
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
    public void onResume() {
        super.onResume();

        ISamplesSearchStrategy strategy = null;
        String subHeading;

        //TODO: Orson - Remove (Old comment)
        //TODO: This whole thing might still change
        if (filterStatus == null)
            filterStatus = ShipmentStatus.NOT_SHIPPED;

        if (searchStrategy == SearchStrategy.BY_FILTER_STATUS && filterStatus != null) {
            subHeading = filterStatus.name();
            strategy = new SamplesSearchByShipmentStatusStrategy(filterStatus);
        } else if (searchStrategy == SearchStrategy.BY_CASE_ID) {
            String format = getResources().getString(R.string.heading_level2_1_tasks_list_by_case);
            subHeading = String.format(format, DataHelper.getShortUuid(recordUuid));
            strategy = new SamplesSearchByCaseStrategy(recordUuid);
        } else {
            subHeading = getResources().getString(R.string.heading_all_records);
            strategy = new SamplesSearchStrategy();
        }

        samples = strategy.search();

        if (samples == null)
            subHeading = getResources().getString(R.string.heading_no_record_found);

        getCommunicator().updateSubHeadingTitle(subHeading);

        this.getListAdapter().replaceAll(samples);
        this.getListAdapter().notifyDataSetChanged();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //recyclerViewForList.setHasFixedSize(true);
        recyclerViewForList.setLayoutManager(linearLayoutManager);
        recyclerViewForList.setAdapter(getListAdapter());
    }

    public static SampleListFragment newInstance(SampleListCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(SampleListFragment.class, capsule);
    }
}
