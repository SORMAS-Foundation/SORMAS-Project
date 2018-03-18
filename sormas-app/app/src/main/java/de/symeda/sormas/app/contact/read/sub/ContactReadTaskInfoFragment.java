package de.symeda.sormas.app.contact.read.sub;

import android.os.Bundle;
import android.support.annotation.Nullable;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentContactReadTaskInfoLayoutBinding;
import de.symeda.sormas.app.task.TaskFormNavigationCapsule;

/**
 * Created by Orson on 02/01/2018.
 */

public class ContactReadTaskInfoFragment extends BaseReadActivityFragment<FragmentContactReadTaskInfoLayoutBinding, Task> {

    private String recordUuid = null;
    private TaskStatus pageStatus = null;
    private Task record;

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
        pageStatus = (TaskStatus) getPageStatusArg(arguments);
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            resultHolder.forItem().add(DatabaseHelper.getTaskDao().queryUuid(recordUuid));
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            if (itemIterator.hasNext())
                record =  itemIterator.next();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentContactReadTaskInfoLayoutBinding contentBinding) {
        contentBinding.setData(record);
    }

    @Override
    public void onAfterLayoutBinding(FragmentContactReadTaskInfoLayoutBinding contentBinding) {

    }

    @Override
    protected String getSubHeadingTitle() {
        String title = "";

        if (pageStatus != null) {
            title = pageStatus.toString();
        }

        return title;
    }

    @Override
    public Task getPrimaryData() {
        return record;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_contact_read_task_info_layout;
    }

    public static ContactReadTaskInfoFragment newInstance(IActivityCommunicator activityCommunicator, TaskFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, ContactReadTaskInfoFragment.class, capsule);
    }
}
