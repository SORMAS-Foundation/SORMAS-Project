package de.symeda.sormas.app.caze.edit;

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
import de.symeda.sormas.app.caze.CaseFormNavigationCapsule;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.util.NavigationHelper;

import de.symeda.sormas.api.caze.InvestigationStatus;

/**
 * Created by Orson on 16/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class CaseEditActivity extends BaseEditActivity {

    private final String DATA_XML_PAGE_MENU = "xml/data_edit_page_case_menu.xml";

    private static final int MENU_INDEX_CASE_INFO = 0;
    private static final int MENU_INDEX_PATIENT_INFO = 1;
    private static final int MENU_INDEX_HOSPITALIZATION = 2;
    private static final int MENU_INDEX_SYMPTOMS = 3;
    private static final int MENU_INDEX_EPIDEMIOLOGICAL_DATA = 4;
    private static final int MENU_INDEX_CONTACTS = 5;
    private static final int MENU_INDEX_SAMPLES = 6;
    private static final int MENU_INDEX_TASKS = 7;

    private boolean showStatusFrame;
    private boolean showTitleBar;
    private boolean showPageMenu;

    private InvestigationStatus pageStatus = null;
    private String recordUuid = null;
    private BaseEditActivityFragment activeFragment = null;

    private MenuItem saveMenu = null;
    private MenuItem addMenu = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //SaveFilterStatusState(outState, filterStatus);
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
        //filterStatus = (EventStatus) getFilterStatusArg(arguments);
        pageStatus = (InvestigationStatus) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);

        this.showStatusFrame = true;
        this.showTitleBar = true;
        this.showPageMenu = true;
    }

    @Override
    public BaseEditActivityFragment getActiveEditFragment() throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(CaseEditActivity.this,
                    recordUuid).setEditPageStatus(pageStatus);
            activeFragment = CaseEditFragment.newInstance(this, dataCapsule);
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

        CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(CaseEditActivity.this,
                recordUuid).setEditPageStatus(pageStatus);

        if (menuItem.getKey() == MENU_INDEX_CASE_INFO) {
            activeFragment = CaseEditFragment.newInstance(this, dataCapsule);
            replaceFragment(activeFragment);
        } else if (menuItem.getKey() == MENU_INDEX_PATIENT_INFO) {
            activeFragment = CaseEditPatientInfoFragment.newInstance(this, dataCapsule);
            replaceFragment(activeFragment);
        } else if (menuItem.getKey() == MENU_INDEX_HOSPITALIZATION) {
            activeFragment = CaseEditHospitalizationFragment.newInstance(this, dataCapsule);
            replaceFragment(activeFragment);
        } else if (menuItem.getKey() == MENU_INDEX_SYMPTOMS) {
            activeFragment = CaseEditSymptomsFragment.newInstance(this, dataCapsule);
            replaceFragment(activeFragment);
        } else if (menuItem.getKey() == MENU_INDEX_EPIDEMIOLOGICAL_DATA) {
            activeFragment = CaseEditEpidemiologicalDataFragment.newInstance(this, dataCapsule);
            replaceFragment(activeFragment);
        } else if (menuItem.getKey() == MENU_INDEX_CONTACTS) {
            activeFragment = CaseEditContactListFragment.newInstance(this, dataCapsule);
            replaceFragment(activeFragment);
        }else if (menuItem.getKey() == MENU_INDEX_SAMPLES) {
            activeFragment = CaseEditSampleListFragment.newInstance(this, dataCapsule);
            replaceFragment(activeFragment);
        } else if (menuItem.getKey() == MENU_INDEX_TASKS) {
            activeFragment = CaseEditTaskListFragment.newInstance(this, dataCapsule);
            replaceFragment(activeFragment);
        }

        processActionbarMenu();
        updateSubHeadingTitle();

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_action_menu, menu);

        saveMenu = menu.findItem(R.id.action_save);
        addMenu = menu.findItem(R.id.action_new);

        saveMenu.setTitle(R.string.action_save_case);

        saveMenu.setVisible(true);
        addMenu.setVisible(false);

        processActionbarMenu();

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
        return R.string.heading_level4_case_edit;
    }

    private void processActionbarMenu() {
        if (activeFragment == null)
            return;

        if (saveMenu != null)
            saveMenu.setVisible(activeFragment.showSaveAction());

        if (addMenu != null)
            addMenu.setVisible(activeFragment.showAddAction());
    }

    public static <TActivity extends AbstractSormasActivity> void
    goToActivity(Context fromActivity, CaseFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, CaseEditActivity.class, dataCapsule);
    }

}