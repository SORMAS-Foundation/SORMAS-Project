package de.symeda.sormas.app.contact.edit.sub;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.contact.ContactFormFollowUpNavigationCapsule;
import de.symeda.sormas.app.util.NavigationHelper;

import de.symeda.sormas.api.visit.VisitStatus;

/**
 * Created by Orson on 13/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class ContactEditFollowUpInfoActivity extends BaseEditActivity {

    private final String DATA_XML_PAGE_MENU = "xml/data_edit_page_3_1_followup_menu.xml";

    private static final int MENU_INDEX_VISIT_INFO = 0;
    private static final int MENU_INDEX_SYMPTOMS_INFO = 1;

    private boolean showStatusFrame;
    private boolean showTitleBar;
    private boolean showPageMenu;

    private String recordUuid = null;
    private VisitStatus pageStatus = null;
    private BaseEditActivityFragment activeFragment = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //SaveFilterStatusState(outState, followUpStatus);
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
        pageStatus = (VisitStatus) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);

        this.showStatusFrame = true;
        this.showTitleBar = true;
        this.showPageMenu = true;
    }

    @Override
    public BaseEditActivityFragment getActiveEditFragment() throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            ContactFormFollowUpNavigationCapsule dataCapsule = new ContactFormFollowUpNavigationCapsule(
                    ContactEditFollowUpInfoActivity.this, recordUuid, pageStatus);
            activeFragment = ContactEditFollowUpVisitInfoFragment.newInstance(this, dataCapsule);
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

        ContactFormFollowUpNavigationCapsule dataCapsule = new ContactFormFollowUpNavigationCapsule(
                ContactEditFollowUpInfoActivity.this, recordUuid, pageStatus);

        if (menuItem.getKey() == MENU_INDEX_VISIT_INFO) {
            activeFragment = ContactEditFollowUpVisitInfoFragment.newInstance(this, dataCapsule);
            replaceFragment(activeFragment);
        } else if (menuItem.getKey() == MENU_INDEX_SYMPTOMS_INFO) {
            activeFragment = ContactEditFollowUpSymptomsFragment.newInstance(this, dataCapsule);
            replaceFragment(activeFragment);
        }

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

        saveMenu.setTitle(R.string.action_save_followup);

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
        return R.string.heading_level3_1_contact_visit_info;
    }

    public static void goToActivity(Context fromActivity, ContactFormFollowUpNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, ContactEditFollowUpInfoActivity.class, dataCapsule);
    }
}