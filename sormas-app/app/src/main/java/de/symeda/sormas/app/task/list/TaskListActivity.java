package de.symeda.sormas.app.task.list;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.widget.AdapterView;

import org.joda.time.DateTime;

import java.util.Random;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.BaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.IListNavigationCapsule;
import de.symeda.sormas.app.core.ListNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;

public class TaskListActivity extends BaseListActivity {

    private TaskStatus statusFilters[] = new TaskStatus[]{TaskStatus.PENDING, TaskStatus.DONE, TaskStatus.NOT_EXECUTABLE};

    private TaskStatus filterStatus = null;
    private SearchBy searchBy = null;
    private String recordUuid = null;
    private BaseListFragment activeFragment = null;

    @Override
    protected void onCreateInner(Bundle savedInstanceState) {
        super.onCreateInner(savedInstanceState);
        filterStatus = (TaskStatus) getFilterStatusArg(savedInstanceState);
        searchBy = (SearchBy) getSearchStrategyArg(savedInstanceState);
        recordUuid = getRecordUuidArg(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveFilterStatusState(outState, filterStatus);
        saveSearchStrategyState(outState, searchBy);
        saveRecordUuidState(outState, recordUuid);
    }

    @Override
    public BaseListFragment getActiveListFragment() {
        if (activeFragment == null) {
            IListNavigationCapsule dataCapsule = new ListNavigationCapsule(TaskListActivity.this, filterStatus, searchBy);
            activeFragment = TaskListFragment.newInstance(dataCapsule);
        }
        return activeFragment;
    }

    @Override
    public int getPageMenuData() {
        return R.xml.data_landing_page_task_menu;
    }

    @Override
    public int onNotificationCountChangingAsync(AdapterView parent, LandingPageMenuItem menuItem, int position) {
        //TODO: Call database and retrieve notification count
        return (int) (new Random(DateTime.now().getMillis() * 1000).nextInt() / 10000000);
    }

    @Override
    protected BaseListFragment getListFragment(LandingPageMenuItem menuItem) {
        TaskStatus status = statusFilters[menuItem.getKey()];

        if (status == null)
            return null;

        filterStatus = status;
        IListNavigationCapsule dataCapsule = new ListNavigationCapsule(TaskListActivity.this, filterStatus, searchBy);

        activeFragment = TaskListFragment.newInstance(dataCapsule);
        return activeFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getNewMenu().setTitle(R.string.action_new_task);
        return true;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level2_tasks_list;
    }

    public static void goToActivity(Context fromActivity, IListNavigationCapsule dataCapsule) {
        BaseListActivity.goToActivity(fromActivity, TaskListActivity.class, dataCapsule);
    }
}
