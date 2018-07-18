package de.symeda.sormas.app.event.edit;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.List;

import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;
import de.symeda.sormas.app.shared.TaskFormNavigationCapsule;
import de.symeda.sormas.app.task.edit.TaskEditActivity;

public class EventEditTaskListFragement extends BaseEditFragment<FragmentFormListLayoutBinding, List<Task>, Event> implements OnListItemClickListener {

    private List<Task> record;
    private EventEditTaskListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_event_tasks);
    }

    @Override
    public List<Task> getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        Event event = getActivityRootData();
        record = DatabaseHelper.getTaskDao().queryByEvent(event);
    }

    @Override
    public void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
        showEmptyListHint(record, R.string.entity_task);

        //Create adapter and set data
        adapter = new EventEditTaskListAdapter(R.layout.row_read_task_list_item_layout, this, record);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        contentBinding.recyclerViewForList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public int getRootEditLayout() {
        return R.layout.fragment_root_list_form_layout;
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_form_list_layout;
    }

    @Override
    public boolean isShowSaveAction() {
        return false;
    }

    @Override
    public boolean isShowAddAction() {
        return false;
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Task task = (Task) item;
        TaskFormNavigationCapsule dataCapsule = new TaskFormNavigationCapsule(getContext(),
                task.getUuid(), task.getTaskStatus());
        TaskEditActivity.goToActivity(getActivity(), dataCapsule);
    }

    public static EventEditTaskListFragement newInstance(EventFormNavigationCapsule capsule, Event activityRootData) {
        return newInstance(EventEditTaskListFragement.class, capsule, activityRootData);
    }
}
