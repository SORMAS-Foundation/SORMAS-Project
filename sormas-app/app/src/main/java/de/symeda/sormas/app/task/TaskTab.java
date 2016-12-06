package de.symeda.sormas.app.task;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.symeda.sormas.api.task.TaskHelper;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.task.TaskDao;
import de.symeda.sormas.app.databinding.TaskFragmentLayoutBinding;
import de.symeda.sormas.app.util.FormTab;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 *
 */
public class TaskTab extends FormTab {

    private TaskFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.task_fragment_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {

        final String taskUuid = (String) getArguments().getString(Task.UUID);
        final TaskDao taskDao = DatabaseHelper.getTaskDao();
        final Task task = taskDao.queryUuid(taskUuid);

        binding.setTask(task);
        super.onResume();

        Button taskDoneBtn = (Button) getView().findViewById(R.id.task_done_btn);
        Iterable<TaskStatus> possibleStatus = TaskHelper.getPossibleStatusChanges(task.getTaskStatus(), ConfigProvider.getUser().getUserRole());
        if(possibleStatus.iterator().hasNext()) {
            taskDoneBtn.setVisibility(View.VISIBLE);
            final TaskStatus taskStatus = possibleStatus.iterator().next();
            taskDoneBtn.setText(taskStatus.getChangeString());
            taskDoneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    taskDao.changeTaskStatus(task, taskStatus);
                    reloadFragment();
                }
            });
        }
        else {
            taskDoneBtn.setVisibility(View.GONE);
        }
    }


    @Override
    protected AbstractDomainObject commit(AbstractDomainObject ado) {
        return null;
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getTask();
    }

}