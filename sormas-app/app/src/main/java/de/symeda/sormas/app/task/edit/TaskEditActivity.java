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
import de.symeda.sormas.app.shared.TaskFormNavigationCapsule;
import de.symeda.sormas.app.util.ConstantHelper;
import de.symeda.sormas.app.util.NavigationHelper;

/**
 * Created by Orson on 22/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TaskEditActivity extends BaseEditActivity<Task> {

    private boolean showStatusFrame = false;
    private boolean showTitleBar = true;
    private boolean showPageMenu = false;
    private final int DATA_XML_PAGE_MENU = -1;

    private TaskStatus pageStatus = null;
    private String recordUuid = null;
    private int activeMenuKey = ConstantHelper.INDEX_FIRST_MENU;
    private BaseEditActivityFragment activeFragment = null;

    private MenuItem saveMenu = null;
    private MenuItem addMenu = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SavePageStatusState(outState, pageStatus);
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
        pageStatus = (TaskStatus) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);

        this.showStatusFrame = false;
        this.showTitleBar = true;
        this.showPageMenu = false;
    }

    @Override
    protected Task getActivityRootData(String recordUuid) {
        return DatabaseHelper.getTaskDao().queryUuid(recordUuid);
    }

    @Override
    protected Task getActivityRootDataIfRecordUuidNull() {
        return null;
    }

    @Override
    public BaseEditActivityFragment getActiveEditFragment(Task activityRootData) throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            TaskFormNavigationCapsule dataCapsule = new TaskFormNavigationCapsule(TaskEditActivity.this,
                    recordUuid, pageStatus);
            activeFragment = TaskEditFragment.newInstance(this, dataCapsule, activityRootData);
        }

        return activeFragment;
    }

    @Override
    public boolean showStatusFrame() {
        return showStatusFrame;
    }

    @Override
    public boolean showTitleBar() {
        return showTitleBar;
    }

    @Override
    public boolean showPageMenu() {
        return showPageMenu;
    }

    @Override
    public Enum getPageStatus() {
        return pageStatus;
    }

    @Override
    public int getPageMenuData() {
        return DATA_XML_PAGE_MENU;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_task);

        return true;
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_action_menu, menu);

        saveMenu = menu.findItem(R.id.action_save);
        addMenu = menu.findItem(R.id.action_new);

        saveMenu.setTitle(R.string.action_save_task);

        processActionbarMenu();

        return true;*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavigationHelper.navigateUpFrom(this);
                return true;

            case R.id.action_save:
                //synchronizeChangedData();
                return true;

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
                /*UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), null);
                AlertDialog dialog = userReportDialog.create();
                dialog.show();*/

                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level4_task_edit;
    }

    public static void goToActivity(Context fromActivity, TaskFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, TaskEditActivity.class, dataCapsule);
    }

}
