package de.symeda.sormas.app.task.list;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.BaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.task.read.TaskReadActivity;

public class TaskListFragment extends BaseListFragment<TaskListAdapter> implements OnListItemClickListener {

    private List<Task> tasks;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerViewForList;

    public static TaskListFragment newInstance(TaskStatus listFilter) {
        return newInstance(TaskListFragment.class, null, listFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewForList = (RecyclerView) view.findViewById(R.id.recyclerViewForList);

        return view;
    }

    @Override
    protected void prepareFragmentData() {
        switch ((TaskStatus) getListFilter()) {
            case PENDING:
                tasks = DatabaseHelper.getTaskDao().queryMyPending();
                break;
            case DONE:
            case REMOVED:
                tasks = DatabaseHelper.getTaskDao().queryMyDoneOrRemoved();
                break;
            case NOT_EXECUTABLE:
                tasks = DatabaseHelper.getTaskDao().queryMyNotExecutable();
                break;
            default:
                throw new IllegalArgumentException(getListFilter().toString());
        }
        getListAdapter().replaceAll(tasks);
    }

    @Override
    public TaskListAdapter getNewListAdapter() {
        return new TaskListAdapter(this.getActivity(), R.layout.row_task_list_item_layout, this, this.tasks);
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Task t = (Task) item;
        TaskReadActivity.startActivity(getContext(), t.getUuid());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerViewForList.setLayoutManager(linearLayoutManager);
        recyclerViewForList.setAdapter(getListAdapter());
    }
}
