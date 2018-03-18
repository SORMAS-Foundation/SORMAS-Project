package de.symeda.sormas.app;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.menu.MainMenuItemSelectedListener;

/**
 * Created by Orson on 03/12/2017.
 */

public abstract class BaseLandingActivity extends AbstractSormasActivity implements INotificationContext {

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
    private BaseLandingActivityFragment fragment;

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
        menuDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.main_navigation_view);
        navigationView.setNavigationItemSelectedListener(new MainMenuItemSelectedListener(this, menuDrawerLayout));

        taskNotificationCounter = (TextView) navigationView.getMenu().findItem(R.id.menu_item_tasks).getActionView().findViewById(R.id.main_menu_notification_counter);
        caseNotificationCounter = (TextView) navigationView.getMenu().findItem(R.id.menu_item_cases).getActionView().findViewById(R.id.main_menu_notification_counter);
        contactNotificationCounter = (TextView) navigationView.getMenu().findItem(R.id.menu_item_contacts).getActionView().findViewById(R.id.main_menu_notification_counter);
        eventNotificationCounter = (TextView) navigationView.getMenu().findItem(R.id.menu_item_events).getActionView().findViewById(R.id.main_menu_notification_counter);
        sampleNotificationCounter = (TextView) navigationView.getMenu().findItem(R.id.menu_item_samples).getActionView().findViewById(R.id.main_menu_notification_counter);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getIntent().getExtras();
        initializeActivity(arguments);

        if (setHomeAsUpIndicator())
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_blue_36dp);

        setupDrawer();

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
        fragment = f;

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            fragment.setArguments(getIntent().getExtras());
            ft.replace(R.id.fragment_frame, fragment);

            if (fragment.getFragmentMenuIndex() > 0)
                ft.addToBackStack(null);

            ft.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fragment != null)
            fragment.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //outState.putInt(KEY_PAGE, currentTab);
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
        /*if (ConfigProvider.getUser()!=null && ConfigProvider.getUser().getUserRole() !=null) {
            userRole = " - " + ConfigProvider.getUser().getUserRole().toShortString();
        }*/
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
        menuDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_menu_blue_36dp);
        menuDrawerLayout.addDrawerListener(menuDrawerToggle);


        taskNotificationCounter.setText("3");
        caseNotificationCounter.setText("10");
        contactNotificationCounter.setText("7");
        eventNotificationCounter.setText("12");
        sampleNotificationCounter.setText("50");
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        /*switch (position) {
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
                showReportsView();
                break;
            case 6:
                showSettingsView();
                // don't keep this button selected
                menuDrawerList.clearChoices();
                break;
            case 7:
                synchronizeCompleteData();
                // don't keep this button selected
                menuDrawerList.clearChoices();
                break;
            default:
                throw new IndexOutOfBoundsException("No action defined for menu entry: " + position);
        }*/
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    /*@Override
    public void showNotification(NotificationType type, String message) {
        NotificationHelper.showNotification(rootView, type, message);
    }

    @Override
    public void hideNotification() {
        NotificationHelper.hideNotification(rootView);
    }*/

    @Override
    public View getRootView() {
        return rootView;
    }
}
