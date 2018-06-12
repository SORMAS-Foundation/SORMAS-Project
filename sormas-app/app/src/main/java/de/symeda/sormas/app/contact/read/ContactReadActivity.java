package de.symeda.sormas.app.contact.read;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.contact.edit.ContactEditActivity;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;
import de.symeda.sormas.app.util.MenuOptionsHelper;

/**
 * Created by Orson on 01/01/2018.
 */

public class ContactReadActivity extends BaseReadActivity<Contact> {

    public static final String TAG = ContactReadActivity.class.getSimpleName();

    private static final int MENU_INDEX_CONTACT_INFO = 0;
    private static final int MENU_INDEX_PERSON_INFO = 1;
    private static final int MENU_INDEX_FOLLOWUP_VISIT = 2;
    private static final int MENU_INDEX_TASK = 3;

    private final int DATA_XML_PAGE_MENU = R.xml.data_form_page_contact_menu; // "xml/data_read_page_contact_menu.xml";

    private AsyncTask jobTask;
    private String recordUuid = null;
    private ContactClassification pageStatus = null;
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
        pageStatus = (ContactClassification) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    protected Contact getActivityRootData(String recordUuid) {
        Contact _contact = DatabaseHelper.getContactDao().queryUuid(recordUuid);
        return _contact;
    }

    @Override
    protected Contact getActivityRootDataIfRecordUuidNull() {
        Person _person = DatabaseHelper.getPersonDao().build();
        Contact _contact = DatabaseHelper.getContactDao().build();

        _contact.setPerson(_person);
        _contact.setReportDateTime(new Date());
        _contact.setContactClassification(ContactClassification.UNCONFIRMED);
        _contact.setContactStatus(ContactStatus.ACTIVE);
        _contact.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
        _contact.setReportingUser(ConfigProvider.getUser());

        return _contact;
    }

    @Override
    public BaseReadActivityFragment getActiveReadFragment(Contact activityRootData) {
        if (activeFragment == null) {
            ContactFormNavigationCapsule dataCapsule = new ContactFormNavigationCapsule(ContactReadActivity.this, recordUuid, pageStatus);
            activeFragment = ContactReadFragment.newInstance(this, dataCapsule, activityRootData);
        }

        return activeFragment;
    }

    @Override
    public int getPageMenuData() {
        return DATA_XML_PAGE_MENU;
    }

    @Override
    protected BaseReadActivityFragment getNextFragment(LandingPageMenuItem menuItem, Contact activityRootData) {
        ContactFormNavigationCapsule dataCapsule = new ContactFormNavigationCapsule(
                ContactReadActivity.this, recordUuid, pageStatus);

        try {
            if (menuItem.getKey() == MENU_INDEX_CONTACT_INFO) {
                activeFragment = ContactReadFragment.newInstance(this, dataCapsule, activityRootData);
            } else if (menuItem.getKey() == MENU_INDEX_PERSON_INFO) {
                activeFragment = ContactReadPersonFragment.newInstance(this, dataCapsule, activityRootData);
            } else if (menuItem.getKey() == MENU_INDEX_FOLLOWUP_VISIT) {
                activeFragment = ContactReadFollowUpVisitListFragment.newInstance(this, dataCapsule, activityRootData);
            } else if (menuItem.getKey() == MENU_INDEX_TASK) {
                activeFragment = ContactReadTaskListFragment.newInstance(this, dataCapsule, activityRootData);
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
        //readMenu.setVisible(false);
        readMenu.setTitle(R.string.action_edit_contact);


        return true;*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!MenuOptionsHelper.handleReadModuleOptionsItemSelected(this, item))
            return super.onOptionsItemSelected(item);

        return true;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level3_contact_read;
    }

    @Override
    public void gotoEditView() {
        if (activeFragment == null)
            return;

        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //showPreloader();
                    //hideFragmentView();
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    Contact record = DatabaseHelper.getContactDao().queryUuid(recordUuid);

                    if (record == null) {
                        // build a new event for empty uuid
                        resultHolder.forItem().add(DatabaseHelper.getEventDao().build());
                    } else {
                        resultHolder.forItem().add(record);
                    }
                }
            });
            jobTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //hidePreloader();
                    //showFragmentView();

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
            //hidePreloader();
            //showFragmentView();
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

