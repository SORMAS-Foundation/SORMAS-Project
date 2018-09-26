package de.symeda.sormas.app.event.read.eventparticipant;

import android.os.Bundle;
import android.view.View;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.databinding.FragmentEventParticipantReadLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentPersonReadLayoutBinding;
import de.symeda.sormas.app.person.edit.PersonEditFragment;
import de.symeda.sormas.app.util.InfrastructureHelper;

import static android.view.View.GONE;

public class EventParticipantReadFragment extends BaseReadFragment<FragmentEventParticipantReadLayoutBinding, EventParticipant, EventParticipant> {

    private EventParticipant record;

    // Static methods

    public static EventParticipantReadFragment newInstance(EventParticipant activityRootData) {
        return newInstance(EventParticipantReadFragment.class, null, activityRootData);
    }

    // Instance methods

    private void setUpFieldVisibilities(FragmentEventParticipantReadLayoutBinding contentBinding) {
        if (record.getResultingCaseUuid() == null
                || DatabaseHelper.getCaseDao().queryUuidBasic(record.getResultingCaseUuid()) == null) {
            contentBinding.eventParticipantButtonsPanel.setVisibility(GONE);
        }
    }

    private void setUpControlListeners(FragmentEventParticipantReadLayoutBinding contentBinding) {
        contentBinding.openEventPersonCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaseReadActivity.startActivity(getActivity(), record.getResultingCaseUuid(), true);
            }
        });
    }

    private void setUpPersonFragmentFieldVisibilities(FragmentPersonReadLayoutBinding contentBinding) {
        InfrastructureHelper.initializeHealthFacilityDetailsFieldVisibility(contentBinding.personOccupationFacility, contentBinding.personOccupationFacilityDetails);
        PersonEditFragment.initializeCauseOfDeathDetailsFieldVisibility(contentBinding.personCauseOfDeath, contentBinding.personCauseOfDeathDisease, contentBinding.personCauseOfDeathDetails);
    }

    // Overrides

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
    }

    @Override
    public void onLayoutBinding(FragmentEventParticipantReadLayoutBinding contentBinding) {
        setUpControlListeners(contentBinding);

        contentBinding.setData(record);
    }

    @Override
    public void onAfterLayoutBinding(FragmentEventParticipantReadLayoutBinding contentBinding) {
        setUpFieldVisibilities(contentBinding);

        if (contentBinding.eventParticipantPersonLayout != null) {
            setUpPersonFragmentFieldVisibilities(contentBinding.eventParticipantPersonLayout);
        }
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_person_involved);
    }

    @Override
    public EventParticipant getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_event_participant_read_layout;
    }
}
