package de.symeda.sormas.app.event.read;

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
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentEventReadTaskLayoutBinding;
import de.symeda.sormas.app.event.EventFormNavigationCapsule;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.task.TaskFormNavigationCapsule;
import de.symeda.sormas.app.task.read.TaskReadActivity;

/**
 * Created by Orson on 26/12/2017.
 */

public class EventReadTaskListFragement extends BaseReadActivityFragment<FragmentEventReadTaskLayoutBinding, List<Task>> implements OnListItemClickListener {

    private String recordUuid;
    private EventStatus pageStatus;
    private List<Task> record;

    private EventReadTaskListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

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
            Event event = DatabaseHelper.getEventDao().queryUuid(recordUuid);

            if (event != null) {
                resultHolder.forList().add(DatabaseHelper.getTaskDao().queryByEvent(event));
            } else {
                resultHolder.forList().add(new ArrayList<Task>());
            }
        } else {
            ITaskResultHolderIterator listIterator = resultHolder.forList().iterator();
            if (listIterator.hasNext())
                record = listIterator.next();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentEventReadTaskLayoutBinding contentBinding) {
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new EventReadTaskListAdapter(EventReadTaskListFragement.this.getActivity(),
                R.layout.row_read_event_task_list_item_layout, EventReadTaskListFragement.this, record);

        getContentBinding().recyclerViewForList.setLayoutManager(linearLayoutManager);
        getContentBinding().recyclerViewForList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAfterLayoutBinding(FragmentEventReadTaskLayoutBinding contentBinding) {

    }

    @Override
    public void onResume() {
        super.onResume();

        final SwipeRefreshLayout swiperefresh = (SwipeRefreshLayout)getRootBinding().getRoot()
                .findViewById(R.id.swiperefresh);

        if (swiperefresh != null) {
            swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getBaseReadActivity().synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly,
                            true, false, swiperefresh, null);
                }
            });
        }
    }

    @Override
    protected String getSubHeadingTitle() {
        return null;
    }

    @Override
    public List<Task> getPrimaryData() {
        return null;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_event_read_task_layout;
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Task task = (Task)item;
        TaskFormNavigationCapsule dataCapsule = new TaskFormNavigationCapsule(getContext(),
                task.getUuid(), task.getTaskStatus());
        TaskReadActivity.goToActivity(getActivity(), dataCapsule);

        /*Task task = (Task)item;

        TaskFormNavigationCapsule dataCapsule = new TaskFormNavigationCapsule(getContext(),
                task.getUuid(), task.getTaskStatus());
        EventReadTaskInfoActivity.goToActivity(getActivity(), dataCapsule);*/


        /*Task record = (Task)item;

        if(record != null) {
            Intent intent = new Intent(getActivity(), EventReadTaskInfoActivity.class);
            intent.putExtra(ConstantHelper.KEY_DATA_UUID, record.getUuid());
            intent.putExtra(ConstantHelper.ARG_PAGE_STATUS, record.getTaskStatus());
            startActivity(intent);
        }*/
    }

    public static EventReadTaskListFragement newInstance(IActivityCommunicator activityCommunicator, EventFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, EventReadTaskListFragement.class, capsule);
    }

}
