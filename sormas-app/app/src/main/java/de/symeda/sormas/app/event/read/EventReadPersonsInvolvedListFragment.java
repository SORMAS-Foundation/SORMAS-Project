package de.symeda.sormas.app.event.read;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.event.read.sub.EventParticipantReadActivity;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;
import de.symeda.sormas.app.shared.EventParticipantFormNavigationCapsule;

/**
 * Created by Orson on 26/12/2017.
 */

public class EventReadPersonsInvolvedListFragment extends BaseReadFragment<FragmentFormListLayoutBinding, List<EventParticipant>, Event> implements OnListItemClickListener {

    private AsyncTask onResumeTask;
    private String recordUuid;
    private EventStatus pageStatus;
    private List<EventParticipant> record;

    private EventReadPersonsInvolvedAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        savePageStatusState(outState, pageStatus);
        saveRecordUuidState(outState, recordUuid);
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
            Event event = getActivityRootData();
            List<EventParticipant> eventParticipantList = new ArrayList<EventParticipant>();

            //Case caze = DatabaseHelper.getCaseDao().queryUuidReference(recordUuid);
            if (event != null) {
                if (event.isUnreadOrChildUnread())
                    DatabaseHelper.getEventDao().markAsRead(event);

                eventParticipantList = DatabaseHelper.getEventParticipantDao().getByEvent(event);
            }

            resultHolder.forList().add(eventParticipantList);
        } else {
            ITaskResultHolderIterator listIterator = resultHolder.forList().iterator();

            if (listIterator.hasNext())
                record = listIterator.next();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
        showEmptyListHint(record, R.string.entity_event_participant);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new EventReadPersonsInvolvedAdapter(EventReadPersonsInvolvedListFragment.this.getActivity(),
                R.layout.row_read_persons_involved_list_item_layout, EventReadPersonsInvolvedListFragment.this, record);

        contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        contentBinding.recyclerViewForList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAfterLayoutBinding(FragmentFormListLayoutBinding contentBinding) {

    }

    @Override
    protected void updateUI(FragmentFormListLayoutBinding contentBinding, List<EventParticipant> eventParticipants) {

    }

    @Override
    public void onPageResume(FragmentFormListLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        final SwipeRefreshLayout swiperefresh = (SwipeRefreshLayout)this.getView().findViewById(R.id.swiperefresh);
        if (swiperefresh != null) {
            swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getBaseActivity().synchronizeData(SynchronizeDataAsync.SyncMode.Changes, true, false, true, swiperefresh, null);
                }
            });
        }

        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

        try {
            DefaultAsyncTask executor = new DefaultAsyncTask(getContext()) {
                @Override
                public void onPreExecute() {
                    //getBaseActivity().showPreloader();
                    //
                }

                @Override
                public void doInBackground(TaskResultHolder resultHolder) {
                    Event event = getActivityRootData();
                    List<EventParticipant> eventParticipantList = new ArrayList<EventParticipant>();

                    //Case caze = DatabaseHelper.getCaseDao().queryUuidReference(recordUuid);
                    if (event != null) {
                        if (event.isUnreadOrChildUnread())
                            DatabaseHelper.getEventDao().markAsRead(event);

                        eventParticipantList = DatabaseHelper.getEventParticipantDao().getByEvent(event);
                    }

                    resultHolder.forList().add(eventParticipantList);
                }
            };
            onResumeTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getBaseActivity().hidePreloader();
                    //getBaseActivity().showFragmentView();

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
            //getBaseActivity().hidePreloader();
            //getBaseActivity().showFragmentView();
        }

    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_persons_involved);
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
        return R.layout.fragment_root_list_form_layout;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_form_list_layout;
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        EventParticipant o = (EventParticipant)item;
        EventParticipantFormNavigationCapsule dataCapsule = new EventParticipantFormNavigationCapsule(getContext(), o.getUuid());
        EventParticipantReadActivity.goToActivity(getActivity(), dataCapsule);
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    public static EventReadPersonsInvolvedListFragment newInstance(EventFormNavigationCapsule capsule, Event activityRootData) {
        return newInstance(EventReadPersonsInvolvedListFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }
}
