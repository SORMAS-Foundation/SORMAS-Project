package de.symeda.sormas.app.event.read;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentEventReadPersonsInvolvedLayoutBinding;
import de.symeda.sormas.app.event.read.sub.EventReadPersonsInvolvedInfoActivity;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;
import de.symeda.sormas.app.util.ConstantHelper;

/**
 * Created by Orson on 26/12/2017.
 */

public class EventReadPersonsInvolvedFragment extends BaseReadActivityFragment<FragmentEventReadPersonsInvolvedLayoutBinding, List<EventParticipant>> implements OnListItemClickListener {

    private AsyncTask onResumeTask;
    private String recordUuid;
    private EventStatus pageStatus;
    private List<EventParticipant> record;

    private EventReadPersonsInvolvedAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

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
        //filterStatus = (EventStatus) getFilterStatusArg(arguments);
        pageStatus = (EventStatus) getPageStatusArg(arguments);
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Event event = DatabaseHelper.getEventDao().queryUuid(recordUuid);

            if (event != null) {
                resultHolder.forList().add(DatabaseHelper.getEventParticipantDao().getByEvent(event));
            } else {
                resultHolder.forList().add(new ArrayList<EventParticipant>());
            }
        } else {
            ITaskResultHolderIterator listIterator = resultHolder.forList().iterator();

            if (listIterator.hasNext())
                record = listIterator.next();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentEventReadPersonsInvolvedLayoutBinding contentBinding) {
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new EventReadPersonsInvolvedAdapter(EventReadPersonsInvolvedFragment.this.getActivity(),
                R.layout.row_read_event_persons_involved_item_layout, EventReadPersonsInvolvedFragment.this, record);

        contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        contentBinding.recyclerViewForList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAfterLayoutBinding(FragmentEventReadPersonsInvolvedLayoutBinding contentBinding) {

    }

    @Override
    public void onPageResume(FragmentEventReadPersonsInvolvedLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        final SwipeRefreshLayout swiperefresh = (SwipeRefreshLayout)this.getView().findViewById(R.id.swiperefresh);
        if (swiperefresh != null) {
            swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getActivityCommunicator().synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly, true, false, true, swiperefresh, null);
                }
            });
        }

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
                    if (recordUuid == null || recordUuid.isEmpty()) {
                        resultHolder.forList().add(new ArrayList<EventParticipant>());
                        return;
                    }

                    Event event = DatabaseHelper.getEventDao().queryUuid(recordUuid);

                    if (event != null) {
                        resultHolder.forList().add(DatabaseHelper.getEventParticipantDao().getByEvent(event));
                    } else {
                        resultHolder.forList().add(new ArrayList<EventParticipant>());
                    }
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

                    ITaskResultHolderIterator listIterator = resultHolder.forList().iterator();
                    if (listIterator.hasNext())
                        record = listIterator.next();

                    requestLayoutRebind();
                }
            });
        } catch (Exception ex) {
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }

    }

    @Override
    protected String getSubHeadingTitle() {
        return null;
    }

    @Override
    public List<EventParticipant> getPrimaryData() {
        return null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void showPersonInfoReadView(EventParticipant record) {

    }

    @Override
    public int getRootReadLayout() {
        return R.layout.fragment_root_list_edit_layout;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_event_read_persons_involved_layout;
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        EventParticipant record = (EventParticipant)item;

        if(record != null) {
            Intent intent = new Intent(getActivity(), EventReadPersonsInvolvedInfoActivity.class);
            intent.putExtra(ConstantHelper.KEY_DATA_UUID, record.getUuid());
            intent.putExtra(ConstantHelper.ARG_PAGE_STATUS, record.getEvent().getEventStatus());
            startActivity(intent);
        }
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    public static EventReadPersonsInvolvedFragment newInstance(IActivityCommunicator activityCommunicator, EventFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, EventReadPersonsInvolvedFragment.class, capsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }
}
