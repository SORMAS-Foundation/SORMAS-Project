package de.symeda.sormas.app.event.edit.sub;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentEventNewPersonShortLayoutBinding;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;

/**
 * Created by Orson on 27/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class EventNewPersonsInvolvedShortFragment extends BaseEditActivityFragment<FragmentEventNewPersonShortLayoutBinding, EventParticipant, EventParticipant> {

    public static final String TAG = EventNewPersonsInvolvedShortFragment.class.getSimpleName();

    private AsyncTask onResumeTask;
    private EventStatus pageStatus = null;
    private String recordUuid = null;
    private String eventUuid = null;
    private EventParticipant record;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
        SaveEventUuidState(outState, eventUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        pageStatus = (EventStatus) getPageStatusArg(arguments);
        eventUuid = getEventUuidArg(arguments);
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
    public void onAfterLayoutBinding(FragmentEventNewPersonShortLayoutBinding contentBinding) {

    }

    @Override
    protected void updateUI(FragmentEventNewPersonShortLayoutBinding contentBinding, EventParticipant eventParticipant) {

    }

    @Override
    public void onPageResume(FragmentEventNewPersonShortLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().showPreloader();
                    //getActivityCommunicator().hideFragmentView();
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    EventParticipant eventParticipant = getActivityRootData();
                    resultHolder.forItem().add(eventParticipant);
                }
            });
            onResumeTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().hidePreloader();
                    //getActivityCommunicator().showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

                    if (itemIterator.hasNext())
                        record = itemIterator.next();

                    if (record != null)
                        requestLayoutRebind();
                    else {
                        getActivity().finish();
                    }
                }
            });
        } catch (Exception ex) {
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }
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
    public boolean showSaveAction() {
        return true;
    }

    @Override
    public boolean showAddAction() {
        return false;
    }

    public static EventNewPersonsInvolvedShortFragment newInstance(IActivityCommunicator activityCommunicator, EventFormNavigationCapsule capsule, EventParticipant activityRootData)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, EventNewPersonsInvolvedShortFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }
}
