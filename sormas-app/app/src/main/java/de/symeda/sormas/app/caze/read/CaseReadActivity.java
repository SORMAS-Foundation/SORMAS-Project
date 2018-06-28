package de.symeda.sormas.app.caze.read;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.caze.CaseSection;
import de.symeda.sormas.app.caze.edit.CaseEditActivity;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.util.MenuOptionsHelper;

/**
 * Created by Orson on 06/01/2018.
 */

public class CaseReadActivity  extends BaseReadActivity<Case> {

    public static final String TAG = CaseReadActivity.class.getSimpleName();

    private final int DATA_XML_PAGE_MENU = R.xml.data_form_page_case_menu; // "xml/data_read_page_case_menu.xml";

    private CaseClassification pageStatus = null;
    private String recordUuid = null;
    private BaseReadActivityFragment activeFragment = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
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
        pageStatus = (CaseClassification) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    @Override
    protected Case getActivityRootData(String recordUuid) {
        return DatabaseHelper.getCaseDao().queryUuidWithEmbedded(recordUuid);
    }

    @Override
    protected Case getActivityRootDataIfRecordUuidNull() {
        return DatabaseHelper.getCaseDao().build(DatabaseHelper.getPersonDao().build());
    }

    @Override
    public BaseReadActivityFragment getActiveReadFragment(Case activityRootData) {
        if (activeFragment == null) {
            CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(
                    CaseReadActivity.this, recordUuid).setReadPageStatus(pageStatus);
            activeFragment = CaseReadFragment.newInstance(this, dataCapsule, activityRootData);
        }

        return activeFragment;
    }

    @Override
    public int getPageMenuData() {
        return DATA_XML_PAGE_MENU;
    }

    @Override
    protected BaseReadActivityFragment getReadFragment(LandingPageMenuItem menuItem, Case activityRootData) {
        CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(
                CaseReadActivity.this, recordUuid).setReadPageStatus(pageStatus);

        CaseSection section = CaseSection.fromMenuKey(menuItem.getKey());
        switch (section) {

            case CASE_INFO:
                activeFragment = CaseReadFragment.newInstance(this, dataCapsule, activityRootData);
                break;
            case PERSON_INFO:
                activeFragment = CaseReadPatientInfoFragment.newInstance(this, dataCapsule, activityRootData);
                break;
            case HOSPITALIZATION:
                activeFragment = CaseReadHospitalizationFragment.newInstance(this, dataCapsule, activityRootData);
                break;
            case SYMPTOMS:
                activeFragment = CaseReadSymptomsFragment.newInstance(this, dataCapsule, activityRootData);
                break;
            case EPIDEMIOLOGICAL_DATA:
                activeFragment = CaseReadEpidemiologicalDataFragment.newInstance(this, dataCapsule, activityRootData);
                break;
            case CONTACTS:
                activeFragment = CaseReadContactListFragment.newInstance(this, dataCapsule, activityRootData);
                break;
            case SAMPLES:
                activeFragment = CaseReadSampleListFragment.newInstance(this, dataCapsule, activityRootData);
                break;
            case TASKS:
                activeFragment = CaseReadTaskListFragment.newInstance(this, dataCapsule, activityRootData);
                break;
            default:
                throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
        }

        return activeFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getEditMenu().setTitle(R.string.action_edit_case);

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
        return R.string.heading_level3_case_read;
    }

    @Override
    public void gotoEditView() {
        if (activeFragment == null)
            return;

        Case record = getStoredActivityRootData();

        CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(CaseReadActivity.this,
                record.getUuid()).setEditPageStatus(record.getInvestigationStatus());
        CaseEditActivity.goToActivity(CaseReadActivity.this, dataCapsule);
    }

    public static void goToActivity(Context fromActivity, CaseFormNavigationCapsule dataCapsule) {
        BaseReadActivity.goToActivity(fromActivity, CaseReadActivity.class, dataCapsule);
    }
}
