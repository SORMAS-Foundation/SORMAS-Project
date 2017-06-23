package de.symeda.sormas.app.sample;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.AbstractRootTabActivity;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleDao;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.rest.RetroProvider;
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

        if (RetroProvider.isConnected()) {
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
                if (RetroProvider.isConnected()) {
                    final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
                    refreshLayout.setRefreshing(true);
                    SyncSamplesTask.syncSamplesWithCallback(getApplicationContext(), getSupportFragmentManager(), new SyncCallback() {
                        @Override
                        public void call(boolean syncFailed) {
                            refreshLayout.setRefreshing(false);
                            if (!syncFailed) {
                                Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_sync_success, Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_sync_error, Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_no_connection, Snackbar.LENGTH_LONG).show();
                }
                return true;

            case R.id.action_markAllAsRead:
                SampleDao sampleDao = DatabaseHelper.getSampleDao();
                List<Sample> samples = sampleDao.queryForAll();
                for (Sample sampleToMark : samples) {
                    sampleDao.markAsRead(sampleToMark);
                }

                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment instanceof SamplesListFragment) {
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

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
