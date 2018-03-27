package de.symeda.sormas.app.caze.read;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentCaseReadSampleLayoutBinding;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.sample.read.SampleReadActivity;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.shared.SampleFormNavigationCapsule;
import de.symeda.sormas.app.util.SampleHelper;

/**
 * Created by Orson on 08/01/2018.
 */

public class CaseReadSampleListFragment extends BaseReadActivityFragment<FragmentCaseReadSampleLayoutBinding, List<Sample>> implements OnListItemClickListener {

    private String recordUuid = null;
    private InvestigationStatus filterStatus = null;
    private CaseClassification pageStatus = null;
    private List<Sample> record;

    private CaseReadSampleListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveFilterStatusState(outState, filterStatus);
        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        filterStatus = (InvestigationStatus) getFilterStatusArg(arguments);
        pageStatus = (CaseClassification) getPageStatusArg(arguments);
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Case caze = DatabaseHelper.getCaseDao().queryUuidReference(recordUuid);
            if (caze != null) {
                resultHolder.forList().add(DatabaseHelper.getSampleDao().queryByCase(caze));
            } else {
                resultHolder.forList().add(new ArrayList<Sample>());
            }
        } else {
            ITaskResultHolderIterator listIterator = resultHolder.forList().iterator();

            if (listIterator.hasNext())
                record =  listIterator.next();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentCaseReadSampleLayoutBinding contentBinding) {
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new CaseReadSampleListAdapter(this.getActivity(), R.layout.row_read_case_sample_list_item_layout, this, record);
        contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        contentBinding.recyclerViewForList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseReadSampleLayoutBinding contentBinding) {

    }

    @Override
    public void onPageResume(FragmentCaseReadSampleLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        final SwipeRefreshLayout swiperefresh = (SwipeRefreshLayout)this.getView().findViewById(R.id.swiperefresh);
        if (swiperefresh != null) {
            swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getActivityCommunicator().synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly, true, false, true, swiperefresh, null);
                }
            });
        }

        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

    }

    @Override
    protected String getSubHeadingTitle() {
        return null;
    }

    @Override
    public List<Sample> getPrimaryData() {
        return record;
    }

    /*@Override
    public FragmentCaseReadSampleLayoutBinding getBinding() {
        return binding;
    }*/

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //recyclerViewForList.setLayoutManager(linearLayoutManager);
        //recyclerViewForList.setAdapter(adapter);
        //binding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        //binding.recyclerViewForList.setAdapter(adapter);
    }

    @Override
    public int getRootReadLayout() {
        return R.layout.fragment_root_list_edit_layout;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_case_read_sample_layout;
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Sample s = (Sample)item;
        SampleFormNavigationCapsule dataCapsule = new SampleFormNavigationCapsule(getContext(), s.getUuid(), SampleHelper.getShipmentStatus(s));
        SampleReadActivity.goToActivity(getActivity(), dataCapsule);


        /*Task record = (Task)item;

        if(record != null) {
            Intent intent = new Intent(getActivity(), CaseReadTaskInfoActivity.class);
            //intent.putExtra(ConstantHelper.ARG_FILTER_STATUS, record.getContact().getFollowUpStatus());
            intent.putExtra(ConstantHelper.KEY_DATA_UUID, record.getUuid());
            intent.putExtra(IStatusElaborator.ARG_TASK_STATUS, record.getTaskStatus());
            intent.putExtra(IStatusElaborator.ARG_FOLLOW_UP_STATUS, followUpStatus);

            startActivity(intent);
        }*/
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    public static CaseReadSampleListFragment newInstance(IActivityCommunicator activityCommunicator, CaseFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, CaseReadSampleListFragment.class, capsule);
    }
}
