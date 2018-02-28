package de.symeda.sormas.app.caze.read;

import android.app.AlertDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.caze.CaseFormNavigationCapsule;
import de.symeda.sormas.app.component.OnLinkClickListener;
import de.symeda.sormas.app.component.dialog.SimpleDialog;
import de.symeda.sormas.app.component.tagview.Tag;
import de.symeda.sormas.app.databinding.FragmentCaseReadSymptomsLayoutBinding;
import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.symptoms.Symptoms;

/**
 * Created by Orson on 08/01/2018.
 */

public class CaseReadSymptomsFragment extends BaseReadActivityFragment<FragmentCaseReadSymptomsLayoutBinding> {

    public static final String TAG = CaseReadHospitalizationFragment.class.getSimpleName();

    private String caseUuid = null;
    private InvestigationStatus filterStatus = null;
    private CaseClassification pageStatus = null;
    private Symptoms record;
    private FragmentCaseReadSymptomsLayoutBinding binding;

    private OnLinkClickListener onLinkClickListener;

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

        setupCallback();

        binding = DataBindingUtil.inflate(inflater, getRootReadLayout(), container, false);
        //record = MemoryDatabaseHelper.CASE.getCases(1).get(0);
        //caze.getSymptoms().getUuid()

        record = MemoryDatabaseHelper.SYMPTOM.getSymptoms(1).get(0);

        binding.setData(record);
        binding.setSymptomsYes(getSymptomsYes());
        binding.setSymptomsUnknown(getSymptomsUnknown());
        binding.setSymptomsNo(getSymptomsNo());
        binding.setLocationClickCallback(onLinkClickListener);

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
    public FragmentCaseReadSymptomsLayoutBinding getBinding() {
        return binding;
    }

    @Override
    public Object getRecord() {
        return record;
    }

    public void showRecordEditView(Case caze) {
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
        return R.layout.fragment_case_read_symptoms_layout;
    }

    public static CaseReadSymptomsFragment newInstance(CaseFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(CaseReadSymptomsFragment.class, capsule);
    }

    private List<Tag> getSymptomsYes() {
        List<Tag> results = new ArrayList();
        results.add(new Tag("Chills or sweats"));
        results.add(new Tag("Conjunctivities (red eyes)"));
        results.add(new Tag("Cough"));
        results.add(new Tag("Cutaneous eruption"));
        results.add(new Tag("Pain behind eyes/sensitivity to light"));
        results.add(new Tag("Fatigue/general weakness"));
        return results;
    }

    private List<Tag> getSymptomsUnknown() {
        List<Tag> results = new ArrayList();
        results.add(new Tag("Fear"));
        results.add(new Tag("Headache"));
        results.add(new Tag("Nausea"));
        results.add(new Tag("Oral Ulcer"));
        results.add(new Tag("Vomiting"));
        results.add(new Tag("Sore throat"));
        return results;
    }

    private List<Tag> getSymptomsNo() {
        List<Tag> results = new ArrayList();
        results.add(new Tag("Bedridden"));
        results.add(new Tag("Lesions"));
        results.add(new Tag("Lymphadenopathy axillary"));
        results.add(new Tag("Lymphadenopathy cervical"));
        results.add(new Tag("Lymphadenopathy inguinal"));
        results.add(new Tag("Muscle pain"));
        return results;
    }

    private void setupCallback() {
        onLinkClickListener = new OnLinkClickListener() {
            @Override
            public void onClick(View v, Object item) {
                /*Symptoms s = (Symptoms)item;
                SimpleDialog simpleDialog = new SimpleDialog(getContext(),
                        R.layout.dialog_location_layout, s.getIllLocation());
                AlertDialog dialog = simpleDialog.show();*/
                //Toast.makeText(getContext(), "Hurray!", Toast.LENGTH_SHORT).show();
            }
        };
    }
}
