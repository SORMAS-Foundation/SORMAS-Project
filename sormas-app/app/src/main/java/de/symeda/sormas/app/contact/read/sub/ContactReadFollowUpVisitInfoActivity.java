package de.symeda.sormas.app.contact.read.sub;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.shared.ContactFormFollowUpNavigationCapsule;
import de.symeda.sormas.app.util.NavigationHelper;

/**
 * Created by Orson on 02/01/2018.
 */

public class ContactReadFollowUpVisitInfoActivity extends BaseReadActivity<Visit> {

    public static final String TAG = ContactReadFollowUpVisitInfoActivity.class.getSimpleName();

    private static final int MENU_INDEX_VISIT_INFO = 0;
    private static final int MENU_INDEX_SYMPTOMS_INFO = 1;

    private final String DATA_XML_PAGE_MENU = "xml/data_read_page_3_1_followup_menu.xml";

    private String recordUuid = null;
    private VisitStatus pageStatus = null;
    private FollowUpStatus filterStatus = null;
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
        filterStatus = (FollowUpStatus) getFilterStatusArg(arguments);
        pageStatus = (VisitStatus) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    protected Visit getActivityRootData(String recordUuid) {
        return DatabaseHelper.getVisitDao().queryUuid(recordUuid);
    }

    @Override
    protected Visit getActivityRootDataIfRecordUuidNull() {
        return null;
    }

    @Override
    public BaseReadActivityFragment getActiveReadFragment(Visit activityRootData) throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            ContactFormFollowUpNavigationCapsule dataCapsule = new ContactFormFollowUpNavigationCapsule(
                    ContactReadFollowUpVisitInfoActivity.this, recordUuid, pageStatus);
            activeFragment = ContactReadFollowUpVisitInfoFragment.newInstance(this, dataCapsule, activityRootData);
        }

        return activeFragment;
    }

    @Override
    public String getPageMenuData() {
        return DATA_XML_PAGE_MENU;
    }

    @Override
    protected BaseReadActivityFragment getNextFragment(LandingPageMenuItem menuItem, Visit activityRootData) {
        ContactFormFollowUpNavigationCapsule dataCapsule = new ContactFormFollowUpNavigationCapsule(
                ContactReadFollowUpVisitInfoActivity.this, recordUuid, pageStatus);

        try {
            if (menuItem.getKey() == MENU_INDEX_VISIT_INFO) {
                activeFragment = ContactReadFollowUpVisitInfoFragment.newInstance(this, dataCapsule, activityRootData);
            } else if (menuItem.getKey() == MENU_INDEX_SYMPTOMS_INFO) {
                activeFragment = ContactReadFollowUpSymptomsFragment.newInstance(this, dataCapsule, activityRootData);
            }
        } catch (InstantiationException e) {
            Log.e(TAG, e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.getMessage());
        }

        return activeFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getEditMenu().setTitle(R.string.action_edit_contact);

        return true;
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.read_action_menu, menu);

        MenuItem readMenu = menu.findItem(R.id.action_edit);
        readMenu.setVisible(false);
        readMenu.setTitle(R.string.action_edit_contact);

        return true;*/
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
        return R.string.heading_level3_1_contact_visit_info;
    }

    public static void goToActivity(Context fromActivity, ContactFormFollowUpNavigationCapsule dataCapsule) {
        BaseReadActivity.goToActivity(fromActivity, ContactReadFollowUpVisitInfoActivity.class, dataCapsule);
    }
}
