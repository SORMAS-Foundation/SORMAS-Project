package de.symeda.sormas.app;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import de.symeda.sormas.app.caze.CaseNewActivity;
import de.symeda.sormas.app.caze.CasesActivity;
import de.symeda.sormas.app.caze.CasesListFilterAdapter;
import de.symeda.sormas.app.task.TasksActivity;
import de.symeda.sormas.app.user.UserActivity;
import de.symeda.sormas.app.util.SlidingTabLayout;

public abstract class SormasRootActivity extends AppCompatActivity {

    private ActionBarDrawerToggle menuDrawerToggle;
    private DrawerLayout menuDrawerLayout;
    private String[] menuTitles;
    private ListView menuDrawerList;

    private ViewPager pager;
    private CasesListFilterAdapter adapter;
    private SlidingTabLayout tabs;
    private CharSequence mainViewTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuTitles = getResources().getStringArray(R.array.main_side_menu);
        // init set first menuItem
        mainViewTitle = menuTitles[0];

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(mainViewTitle);
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
        getActionBar().setTitle(mainViewTitle);
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

    // go to cases overview
    public void showCasesView() {
        Intent intent = new Intent(this, CasesActivity.class);
        //intent.putExtra(Case.UUID, caze.getUuid());
        startActivity(intent);
    }

    public void showCaseNewView() {
        Intent intent = new Intent(this, CaseNewActivity.class);
        //intent.putExtra(Case.UUID, caze.getUuid());
        startActivity(intent);
    }

    // go to tasks overview
    public void showTasksView() {
        Intent intent = new Intent(this, TasksActivity.class);
        //intent.putExtra(Case.UUID, caze.getUuid());
        startActivity(intent);
    }

    // go to user
    public void showUserView() {
        Intent intent = new Intent(this, UserActivity.class);
        //intent.putExtra(Case.UUID, caze.getUuid());
        startActivity(intent);
    }



    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        // Cases
        if(position==0) {
            showCasesView();
        }
        // Tasks
        else if(position==1) {
            showTasksView();
        }
        // Users
        else if(position==2) {
            showUserView();
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }


}
