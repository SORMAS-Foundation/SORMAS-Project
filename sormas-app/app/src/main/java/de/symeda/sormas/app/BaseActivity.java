/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;

import org.apache.commons.lang3.StringUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.synclog.SyncLogDao;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.dialog.InfoDialog;
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
import de.symeda.sormas.app.util.NavigationHelper;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

public abstract class BaseActivity extends BaseLocalizedActivity implements NotificationContext {

    public static final String TAG = BaseActivity.class.getSimpleName();

    private View rootView;
    private ProgressBar preloader;
    private View fragmentFrame;

    // title & status
    private View applicationTitleBar = null;
    private View statusFrame = null;

    // footer menu
    protected PageMenuControl pageMenu = null;
    private List<PageMenuItem> pageItems = new ArrayList<>();
    private PageMenuItem activePageItem = null;
    private int activePagePosition;
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

    public boolean isAccessNeeded() { return true; }

    private static WeakReference<BaseActivity> activeActivity;

    public static BaseActivity getActiveActivity() {
        if (activeActivity != null) {
            return activeActivity.get();
        }
        return null;
    }

    protected static Bundler buildBundle(int activePageKey) {
        return new Bundler().setActivePagePosition(activePageKey);
    }

    public static <TActivity extends BaseActivity> void startActivity(Context context, Class<TActivity> toActivity, Bundler bundler) {
        Intent intent = new Intent(context, toActivity);
        intent.putExtras(bundler.get());
        context.startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        new Bundler(outState).setActivePagePosition(activePagePosition).setFinishInsteadOfUpNav(finishInsteadOfUpNav);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Show the Enter Pin Activity if the user doesn't have access to the app
        if (isAccessNeeded() && !ConfigProvider.isAccessGranted()) {
            Intent intent = new Intent(this, EnterPinActivity.class);
            startActivity(intent);
            finish();
        }

        if (savedInstanceState == null) {
            savedInstanceState = getIntent().getExtras();
        }

        Bundler bundler = new Bundler(savedInstanceState);
        activePagePosition = bundler.getActivePagePosition();
        finishInsteadOfUpNav = bundler.isFinishInsteadOfUpNav();

        setContentView(getRootActivityLayout());

        rootView = findViewById(R.id.base_layout);
        preloader = findViewById(R.id.preloader);
        fragmentFrame = findViewById(R.id.fragment_frame);
        applicationTitleBar = findViewById(R.id.applicationTitleBar);
        statusFrame = findViewById(R.id.statusFrame);

        pageMenu = findViewById(R.id.landingPageMenuControl);
        if (pageMenu != null) {
            pageMenu.setPageMenuItemClickCallback(this::setActivePage);
        }

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.selector_actionbar_back_button);

