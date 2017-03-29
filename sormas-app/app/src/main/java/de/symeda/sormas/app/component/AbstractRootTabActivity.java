package de.symeda.sormas.app.component;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.caze.CaseNewActivity;
import de.symeda.sormas.app.caze.CasesActivity;
import de.symeda.sormas.app.contact.ContactsActivity;
import de.symeda.sormas.app.event.EventsActivity;
import de.symeda.sormas.app.sample.SamplesActivity;
import de.symeda.sormas.app.task.TasksActivity;
import de.symeda.sormas.app.user.UserActivity;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.SyncInfrastructureTask;

public abstract class AbstractRootTabActivity extends AbstractTabActivity {

    private ActionBarDrawerToggle menuDrawerToggle;
    private DrawerLayout menuDrawerLayout;
    private String[] menuTitles;
    private ListView menuDrawerList;

    private CharSequence mainViewTitle;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuTitles = new String[]{
                getResources().getString(R.string.main_menu_tasks),
                getResources().getString(R.string.main_menu_cases),
                getResources().getString(R.string.main_menu_contacts),
                getResources().getString(R.string.main_menu_events),
                getResources().getString(R.string.main_menu_samples),
                getResources().getString(R.string.main_menu_user),
                getResources().getString(R.string.main_menu_sync_all),
        };

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        menuDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        menuDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        menuDrawerList.setAdapter(
                new ArrayAdapter<String>(
                        this,
                        R.layout.drawer_list_item,
                        menuTitles));
        // Set the list's click listener
        menuDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);


        setupDrawer();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

    public void showUserView() {
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }

    public void syncAll() {
        SyncInfrastructureTask.syncAll(new Callback() {
                              @Override
                              public void call() {
                                  if (getSupportFragmentManager().getFragments() != null) {
                                      for (Fragment fragement : getSupportFragmentManager().getFragments()) {
                                          fragement.onResume();
                                      }
                                  }
                              }
                          }, AbstractRootTabActivity.this);
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
                showUserView();
                break;
            case 6:
                syncAll();
                // don't keep this button selected
                menuDrawerList.clearChoices();
                menuDrawerLayout.closeDrawers();
                break;
            default:
                throw new IndexOutOfBoundsException("No action defined for menu entry: " + position);
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("AbstractRootTab Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
            // necessary to prevent the drawer from staying open when the same entry is selected
            menuDrawerLayout.closeDrawers();
        }
    }


}
