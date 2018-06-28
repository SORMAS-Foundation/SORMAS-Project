package de.symeda.sormas.app.sample.read;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.sample.edit.SampleEditActivity;
import de.symeda.sormas.app.shared.SampleFormNavigationCapsule;
import de.symeda.sormas.app.shared.ShipmentStatus;
import de.symeda.sormas.app.util.MenuOptionsHelper;

/**
 * Created by Orson on 10/12/2017.
 */

public class SampleReadActivity extends BaseReadActivity<Sample> {

    private final int DATA_XML_PAGE_MENU = -1;

    private ShipmentStatus pageStatus = null;
    private String recordUuid = null;
    private String caseUuid = null;
    private BaseReadActivityFragment activeFragment = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
        SaveCaseUuidState(outState, caseUuid);
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
        pageStatus = (ShipmentStatus) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
        caseUuid = getCaseUuidArg(arguments);
    }

    @Override
    protected Sample getActivityRootData(String recordUuid) {
        return DatabaseHelper.getSampleDao().queryUuid(recordUuid);
    }

    @Override
    protected Sample getActivityRootDataIfRecordUuidNull() {
        return null;
    }

    @Override
    public BaseReadActivityFragment getActiveReadFragment(Sample activityRootData) {
        if (activeFragment == null) {
            SampleFormNavigationCapsule dataCapsule = new SampleFormNavigationCapsule(SampleReadActivity.this,
                    recordUuid, pageStatus);
            activeFragment = SampleReadFragment.newInstance(this, dataCapsule, activityRootData);
        }

        return activeFragment;
    }

    @Override
    public LandingPageMenuItem getActiveMenuItem() {
        return null;
    }

    @Override
    public boolean showStatusFrame() {
        return false;
    }

    @Override
    public boolean showTitleBar() {
        return true;
    }

    @Override
    public boolean showPageMenu() {
        return false;
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
    protected BaseReadActivityFragment getReadFragment(LandingPageMenuItem menuItem, Sample activityRootData) {
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getEditMenu().setTitle(R.string.action_edit_sample);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!MenuOptionsHelper.handleReadModuleOptionsItemSelected(this, item))
            return super.onOptionsItemSelected(item);

        return true;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level3_sample_read;
    }

    @Override
    public void gotoEditView() {
        if (activeFragment == null)
            return;

        Sample record = (Sample)activeFragment.getPrimaryData();
        String sampleMaterial = (record.getSampleMaterial() != null)? record.getSampleMaterial().toString() : "";

        SampleFormNavigationCapsule dataCapsule = (SampleFormNavigationCapsule)new SampleFormNavigationCapsule(SampleReadActivity.this,
                record.getUuid(), pageStatus)
                .setSampleMaterial(sampleMaterial);
        SampleEditActivity.goToActivity(this, dataCapsule);
    }

    public static void goToActivity(Context fromActivity, SampleFormNavigationCapsule dataCapsule) {
        BaseReadActivity.goToActivity(fromActivity, SampleReadActivity.class, dataCapsule);
    }
}
