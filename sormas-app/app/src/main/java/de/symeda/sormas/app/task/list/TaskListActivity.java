package de.symeda.sormas.app.task.list;

import android.content.Context;
import android.view.Menu;
import android.widget.AdapterView;

import org.joda.time.DateTime;

import java.util.Random;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.BaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.sample.list.SampleListActivity;

public class TaskListActivity extends BaseListActivity {

    private TaskStatus statusFilters[] = new TaskStatus[]{TaskStatus.PENDING, TaskStatus.DONE, TaskStatus.NOT_EXECUTABLE};

    public static void startActivity(Context context, TaskStatus listFilter) {
        BaseListActivity.startActivity(context, TaskListActivity.class, buildBundle(listFilter));
    }

    @Override
    public int getPageMenuData() {
        return R.xml.data_landing_page_task_menu;
    }

    @Override
    public int onNotificationCountChangingAsync(AdapterView parent, PageMenuItem menuItem, int position) {
        //TODO: Call database and retrieve notification count
        return (int) (new Random(DateTime.now().getMillis() * 1000).nextInt() / 10000000);
    }

    @Override
    protected BaseListFragment buildListFragment(PageMenuItem menuItem) {
        TaskStatus listFilter = statusFilters[menuItem.getKey()];
        return TaskListFragment.newInstance(listFilter);
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
}
