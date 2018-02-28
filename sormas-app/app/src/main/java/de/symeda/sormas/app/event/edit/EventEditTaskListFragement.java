package de.symeda.sormas.app.event.edit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewStub;

import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentEditListLayoutBinding;
import de.symeda.sormas.app.event.EventFormNavigationCapsule;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.task.TaskFormNavigationCapsule;
import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import java.util.List;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.task.Task;

/**
 * Created by Orson on 07/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class EventEditTaskListFragement extends BaseEditActivityFragment<FragmentEditListLayoutBinding> implements OnListItemClickListener {

    private String recordUuid = null;
    //private EventStatus filterStatus = null;
    private EventStatus pageStatus = null;
    private List<Task> record;
    private FragmentEditListLayoutBinding binding;
    private EventEditTaskListAdapter adapter;
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
    protected String getSubHeadingTitle() {
        return null;
    }

    @Override
    public AbstractDomainObject getData() {
        return null;
    }

    @Override
    public void onBeforeLayoutBinding(Bundle savedInstanceState) {

        //Get Data
        record = MemoryDatabaseHelper.TASK.getTasks(20);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    public void onLayoutBinding(ViewStub stub, View inflated, FragmentEditListLayoutBinding contentBinding) {
        //binding = DataBindingUtil.inflate(inflater, getEditLayout(), container, true);

        //Create adapter and set data
        adapter = new EventEditTaskListAdapter(this.getActivity(), R.layout.row_read_task_list_item_layout, this, record);

        contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        contentBinding.recyclerViewForList.setAdapter(adapter);


        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAfterLayoutBinding(FragmentEditListLayoutBinding binding) {

    }

    @Override
    public int getRootEditLayout() {
        return R.layout.fragment_root_list_edit_layout;
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_edit_list_layout;
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();

        //adapter.replaceAll(new ArrayList<EventParticipant>(record));
        adapter.notifyDataSetChanged();

        final SwipeRefreshLayout swiperefresh = (SwipeRefreshLayout)getRootBinding().getRoot()
                .findViewById(R.id.swiperefresh);

        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBaseEditActivity().synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly,
                        true, false, swiperefresh, null);
            }
        });
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Task t = (Task)item;
        TaskFormNavigationCapsule dataCapsule = new TaskFormNavigationCapsule(getContext(),
                t.getUuid(), t.getTaskStatus());
        EventEditTaskInfoActivity.goToActivity(getActivity(), dataCapsule);
        /*Task record = (Task)item;

        if(record != null) {
            Intent intent = new Intent(getActivity(), EventReadTaskInfoActivity.class);
            intent.putExtra(ConstantHelper.KEY_DATA_UUID, record.getUuid());
            intent.putExtra(ConstantHelper.ARG_FILTER_STATUS, record.getEvent().getEventStatus());
            startActivity(intent);
        }*/
    }

    public static EventEditTaskListFragement newInstance(EventFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(EventEditTaskListFragement.class, capsule);
    }
}
