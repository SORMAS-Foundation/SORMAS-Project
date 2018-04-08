package de.symeda.sormas.app.caze.list;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.Random;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.BaseListActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.caze.edit.CaseNewActivity;
import de.symeda.sormas.app.component.dialog.MissingWeeklyReportDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.component.dialog.UserReportDialog;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.ICallback;
import de.symeda.sormas.app.core.IListNavigationCapsule;
import de.symeda.sormas.app.core.ListNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.util.MarkAllAsReadHelper;

/**
 * Created by Orson on 05/12/2017.
 */

public class CaseListActivity extends BaseListActivity {

    private final int DATA_XML_PAGE_MENU = R.xml.data_landing_page_case_menu;// "xml/data_landing_page_case_menu.xml";

    private static final int MENU_INDEX_CASE_PENDING = 0;
    private static final int MENU_INDEX_CASE_DONE = 1;
    private static final int MENU_INDEX_CASE_DISCARDED = 2;

    private InvestigationStatus statusFilters[] = new InvestigationStatus[] { InvestigationStatus.PENDING, InvestigationStatus.DONE, InvestigationStatus.DISCARDED };

    private AsyncTask jobTask;
    private InvestigationStatus filterStatus = null;
    private SearchBy searchBy = null;
    private String recordUuid = null;
    private BaseListActivityFragment activeFragment = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveFilterStatusState(outState, filterStatus);
        SaveSearchStrategyState(outState, searchBy);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    protected void initializeActivity(Bundle arguments) {
        filterStatus = (InvestigationStatus) getFilterStatusArg(arguments);
        searchBy = (SearchBy) getSearchStrategyArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    public BaseListActivityFragment getActiveReadFragment() throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            IListNavigationCapsule dataCapsule = new ListNavigationCapsule(CaseListActivity.this, filterStatus, searchBy);
            activeFragment = CaseListFragment.newInstance(this, dataCapsule);
        }

        return activeFragment;
    }

    @Override
    public int getPageMenuData() {
        return DATA_XML_PAGE_MENU;
    }

    @Override
    public int onNotificationCountChanging(AdapterView parent, LandingPageMenuItem menuItem, int position) {
        //TODO: Call database and retrieve notification count
        return (int)(new Random(DateTime.now().getMillis() * 1000).nextInt()/10000000);
    }

    @Override
    protected BaseListActivityFragment getNextFragment(LandingPageMenuItem menuItem) {
        InvestigationStatus status = statusFilters[menuItem.getKey()];

        if (status == null)
            return null;

        filterStatus = status;
        IListNavigationCapsule dataCapsule = new ListNavigationCapsule(CaseListActivity.this, filterStatus, searchBy);

        try {
            activeFragment = CaseListFragment.newInstance(this, dataCapsule);
        } catch (InstantiationException e) {
            Log.e(TAG, e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.getMessage());
        }

        return activeFragment;
    }

    @Override
    public Enum getStatus() {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getNewMenu().setTitle(R.string.action_new_case);

        return true;
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_action_bar, menu);

        MenuItem listMenu = menu.findItem(R.id.action_new);
        //listMenu.setVisible(false);
        listMenu.setTitle(R.string.action_new_case);

        return true;*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // Respond to the action bar's Up/Home button
            /*case android.R.id.home:
                // TODO check parent activity intent as soon as the minimum API level has been increased to 16
                //Intent intent = new Intent(this, CasesLandingActivity.class);
                //startActivity(intent);

                NavigationHelper.navigateUpFrom(this);

                return true;*/

            case R.id.action_new:
                EpiWeek lastEpiWeek = DateHelper.getPreviousEpiWeek(new Date());
                User user = ConfigProvider.getUser();
                if (user.hasUserRole(UserRole.INFORMANT)
                        && DatabaseHelper.getWeeklyReportDao().queryForEpiWeek(lastEpiWeek, ConfigProvider.getUser()) == null) {

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
                    gotoNewView();
                }
                return true;

            case R.id.option_menu_action_sync:
                synchronizeChangedData();
                return true;

            case R.id.option_menu_action_markAllAsRead:
                MarkAllAsReadHelper.markCases(this, new ICallback<AsyncTask>() {
                    @Override
                    public void result(AsyncTask asyncTask) {
                        /*if (asyncTask != null && !asyncTask.isCancelled())
                            asyncTask.cancel(true);*/
                    }
                });
                return true;

            // Report problem button
            case R.id.action_report:
                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), null);
                userReportDialog.show(null);

                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level2_cases_list;
    }

    private void gotoNewView() {
        CaseFormNavigationCapsule dataCapsule = (CaseFormNavigationCapsule)new CaseFormNavigationCapsule(CaseListActivity.this,
                null).setEditPageStatus(filterStatus).setPersonUuid(null);
        CaseNewActivity.goToActivity(this, dataCapsule);
    }

    public static void goToActivity(Context fromActivity, IListNavigationCapsule dataCapsule) {
        BaseListActivity.goToActivity(fromActivity, CaseListActivity.class, dataCapsule);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (activeFragment != null)
            activeFragment.cancelTaskExec();
    }
}
