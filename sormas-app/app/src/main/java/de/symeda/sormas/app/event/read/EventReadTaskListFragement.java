package de.symeda.sormas.app.event.read;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentEventReadTaskLayoutBinding;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.ConstantHelper;
import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.task.Task;

/**
 * Created by Orson on 26/12/2017.
 */

public class EventReadTaskListFragement extends BaseReadActivityFragment<FragmentEventReadTaskLayoutBinding> implements OnListItemClickListener {

    private List<Task> record;
    private FragmentEventReadTaskLayoutBinding binding;

    private EventReadTaskListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);



        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        //Get binding
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding = DataBindingUtil.inflate(inflater, getRootReadLayout(), container, false);

        //Get Data
        record = MemoryDatabaseHelper.TASK.getTasks(20);

        //Create adapter and set data
        adapter = new EventReadTaskListAdapter(this.getActivity(), R.layout.row_read_event_task_list_item_layout, this, record);

        binding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        binding.recyclerViewForList.setAdapter(adapter);


        adapter.notifyDataSetChanged();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        //adapter.replaceAll(new ArrayList<EventParticipant>(record));
        adapter.notifyDataSetChanged();

        binding.swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBaseReadActivity().synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly, true, false, binding.swiperefresh, null);
            }
        });
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
    public FragmentEventReadTaskLayoutBinding getBinding() {
        return binding;
    }

    @Override
    public Object getRecord() {
        return record;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //recyclerViewForList.setLayoutManager(linearLayoutManager);
        //recyclerViewForList.setAdapter(adapter);
        //binding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        //binding.recyclerViewForList.setAdapter(adapter);
    }

    public void showTaskInfoReadView(Event event) {
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
    public int getRootReadLayout() {
        return R.layout.fragment_event_read_task_layout;
    }




    @Override
    public void onListItemClick(View view, int position, Object item) {
        Task record = (Task)item;

        if(record != null) {
            Intent intent = new Intent(getActivity(), EventReadTaskInfoActivity.class);
            intent.putExtra(ConstantHelper.KEY_DATA_UUID, record.getUuid());
            intent.putExtra(ConstantHelper.ARG_FILTER_STATUS, record.getEvent().getEventStatus());
            startActivity(intent);
        }
    }

}
