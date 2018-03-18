package de.symeda.sormas.app.caze.edit.sub;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentTaskEditLayoutBinding;
import de.symeda.sormas.app.task.TaskFormNavigationCapsule;

/**
 * Created by Orson on 16/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class CaseEditTaskInfoFragment  extends BaseEditActivityFragment<FragmentTaskEditLayoutBinding, Task> {

    private String recordUuid = null;
    private TaskStatus pageStatus = null;
    private Task record;

    private View.OnClickListener doneCallback;
    private View.OnClickListener notExecCallback;

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
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.heading_level4_1_case_task_info);
    }

    @Override
    public Task getPrimaryData() {
        return record;
    }

    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            resultHolder.forItem().add(DatabaseHelper.getTaskDao().queryUuid(recordUuid));
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            //Item Data
            if (itemIterator.hasNext())
                record =  itemIterator.next();

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentTaskEditLayoutBinding contentBinding) {
        contentBinding.setData(record);
        contentBinding.setDoneCallback(doneCallback);
        contentBinding.setNotExecCallback(notExecCallback);
    }

    @Override
    public void onAfterLayoutBinding(FragmentTaskEditLayoutBinding contentBinding) {

    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_task_edit_layout;
    }

    private void setupCallback() {

        doneCallback = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        };

        notExecCallback = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContentBinding().txtCommentOnExec.enableErrorState((INotificationContext) getActivity(), "There are many variations of passages of Lorem Ipsum available.");
                //binding.checkbox1.enableErrorState("Hello");
                Toast.makeText(getContext(), "Not Executable", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    public static CaseEditTaskInfoFragment newInstance(IActivityCommunicator activityCommunicator, TaskFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, CaseEditTaskInfoFragment.class, capsule);
    }
}
