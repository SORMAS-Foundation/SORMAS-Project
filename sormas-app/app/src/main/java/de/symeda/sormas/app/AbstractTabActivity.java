package de.symeda.sormas.app;

import android.accounts.AuthenticatorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.net.ConnectException;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.synclog.SyncLogDao;
import de.symeda.sormas.app.component.SyncLogDialog;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.SlidingTabLayout;
import de.symeda.sormas.app.util.SyncCallback;

public abstract class AbstractTabActivity extends AppCompatActivity {

    public static final String KEY_PAGE = "page";

    protected ViewPager pager;
    protected SlidingTabLayout tabs;
    protected int currentTab = 0;

    protected boolean isUserNeeded() {
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void createTabViews(FragmentStatePagerAdapter adapter) {
        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assigning the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(false);

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
            @Override
            public void onPageSelected(int position) {
                setCurrentTab(position);
                invalidateOptionsMenu();
            }
        });
    }

    public void setCurrentTab(int currentTab) {
        this.currentTab = currentTab;
    }

    public void reloadTabs() {
        createTabViews((FragmentStatePagerAdapter) pager.getAdapter());
    }


    public void synchronizeCompleteData() {
        synchronizeData(SynchronizeDataAsync.SyncMode.Complete, true, (SwipeRefreshLayout) findViewById(R.id.swiperefresh));
    }

    public void synchronizeChangedData() {
        synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly, true, (SwipeRefreshLayout) findViewById(R.id.swiperefresh));
    }

    public void synchronizeData(SynchronizeDataAsync.SyncMode syncMode, final boolean showResultSnackbar, final SwipeRefreshLayout swipeRefreshLayout) {

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }

        final SyncLogDao syncLogDao = DatabaseHelper.getSyncLogDao();
        final long syncLogCountBefore = syncLogDao.countOf();

        if (!RetroProvider.isConnected()) {
            try {
                RetroProvider.connect(getApplicationContext());
            } catch (AuthenticatorException e) {
                if (showResultSnackbar) {
                    Snackbar.make(findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
                // switch to LoginActivity is done below
            } catch (RetroProvider.ApiVersionException e) {
                if (showResultSnackbar) {
                    Snackbar.make(findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            } catch (ConnectException e) {
                if (showResultSnackbar) {
                    Snackbar.make(findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_LONG).show();
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

            SynchronizeDataAsync.call(syncMode, getApplicationContext(), new SyncCallback() {
                @Override
                public void call(boolean syncFailed) {

                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
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
                                Snackbar.make(findViewById(android.R.id.content), R.string.snackbar_sync_success, Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), R.string.snackbar_sync_error, Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        if (syncLogCountAfter > syncLogCountBefore) {
                            showConflictSnackbar();
                        }
                    }
                }
            });
        }
        else {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }

            if (showResultSnackbar) {
                Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_no_connection, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void showConflictSnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_sync_conflict, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snackbar_open_synclog, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSyncLog();
            }
        });
        snackbar.show();
    }

    private void openSyncLog() {
        SyncLogDialog syncLogDialog = new SyncLogDialog(this);
        syncLogDialog.show(this);
    }

}
