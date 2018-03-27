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
import de.symeda.sormas.app.util.NavigationHelper;

/**
 * Created by Orson on 10/12/2017.
 */

public class SampleReadActivity extends BaseReadActivity<Sample> {

    private final String DATA_XML_PAGE_MENU = "";

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
        Sample sample;
        if (caseUuid != null && !caseUuid.isEmpty()) {
            Case associatedCase = DatabaseHelper.getCaseDao().queryUuid(caseUuid);
            sample = DatabaseHelper.getSampleDao().build(associatedCase);
        } else {
            sample = DatabaseHelper.getSampleDao().queryUuid(recordUuid);
        }

        return sample;
    }

    @Override
    protected Sample getActivityRootDataIfRecordUuidNull() {
        return null;
    }

    @Override
    public BaseReadActivityFragment getActiveReadFragment(Sample activityRootData) throws IllegalAccessException, InstantiationException {
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
    public String getPageMenuData() {
        return DATA_XML_PAGE_MENU;
    }

    @Override
    protected BaseReadActivityFragment getNextFragment(LandingPageMenuItem menuItem, Sample activityRootData) {
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getEditMenu().setTitle(R.string.action_edit_sample);

        return true;
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.read_action_menu, menu);

        MenuItem readMenu = menu.findItem(R.id.action_edit);
        //readMenu.setVisible(false);
        readMenu.setTitle(R.string.action_edit_sample);

        return true;*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavigationHelper.navigateUpFrom(this);
                return true;

            case R.id.action_edit:
                gotoEditView();
                return true;

            case R.id.option_menu_action_sync:
                //synchronizeChangedData();
                return true;

            case R.id.option_menu_action_markAllAsRead:
                /*CaseDao caseDao = DatabaseHelper.getCaseDao();
                PersonDao personDao = DatabaseHelper.getPersonDao();
                List<Case> cases = caseDao.queryForAll();
                for (Case caseToMark : cases) {
                    caseDao.markAsRead(caseToMark);
                }

                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment instanceof CasesListFragment) {
                        fragment.onResume();
                    }
                }*/
                return true;

            // Report problem button
            case R.id.action_report:
                /*UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), null);
                AlertDialog dialog = userReportDialog.create();
                dialog.show();*/

                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level3_sample_read;
    }


    private void gotoEditView() {
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
