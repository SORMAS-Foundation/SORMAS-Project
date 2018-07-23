package de.symeda.sormas.app.event.edit.eventparticipant;

import android.os.Bundle;

import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.databinding.FragmentEventParticipantNewLayoutBinding;
import de.symeda.sormas.app.shared.EventParticipantFormNavigationCapsule;

public class EventParticipantNewFragment extends BaseEditFragment<FragmentEventParticipantNewLayoutBinding, EventParticipant, EventParticipant> {

    public static final String TAG = EventParticipantNewFragment.class.getSimpleName();

    private EventParticipant record;

    // Instance methods

    public static EventParticipantNewFragment newInstance(EventParticipantFormNavigationCapsule capsule, EventParticipant activityRootData) {
        return newInstance(EventParticipantNewFragment.class, capsule, activityRootData);
    }

    // Overrides

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_new_event_participant);
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
    public void onLayoutBinding(FragmentEventParticipantNewLayoutBinding contentBinding) {
        contentBinding.setData(record);

        if (isLiveValidationDisabled()) {
            disableLiveValidation(true);
        }
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_event_participant_new_layout;
    }

    @Override
    public boolean isShowSaveAction() {
        return true;
    }

    @Override
    public boolean isShowAddAction() {
        return false;
    }

}
