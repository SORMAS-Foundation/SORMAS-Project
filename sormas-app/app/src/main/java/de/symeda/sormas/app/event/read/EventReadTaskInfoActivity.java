package de.symeda.sormas.app.event.read;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.contact.read.sub.ContactReadFollowUpVisitInfoActivity;
import de.symeda.sormas.app.task.TaskFormNavigationCapsule;
import de.symeda.sormas.app.util.NavigationHelper;

/**
 * Created by Orson on 30/12/2017.
 */

public class EventReadTaskInfoActivity  extends BaseReadActivity {

    private String recordUuid = null;
    private EventStatus filterStatus = null;
    private TaskStatus pageStatus = null;
    private BaseReadActivityFragment activeFragment = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveFilterStatusState(outState, filterStatus);
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
        filterStatus = (EventStatus) getFilterStatusArg(arguments);
        pageStatus = (TaskStatus) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    public BaseReadActivityFragment getActiveReadFragment() throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            TaskFormNavigationCapsule dataCapsule = new TaskFormNavigationCapsule(
                    EventReadTaskInfoActivity.this, recordUuid, pageStatus);
            activeFragment = EventReadTaskInfoFragment.newInstance(this, dataCapsule);
        }

        return activeFragment;
    }

    @Override
    public LandingPageMenuItem getActiveMenuItem() {
        return null;
    }

    @Override
    public boolean showStatusFrame() {
        return true;
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
        readMenu.setVisible(false);
        readMenu.setTitle(R.string.action_edit_event);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavigationHelper.navigateUpFrom(this);
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
        return R.string.heading_level3_1_event_read_task_info;
    }

    public static void goToActivity(Context fromActivity, TaskFormNavigationCapsule dataCapsule) {
        BaseReadActivity.goToActivity(fromActivity, EventReadTaskInfoActivity.class, dataCapsule);
    }
}
