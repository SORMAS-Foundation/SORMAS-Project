package de.symeda.sormas.app.event.read.sub;

import android.os.Bundle;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.databinding.FragmentEventReadPersonInvolvedInfoLayoutBinding;
import de.symeda.sormas.app.shared.EventParticipantFormNavigationCapsule;

public class EventParticipantReadFragment extends BaseReadFragment<FragmentEventReadPersonInvolvedInfoLayoutBinding, EventParticipant, EventParticipant> {

    private EventParticipant record;

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
    }

    @Override
    public void onLayoutBinding(FragmentEventReadPersonInvolvedInfoLayoutBinding contentBinding) {
        contentBinding.setData(record);
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
        return R.layout.fragment_event_read_person_involved_info_layout;
    }

    public static EventParticipantReadFragment newInstance(EventParticipantFormNavigationCapsule capsule, EventParticipant activityRootData) {
        return newInstance(EventParticipantReadFragment.class, capsule, activityRootData);
    }
}
