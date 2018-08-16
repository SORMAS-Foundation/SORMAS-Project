package de.symeda.sormas.app.event.read;

import android.os.Bundle;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.databinding.FragmentEventReadLayoutBinding;

public class EventReadFragment extends BaseReadFragment<FragmentEventReadLayoutBinding, Event, Event> {

    private Event record;

    public static EventReadFragment newInstance(Event activityRootData) {
        return newInstance(EventReadFragment.class, null, activityRootData);
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

    @Override
    public boolean showEditAction() {
        User user = ConfigProvider.getUser();
        return user.hasUserRight(UserRight.EVENT_EDIT);
    }

}
