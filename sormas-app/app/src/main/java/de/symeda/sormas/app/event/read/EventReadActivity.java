package de.symeda.sormas.app.event.read;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.event.edit.EventEditActivity;
import de.symeda.sormas.app.event.EventFormNavigationCapsule;
import de.symeda.sormas.app.util.ConstantHelper;
import de.symeda.sormas.app.util.NavigationHelper;

import java.util.ArrayList;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.backend.event.Event;

/**
 * Created by Orson on 24/12/2017.
 */

public class EventReadActivity extends BaseReadActivity {

    private final String DATA_XML_PAGE_MENU = "xml/data_read_page_alert_menu.xml";

    private EventStatus filterStatus = null;
    private EventStatus pageStatus = null;
    private String eventUuid = null;
    private LandingPageMenuItem activeMenuItem = null;
    private int activeMenuKey = ConstantHelper.INDEX_FIRST_MENU;
    private BaseReadActivityFragment activeFragment = new EventReadFragment();

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveFilterStatusState(outState, filterStatus);
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

        /*if (savedInstanceState == null) {
            Bundle b = getIntent().getExtras();
            if (b != null){
                //Get Shipment Status
                filterStatus = getEventStatusArg(b);

                //Get Event Key
                eventUuid = getEventUuidArg(b);
            }

            activeMenuKey = ConstantHelper.INDEX_FIRST_MENU;
            filterStatus = (filterStatus == null)? EventStatus.POSSIBLE : filterStatus;
        } else {
            filterStatus = (EventStatus) savedInstanceState.get(ConstantHelper.ARG_FILTER_STATUS);
            eventUuid = savedInstanceState.getString(ConstantHelper.KEY_EVENT_UUID);
            activeMenuKey = savedInstanceState.getInt(ConstantHelper.KEY_ACTIVE_MENU);
        }*/
    }

    @Override
    protected void initializeActivity(Bundle arguments) {
        filterStatus = (EventStatus) getFilterStatusArg(arguments);
        pageStatus = (EventStatus) getPageStatusArg(arguments);
        eventUuid = getRecordUuidArg(arguments);
    }

    @Override
    public BaseReadActivityFragment getActiveReadFragment() {
        return activeFragment;
    }

    @Override
    public LandingPageMenuItem getActiveMenuItem() {
        return activeMenuItem;
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
        return true;
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
    public boolean onLandingPageMenuClick(AdapterView<?> parent, View view, LandingPageMenuItem menuItem, int position, long id) {
        setActiveMenu(menuItem);

        if (menuItem.getKey() == 0) {
            activeFragment = new EventReadFragment();
            replaceFragment(activeFragment);
        } else if (menuItem.getKey() == 1) {
            activeFragment = new EventReadPersonsInvolvedFragment();
            replaceFragment(activeFragment);
        } else if (menuItem.getKey() == 2) {
            activeFragment = new EventReadTaskListFragement();
            replaceFragment(activeFragment);
        }

        updateSubHeadingTitle();

        return true;
    }

    @Override
    public LandingPageMenuItem onSelectInitialActiveMenuItem(ArrayList<LandingPageMenuItem> menuList) {
        activeMenuItem = menuList.get(0);

        for(LandingPageMenuItem m: menuList){
            if (m.getKey() == activeMenuKey){
                activeMenuItem = m;
            }
        }

        return activeMenuItem;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.read_action_menu, menu);

        MenuItem readMenu = menu.findItem(R.id.action_edit);
        //readMenu.setVisible(false);
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
        return R.string.heading_level3_event_read;
    }


    private void gotoEditView() {
        if (activeFragment == null)
            return;

        Event record = (Event)activeFragment.getRecord();

        EventFormNavigationCapsule dataCapsule = new EventFormNavigationCapsule(EventReadActivity.this,
                record.getUuid(), pageStatus);
        EventEditActivity.goToActivity(this, dataCapsule);
    }



    public static void goToActivity(Context fromActivity, EventFormNavigationCapsule dataCapsule) {
        BaseReadActivity.goToActivity(fromActivity, EventReadActivity.class, dataCapsule);
    }
}

