package de.symeda.sormas.app.reports;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.symeda.sormas.app.AbstractEditTabActivity;
import de.symeda.sormas.app.AbstractRootTabActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.UserReportDialog;

/**
 * Created by Mate Strysewske on 07.09.2017.
 */
public class ReportsActivity extends AbstractRootTabActivity {

    public static final String I18N_PREFIX = "Report";

    private ReportsPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.reports_activity_layout);
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.main_menu_reports));
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter = new ReportsPagerAdapter(getSupportFragmentManager());
        createTabViews(adapter);
        pager.setCurrentItem(currentTab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reports_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Report problem button
            case R.id.action_report:
                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), null);
                AlertDialog dialog = userReportDialog.create();
                dialog.show();

                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
