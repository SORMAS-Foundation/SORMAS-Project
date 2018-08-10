package de.symeda.sormas.app.event.edit.eventparticipant;

import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.databinding.FragmentEventParticipantNewLayoutBinding;

public class EventParticipantNewFragment extends BaseEditFragment<FragmentEventParticipantNewLayoutBinding, EventParticipant, EventParticipant> {

    public static final String TAG = EventParticipantNewFragment.class.getSimpleName();

    private EventParticipant record;

    public static EventParticipantNewFragment newInstance(EventParticipant activityRootData) {
        return newInstance(EventParticipantNewFragment.class, null, activityRootData);
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
    protected void prepareFragmentData() {
        record = getActivityRootData();
    }

    @Override
    public void onLayoutBinding(FragmentEventParticipantNewLayoutBinding contentBinding) {
        contentBinding.setData(record);
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
    public boolean isShowNewAction() {
        return false;
    }

}
