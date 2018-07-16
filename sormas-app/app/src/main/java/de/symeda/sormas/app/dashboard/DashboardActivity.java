package de.symeda.sormas.app.dashboard;

import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.BaseDashboardActivity;
import de.symeda.sormas.app.BaseSummaryFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.dialog.UserReportDialog;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.DashboardNavigationCapsule;
import de.symeda.sormas.app.dashboard.caze.CaseSummaryFragment;
import de.symeda.sormas.app.dashboard.contact.ContactSummaryFragment;
import de.symeda.sormas.app.dashboard.event.EventSummaryFragment;
import de.symeda.sormas.app.dashboard.sample.SampleSummaryFragment;
import de.symeda.sormas.app.dashboard.task.TaskSummaryFragment;

public class DashboardActivity extends BaseDashboardActivity {

    private List<BaseSummaryFragment> activeFragments = null;

    @Override
    protected List<BaseSummaryFragment> getSummaryFragments() {
        if (activeFragments == null) {
            activeFragments = new ArrayList<BaseSummaryFragment>() {{
                add(TaskSummaryFragment.newInstance(new DashboardNavigationCapsule(DashboardActivity.this)));
                add(CaseSummaryFragment.newInstance(new DashboardNavigationCapsule(DashboardActivity.this)));
                add(ContactSummaryFragment.newInstance(new DashboardNavigationCapsule(DashboardActivity.this)));
                add(EventSummaryFragment.newInstance(new DashboardNavigationCapsule(DashboardActivity.this)));
                add(SampleSummaryFragment.newInstance(new DashboardNavigationCapsule(DashboardActivity.this)));
            }};
        }

        return activeFragments;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.main_menu_dashboard;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Menu _menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_action_menu, menu);
        menu.findItem(R.id.action_sync).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_new:
                return true;

            case R.id.option_menu_action_sync:
                synchronizeChangedData();
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
}
