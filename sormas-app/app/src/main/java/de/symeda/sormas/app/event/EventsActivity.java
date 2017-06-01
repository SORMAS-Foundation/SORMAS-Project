package de.symeda.sormas.app.event;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.AbstractRootTabActivity;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.util.SyncCallback;

public class EventsActivity extends AbstractRootTabActivity {

    private EventsListFilterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.cases_activity_layout);
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.main_menu_events) + " - " + ConfigProvider.getUser().getUserRole().toShortString());
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter = new EventsListFilterAdapter(getSupportFragmentManager());
        createTabViews(adapter);
        pager.setCurrentItem(currentTab);

        if (RetroProvider.isConnected()) {
            SyncEventsTask.syncEventsWithoutCallback(getApplicationContext(), getSupportFragmentManager());
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
                if (RetroProvider.isConnected()) {
                    final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
                    refreshLayout.setRefreshing(true);
                    SyncEventsTask.syncEventsWithCallback(getApplicationContext(), getSupportFragmentManager(), new SyncCallback() {
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

            // Report problem button
            case R.id.action_report:
                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), null);
                AlertDialog dialog = userReportDialog.create();
                dialog.show();

                return true;

            case R.id.action_new_case:
                showNewEventView();
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showNewEventView() {
        Intent intent = new Intent(this, EventEditActivity.class);
        intent.putExtra(EventEditActivity.NEW_EVENT, true);
        startActivity(intent);
    }
}
