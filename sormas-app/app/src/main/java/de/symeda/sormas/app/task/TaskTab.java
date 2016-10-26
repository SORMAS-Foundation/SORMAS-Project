package de.symeda.sormas.app.task;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
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
        initModel();

        binding = DataBindingUtil.inflate(inflater, R.layout.task_fragment_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {

        final String taskUuid = (String) getArguments().getString(Task.UUID);
        TaskDao taskDao = DatabaseHelper.getTaskDao();
        binding.setTask(taskDao.queryUuid(taskUuid));

        super.onResume();
    }


    @Override
    protected AbstractDomainObject commit(AbstractDomainObject ado) {
        Task task = (Task) ado;

//        caze.setHealthFacility((Facility) getModel().get(R.id.form_cd_health_facility));

        return task;
    }

    @Override
    public AbstractDomainObject getData() {
        return commit(binding.getTask());
    }
}