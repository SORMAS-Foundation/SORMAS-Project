package de.symeda.sormas.app.contact.edit.sub;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.contact.VisitSection;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.shared.VisitFormNavigationCapsule;
import de.symeda.sormas.app.symptom.Symptom;
import de.symeda.sormas.app.util.MenuOptionsHelper;

public class VisitEditActivity extends BaseEditActivity<Visit> {

    public static final String TAG = VisitEditActivity.class.getSimpleName();

    private AsyncTask saveTask;

    private String contactUuid = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveContactUuidState(outState, contactUuid);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        contactUuid = getContactUuidArg(savedInstanceState);
    }

    @Override
    protected Visit queryActivityRootEntity(String recordUuid) {
        return DatabaseHelper.getVisitDao().queryUuid(recordUuid);
    }

    @Override
    protected Visit buildActivityRootEntity() {
        throw new UnsupportedOperationException();
    }

    @Override
    public VisitStatus getPageStatus() {
        return (VisitStatus)super.getPageStatus();
    }

    @Override
    public int getPageMenuData() {
        return R.xml.data_form_page_followup_menu;
    }

    @Override
    protected BaseEditActivityFragment buildEditFragment(LandingPageMenuItem menuItem, Visit activityRootData) {
        VisitFormNavigationCapsule dataCapsule = new VisitFormNavigationCapsule(
                VisitEditActivity.this, getRootEntityUuid(), getPageStatus());

        VisitSection section = VisitSection.fromMenuKey(menuItem.getKey());
        BaseEditActivityFragment fragment;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!MenuOptionsHelper.handleEditModuleOptionsItemSelected(this, item))
            return super.onOptionsItemSelected(item);

        return true;
    }

    @Override
    public void saveData() {

        VisitSection activeSection = VisitSection.fromMenuKey(getActiveMenuItem().getKey());

        final Visit visit = (Visit) getActiveFragment().getPrimaryData();

        // TODO should this not be done by the user?
        if (activeSection == VisitSection.SYMPTOMS) {
            // Necessary because the entry could've been automatically set, in which case the setValue method of the
            // custom field has not been called
            Symptom s = (Symptom) visit.getSymptoms().getFirstSymptom();
            if (s != null) {
                visit.getSymptoms().setOnsetSymptom(s.getName());
            }
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

        saveTask = new DefaultAsyncTask(getContext(), visit) {

            @Override
            public void doInBackground(TaskResultHolder resultHolder) throws Exception {
                DatabaseHelper.getVisitDao().saveAndSnapshot(visit);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {

                if (taskResult.getResultStatus().isFailed()) {
                    NotificationHelper.showNotification(VisitEditActivity.this, NotificationType.ERROR,
                            String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_visit)));
                } else {
                    NotificationHelper.showNotification(VisitEditActivity.this, NotificationType.SUCCESS,
                            String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_visit)));

                    goToNextMenu();
                }
            }
        }.executeOnThreadPool();
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