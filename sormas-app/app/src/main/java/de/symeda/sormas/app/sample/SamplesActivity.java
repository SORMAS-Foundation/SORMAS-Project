package de.symeda.sormas.app.sample;

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

public class SamplesActivity extends AbstractRootTabActivity {

    private SamplesListFilterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.samples_activity_layout);
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.main_menu_samples));
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter = new SamplesListFilterAdapter(getSupportFragmentManager());
        createTabViews(adapter);
        pager.setCurrentItem(currentTab);

        if (ConnectionHelper.isConnectedToInternet(getApplicationContext())) {
            SyncSamplesTask.syncSamplesWithoutCallback(getApplicationContext(), getSupportFragmentManager());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.samples_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_reload:
                if (ConnectionHelper.isConnectedToInternet(getApplicationContext())) {
                    final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
                    refreshLayout.setRefreshing(true);
                    SyncSamplesTask.syncSamplesWithCallback(getApplicationContext(), getSupportFragmentManager(), new SyncCallback() {
                        @Override
                        public void call(boolean syncFailed) {
                            refreshLayout.setRefreshing(false);
                            if (!syncFailed) {
                                Toast.makeText(getApplicationContext(), R.string.snackbar_sync_success, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.snackbar_sync_error, Toast.LENGTH_LONG).show();
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

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
