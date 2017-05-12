package de.symeda.sormas.app.caze;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.AbstractRootTabActivity;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.util.ConnectionHelper;
import de.symeda.sormas.app.util.SyncCallback;

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

        if (ConnectionHelper.isConnectedToInternet(getApplicationContext())) {
            SyncCasesTask.syncCasesWithoutCallback(getApplicationContext(), getSupportFragmentManager());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cases_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_reload:
                if (ConnectionHelper.isConnectedToInternet(getApplicationContext())) {
                    final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
                    refreshLayout.setRefreshing(true);
                    SyncCasesTask.syncCasesWithCallback(getApplicationContext(), getSupportFragmentManager(), new SyncCallback() {
                        @Override
                        public void call(boolean syncFailed) {
                            refreshLayout.setRefreshing(false);
                            if (!syncFailed) {
                                Toast.makeText(getApplicationContext(), "Synchronization successful.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Synchronization failed. Please try again later. This error has automatically been reported.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "You are not connected to the internet.", Toast.LENGTH_LONG).show();
                }
                return true;

            // Report problem button
            case R.id.action_report:
                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), null);
                AlertDialog dialog = userReportDialog.create();
                dialog.show();

                return true;

            case R.id.action_new_case:
                showCaseNewView();
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
