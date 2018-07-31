package de.symeda.sormas.app.sample.list;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.symeda.sormas.app.BaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.sample.ShipmentStatus;
import de.symeda.sormas.app.sample.read.SampleReadActivity;

public class SampleListFragment extends BaseListFragment<SampleListAdapter> implements OnListItemClickListener {

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
    protected void prepareFragmentData() {

        switch ((ShipmentStatus) getListFilter()) {
            case NOT_SHIPPED:
                samples = DatabaseHelper.getSampleDao().queryNotShipped();
                break;
            case SHIPPED:
                samples = DatabaseHelper.getSampleDao().queryShipped();
                break;
            case RECEIVED:
                samples = DatabaseHelper.getSampleDao().queryReceived();
                break;
            case REFERRED_OTHER_LAB:
                samples = DatabaseHelper.getSampleDao().queryReferred();
                break;
            default:
                throw new IllegalArgumentException(getListFilter().toString());
        }
        getListAdapter().replaceAll(samples);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerViewForList.setLayoutManager(linearLayoutManager);
        recyclerViewForList.setAdapter(getListAdapter());
    }
}
