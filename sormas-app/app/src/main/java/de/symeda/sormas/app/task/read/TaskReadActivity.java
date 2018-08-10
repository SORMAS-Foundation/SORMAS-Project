package de.symeda.sormas.app.task.read;

import android.content.Context;
import android.view.Menu;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.task.edit.TaskEditActivity;
import de.symeda.sormas.app.util.Bundler;
import de.symeda.sormas.app.visit.VisitSection;

public class TaskReadActivity extends BaseReadActivity<Task> {

    public static void startActivity(Context context, String rootUuid) {
        BaseReadActivity.startActivity(context, TaskReadActivity.class, buildBundle(rootUuid));
    }

    @Override
    protected Task queryRootData(String recordUuid) {
        return DatabaseHelper.getTaskDao().queryUuid(recordUuid);
    }

    @Override
    public TaskStatus getPageStatus() {
        return getStoredRootEntity() == null ? null : getStoredRootEntity().getTaskStatus();
    }

    @Override
    protected BaseReadFragment buildReadFragment(PageMenuItem menuItem, Task activityRootData) {
        return TaskReadFragment.newInstance(activityRootData);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getEditMenu().setTitle(R.string.action_edit_task);
        return result;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level3_task_read;
    }

    @Override
    public void goToEditView() {
        TaskEditActivity.startActivity(this, getRootUuid());
    }
}
