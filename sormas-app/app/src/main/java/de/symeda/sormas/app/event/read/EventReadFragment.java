package de.symeda.sormas.app.event.read;

import android.os.Bundle;
import android.support.annotation.Nullable;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentEventReadLayoutBinding;
import de.symeda.sormas.app.event.EventFormNavigationCapsule;

/**
 * Created by Orson on 24/12/2017.
 */

public class EventReadFragment extends BaseReadActivityFragment<FragmentEventReadLayoutBinding, Event> {

    private String recordUuid;
    private EventStatus pageStatus;
    private Event record;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        pageStatus = (EventStatus) getPageStatusArg(arguments);
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            if (recordUuid == null || recordUuid.isEmpty()) {
                // build a new event for empty uuid
                resultHolder.forItem().add(DatabaseHelper.getEventDao().build());
            } else {
                // open the given event
                resultHolder.forItem().add(DatabaseHelper.getEventDao().queryUuid(recordUuid));
            }
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            if (itemIterator.hasNext())
                record = itemIterator.next();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentEventReadLayoutBinding contentBinding) {
        contentBinding.setData(record);
    }

    @Override
    public void onAfterLayoutBinding(FragmentEventReadLayoutBinding contentBinding) {

    }

    @Override
    protected String getSubHeadingTitle() {
        return null;
    }

    @Override
    public Event getPrimaryData() {
        return record;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_event_read_layout;
    }

    public static EventReadFragment newInstance(IActivityCommunicator activityCommunicator, EventFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, EventReadFragment.class, capsule);
    }

}
