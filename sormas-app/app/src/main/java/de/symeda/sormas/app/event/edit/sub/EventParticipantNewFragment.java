package de.symeda.sormas.app.event.edit.sub;

import android.os.Bundle;

import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.databinding.FragmentEventNewPersonShortLayoutBinding;
import de.symeda.sormas.app.shared.EventParticipantFormNavigationCapsule;

public class EventParticipantNewFragment extends BaseEditFragment<FragmentEventNewPersonShortLayoutBinding, EventParticipant, EventParticipant> {

    public static final String TAG = EventParticipantNewFragment.class.getSimpleName();

    private EventParticipant record;

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
    public void onLayoutBinding(FragmentEventNewPersonShortLayoutBinding contentBinding) {
        contentBinding.setData(record);
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_event_new_person_short_layout;
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    @Override
    public boolean isShowSaveAction() {
        return true;
    }

    @Override
    public boolean isShowAddAction() {
        return false;
    }

    public static EventParticipantNewFragment newInstance(EventParticipantFormNavigationCapsule capsule, EventParticipant activityRootData) {
        return newInstance(EventParticipantNewFragment.class, capsule, activityRootData);
    }
}
