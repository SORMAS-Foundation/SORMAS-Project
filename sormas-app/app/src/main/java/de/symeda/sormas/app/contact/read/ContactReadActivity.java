package de.symeda.sormas.app.contact.read;

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
import de.symeda.sormas.app.contact.ContactFormNavigationCapsule;
import de.symeda.sormas.app.contact.edit.ContactEditActivity;
import de.symeda.sormas.app.util.NavigationHelper;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.app.backend.contact.Contact;

/**
 * Created by Orson on 01/01/2018.
 */

public class ContactReadActivity extends BaseReadActivity {

    private static final int MENU_INDEX_CONTACT_INFO = 0;
    private static final int MENU_INDEX_PERSON_INFO = 1;
    private static final int MENU_INDEX_FOLLOWUP_VISIT = 2;
    private static final int MENU_INDEX_TASK = 3;

    private final String DATA_XML_PAGE_MENU = "xml/data_read_page_contact_menu.xml";

    private String contactUuid = null;
    //private FollowUpStatus filterStatus = null;
    private ContactClassification pageStatus = null;
    private BaseReadActivityFragment activeFragment = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //SaveFilterStatusState(outState, filterStatus);
        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, contactUuid);
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
        //filterStatus = (FollowUpStatus) getFilterStatusArg(arguments);
        pageStatus = (ContactClassification) getPageStatusArg(arguments);
        contactUuid = getRecordUuidArg(arguments);
    }


    @Override
    public BaseReadActivityFragment getActiveReadFragment() throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            ContactFormNavigationCapsule dataCapsule = new ContactFormNavigationCapsule(ContactReadActivity.this, contactUuid, pageStatus);
            activeFragment = ContactReadFragment.newInstance(dataCapsule);
        }

        return activeFragment;
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
    public boolean onLandingPageMenuClick(AdapterView<?> parent, View view, LandingPageMenuItem menuItem, int position, long id) throws IllegalAccessException, InstantiationException {
        setActiveMenu(menuItem);

        ContactFormNavigationCapsule dataCapsule = new ContactFormNavigationCapsule(
                ContactReadActivity.this, contactUuid, pageStatus);

        if (menuItem.getKey() == MENU_INDEX_CONTACT_INFO) {
            activeFragment = ContactReadFragment.newInstance(dataCapsule);
            replaceFragment(activeFragment);
        } else if (menuItem.getKey() == MENU_INDEX_PERSON_INFO) {
            activeFragment = ContactReadPersonFragment.newInstance(dataCapsule);
            replaceFragment(activeFragment);
        } else if (menuItem.getKey() == MENU_INDEX_FOLLOWUP_VISIT) {
            activeFragment = ContactReadFollowUpVisitFragment.newInstance(dataCapsule);
            replaceFragment(activeFragment);
        } else if (menuItem.getKey() == MENU_INDEX_TASK) {
            activeFragment = ContactReadTaskListFragment.newInstance(dataCapsule);
            replaceFragment(activeFragment);
        }

        updateSubHeadingTitle();

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.read_action_menu, menu);

        MenuItem readMenu = menu.findItem(R.id.action_edit);
        readMenu.setVisible(false);
        readMenu.setTitle(R.string.action_edit_contact);


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
        return R.string.heading_level3_contact_read;
    }

    private void gotoEditView() {
        if (activeFragment == null)
            return;

        Contact record = (Contact)activeFragment.getRecord();

        ContactFormNavigationCapsule dataCapsule = new ContactFormNavigationCapsule(ContactReadActivity.this,
                record.getUuid(), pageStatus);
        ContactEditActivity.goToActivity(this, dataCapsule);
    }

    public static void goToActivity(Context fromActivity, ContactFormNavigationCapsule dataCapsule) {
        BaseReadActivity.goToActivity(fromActivity, ContactReadActivity.class, dataCapsule);
    }
}

