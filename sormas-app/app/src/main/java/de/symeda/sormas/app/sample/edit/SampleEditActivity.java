package de.symeda.sormas.app.sample.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.sample.ShipmentStatus;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

public class SampleEditActivity extends BaseEditActivity<Sample> {

    private AsyncTask saveTask;

    public static void startActivity(Context context, String rootUuid) {
        BaseEditActivity.startActivity(context, SampleEditActivity.class, buildBundle(rootUuid));
    }

    @Override
    protected Sample queryRootEntity(String recordUuid) {
        return DatabaseHelper.getSampleDao().queryUuid(recordUuid);
    }

    @Override
    protected Sample buildRootEntity() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_sample);
        return result;
    }

    @Override
    public ShipmentStatus getPageStatus() {
        Sample sample = getStoredRootEntity();
        if (sample != null) {
            ShipmentStatus shipmentStatus = sample.getReferredToUuid() != null ?
                    ShipmentStatus.REFERRED_OTHER_LAB : sample.isReceived() ?
                    ShipmentStatus.RECEIVED : sample.isShipped() ? ShipmentStatus.SHIPPED :
                    ShipmentStatus.NOT_SHIPPED;
            return shipmentStatus;
        } else {
            return null;
        }
    }

    @Override
    protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Sample activityRootData) {
        return SampleEditFragment.newInstance(activityRootData);
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level4_sample_edit;
    }

    @Override
    public void saveData() {
        final Sample sampleToSave = getStoredRootEntity();
        SampleEditFragment fragment = (SampleEditFragment) getActiveFragment();

        try {
            FragmentValidator.validate(getContext(), fragment.getContentBinding());
        } catch (ValidationException e) {
            NotificationHelper.showNotification(this, ERROR, e.getMessage());
            return;
        }

        saveTask = new SavingAsyncTask(getRootView(), sampleToSave) {

            @Override
            protected void onPreExecute() {
                showPreloader();
            }

            @Override
            public void doInBackground(TaskResultHolder resultHolder) throws Exception, ValidationException {
                DatabaseHelper.getSampleDao().saveAndSnapshot(sampleToSave);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                hidePreloader();
                super.onPostExecute(taskResult);
            }
        }.executeOnThreadPool();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }
}
