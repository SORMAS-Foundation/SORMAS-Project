package de.symeda.sormas.app.event.read;

import android.os.Bundle;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.databinding.FragmentEventReadLayoutBinding;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;

public class EventReadFragment extends BaseReadFragment<FragmentEventReadLayoutBinding, Event, Event> {

    private Event record;

    // Instance methods

    public static EventReadFragment newInstance(EventFormNavigationCapsule capsule, Event activityRootData) {
        return newInstance(EventReadFragment.class, capsule, activityRootData);
    }

    // Overrides

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
    }

    @Override
    public void onLayoutBinding(FragmentEventReadLayoutBinding contentBinding) {
        contentBinding.setData(record);
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_event_information);
    }

    @Override
    public Event getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_event_read_layout;
    }

}
