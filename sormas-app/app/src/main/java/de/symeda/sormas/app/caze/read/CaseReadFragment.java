package de.symeda.sormas.app.caze.read;

import android.os.Bundle;
import android.support.annotation.Nullable;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.caze.CaseFormNavigationCapsule;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentCaseReadLayoutBinding;

/**
 * Created by Orson on 08/01/2018.
 */

public class CaseReadFragment extends BaseReadActivityFragment<FragmentCaseReadLayoutBinding, Case> {

    private String recordUuid = null;
    private InvestigationStatus filterStatus = null;
    private CaseClassification pageStatus = null;
    private Case record;

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
            resultHolder.forItem().add(DatabaseHelper.getCaseDao().queryUuid(recordUuid));
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            if (itemIterator.hasNext())
                record =  itemIterator.next();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentCaseReadLayoutBinding contentBinding) {
        contentBinding.setData(record);
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseReadLayoutBinding contentBinding) {

    }

    @Override
    protected String getSubHeadingTitle() {
        return null;
    }

    @Override
    public Case getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_case_read_layout;
    }

    public static CaseReadFragment newInstance(IActivityCommunicator activityCommunicator, CaseFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, CaseReadFragment.class, capsule);
    }
}
