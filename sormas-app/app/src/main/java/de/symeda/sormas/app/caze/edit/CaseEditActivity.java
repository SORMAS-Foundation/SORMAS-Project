package de.symeda.sormas.app.caze.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.PlagueType;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.AbstractSormasActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.symptoms.SymptomsDtoHelper;
import de.symeda.sormas.app.component.dialog.ConfirmationDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.contact.edit.ContactNewActivity;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.FragmentCaseEditPatientLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentCaseEditSymptomsInfoLayoutBinding;
import de.symeda.sormas.app.sample.edit.SampleNewActivity;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;
import de.symeda.sormas.app.shared.SampleFormNavigationCapsule;
import de.symeda.sormas.app.shared.ShipmentStatus;
import de.symeda.sormas.app.symptom.Symptom;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.MenuOptionsHelper;
import de.symeda.sormas.app.validation.NewSymptomValidator;
import de.symeda.sormas.app.validation.PersonValidator;

/**
 * Created by Orson on 16/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class CaseEditActivity extends BaseEditActivity<Case> {

    public static final String TAG = CaseEditActivity.class.getSimpleName();

    private final int DATA_XML_PAGE_MENU = R.xml.data_form_page_case_menu;// "xml/data_edit_page_case_menu.xml";

    private AsyncTask saveTask;
    private AsyncTask moveCaseTask;
    private AsyncTask finalizeSaveTask;
    private AsyncTask caseBeforeSaveAndPlagueTypeAlertTask;
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
        pageStatus = (InvestigationStatus) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);

        this.showStatusFrame = true;
        this.showTitleBar = true;
        this.showPageMenu = true;
    }

    @Override
    protected Case getActivityRootData(String recordUuid) {
        return DatabaseHelper.getCaseDao().queryUuid(recordUuid);
    }

    @Override
    protected Case getActivityRootDataIfRecordUuidNull() {
        return DatabaseHelper.getCaseDao().build(DatabaseHelper.getPersonDao().build());
    }

    @Override
    public BaseEditActivityFragment getActiveEditFragment(Case activityRootData) throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(CaseEditActivity.this,
                    recordUuid).setEditPageStatus(pageStatus);
            activeFragment = CaseEditFragment.newInstance(this, dataCapsule, activityRootData);
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
    protected BaseEditActivityFragment getNextFragment(LandingPageMenuItem menuItem, Case activityRootData) {
        CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(CaseEditActivity.this,
                recordUuid).setEditPageStatus(pageStatus);

        try {
            if (menuItem.getKey() == MENU_INDEX_CASE_INFO) {
                activeFragment = CaseEditFragment.newInstance(this, dataCapsule, activityRootData);
            } else if (menuItem.getKey() == MENU_INDEX_PATIENT_INFO) {
                activeFragment = CaseEditPatientInfoFragment.newInstance(this, dataCapsule, activityRootData);
            } else if (menuItem.getKey() == MENU_INDEX_HOSPITALIZATION) {
                activeFragment = CaseEditHospitalizationFragment.newInstance(this, dataCapsule, activityRootData);
            } else if (menuItem.getKey() == MENU_INDEX_SYMPTOMS) {
                activeFragment = CaseEditSymptomsFragment.newInstance(this, dataCapsule, activityRootData);
            } else if (menuItem.getKey() == MENU_INDEX_EPIDEMIOLOGICAL_DATA) {
                activeFragment = CaseEditEpidemiologicalDataFragment.newInstance(this, dataCapsule, activityRootData);
            } else if (menuItem.getKey() == MENU_INDEX_CONTACTS) {
                activeFragment = CaseEditContactListFragment.newInstance(this, dataCapsule, activityRootData);
            }else if (menuItem.getKey() == MENU_INDEX_SAMPLES) {
                activeFragment = CaseEditSampleListFragment.newInstance(this, dataCapsule, activityRootData);
            } else if (menuItem.getKey() == MENU_INDEX_TASKS) {
                activeFragment = CaseEditTaskListFragment.newInstance(this, dataCapsule, activityRootData);
            }

            //processActionbarMenu();
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
        getSaveMenu().setTitle(R.string.action_save_case);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!MenuOptionsHelper.handleEditModuleOptionsItemSelected(this, item))
            return super.onOptionsItemSelected(item);

        return true;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level4_case_edit;
    }

    private boolean updatePlagueType(Case caze) {
        Symptoms symptoms = DatabaseHelper.getSymptomsDao().queryUuid(caze.getSymptoms().getUuid());
        if (symptoms != null) {
            SymptomsDto symptomsDto = new SymptomsDto();
            new SymptomsDtoHelper().fillInnerFromAdo(symptomsDto, symptoms);

            final PlagueType newPlagueType = DiseaseHelper.getPlagueTypeForSymptoms(symptomsDto);
            if (newPlagueType != null && newPlagueType != caze.getPlagueType()) {
                caze.setPlagueType(newPlagueType);
                return true;
            }
        }

        return false;
    }

    private void getCaseBeforeSaveAndPlagueTypeAlert(final Case cazeToSave, final Callback.IAction3<BoolResult, Case, Boolean> callback) {
        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //showPreloader();
                    //hideFragmentView();
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    final Case caseBeforeSaving = DatabaseHelper.getCaseDao().queryUuid(cazeToSave.getUuid());
                    boolean showPlagueTypeChangeAlert = false;
                    if (cazeToSave.getDisease() == Disease.PLAGUE) {
                        showPlagueTypeChangeAlert = updatePlagueType(cazeToSave);
                    }

                    resultHolder.forItem().add(caseBeforeSaving);
                    resultHolder.forOther().add(showPlagueTypeChangeAlert);
                }
            });
            caseBeforeSaveAndPlagueTypeAlertTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //hidePreloader();
                    //showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    Case caseBeforeSaving = null;
                    boolean showPlagueTypeChangeAlert = false;
                    ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
                    ITaskResultHolderIterator otherIterator = resultHolder.forOther().iterator();

                    if (itemIterator.hasNext())
                        caseBeforeSaving = itemIterator.next();

                    if (otherIterator.hasNext())
                        showPlagueTypeChangeAlert = otherIterator.next();

                    callback.call(resultStatus, caseBeforeSaving, showPlagueTypeChangeAlert);
                }
            });
        } catch (Exception ex) {
            //hidePreloader();
            //showFragmentView();
        }
    }

    private void finalizeSaveProcess(final Case caze, final Case caseBeforeSaving, final Callback.IAction<BoolResult> callback) {
        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //showPreloader();
                    //hideFragmentView();
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    Case savedCase = DatabaseHelper.getCaseDao().queryUuid(caze.getUuid());
                    resultHolder.forItem().add(savedCase);
                }
            });
            finalizeSaveTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //hidePreloader();
                    //showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    Case savedCase = null;
                    ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

                    if (itemIterator.hasNext())
                        savedCase = itemIterator.next();

                    /*if (savedCase.getDisease() == Disease.PLAGUE && caseBeforeSaving.getPlagueType() != savedCase.getPlagueType() &&
                            (caseBeforeSaving.getPlagueType() == PlagueType.PNEUMONIC || savedCase.getPlagueType() == PlagueType.PNEUMONIC)) {
                        setAdapter(savedCase);
                    }*/

                    callback.call(resultStatus);
                }
            });
        } catch (Exception ex) {
            //hidePreloader();
            //showFragmentView();
        }
    }

    private void finalizeSaveProcessHelper(BoolResult result) {
        if (!result.isSuccess()) {
            NotificationHelper.showNotification(CaseEditActivity.this, NotificationType.ERROR, result.getMessage());
            return;
        } else {
            NotificationHelper.showNotification(CaseEditActivity.this,
                    NotificationType.SUCCESS,
                    String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_case)));
        }

        if (!goToNextMenu())
            NotificationHelper.showNotification(CaseEditActivity.this, NotificationType.INFO, R.string.notification_reach_last_menu);
    }

    @Override
    public void saveData() {
        if (activeFragment == null)
            return;

        int activeMenuKey = getActiveMenuItem().getKey();

        if (activeMenuKey == MENU_INDEX_CONTACTS || activeMenuKey == MENU_INDEX_SAMPLES || activeMenuKey == MENU_INDEX_TASKS)
            return;

        final Case cazeToSave = getStoredActivityRootData();

        if (cazeToSave == null)
            return;

        saveCaseToDatabase(new Callback.IAction<BoolResult>() {
            @Override
            public void call(BoolResult result) {
                if (!result.isSuccess())
                    return;

                getCaseBeforeSaveAndPlagueTypeAlert(cazeToSave, new Callback.IAction3<BoolResult, Case, Boolean>() {

                    @Override
                    public void call(BoolResult result1, final Case caseBeforeSaving, Boolean showPlagueTypeChangeAlert) {
                        if (cazeToSave.getDisease() == Disease.PLAGUE && showPlagueTypeChangeAlert) {

                            String plagueTypeString = cazeToSave.getPlagueType().toString();
                            String confirmationMessage = String.format(getResources().getString(R.string.alert_plague_type_change), plagueTypeString, plagueTypeString);
                            final ConfirmationDialog confirmationDialog = new ConfirmationDialog(getActiveActivity(), R.string.alert_title_plague_type_change,
                                    confirmationMessage);

                            confirmationDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                                @Override
                                public void onOkClick(View v, Object item, View viewRoot) {
                                    confirmationDialog.dismiss();
                                    finalizeSaveProcess(cazeToSave, caseBeforeSaving, new Callback.IAction<BoolResult>() {
                                        @Override
                                        public void call(BoolResult result) {
                                            finalizeSaveProcessHelper(result);
                                        }
                                    });
                                }
                            });

                            confirmationDialog.show(null);
                        } else {
                            finalizeSaveProcess(cazeToSave, caseBeforeSaving, new Callback.IAction<BoolResult>() {
                                @Override
                                public void call(BoolResult result) {
                                    finalizeSaveProcessHelper(result);
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    @Override
    public void gotoNewView() {
        int activeMenuKey = getActiveMenuItem().getKey();

        if (activeMenuKey == MENU_INDEX_CONTACTS) {
            ContactFormNavigationCapsule dataCapsule = new ContactFormNavigationCapsule(getContext(),
                    ContactClassification.UNCONFIRMED).setCaseUuid(recordUuid);
            ContactNewActivity.goToActivity(this, dataCapsule);
        } else if (activeMenuKey == MENU_INDEX_SAMPLES) {
            SampleFormNavigationCapsule dataCapsule = new SampleFormNavigationCapsule(getContext(),
                    ShipmentStatus.NOT_SHIPPED).setCaseUuid(recordUuid);
            SampleNewActivity.goToActivity(this, dataCapsule);
        }
    }

    public void saveCaseToDatabase(final Callback.IAction<BoolResult> callback) {
        final String saveUnsuccessful = String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_case));

        if (activeFragment == null) {
            callback.call(new BoolResult(false, saveUnsuccessful));
            return;
        }

        int activeMenuKey = getActiveMenuItem().getKey();

        if (activeMenuKey == MENU_INDEX_CONTACTS || activeMenuKey == MENU_INDEX_SAMPLES || activeMenuKey == MENU_INDEX_TASKS) {
            callback.call(new BoolResult(false, saveUnsuccessful));
            return;
        }

        final Case cazeToSave = getStoredActivityRootData();

        if (cazeToSave == null) {
            callback.call(new BoolResult(false, saveUnsuccessful));
            return;
        }

        if (activeMenuKey == MENU_INDEX_CASE_INFO) {
            //caze = (Case)activeFragment.getPrimaryData();
            //caseEditBinding =(FragmentCaseEditLayoutBinding)activeFragment.getContentBinding();
        }

        if (activeMenuKey == MENU_INDEX_PATIENT_INFO) {
            FragmentCaseEditPatientLayoutBinding personBinding = (FragmentCaseEditPatientLayoutBinding)activeFragment.getContentBinding();
            PersonValidator.clearErrors(personBinding);
            if (!PersonValidator.validatePersonData(this, cazeToSave.getPerson(), personBinding)) {
                return;
            }
        }

        if (activeMenuKey == MENU_INDEX_SYMPTOMS) {
            Symptoms symptoms = cazeToSave.getSymptoms();
            //symptoms = (Symptoms)activeFragment.getPrimaryData();

            CaseEditSymptomsFragment symptomsFragment = (CaseEditSymptomsFragment)activeFragment;

            if (symptomsFragment == null)
                return;

            FragmentCaseEditSymptomsInfoLayoutBinding symptomsBinding = symptomsFragment.getContentBinding();

            if (symptoms != null) {
                // Necessary because the entry could've been automatically set, in which case the setValue method of the
                // custom field has not been called
                Symptom s = (Symptom) symptoms.getFirstSymptom();

                if (s != null)
                    symptoms.setOnsetSymptom(s.getName());

                NewSymptomValidator.init(symptomsFragment.getSymptomList());

                /*SymptomsValidator.clearErrorsForSymptoms(symptomsBinding);
                if (!SymptomsValidator.validateCaseSymptoms(symptoms, symptomsBinding)) {
                    return;
                }*/
            }
        }


        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                private CaseDao caseDao = DatabaseHelper.getCaseDao();
                private PersonDao personDao = DatabaseHelper.getPersonDao();

                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    caseDao = DatabaseHelper.getCaseDao();
                    personDao = DatabaseHelper.getPersonDao();
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    try {
                        if (cazeToSave.getPerson() != null)
                            personDao.saveAndSnapshot(cazeToSave.getPerson());

                        if (cazeToSave != null)
                            caseDao.saveAndSnapshot(cazeToSave);
                    } catch (DaoException e) {
                        Log.e(getClass().getName(), "Error while trying to save case", e);
                        Log.e(getClass().getName(), "- root cause: ", ErrorReportingHelper.getRootCause(e));
                        resultHolder.setResultStatus(new BoolResult(false, saveUnsuccessful));
                        ErrorReportingHelper.sendCaughtException(tracker, e, cazeToSave, true);
                    }
                }
            });
            saveTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().hidePreloader();
                    //getActivityCommunicator().showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    if (!resultStatus.isSuccess()) {
                        NotificationHelper.showNotification(CaseEditActivity.this, NotificationType.ERROR, resultStatus.getMessage());
                        //return;
                    } else {
                        NotificationHelper.showNotification(CaseEditActivity.this, NotificationType.SUCCESS, "Case " + DataHelper.getShortUuid(cazeToSave.getUuid()) + " saved");
                    }

                    if (callback != null)
                        callback.call(resultStatus);
                    /*if (!goToNextMenu())
                        NotificationHelper.showNotification(CaseEditActivity.this, NotificationType.INFO, R.string.notification_reach_last_menu);*/

                }
            });
        } catch (Exception ex) {
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }
    }

    public static <TActivity extends AbstractSormasActivity> void
    goToActivity(Context fromActivity, CaseFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, CaseEditActivity.class, dataCapsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (moveCaseTask != null && !moveCaseTask.isCancelled())
            moveCaseTask.cancel(true);

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);

        if (caseBeforeSaveAndPlagueTypeAlertTask != null && !caseBeforeSaveAndPlagueTypeAlertTask.isCancelled())
            caseBeforeSaveAndPlagueTypeAlertTask.cancel(true);

        if (finalizeSaveTask != null && !finalizeSaveTask.isCancelled())
            finalizeSaveTask.cancel(true);

    }

}