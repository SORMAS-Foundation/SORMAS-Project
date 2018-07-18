package de.symeda.sormas.app.caze.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.caze.CaseSection;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.contact.edit.ContactNewActivity;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentPersonEditLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentSymptomsEditLayoutBinding;
import de.symeda.sormas.app.person.edit.PersonEditFragment;
import de.symeda.sormas.app.sample.edit.SampleNewActivity;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;
import de.symeda.sormas.app.shared.SampleFormNavigationCapsule;
import de.symeda.sormas.app.shared.ShipmentStatus;
import de.symeda.sormas.app.symptoms.SymptomsEditFragment;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.validation.PersonValidator;

public class CaseEditActivity extends BaseEditActivity<Case> {

    public static final String TAG = CaseEditActivity.class.getSimpleName();

    private AsyncTask saveTask;

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
        return (CaseClassification) super.getPageStatus();
    }

    @Override
    public int getPageMenuData() {
        return R.xml.data_form_page_case_menu;
    }

    @Override
    protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Case activityRootData) {
        CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(this, getRootEntityUuid(), getPageStatus());

        CaseSection section = CaseSection.fromMenuKey(menuItem.getKey());
        BaseEditFragment fragment;
        switch (section) {

            case CASE_INFO:
                fragment = CaseEditFragment.newInstance(dataCapsule, activityRootData);
                break;
            case PERSON_INFO:
                fragment = PersonEditFragment.newInstance(dataCapsule, activityRootData);
                break;
            case HOSPITALIZATION:
                fragment = CaseEditHospitalizationFragment.newInstance(dataCapsule, activityRootData);
                break;
            case SYMPTOMS:
                fragment = SymptomsEditFragment.newInstance(dataCapsule, activityRootData);
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

    @Override
    public void saveData() {
        saveData(new Consumer<Case>() {
            @Override
            public void accept(Case parameter) {
                goToNextPage();
            }
        });
    }

    public void saveData(final Consumer<Case> successCallback) {

        final Case cazeToSave = getStoredRootEntity();

        CaseSection activeSection = CaseSection.fromMenuKey(getActivePage().getKey());
        if (activeSection == CaseSection.PERSON_INFO) {
            FragmentPersonEditLayoutBinding personBinding = (FragmentPersonEditLayoutBinding) getActiveFragment().getContentBinding();
            PersonValidator.clearErrors(personBinding);
            if (!PersonValidator.validatePersonData(this, cazeToSave.getPerson(), personBinding)) {
                return;
            }
        }

        if (activeSection == CaseSection.SYMPTOMS) {
            FragmentSymptomsEditLayoutBinding symptomsBinding = (FragmentSymptomsEditLayoutBinding) getActiveFragment().getContentBinding();

//            // Necessary because the entry could've been automatically set, in which case the setFieldValue method of the
//            // custom field has not been called
//            Symptom s = (Symptom) symptoms.getFirstSymptom();
//
//            if (s != null)
//                symptoms.setOnsetSymptom(s.getName());

            // TODO validation
//            NewSymptomValidator.validateCaseSymptoms(symptomsFragment.getSymptomList());
//            NewSymptomValidator.init(symptomsFragment.getSymptomList());
//
//            SymptomsValidator.clearErrorsForSymptoms(symptomsBinding);
//            if (!SymptomsValidator.validateCaseSymptoms(symptoms, symptomsBinding)) {
//                return;
//            }
        }

        saveTask = new SavingAsyncTask(getRootView(), cazeToSave) {
            @Override
            protected void onPreExecute() {
                showPreloader();
            }

            @Override
            protected void doInBackground(TaskResultHolder resultHolder) throws DaoException {
                DatabaseHelper.getPersonDao().saveAndSnapshot(cazeToSave.getPerson());
                DatabaseHelper.getCaseDao().saveAndSnapshot(cazeToSave);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                hidePreloader();
                super.onPostExecute(taskResult);
                if (taskResult.getResultStatus().isSuccess()) {
                    successCallback.accept(cazeToSave);
                }
            }
        }.executeOnThreadPool();
    }

    @Override
    public void goToNewView() {
        CaseSection activeSection = CaseSection.fromMenuKey(getActivePage().getKey());

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

    public static <TActivity extends BaseActivity> void goToActivity(Context fromActivity, CaseFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, CaseEditActivity.class, dataCapsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }

}