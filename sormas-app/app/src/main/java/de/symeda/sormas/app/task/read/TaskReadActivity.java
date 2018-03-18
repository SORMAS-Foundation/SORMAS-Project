package de.symeda.sormas.app.task.read;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.task.TaskFormNavigationCapsule;
import de.symeda.sormas.app.task.edit.TaskEditActivity;
import de.symeda.sormas.app.util.ConstantHelper;
import de.symeda.sormas.app.util.NavigationHelper;

/**
 * Created by Orson on 31/12/2017.
 */

public class TaskReadActivity extends BaseReadActivity {
    private TaskStatus pageStatus = null;
    private String recordUuid = null;
    private int activeMenuKey = ConstantHelper.INDEX_FIRST_MENU;
    private BaseReadActivityFragment activeFragment = null;

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
    }

    @Override
    public BaseReadActivityFragment getActiveReadFragment() throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            TaskFormNavigationCapsule dataCapsule = new TaskFormNavigationCapsule(TaskReadActivity.this,
                    recordUuid, pageStatus);
            activeFragment = TaskReadFragment.newInstance(this, dataCapsule);
        }

        return activeFragment;
    }

    @Override
    public LandingPageMenuItem getActiveMenuItem() {
        return null;
    }

    @Override
    public boolean showStatusFrame() {
        return false;
    }

    @Override
    public boolean showTitleBar() {
        return true;
    }

    @Override
    public boolean showPageMenu() {
        return false;
    }

    @Override
    public Enum getPageStatus() {
        return pageStatus;
    }

    @Override
    public String getPageMenuData() {
        return null;
    }

    @Override
    public boolean onLandingPageMenuClick(AdapterView<?> parent, View view, LandingPageMenuItem menuItem, int position, long id) {
        return false;
    }

    @Override
    public LandingPageMenuItem onSelectInitialActiveMenuItem(ArrayList<LandingPageMenuItem> menuList) {
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.read_action_menu, menu);

        MenuItem readMenu = menu.findItem(R.id.action_edit);
        //readMenu.setVisible(false);
        readMenu.setTitle(R.string.action_edit_task);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavigationHelper.navigateUpFrom(this);
                return true;

            case R.id.action_edit:
                gotoEditView();
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
        return R.string.heading_level3_task_read;
    }

    private void gotoEditView() {
        if (activeFragment == null)
            return;

        Task record = (Task)activeFragment.getPrimaryData();

        TaskFormNavigationCapsule dataCapsule = new TaskFormNavigationCapsule(TaskReadActivity.this,
                record.getUuid(), record.getTaskStatus());
        TaskEditActivity.goToActivity(this, dataCapsule);
    }

    public static void goToActivity(Context fromActivity, TaskFormNavigationCapsule dataCapsule) {
        BaseReadActivity.goToActivity(fromActivity, TaskReadActivity.class, dataCapsule);
    }
}
