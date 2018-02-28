package de.symeda.sormas.app.contact.read.sub;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.contact.ContactFormFollowUpNavigationCapsule;
import de.symeda.sormas.app.databinding.FragmentContactReadVisitInfoLayoutBinding;
import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.visit.Visit;

/**
 * Created by Orson on 02/01/2018.
 */

public class ContactReadFollowUpVisitInfoFragment extends BaseReadActivityFragment<FragmentContactReadVisitInfoLayoutBinding> {

    private String recordUuid;
    private VisitStatus pageStatus;
    //private FollowUpStatus followUpStatus;
    private Visit record;
    private FragmentContactReadVisitInfoLayoutBinding binding;

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

        binding.setData(record);
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
    public FragmentContactReadVisitInfoLayoutBinding getBinding() {
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
        return R.layout.fragment_contact_read_visit_info_layout;
    }

    public static ContactReadFollowUpVisitInfoFragment newInstance(ContactFormFollowUpNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(ContactReadFollowUpVisitInfoFragment.class, capsule);
    }


}
