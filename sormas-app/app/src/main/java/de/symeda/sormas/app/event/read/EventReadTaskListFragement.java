package de.symeda.sormas.app.event.read;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.List;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;
import de.symeda.sormas.app.shared.TaskFormNavigationCapsule;
import de.symeda.sormas.app.task.read.TaskReadActivity;

public class EventReadTaskListFragement extends BaseReadFragment<FragmentFormListLayoutBinding, List<Task>, Event> implements OnListItemClickListener {

    private List<Task> record;

    private EventReadTaskListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        Event event = getActivityRootData();
        record = DatabaseHelper.getTaskDao().queryByEvent(event);
    }

    @Override
    public void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
        showEmptyListHint(record, R.string.entity_task);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new EventReadTaskListAdapter(
                R.layout.row_read_event_task_list_item_layout, EventReadTaskListFragement.this, record);

        getContentBinding().recyclerViewForList.setLayoutManager(linearLayoutManager);
        getContentBinding().recyclerViewForList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_event_tasks);
    }

    @Override
    public List<Task> getPrimaryData() {
        return null;
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
        Task task = (Task) item;
        TaskFormNavigationCapsule dataCapsule = new TaskFormNavigationCapsule(getContext(),
                task.getUuid(), task.getTaskStatus());
        TaskReadActivity.goToActivity(getActivity(), dataCapsule);
    }

    public static EventReadTaskListFragement newInstance(EventFormNavigationCapsule capsule, Event activityRootData) {
        return newInstance(EventReadTaskListFragement.class, capsule, activityRootData);
    }
}
