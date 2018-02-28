package de.symeda.sormas.app.caze.read;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.caze.CaseFormNavigationCapsule;
import de.symeda.sormas.app.databinding.FragmentCaseReadHospitalizationLayoutBinding;
import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;

/**
 * Created by Orson on 08/01/2018.
 */

public class CaseReadHospitalizationFragment extends BaseReadActivityFragment<FragmentCaseReadHospitalizationLayoutBinding> {

    public static final String TAG = CaseReadHospitalizationFragment.class.getSimpleName();

    private String caseUuid = null;
    private InvestigationStatus filterStatus = null;
    private CaseClassification pageStatus = null;
    private Hospitalization record;
    private Case caseRecord;
    private ObservableArrayList preHospitalizations = new ObservableArrayList();
    private FragmentCaseReadHospitalizationLayoutBinding binding;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveFilterStatusState(outState, filterStatus);
        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, caseUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        caseUuid = getRecordUuidArg(arguments);
        filterStatus = (InvestigationStatus) getFilterStatusArg(arguments);
        pageStatus = (CaseClassification) getPageStatusArg(arguments);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = DataBindingUtil.inflate(inflater, getRootReadLayout(), container, false);
        record = MemoryDatabaseHelper.HOSPITALIZATION.getHospitalizations(1).get(0);

        //TODO: Orson - Use caseUuid
        caseRecord = MemoryDatabaseHelper.CASE.getCases(1).get(0);

        binding.setData(record);
        binding.setCaze(caseRecord);
        binding.setHospitalizations(getHospitalizations());

        return binding.getRoot();
    }

    @Override
    protected String getSubHeadingTitle() {
        return null;
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getData();
    }

    @Override
    public FragmentCaseReadHospitalizationLayoutBinding getBinding() {
        return binding;
    }

    @Override
    public Object getRecord() {
        return record;
    }

    public void showRecordEditView(Event event) {
        /*Intent intent = new Intent(getActivity(), TaskEditActivity.class);
        intent.putExtra(Task.UUID, task.getUuid());
        if(parentCaseUuid != null) {
            intent.putExtra(KEY_CASE_UUID, parentCaseUuid);
        }
        if(parentContactUuid != null) {
            intent.putExtra(KEY_CONTACT_UUID, parentContactUuid);
        }
        if(parentEventUuid != null) {
            intent.putExtra(KEY_EVENT_UUID, parentEventUuid);
        }
        startActivity(intent);*/
    }

    @Override
    public int getRootReadLayout() {
        return R.layout.fragment_case_read_hospitalization_layout;
    }

    public static CaseReadHospitalizationFragment newInstance(CaseFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(CaseReadHospitalizationFragment.class, capsule);
    }

    private ObservableArrayList getHospitalizations() {
        preHospitalizations.addAll(MemoryDatabaseHelper.PREVIOUS_HOSPITALIZATION.getHospitalizations(2));
        return preHospitalizations;
    }
}
