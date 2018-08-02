package de.symeda.sormas.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.Tracker;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.synclog.SyncLogDao;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.dialog.UserReportDialog;
import de.symeda.sormas.app.component.menu.PageMenuControl;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.NotImplementedException;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.enumeration.StatusElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.login.EnterPinActivity;
import de.symeda.sormas.app.login.LoginActivity;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.settings.SettingsActivity;
import de.symeda.sormas.app.util.Bundler;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.NavigationHelper;
import de.symeda.sormas.app.util.SyncCallback;
import de.symeda.sormas.app.util.UserHelper;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

public abstract class BaseActivity extends AppCompatActivity implements NotificationContext {

    public static final String TAG = BaseActivity.class.getSimpleName();

    protected Tracker tracker;

    private View rootView;
    private ProgressBar preloader;
    private View fragmentFrame;

    // title & status
    private View applicationTitleBar = null;
    private View statusFrame = null;

    // footer menu
    private PageMenuControl pageMenu = null;
    private List<PageMenuItem> pageItems = new ArrayList();
    private PageMenuItem activePageItem = null;
    private int activePageKey = 0;
    private boolean finishInsteadOfUpNav;

    private ActionBarDrawerToggle menuDrawerToggle;
    private DrawerLayout menuDrawerLayout;
    private NavigationView navigationView;
    private TextView taskNotificationCounter;
    private TextView caseNotificationCounter;
    private TextView contactNotificationCounter;
    private TextView eventNotificationCounter;
    private TextView sampleNotificationCounter;

    private ProgressDialog progressDialog = null;

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

    protected static Bundler buildBundle(int activePageKey) {
        return new Bundler().setActivePageKey(activePageKey);
    }

    public static <TActivity extends BaseActivity> void startActivity(Context context, Class<TActivity> toActivity, Bundler bundler) {
        Intent intent = new Intent(context, toActivity);
        intent.putExtras(bundler.get());
        context.startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        new Bundler(outState).setActivePageKey(activePageKey).setFinishInsteadOfUpNav(finishInsteadOfUpNav);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SormasApplication application = (SormasApplication) getApplication();
        tracker = application.getDefaultTracker();

        // Show the Enter Pin Activity if the user doesn't have access to the app
        if (!ConfigProvider.isAccessGranted()) {
            Intent intent = new Intent(this, EnterPinActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (savedInstanceState == null) {
            savedInstanceState = getIntent().getExtras();
        }

        Bundler bundler = new Bundler(savedInstanceState);
        activePageKey = bundler.getActivePageKey();
        finishInsteadOfUpNav = bundler.isFinishInsteadOfUpNav();

        setContentView(getRootActivityLayout());

        rootView = findViewById(R.id.base_layout);
        preloader = (ProgressBar) findViewById(R.id.preloader);
        fragmentFrame = findViewById(R.id.fragment_frame);
        applicationTitleBar = findViewById(R.id.applicationTitleBar);
        statusFrame = findViewById(R.id.statusFrame);

        pageMenu = (PageMenuControl) findViewById(R.id.landingPageMenuControl);
        if (pageMenu != null) {
            pageMenu.setPageMenuClickListener(new PageMenuControl.PageMenuClickListener() {
                @Override
                public boolean onPageMenuClick(AdapterView<?> parent, View view, PageMenuItem menuItem, int position, long id) throws IllegalAccessException, InstantiationException {
                    return setActivePage(menuItem);
                }
            });
            pageMenu.setPageMenuInititalSelectionProvider(new PageMenuControl.PageMenuInitialSelectionProvider() {
                @Override
                public PageMenuItem getInititalSelectedPageMenuItem(List<PageMenuItem> menuList) {
                    return initPageMenuAndGetInitialSelection(menuList);
                }
            });
        }

        Drawable drawable = ContextCompat.getDrawable(this,
                R.drawable.selector_actionbar_back_button);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.applicationToolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(drawable);

            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(getResources().getString(getActivityTitle()));

        preSetupDrawer(savedInstanceState);
        onCreateInner(savedInstanceState);

        if (!isSubActivitiy())
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_blue_36dp);

        setupDrawer(navigationView);
    }


    protected abstract void onCreateInner(Bundle savedInstanceState);

    @Override
    protected void onResume() {
        activeActivity = new WeakReference<>(this);
        super.onResume();

        // Show the Enter Pin Activity if the user doesn't have access to the app
        if (!ConfigProvider.isAccessGranted()) {
            Intent intent = new Intent(this, EnterPinActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (applicationTitleBar != null && isShowTitleBar()) {
            applicationTitleBar.setVisibility(View.VISIBLE);
            updateStatusFrame();
        }
        updatePageMenu();
    }

    protected void updateStatusFrame() {
        if (statusFrame != null) {
            if (getPageStatus() != null) {
                Context statusFrameContext = statusFrame.getContext();

                Drawable drw = (Drawable) ContextCompat.getDrawable(statusFrameContext, R.drawable.indicator_status_circle);
                drw.setColorFilter(statusFrameContext.getResources().getColor(getStatusColorResource()), PorterDuff.Mode.SRC);

                TextView txtStatusName = (TextView) statusFrame.findViewById(R.id.txtStatusName);
                ImageView imgStatus = (ImageView) statusFrame.findViewById(R.id.statusIcon);


                txtStatusName.setText(getStatusName());
                imgStatus.setBackground(drw);

                statusFrame.setVisibility(View.VISIBLE);
            } else {
                statusFrame.setVisibility(View.GONE);
            }
        }
    }

    protected void updatePageMenu() {
        List<PageMenuItem> menuItems = getPageMenuData();
        if (pageMenu != null && menuItems != null) {
            ensureFabHiddenOnSoftKeyboardShown(pageMenu);
            pageMenu.hide();
            pageMenu.setMenuData(menuItems);
        }
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

    private void preSetupDrawer(Bundle savedInstanceState) {

        menuDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.main_navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.menu_item_dashboard) {
                    NavigationHelper.goToDashboard(getContext());
                } else if (id == R.id.menu_item_tasks) {
                    NavigationHelper.goToTasks(getContext());
                } else if (id == R.id.menu_item_cases) {
                    NavigationHelper.goToCases(getContext());
                } else if (id == R.id.menu_item_contacts) {
                    NavigationHelper.goToContacts(getContext());
                } else if (id == R.id.menu_item_events) {
                    NavigationHelper.goToEvents(getContext());
                } else if (id == R.id.menu_item_samples) {
                    NavigationHelper.goToSamples(getContext());
                } else if (id == R.id.menu_item_reports) {
                    NavigationHelper.goToReports(getContext());
                }

                // necessary to prevent the drawer from staying open when the same entry is selected
                menuDrawerLayout.closeDrawers();

                return true;
            }
        });

        taskNotificationCounter = (TextView) navigationView.getMenu().findItem(R.id.menu_item_tasks).getActionView().findViewById(R.id.main_menu_notification_counter);
        caseNotificationCounter = (TextView) navigationView.getMenu().findItem(R.id.menu_item_cases).getActionView().findViewById(R.id.main_menu_notification_counter);
        contactNotificationCounter = (TextView) navigationView.getMenu().findItem(R.id.menu_item_contacts).getActionView().findViewById(R.id.main_menu_notification_counter);
        eventNotificationCounter = (TextView) navigationView.getMenu().findItem(R.id.menu_item_events).getActionView().findViewById(R.id.main_menu_notification_counter);
        sampleNotificationCounter = (TextView) navigationView.getMenu().findItem(R.id.menu_item_samples).getActionView().findViewById(R.id.main_menu_notification_counter);
    }

