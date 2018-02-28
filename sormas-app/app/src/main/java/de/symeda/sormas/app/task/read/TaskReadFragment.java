package de.symeda.sormas.app.task.read;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.databinding.FragmentTaskReadLayoutBinding;
import de.symeda.sormas.app.task.TaskFormNavigationCapsule;
import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.task.Task;

/**
 * Created by Orson on 31/12/2017.
 */

public class TaskReadFragment extends BaseReadActivityFragment<FragmentTaskReadLayoutBinding> {

    private String recordUuid = null;
    private TaskStatus pageStatus = null;
    private Task record;
    private FragmentTaskReadLayoutBinding binding;

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
        pageStatus = (TaskStatus) getPageStatusArg(arguments);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = DataBindingUtil.inflate(inflater, getRootReadLayout(), container, false);
        record = MemoryDatabaseHelper.TASK.getTasks(1).get(0);

        binding.setData(record);

        return binding.getRoot();
    }

    @Override
    protected String getSubHeadingTitle() {
        String title = "";

        if (binding != null) {
            title = binding.getData().getTaskStatus().toString();
        }

        return title;
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getData();
    }

    @Override
    public FragmentTaskReadLayoutBinding getBinding() {
        return binding;
    }

    @Override
    public Object getRecord() {
        return record;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public int getRootReadLayout() {
        return R.layout.fragment_task_read_layout;
    }

    public static TaskReadFragment newInstance(TaskFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(TaskReadFragment.class, capsule);
    }

}
