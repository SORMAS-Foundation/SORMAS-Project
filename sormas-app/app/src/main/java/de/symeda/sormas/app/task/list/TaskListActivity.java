package de.symeda.sormas.app.task.list;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;

import org.joda.time.DateTime;

import java.util.Random;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.BaseListActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.IListNavigationCapsule;
import de.symeda.sormas.app.core.ListNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;

/**
 * Created by Orson on 01/12/2017.
 */

public class TaskListActivity  extends BaseListActivity {

    private final int DATA_XML_PAGE_MENU = R.xml.data_landing_page_task_menu;//  "xml/data_landing_page_task_menu.xml";

    private static final int MENU_INDEX_TASK_PENDING = 0;
    private static final int MENU_INDEX_TASK_DONE = 1;
    private static final int MENU_INDEX_TASK_NOT_EXECUTABLE = 2;

    private TaskStatus statusFilters[] = new TaskStatus[] { TaskStatus.PENDING, TaskStatus.DONE, TaskStatus.NOT_EXECUTABLE };

    private TaskStatus filterStatus = null;
    private SearchBy searchBy = null;
    private String recordUuid = null;
    private BaseListActivityFragment activeFragment = null;
    private boolean showStatusFrame;
    private boolean showTitleBar;
    private MenuItem newMenu = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveFilterStatusState(outState, filterStatus);
        SaveSearchStrategyState(outState, searchBy);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initializeActivity(Bundle arguments) {
        filterStatus = (TaskStatus) getFilterStatusArg(arguments);
        searchBy = (SearchBy) getSearchStrategyArg(arguments);
        recordUuid = getRecordUuidArg(arguments);

        this.showStatusFrame = true;
        this.showTitleBar = true;
    }

    @Override
    public BaseListActivityFragment getActiveReadFragment() throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            IListNavigationCapsule dataCapsule = new ListNavigationCapsule(TaskListActivity.this, filterStatus, searchBy);
            activeFragment = TaskListFragment.newInstance(this, dataCapsule);
        }

        return activeFragment;
    }

    @Override
    public int getPageMenuData() {
        return DATA_XML_PAGE_MENU;
    }

    @Override
    public int onNotificationCountChanging(AdapterView parent, LandingPageMenuItem menuItem, int position) {
        //TODO: Call database and retrieve notification count
        return (int)(new Random(DateTime.now().getMillis() * 1000).nextInt()/10000000);
    }

    @Override
    protected BaseListActivityFragment getNextFragment(LandingPageMenuItem menuItem) {
        TaskStatus status = statusFilters[menuItem.getKey()];

        if (status == null)
            return null;

        filterStatus = status;
        IListNavigationCapsule dataCapsule = new ListNavigationCapsule(TaskListActivity.this, filterStatus, searchBy);

        try {
            activeFragment = TaskListFragment.newInstance(this, dataCapsule);
        } catch (InstantiationException e) {
            Log.e(TAG, e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.getMessage());
        }

        return activeFragment;
    }

    @Override
    public Enum getStatus() {
        return null;
    }

    @Override
    public boolean showStatusFrame() {
        return false;
    }

    @Override
    public boolean showTitleBar() {
        return showTitleBar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getNewMenu().setTitle(R.string.action_new_task);

        return true;
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_action_bar, menu);

        MenuItem listMenu = menu.findItem(R.id.action_new);
        listMenu.setVisible(false);
        listMenu.setTitle(R.string.action_new_task);

        return true;*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            /*case android.R.id.home:
                // TODO check parent activity intent as soon as the minimum API level has been increased to 16
                //Intent intent = new Intent(this, TasksLandingActivity.class);
                //startActivity(intent);
                if (activeFragment != null)
                    activeFragment.cancelTaskExec();

                NavigationHelper.navigateUpFrom(this);

                return true;*/

            case R.id.option_menu_action_sync:
                //synchronizeChangedData();
                return true;

            case R.id.option_menu_action_markAllAsRead:
                /*CaseDao caseDao = DatabaseHelper.getCaseDao();
                PersonDao personDao = DatabaseHelper.getPersonDao();
                List<Case> cases = caseDao.queryForAll();
                for (Case caseToMark : cases) {
                    caseDao.markAsRead(caseToMark);
                }

                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment instanceof CasesListFragment) {
                        fragment.onResume();
                    }
                }*/
                return true;

            // Report problem button
            case R.id.action_report:
                /*Task task = (Task) taskForm.getPrimaryData();

                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), task.getUuid());
                AlertDialog dialog = userReportDialog.create();
                dialog.show();*/

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level2_tasks_list;
    }

    public static void goToActivity(Context fromActivity, IListNavigationCapsule dataCapsule) {
        BaseListActivity.goToActivity(fromActivity, TaskListActivity.class, dataCapsule);
    }
}