        final Toolbar toolbar = findViewById(R.id.applicationToolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(drawable);

            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(getResources().getString(getActivityTitle()));

        preSetupDrawer(savedInstanceState);
        onCreateInner(savedInstanceState);

        if (!isSubActivity()) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_blue_36dp);
        }

        setupDrawer(navigationView);
    }

    protected abstract void onCreateInner(Bundle savedInstanceState);

    @Override
    protected void onResume() {
        activeActivity = new WeakReference<>(this);
        super.onResume();

        // Show the Enter Pin Activity if the user doesn't have access to the app
        if (isAccessNeeded() && !ConfigProvider.isAccessGranted()) {
            Intent intent = new Intent(this, EnterPinActivity.class);
            startActivity(intent);
            finish();
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

            List<PageMenuItem> displayedMenuItems = new ArrayList<>();
            for (PageMenuItem item : menuItems) {
                if (item != null) {
                    displayedMenuItems.add(item);
                }
            }
            pageMenu.setMenuData(displayedMenuItems);
            pageMenu.markActiveMenuItem(initializePageMenu(menuItems));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (activeActivity != null && activeActivity.get() == this) {
            activeActivity = null;
        }
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
                } else if (id == R.id.menu_item_aggregate_reports) {
                    NavigationHelper.goToAggregateReports(getContext());
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

    protected abstract boolean isSubActivity();

    public boolean onOptionsItemSelected(MenuItem item) {
        if (!isSubActivity() && menuDrawerToggle.onOptionsItemSelected(item)) {
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

            case R.id.action_help:
                StringBuilder helpStringBuilder = new StringBuilder();
                extendHelpString(helpStringBuilder, (ViewGroup) this.findViewById(R.id.main_content));
                InfoDialog infoDialog = new InfoDialog(getContext(),
                        R.layout.dialog_screen_help_layout, Html.fromHtml(helpStringBuilder.toString()));
                infoDialog.show();
                return true;

            // Report problem button
            case R.id.action_report:
                UserReportDialog userReportDialog = new UserReportDialog(this, getClass().getSimpleName(), null);
                userReportDialog.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawer(NavigationView navView) {
        if (navView != null) {
            View headerView = navView.getHeaderView(0);

            if (headerView == null) {
                return;
            }

            TextView userName = (TextView) headerView.findViewById(R.id.userFullName);
            TextView userRole = (TextView) headerView.findViewById(R.id.userRole);

            if (userName == null)
                return;

            if (userRole == null)
                return;

            User user = ConfigProvider.getUser();

            if (user == null) {
                return;
            } else {
                userName.setText(R.string.value_no_username);
                userRole.setText(R.string.value_role_unassigned);
            }

            userName.setText(user.getLastName() + " " + user.getFirstName());
            userRole.setText(user.getUserRolesString());

            Menu menuNav = navView.getMenu();

            MenuItem dashboardMenu = menuNav.findItem(R.id.menu_item_dashboard);
            MenuItem taskMenu = menuNav.findItem(R.id.menu_item_tasks);
            MenuItem caseMenu = menuNav.findItem(R.id.menu_item_cases);
            MenuItem aggregateReportsMenu = menuNav.findItem(R.id.menu_item_aggregate_reports);
            MenuItem contactMenu = menuNav.findItem(R.id.menu_item_contacts);
            MenuItem eventMenu = menuNav.findItem(R.id.menu_item_events);
            MenuItem sampleMenu = menuNav.findItem(R.id.menu_item_samples);
            MenuItem reportMenu = menuNav.findItem(R.id.menu_item_reports);

            // TODO implement dashboard
            if (dashboardMenu != null)
                dashboardMenu.setVisible(false);

            if (taskMenu != null)
                taskMenu.setVisible(ConfigProvider.hasUserRight(UserRight.TASK_VIEW));

            if (caseMenu != null)
                caseMenu.setVisible(ConfigProvider.hasUserRight(UserRight.CASE_VIEW));

            if (aggregateReportsMenu != null)
                aggregateReportsMenu.setVisible(ConfigProvider.hasUserRight(UserRight.AGGREGATE_REPORT_EDIT) && !DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.AGGREGATE_REPORTING));

            if (sampleMenu != null)
                sampleMenu.setVisible(ConfigProvider.hasUserRight(UserRight.SAMPLE_VIEW));

            if (eventMenu != null)
                eventMenu.setVisible(ConfigProvider.hasUserRight(UserRight.EVENT_VIEW) && !DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.EVENT_SURVEILLANCE));

            if (contactMenu != null)
                contactMenu.setVisible(ConfigProvider.hasUserRight(UserRight.CONTACT_VIEW));

            if (reportMenu != null)
                reportMenu.setVisible(ConfigProvider.hasUserRight(UserRight.WEEKLYREPORT_VIEW) && !DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.WEEKLY_REPORTING));

        }

        menuDrawerToggle = new ActionBarDrawerToggle(
                this,
                menuDrawerLayout,
                R.string.caption_drawer_open,
                R.string.caption_drawer_close) {

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
        activePagePosition = activePageItem.getPosition();
        return openPage(activePageItem);
    }

    public PageMenuItem getActivePage() {
        return activePageItem;
    }

    public Context getContext() {
        return this;
    }

    public void showPreloader() {
        if (fragmentFrame != null) {
            fragmentFrame.setVisibility(View.GONE);
        }
        if (preloader != null) {
            preloader.setVisibility(View.VISIBLE);
        }
    }

    public void hidePreloader() {
        if (preloader != null) {
            preloader.setVisibility(View.GONE);
        }
        if (fragmentFrame != null) {
            fragmentFrame.setVisibility(View.VISIBLE);
        }
    }

    public void setPageMenuVisibility(boolean visible) {
        if (visible) {
            pageMenu.setVisibility(View.VISIBLE);
        } else {
            pageMenu.setVisibility(View.GONE);
        }
    }

    public boolean isShowTitleBar() {
        return true;
    }

    public abstract Enum getPageStatus();

    public String getStatusName() {
        Enum pageStatus = getPageStatus();

        if (pageStatus != null) {
            StatusElaborator elaborator = StatusElaboratorFactory.getElaborator(pageStatus);
            if (elaborator != null) {
                return elaborator.getFriendlyName(getContext());
            }
        }

        return "";
    }

    public int getStatusColorResource() {
        Enum pageStatus = getPageStatus();

        if (pageStatus != null) {
            StatusElaborator elaborator = StatusElaboratorFactory.getElaborator(pageStatus);
            if (elaborator != null) {
                return elaborator.getColorIndicatorResource();
            }
        }

        return R.color.noColor;
    }

    public void synchronizeChangedData() {
        SwipeRefreshLayout refreshLayout = findViewById(R.id.swiperefresh);
        synchronizeData(SynchronizeDataAsync.SyncMode.Changes, true, true, refreshLayout, getSynchronizeResultCallback(), null);
    }

    private boolean checkActiveUser() {
        if (ConfigProvider.getUser() == null) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            getContext().startActivity(intent);
            finish();
            return false;
        }
        return true;
    }

    public void synchronizeData(final SynchronizeDataAsync.SyncMode syncMode, final boolean showUpgradePrompt, final boolean showProgressDialog, final SwipeRefreshLayout swipeRefreshLayout, final Callback resultCallback, final Callback beforeSyncCallback) {
        if (!checkActiveUser()) return;

        if (RetroProvider.isConnectedOrConnecting()) {
            NotificationHelper.showNotification(BaseActivity.this, NotificationType.WARNING, "Background synchronization already in progress.");

            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            return;
        }

        if (showProgressDialog) {
            if (progressDialog == null || !progressDialog.isShowing()) {
                boolean isInitialSync = DatabaseHelper.getFacilityDao().isEmpty();
                progressDialog = ProgressDialog.show(this, getString(R.string.heading_synchronization),
                        getString(isInitialSync ? R.string.info_initial_synchronization : R.string.info_synchronizing), true);
            }
        } else {
            progressDialog = null;
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(true);
            }
        }

        final SyncLogDao syncLogDao = DatabaseHelper.getSyncLogDao();
        final long syncLogCountBefore = syncLogDao.countOf();

        RetroProvider.connectAsyncHandled(this, showUpgradePrompt, false,
                result -> {

                    if (Boolean.TRUE.equals(result)) {

                        if (beforeSyncCallback != null) beforeSyncCallback.call();

                        SynchronizeDataAsync.call(syncMode, getApplicationContext(), (syncFailed, syncFailedMessage) -> {

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
                                } else if (SynchronizeDataAsync.hasAnyUnsynchronizedData()) {
                                    NotificationHelper.showNotification(BaseActivity.this, NotificationType.WARNING, R.string.message_sync_not_synchronized);
                                } else {
                                    NotificationHelper.showNotification(BaseActivity.this, NotificationType.SUCCESS, R.string.message_sync_success);
                                }
                            } else {
                                NotificationHelper.showNotification(BaseActivity.this, NotificationType.ERROR, syncFailedMessage);
                                checkActiveUser();
                            }

                            RetroProvider.disconnect();

                            if (resultCallback != null) resultCallback.call();
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
                });
    }

    private void showConflictSnackbar() {
        NotificationHelper.showNotification(BaseActivity.this, NotificationType.ERROR, R.string.message_sync_conflict);

        // TODO allow user to open sync log from here
//        snackbar.setAction(R.string.snackbar_open_synclog, new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openSyncLog();
//            }
//        });
//        snackbar.show();
    }

    private void extendHelpString(StringBuilder sb, ViewGroup parent) {
        if (parent == null) return;
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ControlPropertyField && child.getVisibility() == View.VISIBLE) {
                ControlPropertyField propertyField = (ControlPropertyField) child;
                if (propertyField.getCaption() != null) {
                    sb.append("<b>").append(Html.escapeHtml(propertyField.getCaption())).append("</b>").append("<br>");
                    if (!StringUtils.isEmpty(propertyField.getDescription())) {
                        sb.append(Html.escapeHtml(propertyField.getDescription()));
                    } else {
                        sb.append(Html.escapeHtml("-"));
                    }
                    sb.append("<br><br>");
                }
            } else if (child instanceof ViewGroup) {
                extendHelpString(sb, (ViewGroup) child);
            }
        }
    }

    public void goToSettings(View view) {
        NavigationHelper.goToSettings(view.getContext());
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

    protected Callback getSynchronizeResultCallback() {
        return null;
    }

    private PageMenuItem initializePageMenu(List<PageMenuItem> menuList) {
        this.pageItems = menuList;
        activePageItem = menuList.get(0);
        for (int i = 0; i < menuList.size(); i++) {
            if (i == activePagePosition) {
                activePageItem = menuList.get(i);
            }
        }

        return activePageItem;
    }

    protected boolean goToNextPage() {
        if (activePagePosition >= pageItems.size() - 1) {
            return false; // last page
        }

        int newPosition = activePagePosition + 1;
        PageMenuItem pageItem = null;

        while (pageItem == null) {
            pageItem = pageItems.get(newPosition);
            newPosition++;

            if (newPosition >= pageItems.size() - 1) {
                return false;
            }
        }

        pageMenu.markActiveMenuItem(pageItem);
        return setActivePage(pageItem);
    }

    protected void ensureFabHiddenOnSoftKeyboardShown(final PageMenuControl landingPageMenuControl) {
        final View _rootView = getRootView();

        if (_rootView == null)
            return;

        _rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                // in android there is currently no way to track the software keyboard.
                // as a workaround we are checking the bottom height diff:
                // https://stackoverflow.com/questions/4745988/how-do-i-detect-if-software-keyboard-is-visible-on-android-device
                Rect r = new Rect();
                _rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = _rootView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                if (!(BaseActivity.this instanceof PagedBaseListActivity)) {
                    if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                        // keyboard is opened
                        if (landingPageMenuControl != null) {
                            landingPageMenuControl.hideAll();
                        }
                    }
                    else {
                        // keyboard is closed
                        if (landingPageMenuControl != null) {
                            landingPageMenuControl.showFab();
                        }
                    }
                } else {
                    if (landingPageMenuControl != null) {
                        landingPageMenuControl.showFab();
                    }
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

}
