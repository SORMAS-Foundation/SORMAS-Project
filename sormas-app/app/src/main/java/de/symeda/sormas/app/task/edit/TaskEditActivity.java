package de.symeda.sormas.app.task.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.shared.TaskFormNavigationCapsule;

public class TaskEditActivity extends BaseEditActivity<Task> {

    private AsyncTask saveTask;


    @Override
    public TaskStatus getPageStatus() {
        return (TaskStatus)super.getPageStatus();
    }

    @Override
    protected Task queryRootEntity(String recordUuid) {
        return DatabaseHelper.getTaskDao().queryUuid(recordUuid);
    }

    @Override
    protected Task buildRootEntity() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveData() {
        final Task taskToSave = getStoredRootEntity();

        saveTask = new SavingAsyncTask(getRootView(), taskToSave) {

            @Override
            public void doInBackground(TaskResultHolder resultHolder) throws DaoException, ValidationException {
                validateData(taskToSave);
                DatabaseHelper.getTaskDao().saveAndSnapshot(taskToSave);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                super.onPostExecute(taskResult);

                if (taskResult.getResultStatus().isSuccess()) {
                    finish();
                }
            }
        }.executeOnThreadPool();
    }

    private void validateData(Task data) throws ValidationException {
        if (data.getTaskStatus() == TaskStatus.NOT_EXECUTABLE
                && DataHelper.isNullOrEmpty(data.getAssigneeReply())) {
            // TODO I18n: Replace with text from I18nProperties?
            throw new ValidationException(getContext().getResources().getString(R.string.snackbar_task_reply));
        }

        if (data.getTaskStatus() == TaskStatus.DONE
                && data.getTaskType() == TaskType.CASE_INVESTIGATION) {
            Case caze = DatabaseHelper.getCaseDao().queryUuidBasic(data.getCaze().getUuid());
            CaseDataDto cazeDto = new CaseDataDto();
            CaseDtoHelper.fillDto(cazeDto, caze);
            CaseLogic.validateInvestigationDoneAllowed(cazeDto);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_task);
        return result;
    }

    @Override
    protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Task activityRootData) {
        TaskFormNavigationCapsule dataCapsule = new TaskFormNavigationCapsule(this, getRootEntityUuid(), getPageStatus());
        return TaskEditFragment.newInstance(dataCapsule, activityRootData);
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level4_task_edit;
    }

    public static void goToActivity(Context fromActivity, TaskFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, TaskEditActivity.class, dataCapsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }
}
