package de.symeda.sormas.app.task;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.List;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.task.Task;

/**
 * Created by Stefan Szczesny on 24.10.2016.
 */
public class TasksListFragment extends ListFragment {

    public static final String ARG_FILTER_STATUS = "filterStatus";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cases_list_layout, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        List<Task> tasks;
        Bundle arguments = getArguments();

        if (arguments.containsKey(ARG_FILTER_STATUS) && TaskStatus.PENDING.equals((TaskStatus)arguments.getSerializable(ARG_FILTER_STATUS))) {
            tasks = DatabaseHelper.getTaskDao().queryPending();
        } else {
            tasks = DatabaseHelper.getTaskDao().queryFinished();
        }

        ArrayAdapter<Task> listAdapter = (ArrayAdapter<Task>)getListAdapter();
        listAdapter.clear();
        listAdapter.addAll(tasks);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TasksListArrayAdapter adapter = new TasksListArrayAdapter(
                this.getActivity(),           // Context for the activity.
                R.layout.tasks_list_item);    // Layout to use (create)

        setListAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent,
                    View viewClicked,
                    int position, long id) {
                Task task = (Task)getListAdapter().getItem(position);
                showTaskEditView(task);
            }
        });
    }

    public void showTaskEditView(Task task) {
        Intent intent = new Intent(getActivity(), TaskEditActivity.class);
        intent.putExtra(Task.UUID, task.getUuid());
        startActivity(intent);
    }
}
