package de.symeda.sormas.app.contact.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.AbstractSormasActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.FragmentContactEditLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentContactEditPersonLayoutBinding;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.NavigationHelper;

/**
 * Created by Orson on 12/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class ContactEditActivity extends BaseEditActivity<Contact> {

    public static final String TAG = ContactEditActivity.class.getSimpleName();

    private final String DATA_XML_PAGE_MENU = "xml/data_edit_page_contact_menu.xml";

    private AsyncTask saveTask;
    private static final int MENU_INDEX_CONTACT_INFO = 0;
    private static final int MENU_INDEX_PERSON_INFO = 1;
    private static final int MENU_INDEX_FOLLOWUP_VISIT = 2;
    private static final int MENU_INDEX_TASK = 3;

    private boolean showStatusFrame;
    private boolean showTitleBar;
    private boolean showPageMenu;
    private MenuItem saveMenu = null;
    private MenuItem addMenu = null;

    private ContactClassification pageStatus = null;
    private String recordUuid = null;
    private BaseEditActivityFragment activeFragment = null; //

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

        this.showStatusFrame = true;
        this.showTitleBar = true;
        this.showPageMenu = true;
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
    public BaseEditActivityFragment getActiveEditFragment(Contact activityRootData) throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            ContactFormNavigationCapsule dataCapsule = new ContactFormNavigationCapsule(ContactEditActivity.this,
                    recordUuid, pageStatus);
            activeFragment = ContactEditFragment.newInstance(this, dataCapsule, activityRootData);
        }

        return activeFragment;
    }

    @Override
    public String getPageMenuData() {
        return DATA_XML_PAGE_MENU;
    }

    @Override
    protected BaseEditActivityFragment getNextFragment(LandingPageMenuItem menuItem, Contact activityRootData) {
        ContactFormNavigationCapsule dataCapsule = new ContactFormNavigationCapsule(ContactEditActivity.this,
                recordUuid, pageStatus);

        try {
            if (menuItem.getKey() == MENU_INDEX_CONTACT_INFO) {
                activeFragment = ContactEditFragment.newInstance(this, dataCapsule, activityRootData);
            } else if (menuItem.getKey() == MENU_INDEX_PERSON_INFO) {
                activeFragment = ContactEditPersonFragment.newInstance(this, dataCapsule, activityRootData);
            } else if (menuItem.getKey() == MENU_INDEX_FOLLOWUP_VISIT) {
                activeFragment = ContactEditFollowUpVisitListFragment.newInstance(this, dataCapsule, activityRootData);
            } else if (menuItem.getKey() == MENU_INDEX_TASK) {
                activeFragment = ContactEditTaskListFragment.newInstance(this, dataCapsule, activityRootData);
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
        getSaveMenu().setTitle(R.string.action_save_contact);

        return true;
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_action_menu, menu);

        saveMenu = menu.findItem(R.id.action_save);
        addMenu = menu.findItem(R.id.action_new);

        saveMenu.setTitle(R.string.action_save_contact);

        processActionbarMenu();

        return true;*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavigationHelper.navigateUpFrom(this);
                return true;

            case R.id.action_save:
                saveData();
                return true;

            case R.id.option_menu_action_sync:
                //synchronizeChangedData();
                return true;

            case R.id.option_menu_action_markAllAsRead:
                markAllAsRead();
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

    private void saveData() {
        if (activeFragment == null)
            return;

        int activeMenuKey = getActiveMenuItem().getKey();

        if (activeMenuKey == MENU_INDEX_FOLLOWUP_VISIT || activeMenuKey == MENU_INDEX_TASK)
            return;

        FragmentContactEditLayoutBinding contactEditBinding = null;
        FragmentContactEditPersonLayoutBinding contactEditPersonBinding = null;

        Contact contact = null;
        Person person = null;

        if (activeMenuKey == MENU_INDEX_CONTACT_INFO) {
            contact = (Contact)activeFragment.getPrimaryData();
            contactEditBinding =(FragmentContactEditLayoutBinding)activeFragment.getContentBinding();
        }

        if (activeMenuKey == MENU_INDEX_PERSON_INFO) {
            contact = (Contact)activeFragment.getPrimaryData();

            if (contact != null)
                person = contact.getPerson();

            contactEditPersonBinding = (FragmentContactEditPersonLayoutBinding)activeFragment.getContentBinding();
        }

        if (contact == null && person == null)
            return;


        //TODO: Validation
        /*ContactDataFragmentLayoutBinding contactDataBinding = ((ContactEditDataForm)getTabByPosition(adapter.getPositionOfTab(ContactEditTabs.CONTACT_DATA))).getBinding();
        PersonEditFragmentLayoutBinding personBinding = ((PersonEditForm)getTabByPosition(adapter.getPositionOfTab(ContactEditTabs.PERSON))).getBinding();

        PersonValidator.clearErrors(personBinding);

        int validationErrorTab = -1;

        if (!PersonValidator.validatePersonData(person, personBinding)) {
            validationErrorTab = adapter.getPositionOfTab(ContactEditTabs.PERSON);
        }

        if (validationErrorTab >= 0) {
            pager.setCurrentItem(validationErrorTab);
            return true;
        }*/

        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                private ContactDao cDao;
                private PersonDao pDao;
                private Contact c;
                private Person p;
                private String saveUnsuccessful;

                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    cDao = DatabaseHelper.getContactDao();
                    pDao = DatabaseHelper.getPersonDao();
                    saveUnsuccessful = String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_contact));

                    if (c.getRelationToCase() == ContactRelation.SAME_HOUSEHOLD && p.getAddress().isEmptyLocation()) {
                        p.getAddress().setRegion(c.getCaze().getRegion());
                        p.getAddress().setDistrict(c.getCaze().getDistrict());
                        p.getAddress().setCommunity(c.getCaze().getCommunity());
                    }
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    try {
                        if (this.p != null)
                            pDao.saveAndSnapshot(this.p);

                        if (this.c != null)
                            cDao.saveAndSnapshot(this.c);
                    } catch (DaoException e) {
                        Log.e(getClass().getName(), "Error while trying to save contact", e);
                        resultHolder.setResultStatus(new BoolResult(false, saveUnsuccessful));
                        ErrorReportingHelper.sendCaughtException(tracker, e, this.c, true);
                    }
                }

                private IJobDefinition init(Contact c, Person p) {
                    this.c = c;
                    this.p = p;

                    return this;
                }

            }.init(contact, person));
            saveTask = executor.execute(new ITaskResultCallback() {
                private Contact c;

                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().hidePreloader();
                    //getActivityCommunicator().showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    if (!resultStatus.isSuccess()) {
                        NotificationHelper.showNotification(ContactEditActivity.this, NotificationType.ERROR, resultStatus.getMessage());
                        return;
                    } else {
                        NotificationHelper.showNotification(ContactEditActivity.this, NotificationType.SUCCESS, "Contact " + DataHelper.getShortUuid(this.c.getUuid()) + " saved");
                    }

                    if (!goToNextMenu())
                        NotificationHelper.showNotification(ContactEditActivity.this, NotificationType.INFO, R.string.notification_reach_last_menu);


                    //move to next page

                    /*if (RetroProvider.isConnected()) {
                        SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.ChangesOnly, ContactEditActivity.this, new SyncCallback() {
                            @Override
                            public void call(boolean syncFailed, String syncFailedMessage) {
                                if (syncFailed) {
                                    NotificationHelper.showNotification(ContactEditActivity.this, NotificationType.WARNING, String.format(getResources().getString(R.string.snackbar_sync_error_saved), getResources().getString(R.string.entity_contact)));
                                } else {
                                    NotificationHelper.showNotification(ContactEditActivity.this, NotificationType.SUCCESS, String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_contact)));
                                }
                                finish();
                            }
                        });
                    } else {
                        NotificationHelper.showNotification(ContactEditActivity.this, NotificationType.SUCCESS, String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_contact)));
                        finish();
                    }*/

                }

                private ITaskResultCallback init(Contact c) {
                    this.c = c;

                    return this;
                }

            }.init(contact));
        } catch (Exception ex) {
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }


    }

    private void markAllAsRead() {
        /*switch (tab) {
            case VISITS:
                VisitDao visitDao = DatabaseHelper.getVisitDao();
                List<Visit> visits = visitDao.getByContact(contact);
                for (Visit visitToMark : visits) {
                    visitDao.markAsRead(visitToMark);
                }

                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment instanceof VisitsListFragment) {
                        fragment.onResume();
                    }
                }
                break;
            case TASKS:
                TaskDao taskDao = DatabaseHelper.getTaskDao();
                List<Task> tasks = taskDao.queryByContact(contact);
                for (Task taskToMark : tasks) {
                    taskDao.markAsRead(taskToMark);
                }

                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment instanceof TasksListFragment) {
                        fragment.onResume();
                    }
                }
                break;
        }*/
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level4_contact_edit;
    }

    private void processActionbarMenu() {
        if (activeFragment == null)
            return;

        if (saveMenu != null)
            saveMenu.setVisible(activeFragment.showSaveAction());

        if (addMenu != null)
            addMenu.setVisible(activeFragment.showAddAction());

        /*int activeMenuKey = getActiveMenuItem().getKey();

        if (activeMenuKey == MENU_INDEX_CONTACT_INFO) {



        } else if (activeMenuKey == MENU_INDEX_CONTACT_INFO) {

        } else if (activeMenuKey == MENU_INDEX_FOLLOWUP_VISIT) {

        } else if (activeMenuKey == MENU_INDEX_TASK) {

        }*/
    }

    public static <TActivity extends AbstractSormasActivity> void
    goToActivity(Context fromActivity, ContactFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, ContactEditActivity.class, dataCapsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }

}