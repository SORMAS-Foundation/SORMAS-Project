package de.symeda.sormas.app.contact.read.sub;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.contact.VisitSection;
import de.symeda.sormas.app.contact.edit.sub.VisitEditActivity;
import de.symeda.sormas.app.shared.VisitFormNavigationCapsule;
import de.symeda.sormas.app.util.MenuOptionsHelper;

/**
 * Created by Orson on 02/01/2018.
 */

public class VisitReadActivity extends BaseReadActivity<Visit> {

    public static final String TAG = VisitReadActivity.class.getSimpleName();

    private final int DATA_XML_PAGE_MENU = R.xml.data_form_page_followup_menu; // "xml/data_read_page_3_1_followup_menu.xml";

    private String recordUuid = null;
    private VisitStatus pageStatus = null;
    private FollowUpStatus filterStatus = null;
    private BaseReadActivityFragment activeFragment = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveFilterStatusState(outState, filterStatus);
        savePageStatusState(outState, pageStatus);
        saveRecordUuidState(outState, recordUuid);
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
        filterStatus = (FollowUpStatus) getFilterStatusArg(arguments);
        pageStatus = (VisitStatus) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    protected Visit getActivityRootData(String recordUuid) {
        return DatabaseHelper.getVisitDao().queryUuid(recordUuid);
    }

    @Override
    public BaseReadActivityFragment getActiveReadFragment(Visit activityRootData) {
        if (activeFragment == null) {
            VisitFormNavigationCapsule dataCapsule = new VisitFormNavigationCapsule(
                    VisitReadActivity.this, recordUuid, pageStatus);
            activeFragment = VisitReadFragment.newInstance(dataCapsule, activityRootData);
        }

        return activeFragment;
    }

    @Override
    public int getPageMenuData() {
        return DATA_XML_PAGE_MENU;
    }

    @Override
    protected BaseReadActivityFragment getReadFragment(LandingPageMenuItem menuItem, Visit activityRootData) {
        VisitFormNavigationCapsule dataCapsule = new VisitFormNavigationCapsule(
                VisitReadActivity.this, recordUuid, pageStatus);

        VisitSection section = VisitSection.fromMenuKey(menuItem.getKey());
        switch (section) {

            case VISIT_INFO:
                activeFragment = VisitReadFragment.newInstance(dataCapsule, activityRootData);
                break;
            case SYMPTOMS:
                activeFragment = VisitReadSymptomsFragment.newInstance(dataCapsule, activityRootData);
                break;
        }

        return activeFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getEditMenu().setTitle(R.string.action_edit_contact);

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
        return R.string.heading_level3_1_contact_visit_info;
    }

    public static void goToActivity(Context fromActivity, VisitFormNavigationCapsule dataCapsule) {
        BaseReadActivity.goToActivity(fromActivity, VisitReadActivity.class, dataCapsule);
    }

    @Override
    public void goToEditView() {
        VisitFormNavigationCapsule dataCapsule = new VisitFormNavigationCapsule(VisitReadActivity.this,
                recordUuid, pageStatus);
        VisitEditActivity.goToActivity(VisitReadActivity.this, dataCapsule);
    }
}
