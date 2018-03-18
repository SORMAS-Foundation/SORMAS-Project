package de.symeda.sormas.app.event.edit;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import de.symeda.sormas.app.AbstractSormasActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.event.EventFormNavigationCapsule;
import de.symeda.sormas.app.util.ConstantHelper;
import de.symeda.sormas.app.util.NavigationHelper;

import de.symeda.sormas.api.event.EventStatus;

/**
 * Created by Orson on 07/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class EventEditActivity extends BaseEditActivity {

    private final String DATA_XML_PAGE_MENU = "xml/data_edit_page_alert_menu.xml";

    private static final int MENU_INDEX_EVENT_INFO = 0;
    private static final int MENU_INDEX_EVENT__PERSON_INVOLVED = 1;
    private static final int MENU_INDEX_EVENT_TASK = 2;

    private boolean showStatusFrame = false;
    private boolean showTitleBar = true;
    private boolean showPageMenu = false;

    private EventStatus pageStatus = null;
    private String eventUuid = null;
    private int activeMenuKey = ConstantHelper.INDEX_FIRST_MENU;
    private BaseEditActivityFragment activeFragment = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //SaveFilterStatusState(outState, filterStatus);
        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, eventUuid);
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
        //filterStatus = (EventStatus) getFilterStatusArg(arguments);
        pageStatus = (EventStatus) getPageStatusArg(arguments);
        eventUuid = getRecordUuidArg(arguments);

        this.showStatusFrame = true;
        this.showTitleBar = true;
        this.showPageMenu = true;
    }

    @Override
    public BaseEditActivityFragment getActiveEditFragment() throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            EventFormNavigationCapsule dataCapsule = new EventFormNavigationCapsule(EventEditActivity.this,
                    eventUuid, pageStatus);
            activeFragment = EventEditFragment.newInstance(this, dataCapsule);
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
    public String getPageMenuData() {
        return DATA_XML_PAGE_MENU;
    }

    @Override
    public boolean onLandingPageMenuClick(AdapterView<?> parent, View view, LandingPageMenuItem menuItem, int position, long id) throws IllegalAccessException, InstantiationException {
        setActiveMenu(menuItem);

        EventFormNavigationCapsule dataCapsule = new EventFormNavigationCapsule(EventEditActivity.this,
                eventUuid, pageStatus);

        if (menuItem.getKey() == MENU_INDEX_EVENT_INFO) {
            activeFragment = EventEditFragment.newInstance(this, dataCapsule);
        } else if (menuItem.getKey() == MENU_INDEX_EVENT__PERSON_INVOLVED) {
            activeFragment = EventEditPersonsInvolvedListFragment.newInstance(this, dataCapsule);
        } else if (menuItem.getKey() == MENU_INDEX_EVENT_TASK) {
            activeFragment = EventEditTaskListFragement.newInstance(this, dataCapsule);
        }

        replaceFragment(activeFragment);
        updateSubHeadingTitle();

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_action_menu, menu);

        MenuItem saveMenu = menu.findItem(R.id.action_save);
        MenuItem addMenu = menu.findItem(R.id.action_new);

        saveMenu.setVisible(true);
        addMenu.setVisible(false);

        saveMenu.setTitle(R.string.action_save_sample);

        return true;
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
        return R.string.heading_level4_event_edit;
    }

    public static <TActivity extends AbstractSormasActivity> void
    goToActivity(Context fromActivity, EventFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, EventEditActivity.class, dataCapsule);
    }

}