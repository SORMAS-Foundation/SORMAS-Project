package de.symeda.sormas.app.contact.read;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.contact.ContactFormNavigationCapsule;
import de.symeda.sormas.app.contact.edit.ContactEditActivity;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.util.NavigationHelper;

/**
 * Created by Orson on 01/01/2018.
 */

public class ContactReadActivity extends BaseReadActivity {

    private static final int MENU_INDEX_CONTACT_INFO = 0;
    private static final int MENU_INDEX_PERSON_INFO = 1;
    private static final int MENU_INDEX_FOLLOWUP_VISIT = 2;
    private static final int MENU_INDEX_TASK = 3;

    private final String DATA_XML_PAGE_MENU = "xml/data_read_page_contact_menu.xml";

    private AsyncTask jobTask;
    private String recordUuid = null;
    //private FollowUpStatus filterStatus = null;
    private ContactClassification pageStatus = null;
    private BaseReadActivityFragment activeFragment = null;

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
        //filterStatus = (FollowUpStatus) getFilterStatusArg(arguments);
        pageStatus = (ContactClassification) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }


    @Override
    public BaseReadActivityFragment getActiveReadFragment() throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            ContactFormNavigationCapsule dataCapsule = new ContactFormNavigationCapsule(ContactReadActivity.this, recordUuid, pageStatus);
            activeFragment = ContactReadFragment.newInstance(this, dataCapsule);
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
                ContactReadActivity.this, recordUuid, pageStatus);

        if (menuItem.getKey() == MENU_INDEX_CONTACT_INFO) {
            activeFragment = ContactReadFragment.newInstance(this, dataCapsule);
            replaceFragment(activeFragment);
        } else if (menuItem.getKey() == MENU_INDEX_PERSON_INFO) {
            activeFragment = ContactReadPersonFragment.newInstance(this, dataCapsule);
            replaceFragment(activeFragment);
        } else if (menuItem.getKey() == MENU_INDEX_FOLLOWUP_VISIT) {
            activeFragment = ContactReadFollowUpVisitListFragment.newInstance(this, dataCapsule);
            replaceFragment(activeFragment);
        } else if (menuItem.getKey() == MENU_INDEX_TASK) {
            activeFragment = ContactReadTaskListFragment.newInstance(this, dataCapsule);
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
        //readMenu.setVisible(false);
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

        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                @Override
                public void preExecute() {
                    showPreloader();
                    hideFragmentView();
                }

                @Override
                public void execute(TaskResultHolder resultHolder) {
                    Contact record = DatabaseHelper.getContactDao().queryUuid(recordUuid);

                    if (record == null) {
                        // build a new event for empty uuid
                        resultHolder.forItem().add(DatabaseHelper.getEventDao().build());
                    } else {
                        resultHolder.forItem().add(record);
                    }
                }
            });
            jobTask = executor.search(new ITaskResultCallback() {
                @Override
                public void searchResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    hidePreloader();
                    showFragmentView();

                    if (resultHolder == null)
                        return;

                    ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
                    if (itemIterator.hasNext()) {
                        Contact record = itemIterator.next();

                        ContactFormNavigationCapsule dataCapsule = new ContactFormNavigationCapsule(ContactReadActivity.this,
                                record.getUuid(), pageStatus);
                        ContactEditActivity.goToActivity(ContactReadActivity.this, dataCapsule);
                    }
                }
            });
        } catch (Exception ex) {
            hidePreloader();
            showFragmentView();
        }
    }

    public static void goToActivity(Context fromActivity, ContactFormNavigationCapsule dataCapsule) {
        BaseReadActivity.goToActivity(fromActivity, ContactReadActivity.class, dataCapsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (jobTask != null && !jobTask.isCancelled())
            jobTask.cancel(true);
    }
}

