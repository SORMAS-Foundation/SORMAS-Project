package de.symeda.sormas.app.sample.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;

import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.shared.SampleFormNavigationCapsule;
import de.symeda.sormas.app.shared.ShipmentStatus;

public class SampleEditActivity extends BaseEditActivity<Sample> {

    private AsyncTask saveTask;

    @Override
    protected Sample queryRootEntity(String recordUuid) {
        return DatabaseHelper.getSampleDao().queryUuid(recordUuid);
    }

    @Override
    protected Sample buildRootEntity() {
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_sample);
        return result;
    }

    @Override
    public ShipmentStatus getPageStatus() {
        return (ShipmentStatus) super.getPageStatus();
    }

    @Override
    protected BaseEditFragment buildEditFragment(LandingPageMenuItem menuItem, Sample activityRootData) {

        SampleFormNavigationCapsule dataCapsule = new SampleFormNavigationCapsule(
                SampleEditActivity.this, getRootEntityUuid(), getPageStatus());
        return SampleEditFragment.newInstance(dataCapsule, activityRootData);
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level4_sample_edit;
    }

    @Override
    public void saveData() {

        final Sample sampleToSave = (Sample) getActiveFragment().getPrimaryData();

        // TODO validate
//        SampleValidator.clearErrorsForSampleData(getContentBinding());
////        if (!SampleValidator.validateSampleData(nContext, sampleToSave, getContentBinding())) {
////            return;
////        }


        saveTask = new DefaultAsyncTask(getContext(), sampleToSave) {

            @Override
            public void doInBackground(TaskResultHolder resultHolder) throws Exception {
                DatabaseHelper.getSampleDao().saveAndSnapshot(sampleToSave);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {

                if (taskResult.getResultStatus().isFailed()) {
                    NotificationHelper.showNotification(SampleEditActivity.this, NotificationType.ERROR,
                            String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_sample)));
                } else {
                    NotificationHelper.showNotification(SampleEditActivity.this, NotificationType.SUCCESS,
                            String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_sample)));

                    goToNextMenu();
                }
            }
        }.executeOnThreadPool();
    }

    public static <TActivity extends BaseActivity> void goToActivity(Context fromActivity, SampleFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, SampleEditActivity.class, dataCapsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }

}
