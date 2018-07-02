package de.symeda.sormas.app.task.edit;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
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
    protected Task queryRootEntity(String recordUuid) {
        return DatabaseHelper.getTaskDao().queryUuid(recordUuid);
    }

    @Override
    protected Task buildRootEntity() {
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
    protected BaseEditFragment buildEditFragment(LandingPageMenuItem menuItem, Task activityRootData) {
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
}
