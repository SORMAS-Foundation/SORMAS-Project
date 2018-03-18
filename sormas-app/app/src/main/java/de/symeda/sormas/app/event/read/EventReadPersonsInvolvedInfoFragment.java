package de.symeda.sormas.app.event.read;

import android.os.Bundle;
import android.support.annotation.Nullable;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentEventReadPersonInvolvedInfoLayoutBinding;
import de.symeda.sormas.app.event.EventFormNavigationCapsule;

/**
 * Created by Orson on 28/12/2017.
 */

public class EventReadPersonsInvolvedInfoFragment extends BaseReadActivityFragment<FragmentEventReadPersonInvolvedInfoLayoutBinding, EventParticipant> {

    private String recordUuid;
    private EventStatus pageStatus;
    private EventParticipant record;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //SaveFilterStatusState(outState, filterStatus);
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
                resultHolder.forItem().add(DatabaseHelper.getEventParticipantDao().build());
            } else {
                // open the given event
                resultHolder.forItem().add(DatabaseHelper.getEventParticipantDao().queryUuid(recordUuid));
            }
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            if (itemIterator.hasNext())
                record = itemIterator.next();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentEventReadPersonInvolvedInfoLayoutBinding contentBinding) {
        contentBinding.setData(record);
    }

    @Override
    public void onAfterLayoutBinding(FragmentEventReadPersonInvolvedInfoLayoutBinding contentBinding) {

    }

    @Override
    protected String getSubHeadingTitle() {
        String title = "";

        if (pageStatus != null) {
            title = pageStatus.toString();
        }

        return title;
    }

    @Override
    public EventParticipant getPrimaryData() {
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
    public int getReadLayout() {
        return R.layout.fragment_event_read_person_involved_info_layout;
    }

    public static EventReadPersonsInvolvedInfoFragment newInstance(IActivityCommunicator activityCommunicator, EventFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, EventReadPersonsInvolvedInfoFragment.class, capsule);
    }
}
