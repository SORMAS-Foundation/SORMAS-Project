package de.symeda.sormas.app;

import android.accounts.AuthenticatorException;
import android.app.ProgressDialog;
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

import com.google.android.gms.analytics.Tracker;

import java.lang.ref.WeakReference;
import java.net.ConnectException;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.synclog.SyncLogDao;
import de.symeda.sormas.app.component.SyncLogDialog;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.SlidingTabLayout;
import de.symeda.sormas.app.util.SyncCallback;

public abstract class AbstractSormasActivity extends AppCompatActivity {

    protected Tracker tracker;

    protected boolean isUserNeeded() {
        return true;
    }
    public boolean isEditing() {
        return false;
    }

    private static WeakReference<AbstractSormasActivity> activeActivity;

    public static AbstractSormasActivity getActiveActivity() {
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

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

    public void synchronizeCompleteData() {
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        synchronizeData(SynchronizeDataAsync.SyncMode.Complete, true, refreshLayout == null, refreshLayout);
    }

    public void synchronizeChangedData() {
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly, true, refreshLayout == null, refreshLayout);
    }

    public void synchronizeData(SynchronizeDataAsync.SyncMode syncMode, final boolean showResultSnackbar, final boolean showProgressDialog, final SwipeRefreshLayout swipeRefreshLayout) {

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
                    Snackbar.make(findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_LONG).show();
                    errorMessage = e.getMessage();
                }
                // switch to LoginActivity is done below
            } catch (RetroProvider.ApiVersionException e) {
                if (showResultSnackbar) {
                    Snackbar.make(findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_LONG).show();
                    errorMessage = e.getMessage();
                }
            } catch (ConnectException e) {
                if (showResultSnackbar) {
                    Snackbar.make(findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_LONG).show();
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

            final ProgressDialog progressDialog;
            if (showProgressDialog) {
                progressDialog = ProgressDialog.show(this, getString(R.string.headline_synchronization),
                        getString(R.string.hint_synchronization), true);
            } else {
                progressDialog = null;
            }

            SynchronizeDataAsync.call(syncMode, getApplicationContext(), new SyncCallback() {
                @Override
                public void call(boolean syncFailed) {

                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (progressDialog != null) {
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
                Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_LONG).show();
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
