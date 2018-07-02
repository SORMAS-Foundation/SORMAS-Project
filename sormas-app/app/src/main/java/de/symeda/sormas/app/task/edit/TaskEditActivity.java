package de.symeda.sormas.app.task.edit;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.shared.TaskFormNavigationCapsule;
import de.symeda.sormas.app.util.MenuOptionsHelper;

public class TaskEditActivity extends BaseEditActivity<Task> {

    @Override
    public TaskStatus getPageStatus() {
        return (TaskStatus)super.getPageStatus();
    }

    @Override
    protected Task queryActivityRootEntity(String recordUuid) {
        return DatabaseHelper.getTaskDao().queryUuid(recordUuid);
    }

    @Override
    protected Task buildActivityRootEntity() {
        return null;
    }

    @Override
    public boolean isShowStatusFrame() {
        return false;
    }

    @Override
    public void saveData() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_task);
        return result;
    }

    @Override
    protected BaseEditActivityFragment buildEditFragment(LandingPageMenuItem menuItem, Task activityRootData) {
        TaskFormNavigationCapsule dataCapsule = new TaskFormNavigationCapsule(TaskEditActivity.this,
                getRootEntityUuid(), getPageStatus());
        return TaskEditFragment.newInstance(dataCapsule, activityRootData);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!MenuOptionsHelper.handleEditModuleOptionsItemSelected(this, item))
            return super.onOptionsItemSelected(item);

        return true;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level4_task_edit;
    }

    public static void goToActivity(Context fromActivity, TaskFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, TaskEditActivity.class, dataCapsule);
    }

}
