package de.symeda.sormas.app.caze.edit;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.List;

import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.sample.edit.SampleEditActivity;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.shared.SampleFormNavigationCapsule;
import de.symeda.sormas.app.util.SampleHelper;

public class CaseEditSampleListFragment extends BaseEditFragment<FragmentFormListLayoutBinding, List<Sample>, Case> implements OnListItemClickListener {

    private List<Sample> record;
    private CaseEditSampleListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

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
    protected void prepareFragmentData(Bundle savedInstanceState) {
        Case caze = getActivityRootData();
        record = DatabaseHelper.getSampleDao().queryByCase(caze);
    }

    @Override
    public void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
        showEmptyListHintWithAdd(record, R.string.entity_sample);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new CaseEditSampleListAdapter(R.layout.row_edit_sample_list_item_layout, this, record);
        contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        contentBinding.recyclerViewForList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public int getRootEditLayout() {
        return R.layout.fragment_root_list_form_layout;
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_form_list_layout;
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Sample s = (Sample) item;
        SampleFormNavigationCapsule dataCapsule = new SampleFormNavigationCapsule(getContext(), s.getUuid(), SampleHelper.getShipmentStatus(s));
        SampleEditActivity.goToActivity(getActivity(), dataCapsule);
    }

    @Override
    public boolean isShowSaveAction() {
        return false;
    }

    @Override
    public boolean isShowAddAction() {
        return true;
    }

    public static CaseEditSampleListFragment newInstance(CaseFormNavigationCapsule capsule, Case activityRootData) {
        return newInstance(CaseEditSampleListFragment.class, capsule, activityRootData);
    }
}
