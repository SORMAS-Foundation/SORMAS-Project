package de.symeda.sormas.app.sample.read;

import android.content.Context;
import android.view.Menu;

import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.sample.ShipmentStatus;
import de.symeda.sormas.app.sample.edit.SampleEditActivity;

public class SampleReadActivity extends BaseReadActivity<Sample> {

    public static void startActivity(Context context, String rootUuid) {
        BaseReadActivity.startActivity(context, SampleReadActivity.class, buildBundle(rootUuid));
    }

    @Override
    protected Sample queryRootData(String recordUuid) {
        return DatabaseHelper.getSampleDao().queryUuid(recordUuid);
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
    protected BaseReadFragment buildReadFragment(PageMenuItem menuItem, Sample activityRootData) {
        return SampleReadFragment.newInstance(activityRootData);
    }

    @Override
    public PageMenuItem getActivePage() {
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getEditMenu().setTitle(R.string.action_edit_sample);
        return result;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level3_sample_read;
    }

    @Override
    public void goToEditView() {
        SampleEditActivity.startActivity(getContext(), getRootUuid());
    }
}
