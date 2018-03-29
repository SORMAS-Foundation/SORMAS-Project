package de.symeda.sormas.app.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.symeda.sormas.app.BaseLandingActivity;
import de.symeda.sormas.app.BaseLandingActivityFragment;
import de.symeda.sormas.app.R;

/**
 * Created by Orson on 20/11/2017.
 */

public class DashboardActivity extends BaseLandingActivity {

    private BaseLandingActivityFragment activeFragment = null;
    //private SurveillanceOfficerDashboardForm dashboardForm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.fragment_dashboard_surveillance_officer_layout);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initializeActivity(Bundle arguments) {

    }

    @Override
    public BaseLandingActivityFragment getActiveLandingFragment() {
        return null;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.main_menu_dashboard;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        final Menu _menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_action_menu, menu);
        menu.findItem(R.id.action_sync).setVisible(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