    protected abstract boolean isSubActivitiy();

    public boolean onOptionsItemSelected(MenuItem item) {

        if (!isSubActivitiy()
                && menuDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                if (finishInsteadOfUpNav) {
                    finish();
                } else {
                    NavigationHelper.navigateUpFrom(this);
                }
                return true;

            case R.id.action_sync:
                synchronizeChangedData();
                return true;

            case R.id.action_new:
                goToNewView();
                return true;

            case R.id.action_readAll:
                // TODO
                return true;

            // Report problem button
            case R.id.action_report:
                UserReportDialog userReportDialog = new UserReportDialog(this, getClass().getSimpleName(), null);
                userReportDialog.show(null);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawer(NavigationView navView) {
        if (navView != null) {
            View headerView = navView.getHeaderView(0);

            if (headerView == null)
                return;

            TextView userName = (TextView) headerView.findViewById(R.id.userFullName);
            TextView userRole = (TextView) headerView.findViewById(R.id.userRole);

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

    public List<PageMenuItem> getPageMenuData() {
        return null;
    }

    protected boolean setActivePage(PageMenuItem menuItem) {
        // Validate edit activities and don't allow page change when there are errors
        if (this instanceof BaseEditActivity) {
            try {
                FragmentValidator.validate(getContext(), ((BaseEditActivity) this).getActiveFragment().getContentBinding());
            } catch (ValidationException e) {
                NotificationHelper.showNotification(this, ERROR, e.getMessage());
                return false;
            }
        }

        activePageItem = menuItem;
        activePageKey = activePageItem.getKey();
        return openPage(activePageItem);
    }

    public PageMenuItem getActivePage() {
        return activePageItem;
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

    public boolean isShowTitleBar() {
        return true;
    }

    public abstract Enum getPageStatus();

    public String getStatusName() {
        Enum pageStatus = getPageStatus();

        if (pageStatus != null) {
            StatusElaborator elaborator = StatusElaboratorFactory.getElaborator(pageStatus);
            if (elaborator != null)
                return elaborator.getFriendlyName(getContext());
        }

        return "";
    }

    public int getStatusColorResource() {
        Enum pageStatus = getPageStatus();

        if (pageStatus != null) {
            StatusElaborator elaborator = StatusElaboratorFactory.getElaborator(pageStatus);
            if (elaborator != null)
                return elaborator.getColorIndicatorResource();
        }

        return R.color.noColor;
    }

    public void synchronizeChangedData() {
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        synchronizeData(SynchronizeDataAsync.SyncMode.Changes, true, true, refreshLayout, null, null);
    }


    public void synchronizeData(final SynchronizeDataAsync.SyncMode syncMode, final boolean showUpgradePrompt, final boolean showProgressDialog, final SwipeRefreshLayout swipeRefreshLayout, final Callback resultCallback, final Callback beforeSyncCallback) {

        if (ConfigProvider.getUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        if (showProgressDialog) {
            if (progressDialog == null || !progressDialog.isShowing()) {
                progressDialog = ProgressDialog.show(this, getString(R.string.headline_synchronization),
                        getString(R.string.hint_synchronization), true);
            }
        } else {
            progressDialog = null;
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(true);
            }
        }

        if (!RetroProvider.isConnected()) {
            RetroProvider.connectAsyncHandled(this, showUpgradePrompt, false,
                    new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean result) {
                            if (Boolean.TRUE.equals(result)) {
                                // try again
                                synchronizeData(syncMode, showUpgradePrompt, showProgressDialog, swipeRefreshLayout, resultCallback, beforeSyncCallback);
                            } else {
                                if (swipeRefreshLayout != null) {
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                    progressDialog = null;
                                }
                            }
                        }
                    });
            return;
        }

        final SyncLogDao syncLogDao = DatabaseHelper.getSyncLogDao();
        final long syncLogCountBefore = syncLogDao.countOf();

        if (RetroProvider.isConnected()) {

            if (beforeSyncCallback != null) beforeSyncCallback.call();

            SynchronizeDataAsync.call(syncMode, getApplicationContext(), new SyncCallback() {
                @Override
                public void call(boolean syncFailed, String syncFailedMessage) {

                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                    BaseActivity.this.onResume();

                    long syncLogCountAfter = syncLogDao.countOf();

                    if (!syncFailed) {
                        if (syncLogCountAfter > syncLogCountBefore) {
                            showConflictSnackbar();
                        } else {
                            NotificationHelper.showNotification(BaseActivity.this, NotificationType.SUCCESS, R.string.snackbar_sync_success);
                        }
                    } else {
                        NotificationHelper.showNotification(BaseActivity.this, NotificationType.ERROR, syncFailedMessage);
                    }

                    if (resultCallback != null) resultCallback.call();
                }
            });
        } else {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

    private void showConflictSnackbar() {

        NotificationHelper.showNotification(BaseActivity.this, NotificationType.ERROR, R.string.snackbar_sync_conflict);

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

    @Override
    public View getRootView() {
        return rootView;
    }

    protected int getRootActivityLayout() {
        return R.layout.activity_root_layout;
    }

    protected abstract int getActivityTitle();

    protected abstract boolean openPage(PageMenuItem menuItem);

    public PageMenuItem initPageMenuAndGetInitialSelection(List<PageMenuItem> menuList) {
        this.pageItems = menuList;
        activePageItem = menuList.get(0);
        for (PageMenuItem m : menuList) {
            if (m.getKey() == activePageKey) {
                activePageItem = m;
            }
        }
        return activePageItem;
    }


    protected boolean goToNextPage() {
        if (activePageKey == pageItems.size() - 1)
            return false; // last page
        int newMenukey = activePageKey + 1;
        PageMenuItem pageItem = pageItems.get(newMenukey);
        pageMenu.markActiveMenuItem(pageItem);
        return setActivePage(pageItem);
    }

    private void ensureFabHiddenOnSoftKeyboardShown(final PageMenuControl landingPageMenuControl) {
        final View _rootView = getRootView();

        if (_rootView == null)
            return;

        _rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                _rootView.getWindowVisibleDisplayFrame(r);
                int heightDiff = _rootView.getRootView().getHeight() - (r.bottom - r.top);

                if (heightDiff > 100) {
                    if (landingPageMenuControl != null) {
                        landingPageMenuControl.hideAll();
                    }
                } else {
                    landingPageMenuControl.showFab();
                }
            }
        });
    }


}
