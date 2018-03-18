package de.symeda.sormas.app.caze.list;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.BaseListActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.caze.CaseFormNavigationCapsule;
import de.symeda.sormas.app.caze.edit.CaseNewActivity;
import de.symeda.sormas.app.caze.landing.CaseLandingToListCapsule;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.util.NavigationHelper;

/**
 * Created by Orson on 05/12/2017.
 */

public class CaseListActivity extends BaseListActivity {

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
            CaseListCapsule dataCapsule = new CaseListCapsule(CaseListActivity.this, filterStatus, searchBy);
            activeFragment = CaseListFragment.newInstance(this, dataCapsule);
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_action_bar, menu);

        MenuItem listMenu = menu.findItem(R.id.action_new);
        //listMenu.setVisible(false);
        listMenu.setTitle(R.string.action_new_case);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                // TODO check parent activity intent as soon as the minimum API level has been increased to 16
                //Intent intent = new Intent(this, CasesLandingActivity.class);
                //startActivity(intent);
                if (activeFragment != null)
                    activeFragment.cancelTaskExec();

                NavigationHelper.navigateUpFrom(this);

                return true;

            case R.id.action_new:
                gotoNewView();


                //synchronizeChangedData();
                /*EpiWeek lastEpiWeek = DateHelper.getPreviousEpiWeek(new Date());
                User user = ConfigProvider.getUser();
                if (user.getUserRole() == UserRole.INFORMANT && DatabaseHelper.getWeeklyReportDao().queryForEpiWeek(lastEpiWeek, ConfigProvider.getUser()) == null) {
                    AlertDialog noLastWeeklyReportDialog = buildNoLastWeeklyReportDialog();
                    noLastWeeklyReportDialog.show();
                } else {
                    showCaseNewView();
                }*/
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
    protected int getRootActivityLayout() {
        return R.layout.activity_root_with_title_layout;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level2_cases_list;
    }

    private AlertDialog buildNoLastWeeklyReportDialog() {
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_missing_report);
        builder.setTitle(R.string.alert_title_missing_report);
        builder.setIcon(R.drawable.ic_info_outline_black_24dp);
        AlertDialog dialog = builder.create();
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.action_open_reports),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(CasesActivity.this, ReportsActivity.class);
                        startActivity(intent);
                    }
                }
        );
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.action_close),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );

        return dialog;*/
        return null;
    }

    private void gotoNewView() {
        CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(CaseListActivity.this,
                "").setEditPageStatus(filterStatus);
        CaseNewActivity.goToActivity(this, dataCapsule);
    }

    public static void goToActivity(Context fromActivity, CaseLandingToListCapsule dataCapsule) {
        BaseListActivity.goToActivity(fromActivity, CaseListActivity.class, dataCapsule);
    }
}
