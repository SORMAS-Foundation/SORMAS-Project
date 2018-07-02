package de.symeda.sormas.app.sample.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;

import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.shared.SampleFormNavigationCapsule;
import de.symeda.sormas.app.shared.ShipmentStatus;
import de.symeda.sormas.app.util.MenuOptionsHelper;

public class SampleNewActivity extends BaseEditActivity<Sample> {

    public static final String TAG = SampleNewActivity.class.getSimpleName();

    private AsyncTask saveTask;

    private String caseUuid = null;

    @Override
    public ShipmentStatus getPageStatus() {
        return (ShipmentStatus)super.getPageStatus();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveCaseUuidState(outState, caseUuid);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        caseUuid = getCaseUuidArg(savedInstanceState);
    }

    @Override
    protected Sample queryActivityRootEntity(String recordUuid) {
        return null;
    }

    @Override
    protected Sample buildActivityRootEntity() {
        Sample sample = null;
        if (caseUuid != null && !caseUuid.isEmpty()) {
            Case associatedCase = DatabaseHelper.getCaseDao().queryUuidReference(caseUuid);
            sample = DatabaseHelper.getSampleDao().build(associatedCase);
        }
        return sample;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_sample);
        return result;
    }

    @Override
    protected BaseEditActivityFragment buildEditFragment(LandingPageMenuItem menuItem, Sample activityRootData) {
        SampleFormNavigationCapsule dataCapsule = new SampleFormNavigationCapsule(
                SampleNewActivity.this, getRootEntityUuid(), getPageStatus()).setCaseUuid(caseUuid);
        return SampleNewFragment.newInstance(dataCapsule, activityRootData);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!MenuOptionsHelper.handleEditModuleOptionsItemSelected(this, item))
            return super.onOptionsItemSelected(item);

        return true;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_sample_new;
    }

    @Override
    public void saveData() {

        final Sample sampleToSave = (Sample)getActiveFragment().getPrimaryData();

        if (sampleToSave.getReportingUser() == null) {
            sampleToSave.setReportingUser(ConfigProvider.getUser());
        }
        if (sampleToSave.getReportDateTime() == null) {
            sampleToSave.setReportDateTime(new Date());
        }

        // TODO: re-enable validation
//        SampleValidator.clearErrorsForSampleData(getContentBinding());
//        if (!SampleValidator.validateSampleData(nContext, sampleToSave, getContentBinding())) {
//            return;
//        }

        saveTask = new DefaultAsyncTask(getContext(), sampleToSave) {

            @Override
            protected void onPreExecute() {
                showPreloader();
            }

            @Override
            public void doInBackground(TaskResultHolder resultHolder) throws DaoException {
                DatabaseHelper.getSampleDao().saveAndSnapshot(sampleToSave);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {

                hidePreloader();

                if (taskResult.getResultStatus().isFailed()) {
                    NotificationHelper.showNotification(SampleNewActivity.this, NotificationType.ERROR,
                            String.format(getResources().getString(R.string.snackbar_save_error), sampleToSave.getEntityName()));
                } else {
                    NotificationHelper.showNotification(SampleNewActivity.this, NotificationType.SUCCESS,
                            String.format(getResources().getString(R.string.snackbar_save_success), sampleToSave.getEntityName()));

                    finish();
                }
            }
        }.executeOnThreadPool();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }

    public static <TActivity extends BaseActivity> void goToActivity(Context fromActivity, SampleFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, SampleNewActivity.class, dataCapsule);
    }
}
