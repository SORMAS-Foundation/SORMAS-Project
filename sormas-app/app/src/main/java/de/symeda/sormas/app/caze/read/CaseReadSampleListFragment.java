package de.symeda.sormas.app.caze.read;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.List;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.sample.read.SampleReadActivity;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.shared.SampleFormNavigationCapsule;
import de.symeda.sormas.app.util.SampleHelper;

public class CaseReadSampleListFragment extends BaseReadFragment<FragmentFormListLayoutBinding, List<Sample>, Case> implements OnListItemClickListener {

    private List<Sample> record;

    private CaseReadSampleListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        Case caze = getActivityRootData();
        record = DatabaseHelper.getSampleDao().queryByCase(caze);
    }

    @Override
    public void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
        showEmptyListHint(record, R.string.entity_sample);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new CaseReadSampleListAdapter(R.layout.row_read_case_sample_list_item_layout, this, record);
        contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        contentBinding.recyclerViewForList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_case_samples);
    }

    @Override
    public List<Sample> getPrimaryData() {
        return record;
    }

    @Override
    public int getRootReadLayout() {
        return R.layout.fragment_root_list_form_layout;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_form_list_layout;
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Sample s = (Sample) item;
        SampleFormNavigationCapsule dataCapsule = new SampleFormNavigationCapsule(getContext(), s.getUuid(), SampleHelper.getShipmentStatus(s));
        SampleReadActivity.goToActivity(getActivity(), dataCapsule);
    }

    public static CaseReadSampleListFragment newInstance(CaseFormNavigationCapsule capsule, Case activityRootData) {
        return newInstance(CaseReadSampleListFragment.class, capsule, activityRootData);
    }
}
