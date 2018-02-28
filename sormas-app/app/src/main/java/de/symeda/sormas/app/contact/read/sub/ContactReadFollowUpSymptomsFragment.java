package de.symeda.sormas.app.contact.read.sub;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.tagview.Tag;
import de.symeda.sormas.app.contact.ContactFormFollowUpNavigationCapsule;
import de.symeda.sormas.app.databinding.FragmentContactReadSymptomsInfoLayoutBinding;
import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.visit.Visit;

/**
 * Created by Orson on 02/01/2018.
 */

public class ContactReadFollowUpSymptomsFragment extends BaseReadActivityFragment<FragmentContactReadSymptomsInfoLayoutBinding> {

    private String recordUuid;
    private VisitStatus pageStatus;
    //private FollowUpStatus followUpStatus;
    private Visit record;
    private FragmentContactReadSymptomsInfoLayoutBinding binding;

    private List<Tag> results;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //SaveFilterStatusState(outState, followUpStatus);
        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        //followUpStatus = (FollowUpStatus) getFilterStatusArg(arguments);
        pageStatus = (VisitStatus) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = DataBindingUtil.inflate(inflater, getRootReadLayout(), container, false);
        record = MemoryDatabaseHelper.VISIT.getVisits(1).get(0);

        binding.setData(record.getSymptoms());

        binding.setSymptomsYes(getSymptomsYes());
        binding.setSymptomsUnknown(getSymptomsUnknown());
        binding.setSymptomsNo(getSymptomsNo());


        /*binding.tvSymptomsYes.addTags(getSymptomsYes());
        binding.tvSymptomsNo.addTags(getSymptomsUnknown());
        binding.tvSymptomsUnknown.addTags(getSymptomsNo());*/
        //getSymptoms(binding.tagGroup);


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
    public FragmentContactReadSymptomsInfoLayoutBinding getBinding() {
        return binding;
    }

    @Override
    public Object getRecord() {
        return record;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void showRecordEditView(EventParticipant item) {
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
        return R.layout.fragment_contact_read_symptoms_info_layout;
    }

    public static ContactReadFollowUpSymptomsFragment newInstance(ContactFormFollowUpNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(ContactReadFollowUpSymptomsFragment.class, capsule);
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
}
