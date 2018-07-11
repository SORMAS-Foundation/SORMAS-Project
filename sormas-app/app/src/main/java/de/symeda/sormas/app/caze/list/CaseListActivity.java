package de.symeda.sormas.app.caze.list;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import java.util.Date;
import java.util.Random;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.BaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.caze.edit.CaseNewActivity;
import de.symeda.sormas.app.component.dialog.MissingWeeklyReportDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.IListNavigationCapsule;
import de.symeda.sormas.app.core.ListNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.util.MenuOptionsHelper;

public class CaseListActivity extends BaseListActivity {

    private InvestigationStatus statusFilters[] = new InvestigationStatus[]{InvestigationStatus.PENDING, InvestigationStatus.DONE, InvestigationStatus.DISCARDED};

    private InvestigationStatus filterStatus = null;
    private SearchBy searchBy = null;
    private String recordUuid = null;
    private BaseListFragment activeFragment = null;

    @Override
    public int getPageMenuData() {
        return R.xml.data_landing_page_case_menu;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveFilterStatusState(outState, filterStatus);
        saveSearchStrategyState(outState, searchBy);
        saveRecordUuidState(outState, recordUuid);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        filterStatus = (InvestigationStatus) getFilterStatusArg(savedInstanceState);
        searchBy = (SearchBy) getSearchStrategyArg(savedInstanceState);
        recordUuid = getRecordUuidArg(savedInstanceState);
    }

    @Override
    public BaseListFragment getActiveListFragment() {
        if (activeFragment == null) {
            IListNavigationCapsule dataCapsule = new ListNavigationCapsule(CaseListActivity.this, filterStatus, searchBy);
            activeFragment = CaseListFragment.newInstance(dataCapsule);
        }

        return activeFragment;
    }

    @Override
    public int onNotificationCountChangingAsync(AdapterView parent, LandingPageMenuItem menuItem, int position) {
        //TODO: Call database and retrieve notification count
        return new Random().nextInt(100);
        //return (int)(new Random(DateTime.now().getMillis() * 1000).nextInt()/10000000);
    }

    @Override
    protected BaseListFragment getListFragment(LandingPageMenuItem menuItem) {
        InvestigationStatus status = statusFilters[menuItem.getKey()];

        if (status == null)
            return null;

        filterStatus = status;
        IListNavigationCapsule dataCapsule = new ListNavigationCapsule(CaseListActivity.this, filterStatus, searchBy);

        activeFragment = CaseListFragment.newInstance(dataCapsule);
        return activeFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getNewMenu().setTitle(R.string.action_new_case);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!MenuOptionsHelper.handleListModuleOptionsItemSelected(this, item))
            return super.onOptionsItemSelected(item);

        return true;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level2_cases_list;
    }

    @Override
    public void goToNewView() {
        EpiWeek lastEpiWeek = DateHelper.getPreviousEpiWeek(new Date());
        User user = ConfigProvider.getUser();
        if (user.hasUserRole(UserRole.INFORMANT)
                && DatabaseHelper.getWeeklyReportDao().queryForEpiWeek(lastEpiWeek, ConfigProvider.getUser()) == null) {

            // TODO reactivate reports
            MissingWeeklyReportDialog confirmationDialog = new MissingWeeklyReportDialog(this);
            confirmationDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                @Override
                public void onOkClick(View v, Object item, View viewRoot) {
                    /*Intent intent = new Intent(CaseListActivity.this, ReportsActivity.class);
                    startActivity(intent);*/
                }
            });

            confirmationDialog.show(null);
        } else {
            CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(this,
                    null, CaseClassification.NOT_CLASSIFIED).setPersonUuid(null);
            CaseNewActivity.goToActivity(this, dataCapsule);
        }
    }

    public static void goToActivity(Context fromActivity, IListNavigationCapsule dataCapsule) {
        BaseListActivity.goToActivity(fromActivity, CaseListActivity.class, dataCapsule);
    }

    @Override
    public boolean isEntryCreateAllowed() {
        User user = ConfigProvider.getUser();
        return user.hasUserRight(UserRight.CASE_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (activeFragment != null)
            activeFragment.cancelTaskExec();
    }
}
