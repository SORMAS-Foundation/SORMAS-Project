package de.symeda.sormas.app;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.util.UserHelper;

/**
 * Created by Orson on 03/12/2017.
 */

public abstract class BaseLandingActivity extends AbstractSormasActivity implements INotificationContext {

    public static final String TAG = BaseLandingActivity.class.getSimpleName();

    private ActionBarDrawerToggle menuDrawerToggle;
    private DrawerLayout menuDrawerLayout;
    private String[] menuTitles;
    private ListView menuDrawerList;

    private View rootView;
    private CharSequence mainViewTitle;
    private NavigationView navigationView;
    private TextView taskNotificationCounter;
    private TextView caseNotificationCounter;
    private TextView contactNotificationCounter;
    private TextView eventNotificationCounter;
    private TextView sampleNotificationCounter;


    private View fragmentFrame = null;
    private BaseLandingActivityFragment activeFragment;
    private MenuItem newMenu = null;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        initializeActivity(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void showFragmentView() {
        if (fragmentFrame != null)
            fragmentFrame.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideFragmentView() {
        if (fragmentFrame != null)
            fragmentFrame.setVisibility(View.GONE);
    }

    protected void initializeBaseActivity(Bundle savedInstanceState) {
        rootView = findViewById(R.id.base_layout);
        /*menuDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.main_navigation_view);
        navigationView.setNavigationItemSelectedListener(new MainMenuItemSelectedListener(this, menuDrawerLayout));

        taskNotificationCounter = (TextView) navigationView.getMenu().findItem(R.id.menu_item_tasks).getActionView().findViewById(R.id.main_menu_notification_counter);
        caseNotificationCounter = (TextView) navigationView.getMenu().findItem(R.id.menu_item_cases).getActionView().findViewById(R.id.main_menu_notification_counter);
        contactNotificationCounter = (TextView) navigationView.getMenu().findItem(R.id.menu_item_contacts).getActionView().findViewById(R.id.main_menu_notification_counter);
        eventNotificationCounter = (TextView) navigationView.getMenu().findItem(R.id.menu_item_events).getActionView().findViewById(R.id.main_menu_notification_counter);
        sampleNotificationCounter = (TextView) navigationView.getMenu().findItem(R.id.menu_item_samples).getActionView().findViewById(R.id.main_menu_notification_counter);*/

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getIntent().getExtras();
        initializeActivity(arguments);

        /*if (setHomeAsUpIndicator())
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_blue_36dp);

        setupDrawer(navigationView);*/

        fragmentFrame = findViewById(R.id.fragment_frame);
        if (fragmentFrame != null) {
            try {
                if (savedInstanceState == null) {
                    replaceFragment(getActiveLandingFragment());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract void initializeActivity(Bundle arguments);

    protected boolean setHomeAsUpIndicator() {
        return true;
    }

    public abstract BaseLandingActivityFragment getActiveLandingFragment() throws IllegalAccessException, InstantiationException;

    public void replaceFragment(BaseLandingActivityFragment f) {
        activeFragment = f;

        if (activeFragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            activeFragment.setArguments(getIntent().getExtras());
            ft.replace(R.id.fragment_frame, activeFragment);

            if (activeFragment.getFragmentMenuIndex() > 0)
                ft.addToBackStack(null);

            ft.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (activeFragment != null)
            activeFragment.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //outState.putInt(KEY_PAGE, currentTab);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.landing_action_bar, menu);

        newMenu = menu.findItem(R.id.action_new);

        processActionbarMenu();

        return true;
    }

    private void processActionbarMenu() {
        if (activeFragment == null)
            return;

        if (newMenu != null)
            newMenu.setVisible(activeFragment.showNewAction());
    }

    public MenuItem getNewMenu() {
        return newMenu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*if (menuDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mainViewTitle = title;
        String userRole = "";
        /*if (ConfigProvider.getUser()!=null && ConfigProvider.getUser().getUserRole() !=null) {
            userRole = " - " + ConfigProvider.getUser().getUserRole().toShortString();
        }*/
        getSupportActionBar().setTitle(mainViewTitle + userRole);
    }

    private void setupDrawer(NavigationView navView) {
        if (navView != null) {
            View headerView = navView.getHeaderView(0);

            if (headerView == null)
                return;

            TextView userName = (TextView)headerView.findViewById(R.id.userFullName);
            TextView userRole = (TextView)headerView.findViewById(R.id.userRole);

            if (userName == null)
                return;

            if (userRole == null)
                return;

            User user = ConfigProvider.getUser();

            if (user == null)
                return;
            else {
                userName.setText(R.string.userNamePlaceholder);
                userRole.setText(R.string.userRolePlaceholder);
            }

            userName.setText(user.getLastName() + " " + user.getFirstName());
            userRole.setText(UserHelper.getUserRole(user));


            Menu menuNav = navView.getMenu();

            MenuItem dashboardMenu = menuNav.findItem(R.id.menu_item_dashboard);
            MenuItem taskMenu = menuNav.findItem(R.id.menu_item_tasks);
            MenuItem caseMenu = menuNav.findItem(R.id.menu_item_cases);
            MenuItem contactMenu = menuNav.findItem(R.id.menu_item_contacts);
            MenuItem eventMenu = menuNav.findItem(R.id.menu_item_events);
            MenuItem sampleMenu = menuNav.findItem(R.id.menu_item_samples);
            MenuItem reportMenu = menuNav.findItem(R.id.menu_item_reports);

            if (taskMenu != null)
                taskMenu.setVisible(user.hasUserRight(UserRight.TASK_VIEW));

            if (caseMenu != null)
                caseMenu.setVisible(user.hasUserRight(UserRight.CASE_VIEW));

            if (sampleMenu != null)
                sampleMenu.setVisible(user.hasUserRight(UserRight.SAMPLE_VIEW));

            if (eventMenu != null)
                eventMenu.setVisible(user.hasUserRight(UserRight.EVENT_VIEW));

            if (contactMenu != null)
                contactMenu.setVisible(user.hasUserRight(UserRight.CONTACT_VIEW));

            if (reportMenu != null)
                reportMenu.setVisible(user.hasUserRight(UserRight.WEEKLYREPORT_VIEW));

        }

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
        menuDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_menu_blue_36dp);
        menuDrawerLayout.addDrawerListener(menuDrawerToggle);


        taskNotificationCounter.setText("3");
        caseNotificationCounter.setText("10");
        contactNotificationCounter.setText("7");
        eventNotificationCounter.setText("12");
        sampleNotificationCounter.setText("50");
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public View getRootView() {
        return rootView;
    }
}
