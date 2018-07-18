package de.symeda.sormas.app.event.edit.eventparticipant;

import android.os.Bundle;

import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.databinding.FragmentEventParticipantEditLayoutBinding;
import de.symeda.sormas.app.person.edit.PersonEditFragment;
import de.symeda.sormas.app.shared.EventParticipantFormNavigationCapsule;

public class EventParticipantEditFragment extends BaseEditFragment<FragmentEventParticipantEditLayoutBinding, EventParticipant, EventParticipant> {

    public static final String TAG = EventParticipantEditFragment.class.getSimpleName();

    private EventParticipant record;

    // Instance methods

    public static EventParticipantEditFragment newInstance(EventParticipantFormNavigationCapsule capsule, EventParticipant activityRootData) {
        return newInstance(EventParticipantEditFragment.class, capsule, activityRootData);
    }

    // Overrides

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_person_involved);
    }

    @Override
    public EventParticipant getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
    }

    @Override
    public void onLayoutBinding(FragmentEventParticipantEditLayoutBinding contentBinding) {
        contentBinding.setData(record);
        contentBinding.setPresentConditionClass(PresentCondition.class);
    }

    @Override
    public void onAfterLayoutBinding(FragmentEventParticipantEditLayoutBinding contentBinding) {
        if (contentBinding.eventParticipantPersonLayout != null) {
            PersonEditFragment.setUpLayoutBinding(this, record.getPerson(), contentBinding.eventParticipantPersonLayout);
        }
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_event_participant_edit_layout;
    }

}
