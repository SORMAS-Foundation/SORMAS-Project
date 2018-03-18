package de.symeda.sormas.app.caze.read;

import android.os.Bundle;
import android.support.annotation.Nullable;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.caze.CaseFormNavigationCapsule;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentCaseReadPatientInfoLayoutBinding;

/**
 * Created by Orson on 08/01/2018.
 */

public class CaseReadPatientInfoFragment extends BaseReadActivityFragment<FragmentCaseReadPatientInfoLayoutBinding, Case> {

    private String recordUuid = null;
    private InvestigationStatus filterStatus = null;
    private CaseClassification pageStatus = null;
    private Case record;
    private Person personRecord;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        pageStatus = (CaseClassification) getPageStatusArg(arguments);
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            resultHolder.forItem().add(DatabaseHelper.getCaseDao().queryUuidReference(recordUuid));
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            if (itemIterator.hasNext())
                record =  itemIterator.next();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentCaseReadPatientInfoLayoutBinding contentBinding) {

    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseReadPatientInfoLayoutBinding contentBinding) {
        if (record != null) {
            personRecord = record.getPerson();
            contentBinding.setData(personRecord);
        }
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
        return R.layout.fragment_case_read_patient_info_layout;
    }

    public static CaseReadPatientInfoFragment newInstance(IActivityCommunicator activityCommunicator, CaseFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, CaseReadPatientInfoFragment.class, capsule);
    }
}
