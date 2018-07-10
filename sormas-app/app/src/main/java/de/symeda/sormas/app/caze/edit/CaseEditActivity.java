package de.symeda.sormas.app.caze.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.symptoms.SymptomsDtoHelper;
import de.symeda.sormas.app.caze.CaseSection;
import de.symeda.sormas.app.component.dialog.ConfirmationDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.contact.edit.ContactNewActivity;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
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
import de.symeda.sormas.app.util.MenuOptionsHelper;
import de.symeda.sormas.app.validation.NewSymptomValidator;
import de.symeda.sormas.app.validation.PersonValidator;

public class CaseEditActivity extends BaseEditActivity<Case> {

    public static final String TAG = CaseEditActivity.class.getSimpleName();

    private AsyncTask saveTask;
    private AsyncTask caseBeforeSaveAndPlagueTypeAlertTask;

    private BaseEditFragment fragment;

    @Override
    protected Case queryRootEntity(String recordUuid) {
        return DatabaseHelper.getCaseDao().queryUuidWithEmbedded(recordUuid);
    }

    @Override
    protected Case buildRootEntity() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CaseClassification getPageStatus() {
        return (CaseClassification)super.getPageStatus();
    }

    @Override
    public int getPageMenuData() {
        return R.xml.data_form_page_case_menu;
    }

    @Override
    protected BaseEditFragment buildEditFragment(LandingPageMenuItem menuItem, Case activityRootData) {
        CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(this, getRootEntityUuid(), getPageStatus());

        CaseSection section = CaseSection.fromMenuKey(menuItem.getKey());
        switch (section) {

            case CASE_INFO:
                fragment = CaseEditFragment.newInstance(dataCapsule, activityRootData);
                break;
            case PERSON_INFO:
                fragment = CaseEditPersonFragment.newInstance(dataCapsule, activityRootData);
                break;
            case HOSPITALIZATION:
                fragment = CaseEditHospitalizationFragment.newInstance(dataCapsule, activityRootData);
                break;
            case SYMPTOMS:
                fragment = CaseEditSymptomsFragment.newInstance(dataCapsule, activityRootData);
                break;
            case EPIDEMIOLOGICAL_DATA:
                fragment = CaseEditEpidemiologicalDataFragment.newInstance(dataCapsule, activityRootData);
                break;
            case CONTACTS:
                fragment = CaseEditContactListFragment.newInstance(dataCapsule, activityRootData);
                break;
            case SAMPLES:
                fragment = CaseEditSampleListFragment.newInstance(dataCapsule, activityRootData);
                break;
            case TASKS:
                fragment = CaseEditTaskListFragment.newInstance(dataCapsule, activityRootData);
                break;
            default:
                throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
        }

        return fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_case);

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

        caseBeforeSaveAndPlagueTypeAlertTask = new DefaultAsyncTask(getContext()) {
            @Override
            public void doInBackground(TaskResultHolder resultHolder) {
                final Case caseBeforeSaving = DatabaseHelper.getCaseDao().queryUuidWithEmbedded(cazeToSave.getUuid());
                boolean showPlagueTypeChangeAlert = false;
                if (cazeToSave.getDisease() == Disease.PLAGUE) {
                    showPlagueTypeChangeAlert = updatePlagueType(cazeToSave);
                }

                resultHolder.forItem().add(caseBeforeSaving);
                resultHolder.forOther().add(showPlagueTypeChangeAlert);
            }

            @Override
            public void postExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                if (resultHolder == null) {
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
        }.executeOnThreadPool();
    }

