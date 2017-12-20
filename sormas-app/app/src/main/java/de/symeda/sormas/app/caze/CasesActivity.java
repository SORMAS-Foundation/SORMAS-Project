package de.symeda.sormas.app.caze;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.AbstractRootTabActivity;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.reports.ReportsActivity;

public class CasesActivity extends AbstractRootTabActivity {

    private CasesListFilterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.cases_activity_layout);
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.main_menu_cases));
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter = new CasesListFilterAdapter(getSupportFragmentManager());
        createTabViews(adapter);
        pager.setCurrentItem(currentTab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cases_action_bar, menu);

        User user = ConfigProvider.getUser();
        if (user != null ) {
            menu.findItem(R.id.action_new_case).setVisible(user.hasUserRight(UserRight.CASE_CREATE));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_reload:
                synchronizeChangedData();
                return true;

            case R.id.action_markAllAsRead:
                CaseDao caseDao = DatabaseHelper.getCaseDao();
                PersonDao personDao = DatabaseHelper.getPersonDao();
                List<Case> cases = caseDao.queryForAll();
                for (Case caseToMark : cases) {
                    caseDao.markAsRead(caseToMark);
                }

                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment instanceof CasesListFragment) {
                        fragment.onResume();
                    }
                }
                return true;

            // Report problem button
            case R.id.action_report:
                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), null);
                AlertDialog dialog = userReportDialog.create();
                dialog.show();

                return true;

            case R.id.action_new_case:
                EpiWeek lastEpiWeek = DateHelper.getPreviousEpiWeek(new Date());
                User user = ConfigProvider.getUser();
                if (user.hasUserRole(UserRole.INFORMANT)
                        && DatabaseHelper.getWeeklyReportDao().queryForEpiWeek(lastEpiWeek, ConfigProvider.getUser()) == null) {
                    AlertDialog noLastWeeklyReportDialog = buildNoLastWeeklyReportDialog();
                    noLastWeeklyReportDialog.show();
                } else {
                    showCaseNewView();
                }

                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private AlertDialog buildNoLastWeeklyReportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

        return dialog;
    }

}
