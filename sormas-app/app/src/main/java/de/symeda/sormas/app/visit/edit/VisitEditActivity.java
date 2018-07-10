package de.symeda.sormas.app.visit.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.visit.VisitSection;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.shared.VisitFormNavigationCapsule;
import de.symeda.sormas.app.symptom.Symptom;

public class VisitEditActivity extends BaseEditActivity<Visit> {

    public static final String TAG = VisitEditActivity.class.getSimpleName();

    private AsyncTask saveTask;

    @Override
    protected Visit queryRootEntity(String recordUuid) {
        return DatabaseHelper.getVisitDao().queryUuid(recordUuid);
    }

    @Override
    protected Visit buildRootEntity() {
        throw new UnsupportedOperationException();
    }

    @Override
    public VisitStatus getPageStatus() {
        return (VisitStatus) super.getPageStatus();
    }

    @Override
    public int getPageMenuData() {
        return R.xml.data_form_page_followup_menu;
    }

    @Override
    protected BaseEditFragment buildEditFragment(LandingPageMenuItem menuItem, Visit activityRootData) {
        VisitFormNavigationCapsule dataCapsule = new VisitFormNavigationCapsule(
                VisitEditActivity.this, getRootEntityUuid(), getPageStatus());

        VisitSection section = VisitSection.fromMenuKey(menuItem.getKey());
        BaseEditFragment fragment;
        switch (section) {
            case VISIT_INFO:
                fragment = VisitEditFragment.newInstance(dataCapsule, activityRootData);
                break;
            case SYMPTOMS:
                fragment = VisitEditSymptomsFragment.newInstance(dataCapsule, activityRootData);
                break;
            default:
                throw new IllegalArgumentException(DataHelper.toStringNullable(section));
        }

        return fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_followup);
        return result;
    }

    @Override
    public void saveData() {

        final Visit visit = getStoredRootEntity();

        // TODO #664 should this not be done by the user?
        VisitSection activeSection = VisitSection.fromMenuKey(getActiveMenuItem().getKey());
        if (activeSection == VisitSection.SYMPTOMS) {
            // Necessary because the entry could've been automatically set, in which case the setValue method of the
            // custom field has not been called
            Symptom s = (Symptom) visit.getSymptoms().getFirstSymptom();
            if (s != null) {
                visit.getSymptoms().setOnsetSymptom(s.getName());
            }
        }

        saveTask = new SavingAsyncTask(getRootView(), visit) {

            @Override
            protected void onPreExecute() {
                showPreloader();
            }

            @Override
            public void doInBackground(TaskResultHolder resultHolder) throws Exception {
                validateData(visit);
                DatabaseHelper.getVisitDao().saveAndSnapshot(visit);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                hidePreloader();
                super.onPostExecute(taskResult);
                if (taskResult.getResultStatus().isSuccess()) {
                    goToNextMenu();
                }
            }
        }.executeOnThreadPool();
    }

    private void validateData(Visit data) throws ValidationException {
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
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level3_1_contact_visit_info;
    }

    public static void goToActivity(Context fromActivity, VisitFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, VisitEditActivity.class, dataCapsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }
}