package de.symeda.sormas.app;

import android.accounts.AuthenticatorException;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.Tracker;

import java.lang.ref.WeakReference;
import java.net.ConnectException;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.synclog.SyncLogDao;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.core.NotImplementedException;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.login.LoginActivity;
import de.symeda.sormas.app.menu.MainMenuItemSelectedListener;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.settings.SettingsActivity;
import de.symeda.sormas.app.util.AppUpdateController;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.ConstantHelper;
import de.symeda.sormas.app.util.SyncCallback;
import de.symeda.sormas.app.util.UserHelper;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String TAG = BaseActivity.class.getSimpleName();

    protected Tracker tracker;

    private ProgressBar preloader;
    private View fragmentFrame;

    private ActionBarDrawerToggle menuDrawerToggle;
    private DrawerLayout menuDrawerLayout;
    private NavigationView navigationView;
    private TextView taskNotificationCounter;
    private TextView caseNotificationCounter;
    private TextView contactNotificationCounter;
    private TextView eventNotificationCounter;
    private TextView sampleNotificationCounter;

    private ProgressDialog progressDialog = null;

    protected boolean isUserNeeded() {
        return true;
    }
    public boolean isEditing() {
        return false;
    }

    private static WeakReference<BaseActivity> activeActivity;

    public static BaseActivity getActiveActivity() {
        if (activeActivity != null) {
            return activeActivity.get();
        }
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SormasApplication application = (SormasApplication) getApplication();
        tracker = application.getDefaultTracker();

        if (savedInstanceState == null) {
            savedInstanceState = getIntent().getBundleExtra(ConstantHelper.ARG_NAVIGATION_CAPSULE_INTENT_DATA);
        }

        // Show the Enter Pin Activity if the user doesn't have access to the app
        if (!ConfigProvider.isAccessGranted()) {
            Intent intent = new Intent(this, EnterPinActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(getRootActivityLayout());

        preloader = (ProgressBar)findViewById(R.id.preloader);
        fragmentFrame = findViewById(R.id.fragment_frame);

        SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);

        Drawable drawable = ContextCompat.getDrawable(this,
                R.drawable.selector_actionbar_back_button);

        final Toolbar toolbar = (Toolbar)findViewById(R.id.applicationToolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(drawable);

            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(getResources().getString(getActivityTitle()));

        preSetupDrawer(savedInstanceState);
        onCreateBaseActivity(savedInstanceState);

        if (setHomeAsUpIndicator())
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_blue_36dp);

        setupDrawer(navigationView);
    }

    private void preSetupDrawer(Bundle savedInstanceState) {

        menuDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.main_navigation_view);
        navigationView.setNavigationItemSelectedListener(new MainMenuItemSelectedListener(this, menuDrawerLayout));

        taskNotificationCounter = (TextView) navigationView.getMenu().findItem(R.id.menu_item_tasks).getActionView().findViewById(R.id.main_menu_notification_counter);
        caseNotificationCounter = (TextView) navigationView.getMenu().findItem(R.id.menu_item_cases).getActionView().findViewById(R.id.main_menu_notification_counter);
        contactNotificationCounter = (TextView) navigationView.getMenu().findItem(R.id.menu_item_contacts).getActionView().findViewById(R.id.main_menu_notification_counter);
        eventNotificationCounter = (TextView) navigationView.getMenu().findItem(R.id.menu_item_events).getActionView().findViewById(R.id.main_menu_notification_counter);
        sampleNotificationCounter = (TextView) navigationView.getMenu().findItem(R.id.menu_item_samples).getActionView().findViewById(R.id.main_menu_notification_counter);
    }


    protected boolean setHomeAsUpIndicator() {
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (menuDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

            // TODO implement dashboard
            if (dashboardMenu != null)
                dashboardMenu.setVisible(false);

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

        // TODO implement unread entity counters
        taskNotificationCounter.setVisibility(View.GONE);
        caseNotificationCounter.setVisibility(View.GONE);
        contactNotificationCounter.setVisibility(View.GONE);
        eventNotificationCounter.setVisibility(View.GONE);
        sampleNotificationCounter.setVisibility(View.GONE);
//        taskNotificationCounter.setText("3");
//        caseNotificationCounter.setText("10");
//        contactNotificationCounter.setText("7");
//        eventNotificationCounter.setText("12");
//        sampleNotificationCounter.setText("50");
    }

    public Context getContext() {
        return this;
    }

    public void showPreloader() {
        fragmentFrame.setVisibility(View.GONE);
        preloader.setVisibility(View.VISIBLE);
    }

    public void hidePreloader() {
        preloader.setVisibility(View.GONE);
        fragmentFrame.setVisibility(View.VISIBLE);
    }

    protected abstract void onCreateBaseActivity(Bundle savedInstanceState);

    @Override
    protected void onResume() {
        activeActivity = new WeakReference<>(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (activeActivity != null && activeActivity.get() == this)
            activeActivity = null;
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        super.onDestroy();
    }

    public void synchronizeCompleteData() {
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        synchronizeData(SynchronizeDataAsync.SyncMode.Complete, true, refreshLayout == null, true, refreshLayout, null);
    }

    public void synchronizeChangedData() {
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        synchronizeData(SynchronizeDataAsync.SyncMode.Changes, true, refreshLayout == null, true, refreshLayout, null);
    }

    public void synchronizeChangedData(Callback callback) {
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        synchronizeData(SynchronizeDataAsync.SyncMode.Changes, true, refreshLayout == null, false, refreshLayout, callback);
    }

    public void synchronizeData(final SynchronizeDataAsync.SyncMode syncMode, final boolean showResultSnackbar, final boolean showProgressDialog, boolean showUpgradePrompt, final SwipeRefreshLayout swipeRefreshLayout, final Callback callback) {

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }

        final SyncLogDao syncLogDao = DatabaseHelper.getSyncLogDao();
        final long syncLogCountBefore = syncLogDao.countOf();

        String errorMessage = "";

        if (!RetroProvider.isConnected()) {
            try {
                RetroProvider.connect(getApplicationContext());
            } catch (AuthenticatorException e) {
                if (showResultSnackbar) {
                    NotificationHelper.showNotification((NotificationContext) this, NotificationType.ERROR, e.getMessage());
                    errorMessage = e.getMessage();
                }
                // switch to LoginActivity is done below
            } catch (final RetroProvider.ApiVersionException e) {
                if (showUpgradePrompt && e.getAppUrl() != null && !e.getAppUrl().isEmpty()) {
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    AppUpdateController.getInstance().updateApp(this, e.getAppUrl(), e.getVersion(), true,
                            new Callback() {
                                @Override
                                public void call() {
                                    synchronizeData(syncMode, showResultSnackbar, showProgressDialog, false, swipeRefreshLayout, callback);
                                }
                            });
                    return;
                } else if (showResultSnackbar) {
                    NotificationHelper.showNotification((NotificationContext) this, NotificationType.ERROR, e.getMessage());
                    errorMessage = e.getMessage();
                }
            } catch (ConnectException e) {
                if (showResultSnackbar) {
                    NotificationHelper.showNotification((NotificationContext) this, NotificationType.ERROR, e.getMessage());
                    errorMessage = e.getMessage();
                }
            }
        }

        if (isUserNeeded() && ConfigProvider.getUser() == null) {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        if (RetroProvider.isConnected()) {

            if (showProgressDialog) {
                progressDialog = ProgressDialog.show(this, getString(R.string.headline_synchronization),
                        getString(R.string.hint_synchronization), true);
            } else {
                progressDialog = null;
            }

            SynchronizeDataAsync.call(syncMode, getApplicationContext(), new SyncCallback() {
                @Override
                public void call(boolean syncFailed, String syncFailedMessage) {

                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    if (getSupportFragmentManager().getFragments() != null) {
                        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                            if (fragment != null && fragment.isVisible()) {
                                fragment.onResume();
                            }
                        }
                    }

                    long syncLogCountAfter = syncLogDao.countOf();

                    if (showResultSnackbar) {
                        if (!syncFailed) {
                            if (syncLogCountAfter > syncLogCountBefore) {
                                showConflictSnackbar();
                            } else {
                                NotificationHelper.showNotification((NotificationContext) BaseActivity.this, NotificationType.SUCCESS, R.string.snackbar_sync_success);
                            }
                        } else {
                            NotificationHelper.showNotification((NotificationContext) BaseActivity.this, NotificationType.ERROR, syncFailedMessage);
                        }
                    } else {
                        if (syncLogCountAfter > syncLogCountBefore) {
                            showConflictSnackbar();
                        }
                    }

                    if (callback != null) {
                        callback.call();
                    }
                }
            });
        }
        else {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }

            if (showResultSnackbar) {
                NotificationHelper.showNotification((NotificationContext) BaseActivity.this, NotificationType.ERROR, errorMessage);
            }
        }
    }

    private void showConflictSnackbar() {

        NotificationHelper.showNotification((NotificationContext) BaseActivity.this, NotificationType.ERROR, R.string.snackbar_sync_conflict);

        // TODO allow user to open sync log from here
//        snackbar.setAction(R.string.snackbar_open_synclog, new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openSyncLog();
//            }
//        });
//        snackbar.show();
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }

    public void goToNewView() {
        throw new NotImplementedException("goToNewView");
    }

    protected int getRootActivityLayout() {
        return R.layout.activity_root_layout;
    }

    protected abstract int getActivityTitle();
}
