package de.symeda.sormas.app.caze.read;

import android.content.Context;
import android.view.Menu;

import java.util.List;

import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.caze.CaseSection;
import de.symeda.sormas.app.caze.edit.CaseEditActivity;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.person.read.PersonReadFragment;
import de.symeda.sormas.app.symptoms.SymptomsReadFragment;
import de.symeda.sormas.app.util.Bundler;

public class CaseReadActivity extends BaseReadActivity<Case> {

    public static final String TAG = CaseReadActivity.class.getSimpleName();

    public static void startActivity(Context context, String rootUuid) {
        BaseActivity.startActivity(context, CaseReadActivity.class, buildBundle(rootUuid));
    }

    public static Bundler buildBundle(String rootUuid) {
        return BaseReadActivity.buildBundle(rootUuid);
    }

    @Override
    public CaseClassification getPageStatus() {
        return getStoredRootEntity() == null ? null : getStoredRootEntity().getCaseClassification();
    }

    @Override
    protected Case queryRootData(String recordUuid) {
        return DatabaseHelper.getCaseDao().queryUuidWithEmbedded(recordUuid);
    }

    @Override
    public List<PageMenuItem> getPageMenuData() {
        List<PageMenuItem> menuItems = PageMenuItem.fromEnum(CaseSection.values(), getContext());
        Case caze = getStoredRootEntity();
        if (caze != null && !DiseaseHelper.hasContactFollowUp(caze.getDisease(), caze.getPlagueType())) {
            menuItems.remove(CaseSection.CONTACTS.ordinal());
        }
        return menuItems;
    }

    @Override
    protected BaseReadFragment buildReadFragment(PageMenuItem menuItem, Case activityRootData) {

        CaseSection section = CaseSection.fromOrdinal(menuItem.getKey());
        BaseReadFragment fragment;
        switch (section) {

            case CASE_INFO:
                fragment = CaseReadFragment.newInstance(activityRootData);
                break;
            case PERSON_INFO:
                fragment = PersonReadFragment.newInstance(activityRootData);
                break;
            case HOSPITALIZATION:
                fragment = CaseReadHospitalizationFragment.newInstance(activityRootData);
                break;
            case SYMPTOMS:
                fragment = SymptomsReadFragment.newInstance(activityRootData);
                break;
            case EPIDEMIOLOGICAL_DATA:
                fragment = CaseReadEpidemiologicalDataFragment.newInstance(activityRootData);
                break;
            case CONTACTS:
                fragment = CaseReadContactListFragment.newInstance(activityRootData);
                break;
            case SAMPLES:
                fragment = CaseReadSampleListFragment.newInstance(activityRootData);
                break;
            case TASKS:
                fragment = CaseReadTaskListFragment.newInstance(activityRootData);
                break;
            default:
                throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
        }

        return fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getEditMenu().setTitle(R.string.action_edit_case);
        return result;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level3_case_read;
    }

    @Override
    public void goToEditView() {
        CaseSection section = CaseSection.fromOrdinal(getActivePage().getKey());
        CaseEditActivity.startActivity(CaseReadActivity.this, getRootUuid(), section);
    }
}
