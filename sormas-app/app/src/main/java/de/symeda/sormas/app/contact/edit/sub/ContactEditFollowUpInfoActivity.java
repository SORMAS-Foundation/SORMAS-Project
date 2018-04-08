package de.symeda.sormas.app.contact.edit.sub;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.backend.visit.VisitDao;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.FragmentContactEditSymptomsInfoLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentContactEditVisitInfoLayoutBinding;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.shared.ContactFormFollowUpNavigationCapsule;
import de.symeda.sormas.app.symptom.Symptom;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.NavigationHelper;
import de.symeda.sormas.app.util.SyncCallback;

/**
 * Created by Orson on 13/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class ContactEditFollowUpInfoActivity extends BaseEditActivity<Visit> {

    public static final String TAG = ContactEditFollowUpInfoActivity.class.getSimpleName();

    private final int DATA_XML_PAGE_MENU = R.xml.data_form_page_followup_menu;// "xml/data_edit_page_3_1_followup_menu.xml";

    private static final int MENU_INDEX_VISIT_INFO = 0;
    private static final int MENU_INDEX_SYMPTOMS_INFO = 1;

    private AsyncTask saveTask;
    private boolean showStatusFrame;
    private boolean showTitleBar;
    private boolean showPageMenu;

    private String recordUuid = null;
    private String contactUuid = null;
    private VisitStatus pageStatus = null;
    private BaseEditActivityFragment activeFragment = null;

    private MenuItem saveMenu = null;
    private MenuItem addMenu = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //SaveFilterStatusState(outState, followUpStatus);
        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
        SaveContactUuidState(outState, contactUuid);
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
        contactUuid = getContactUuidArg(arguments);

        this.showStatusFrame = true;
        this.showTitleBar = true;
        this.showPageMenu = true;
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
    public BaseEditActivityFragment getActiveEditFragment(Visit activityRootData) throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            ContactFormFollowUpNavigationCapsule dataCapsule = (ContactFormFollowUpNavigationCapsule)new ContactFormFollowUpNavigationCapsule(
                    ContactEditFollowUpInfoActivity.this, recordUuid, pageStatus)
                    .setContactUuid(contactUuid);
            activeFragment = ContactEditFollowUpVisitInfoFragment.newInstance(this, dataCapsule, activityRootData);
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
    public int getPageMenuData() {
        return DATA_XML_PAGE_MENU;
    }

    @Override
    protected BaseEditActivityFragment getNextFragment(LandingPageMenuItem menuItem, Visit activityRootData) {
        ContactFormFollowUpNavigationCapsule dataCapsule = new ContactFormFollowUpNavigationCapsule(
                ContactEditFollowUpInfoActivity.this, recordUuid, pageStatus);

        try {
            if (menuItem.getKey() == MENU_INDEX_VISIT_INFO) {
                activeFragment = ContactEditFollowUpVisitInfoFragment.newInstance(this, dataCapsule, activityRootData);
            } else if (menuItem.getKey() == MENU_INDEX_SYMPTOMS_INFO) {
                activeFragment = ContactEditFollowUpSymptomsFragment.newInstance(this, dataCapsule, activityRootData);
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
        getSaveMenu().setTitle(R.string.action_save_followup);

        return true;
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_action_menu, menu);

        saveMenu = menu.findItem(R.id.action_save);
        addMenu = menu.findItem(R.id.action_new);

        saveMenu.setTitle(R.string.action_save_followup);

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

    private void saveData() {
        if (activeFragment == null)
            return;

        int activeMenuKey = getActiveMenuItem().getKey();

        FragmentContactEditVisitInfoLayoutBinding visitEditBinding = null;
        FragmentContactEditSymptomsInfoLayoutBinding visitEditSymptomBinding = null;

        Visit visit = null;
        Symptoms symptoms = null;

        if (activeMenuKey == MENU_INDEX_VISIT_INFO) {
            visit = (Visit)activeFragment.getPrimaryData();
            visitEditBinding =(FragmentContactEditVisitInfoLayoutBinding)activeFragment.getContentBinding();
        }

        if (activeMenuKey == MENU_INDEX_SYMPTOMS_INFO) {
            visit = (Visit)activeFragment.getPrimaryData();
            visitEditSymptomBinding = (FragmentContactEditSymptomsInfoLayoutBinding)activeFragment.getContentBinding();
        }

        if (visit == null)
            return;

        // Necessary because the entry could've been automatically set, in which case the setValue method of the
        // custom field has not been called
        if (visit.getSymptoms() != null && visitEditSymptomBinding != null) {
            Symptom s = (Symptom) visit.getSymptoms().getFirstSymptom();

            if (s != null)
                visit.getSymptoms().setOnsetSymptom(s.getName());
        }


        //TODO: Validation
        /*VisitValidator.clearErrorsForVisitData(visitDataBinding);
        SymptomsValidator.clearErrorsForSymptoms(symptomsBinding);

        int validationErrorTab = -1;

        if (!SymptomsValidator.validateVisitSymptoms(visit, symptoms, symptomsBinding)) {
            validationErrorTab = VisitEditTabs.SYMPTOMS.ordinal();
        }
        if (!VisitValidator.validateVisitData(visit, contact, visitDataBinding)) {
            validationErrorTab = VisitEditTabs.VISIT_DATA.ordinal();
        }

        if (validationErrorTab >= 0) {
            pager.setCurrentItem(validationErrorTab);
            return true;
        }*/

        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                private Visit v;
                private String saveUnsuccessful;

                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    saveUnsuccessful = String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_visit));
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    try {
                        /*if (this.s != null)
                            v.setSymptoms(this.s);*/

                        if (this.v != null) {
                            v.setVisitUser(ConfigProvider.getUser());
                            VisitDao visitDao = DatabaseHelper.getVisitDao();
                            visitDao.saveAndSnapshot(this.v);
                        }
                    } catch (DaoException e) {
                        Log.e(getClass().getName(), "Error while trying to save visit", e);
                        resultHolder.setResultStatus(new BoolResult(false, saveUnsuccessful));
                        ErrorReportingHelper.sendCaughtException(tracker, e, this.v, true);
                    }
                }

                private IJobDefinition init(Visit v) {
                    this.v = v;

                    return this;
                }

            }.init(visit));
            saveTask = executor.execute(new ITaskResultCallback() {
                private Visit v;

                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().hidePreloader();
                    //getActivityCommunicator().showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    if (!resultStatus.isSuccess()) {
                        NotificationHelper.showNotification(ContactEditFollowUpInfoActivity.this, NotificationType.ERROR, resultStatus.getMessage());
                        return;
                    } else {
                        NotificationHelper.showNotification(ContactEditFollowUpInfoActivity.this, NotificationType.SUCCESS, "Visit " + DataHelper.getShortUuid(this.v.getUuid()) + " saved");
                    }

                    if (RetroProvider.isConnected()) {
                        SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.ChangesOnly, ContactEditFollowUpInfoActivity.this, new SyncCallback() {
                            @Override
                            public void call(boolean syncFailed, String syncFailedMessage) {
                                if (syncFailed) {
                                    NotificationHelper.showNotification(ContactEditFollowUpInfoActivity.this, NotificationType.WARNING, String.format(getResources().getString(R.string.snackbar_sync_error_saved), getResources().getString(R.string.entity_contact)));
                                } else {
                                    NotificationHelper.showNotification(ContactEditFollowUpInfoActivity.this, NotificationType.SUCCESS, String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_contact)));
                                }
                                //finish();

                                if (!goToNextMenu())
                                    NotificationHelper.showNotification(ContactEditFollowUpInfoActivity.this, NotificationType.INFO, R.string.notification_reach_last_menu);
                            }
                        });
                    } else {
                        NotificationHelper.showNotification(ContactEditFollowUpInfoActivity.this, NotificationType.WARNING, String.format(getResources().getString(R.string.snackbar_save_success_couldnot_sync), getResources().getString(R.string.entity_contact)));
                        //finish();
                    }

                }

                private ITaskResultCallback init(Visit v) {
                    this.v = v;

                    return this;
                }

            }.init(visit));
        } catch (Exception ex) {
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }


    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level3_1_contact_visit_info;
    }

    private void processActionbarMenu() {
        if (activeFragment == null)
            return;

        if (saveMenu != null)
            saveMenu.setVisible(activeFragment.showSaveAction());

        if (addMenu != null)
            addMenu.setVisible(activeFragment.showAddAction());
    }

    public static void goToActivity(Context fromActivity, ContactFormFollowUpNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, ContactEditFollowUpInfoActivity.class, dataCapsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }
}