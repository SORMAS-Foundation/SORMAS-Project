package de.symeda.sormas.app.caze.read;

import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.annotation.Nullable;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentCaseReadHospitalizationLayoutBinding;

/**
 * Created by Orson on 08/01/2018.
 */

public class CaseReadHospitalizationFragment extends BaseReadActivityFragment<FragmentCaseReadHospitalizationLayoutBinding, Hospitalization> {

    public static final String TAG = CaseReadHospitalizationFragment.class.getSimpleName();

    private String recordUuid = null;
    private InvestigationStatus filterStatus = null;
    private CaseClassification pageStatus = null;
    private Hospitalization record;
    private Case caseRecord;
    private ObservableArrayList preHospitalizations = new ObservableArrayList();

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
            Hospitalization hospitalization = null;
            Case caze = DatabaseHelper.getCaseDao().queryUuid(recordUuid);
            if (caze != null)
                hospitalization = DatabaseHelper.getHospitalizationDao().queryUuid(caze.getHospitalization().getUuid());

            resultHolder.forItem().add(hospitalization);
            resultHolder.forItem().add(caze);
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            if (itemIterator.hasNext())
                record =  itemIterator.next();

            //TODO: Orson - Use recordUuid (Verify this todo)
            if (itemIterator.hasNext())
                caseRecord =  itemIterator.next();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentCaseReadHospitalizationLayoutBinding contentBinding) {
        contentBinding.setData(record);
        contentBinding.setCaze(caseRecord);
        contentBinding.setHospitalizations(getHospitalizations());
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseReadHospitalizationLayoutBinding contentBinding) {

    }

    @Override
    public void onPageResume(FragmentCaseReadHospitalizationLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

    }

    @Override
    protected String getSubHeadingTitle() {
        return null;
    }

    @Override
    public Hospitalization getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_case_read_hospitalization_layout;
    }

    public static CaseReadHospitalizationFragment newInstance(IActivityCommunicator activityCommunicator, CaseFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, CaseReadHospitalizationFragment.class, capsule);
    }

    private ObservableArrayList getHospitalizations() {
        if (record != null && preHospitalizations != null)
            preHospitalizations.addAll(record.getPreviousHospitalizations());

        //preHospitalizations.addAll(MemoryDatabaseHelper.PREVIOUS_HOSPITALIZATION.getHospitalizations(2));
        return preHospitalizations;
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }
}
