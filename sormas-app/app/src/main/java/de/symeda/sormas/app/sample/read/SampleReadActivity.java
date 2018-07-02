package de.symeda.sormas.app.sample.read;

import android.content.Context;
import android.view.Menu;

import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.sample.edit.SampleEditActivity;
import de.symeda.sormas.app.shared.SampleFormNavigationCapsule;
import de.symeda.sormas.app.shared.ShipmentStatus;

public class SampleReadActivity extends BaseReadActivity<Sample> {

    @Override
    protected Sample queryRootData(String recordUuid) {
        return DatabaseHelper.getSampleDao().queryUuid(recordUuid);
    }

    @Override
    public ShipmentStatus getPageStatus() {
        return (ShipmentStatus)super.getPageStatus();
    }

    @Override
    protected BaseReadFragment buildReadFragment(LandingPageMenuItem menuItem, Sample activityRootData) {
        SampleFormNavigationCapsule dataCapsule = new SampleFormNavigationCapsule(this, getRootEntityUuid(), getPageStatus());
        return SampleReadFragment.newInstance(dataCapsule, activityRootData);
    }

    @Override
    public LandingPageMenuItem getActiveMenuItem() {
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
        SampleFormNavigationCapsule dataCapsule = new SampleFormNavigationCapsule(this, getRootEntityUuid(), getPageStatus());
        SampleEditActivity.goToActivity(this, dataCapsule);
    }

    public static void goToActivity(Context fromActivity, SampleFormNavigationCapsule dataCapsule) {
        BaseReadActivity.goToActivity(fromActivity, SampleReadActivity.class, dataCapsule);
    }
}
