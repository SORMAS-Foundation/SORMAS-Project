package de.symeda.sormas.app;

import android.accounts.AuthenticatorException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.net.ConnectException;

import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.caze.CaseNewActivity;
import de.symeda.sormas.app.caze.CasesActivity;
import de.symeda.sormas.app.component.SyncLogDialog;
import de.symeda.sormas.app.contact.ContactsActivity;
import de.symeda.sormas.app.event.EventsActivity;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.sample.SamplesActivity;
import de.symeda.sormas.app.settings.SettingsActivity;
import de.symeda.sormas.app.task.TasksActivity;
import de.symeda.sormas.app.util.SyncCallback;

public abstract class AbstractRootTabActivity extends AbstractTabActivity {

    private ActionBarDrawerToggle menuDrawerToggle;
    private DrawerLayout menuDrawerLayout;
    private String[] menuTitles;
    private ListView menuDrawerList;

    private CharSequence mainViewTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuTitles = new String[]{
                getResources().getString(R.string.main_menu_tasks),
                getResources().getString(R.string.main_menu_cases),
                getResources().getString(R.string.main_menu_contacts),
                getResources().getString(R.string.main_menu_events),
                getResources().getString(R.string.main_menu_samples),
                getResources().getString(R.string.main_menu_settings),
                getResources().getString(R.string.main_menu_sync_all),
                getResources().getString(R.string.main_menu_logout) + " (" + ConfigProvider.getUsername() + ")"
        };

        menuDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        menuDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        menuDrawerList.setAdapter(
                new ArrayAdapter<String>(
                        this,
                        R.layout.drawer_list_item,
                        menuTitles));

        // Set the list's click listener
        menuDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
                // necessary to prevent the drawer from staying open when the same entry is selected
                menuDrawerLayout.closeDrawers();
            }
        });

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        setupDrawer();

        BroadcastReceiver messageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_sync_conflict, Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(R.string.snackbar_open_synclog, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openSyncLog();
                    }
                });
                snackbar.show();
            }
        };

        IntentFilter filter = new IntentFilter("SYNC_ERROR_SNACKBAR");
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(messageReceiver, filter);


        syncAll(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle params = getIntent().getExtras();
        if(params!=null) {
            if (params.containsKey(KEY_PAGE)) {
                currentTab = params.getInt(KEY_PAGE);
            }
        }

        synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly, false, null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_PAGE, currentTab);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (menuDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mainViewTitle = title;
        String userRole = "";
        if (ConfigProvider.getUser()!=null && ConfigProvider.getUser().getUserRole() !=null) {
            userRole = " - " + ConfigProvider.getUser().getUserRole().toShortString();
        }
        getSupportActionBar().setTitle(mainViewTitle + userRole);
    }

    private void setupDrawer() {

        menuDrawerToggle = new ActionBarDrawerToggle(
                this,
                menuDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getSupportActionBar().setTitle("");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getSupportActionBar().setTitle(mainViewTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        menuDrawerToggle.setDrawerIndicatorEnabled(true);
        menuDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_drawer);
        menuDrawerLayout.addDrawerListener(menuDrawerToggle);
    }

    private void openSyncLog() {
        SyncLogDialog syncLogDialog = new SyncLogDialog(this);
        syncLogDialog.show(this);
    }

    public void showCasesView() {
        Intent intent = new Intent(this, CasesActivity.class);
        startActivity(intent);
    }

    public void showCaseNewView() {
        Intent intent = new Intent(this, CaseNewActivity.class);
        startActivity(intent);
    }

    public void showContactsView() {
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
    }

    public void showTasksView() {
        Intent intent = new Intent(this, TasksActivity.class);
        startActivity(intent);
    }

    public void showEventsView() {
        Intent intent = new Intent(this, EventsActivity.class);
        startActivity(intent);
    }

    public void showSamplesView() {
        Intent intent = new Intent(this, SamplesActivity.class);
        startActivity(intent);
    }

    public void showSettingsView() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void logout() {
        ConfigProvider.clearUsernameAndPassword();
        ConfigProvider.clearPin();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        switch (position) {
            case 0:
                showTasksView();
                break;
            case 1:
                showCasesView();
                break;
            case 2:
                showContactsView();
                break;
            case 3:
                showEventsView();
                break;
            case 4:
                showSamplesView();
                break;
            case 5:
                showSettingsView();
                // don't keep this button selected
                menuDrawerList.clearChoices();
                break;
            case 6:
                synchronizeCompleteData();
                // don't keep this button selected
                menuDrawerList.clearChoices();
                break;
            case 7:
                logout();
                break;
            default:
                throw new IndexOutOfBoundsException("No action defined for menu entry: " + position);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
