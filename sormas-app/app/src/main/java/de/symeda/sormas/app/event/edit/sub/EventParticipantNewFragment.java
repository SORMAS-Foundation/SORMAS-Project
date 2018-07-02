package de.symeda.sormas.app.event.edit.sub;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentEventNewPersonShortLayoutBinding;
import de.symeda.sormas.app.shared.EventParticipantFormNavigationCapsule;

public class EventParticipantNewFragment extends BaseEditFragment<FragmentEventNewPersonShortLayoutBinding, EventParticipant, EventParticipant> {

    public static final String TAG = EventParticipantNewFragment.class.getSimpleName();

    private AsyncTask onResumeTask;
    private String eventUuid = null;
    private EventParticipant record;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveEventUuidState(outState, eventUuid);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        eventUuid = getEventUuidArg(savedInstanceState);
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();

        return String.format(r.getString(R.string.heading_sub_event_person_involved_new), DataHelper.getShortUuid(eventUuid));
    }

    @Override
    public EventParticipant getPrimaryData() {
        return record;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            EventParticipant eventParticipant = getActivityRootData();
            resultHolder.forItem().add(eventParticipant);
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            if (itemIterator.hasNext())
                record = itemIterator.next();

            if (record == null)
                getActivity().finish();
        }

        return true;
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }
}