    private void finalizeSaveProcess(BoolResult result, final Case caze, final Case caseBeforeSaving) {

//        if (savedCase.getDisease() == Disease.PLAGUE && caseBeforeSaving.getPlagueType() != savedCase.getPlagueType() &&
//                (caseBeforeSaving.getPlagueType() == PlagueType.PNEUMONIC || savedCase.getPlagueType() == PlagueType.PNEUMONIC)) {
//            setAdapter(savedCase);
//        }

        if (result.isFailed()) {
            NotificationHelper.showNotification(CaseEditActivity.this, NotificationType.ERROR,
                    String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_case)));
        } else {
            NotificationHelper.showNotification(CaseEditActivity.this, NotificationType.SUCCESS,
                    String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_case)));

            goToNextMenu();
        }
    }

    @Override
    public void saveData() {

        CaseSection activeSection = CaseSection.fromMenuKey(getActiveMenuItem().getKey());

        if (activeSection == CaseSection.CONTACTS || activeSection == CaseSection.SAMPLES || activeSection == CaseSection.TASKS)
            return;

        final Case cazeToSave = getStoredRootEntity();

        if (cazeToSave == null)
            return;

        saveCaseToDatabase(new Callback.IAction<BoolResult>() {
            @Override
            public void call(BoolResult result) {
                if (!result.isSuccess())
                    return;

                getCaseBeforeSaveAndPlagueTypeAlert(cazeToSave, new Callback.IAction3<BoolResult, Case, Boolean>() {

                    @Override
                    public void call(final BoolResult result, final Case caseBeforeSaving, Boolean showPlagueTypeChangeAlert) {
                        if (cazeToSave.getDisease() == Disease.PLAGUE && showPlagueTypeChangeAlert) {

                            String plagueTypeString = cazeToSave.getPlagueType().toString();
                            String confirmationMessage = String.format(getResources().getString(R.string.alert_plague_type_change), plagueTypeString, plagueTypeString);
                            final ConfirmationDialog confirmationDialog = new ConfirmationDialog(getActiveActivity(), R.string.alert_title_plague_type_change, confirmationMessage);

                            confirmationDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                                @Override
                                public void onOkClick(View v, Object item, View viewRoot) {
                                    confirmationDialog.dismiss();
                                    finalizeSaveProcess(result, cazeToSave, caseBeforeSaving);
                                }
                            });
                            confirmationDialog.show(null);
                        } else {
                            finalizeSaveProcess(result, cazeToSave, caseBeforeSaving);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void goToNewView() {
        CaseSection activeSection = CaseSection.fromMenuKey(getActiveMenuItem().getKey());

        if (activeSection == CaseSection.CONTACTS) {
            ContactFormNavigationCapsule dataCapsule = new ContactFormNavigationCapsule(getContext(),
                    ContactClassification.UNCONFIRMED).setCaseUuid(getRootEntityUuid());
            ContactNewActivity.goToActivity(this, dataCapsule);
        } else if (activeSection == CaseSection.SAMPLES) {
            SampleFormNavigationCapsule dataCapsule = new SampleFormNavigationCapsule(getContext(),
                    ShipmentStatus.NOT_SHIPPED).setCaseUuid(getRootEntityUuid());
            SampleNewActivity.goToActivity(this, dataCapsule);
        }
    }

    public void saveCaseToDatabase(final Callback.IAction<BoolResult> callback) {
        final String saveUnsuccessful = String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_case));

        CaseSection activeSection = CaseSection.fromMenuKey(getActiveMenuItem().getKey());

        if (activeSection == CaseSection.CONTACTS || activeSection == CaseSection.SAMPLES || activeSection == CaseSection.TASKS) {
            callback.call(new BoolResult(false, saveUnsuccessful));
            return;
        }

        final Case cazeToSave = getStoredRootEntity();

        if (cazeToSave == null) {
            callback.call(new BoolResult(false, saveUnsuccessful));
            return;
        }

        if (activeSection == CaseSection.PERSON_INFO) {
            FragmentCaseEditPatientLayoutBinding personBinding = (FragmentCaseEditPatientLayoutBinding) getActiveFragment().getContentBinding();
            PersonValidator.clearErrors(personBinding);
            if (!PersonValidator.validatePersonData(this, cazeToSave.getPerson(), personBinding)) {
                return;
            }
        }

        if (activeSection == CaseSection.SYMPTOMS) {
            Symptoms symptoms = cazeToSave.getSymptoms();
            //symptoms = (Symptoms)activeFragment.getPrimaryData();

            CaseEditSymptomsFragment symptomsFragment = (CaseEditSymptomsFragment) getActiveFragment();

            if (symptomsFragment == null)
                return;

            FragmentCaseEditSymptomsInfoLayoutBinding symptomsBinding = symptomsFragment.getContentBinding();

            if (symptoms != null) {
                // Necessary because the entry could've been automatically set, in which case the setValue method of the
                // custom field has not been called
                Symptom s = (Symptom) symptoms.getFirstSymptom();

                if (s != null)
                    symptoms.setOnsetSymptom(s.getName());

                NewSymptomValidator.validateCaseSymptoms(symptomsFragment.getSymptomList());
                //NewSymptomValidator.init(symptomsFragment.getSymptomList());

                /*SymptomsValidator.clearErrorsForSymptoms(symptomsBinding);
                if (!SymptomsValidator.validateCaseSymptoms(symptoms, symptomsBinding)) {
                    return;
                }*/
            }
        }

        saveTask = new SavingAsyncTask(getRootView(), cazeToSave) {

                    @Override
                    protected void doInBackground(TaskResultHolder resultHolder) throws DaoException {
                        DatabaseHelper.getPersonDao().saveAndSnapshot(cazeToSave.getPerson());
                        DatabaseHelper.getCaseDao().saveAndSnapshot(cazeToSave);
                    }

                    @Override
                    protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {

                        if (taskResult.getResultStatus().isSuccess()) {
                            goToNextMenu();
                        }
                    }
                }.executeOnThreadPool();
    }

    public static <TActivity extends BaseActivity> void goToActivity(Context fromActivity, CaseFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, CaseEditActivity.class, dataCapsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);

        if (caseBeforeSaveAndPlagueTypeAlertTask != null && !caseBeforeSaveAndPlagueTypeAlertTask.isCancelled())
            caseBeforeSaveAndPlagueTypeAlertTask.cancel(true);
    }

}