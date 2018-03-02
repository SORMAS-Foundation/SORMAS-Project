package de.symeda.sormas.app;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.analytics.Tracker;

import java.lang.ref.WeakReference;

import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.settings.SettingsActivity;
import de.symeda.sormas.app.util.Callback;

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

        setContentView(getRootActivityLayout());

        Drawable drawable = ContextCompat.getDrawable(this,
                R.drawable.selector_actionbar_back_button);

        final Toolbar toolbar = (Toolbar)findViewById(R.id.applicationToolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(drawable);
            //toolbar.setSubtitle("Hello");

            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(getResources().getString(getActivityTitle()));

        initializeBaseActivity(savedInstanceState);

        // Show the Enter Pin Activity if the user doesn't have access to the app
        if (!ConfigProvider.isAccessGranted()) {
            Intent intent = new Intent(this, EnterPinActivity.class);
            startActivity(intent);
            return;
        }
    }

    protected abstract void initializeBaseActivity(Bundle savedInstanceState);

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
        synchronizeData(SynchronizeDataAsync.SyncMode.Complete, true, refreshLayout == null, refreshLayout, null);
    }

    public void synchronizeChangedData() {
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly, true, refreshLayout == null, refreshLayout, null);
    }

    public void synchronizeChangedData(Callback callback) {
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly, true, refreshLayout == null, refreshLayout, callback);
    }

    public void synchronizeData(SynchronizeDataAsync.SyncMode syncMode, final boolean showResultSnackbar, final boolean showProgressDialog, final SwipeRefreshLayout swipeRefreshLayout, final Callback callback) {
        swipeRefreshLayout.setRefreshing(false);
        //TODO: Orson - Uncomment synchronizeData
        /*if (swipeRefreshLayout != null) {
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

            final TeboProgressDialog progressDialog;
            if (showProgressDialog) {
                progressDialog = TeboProgressDialog.show(this, getString(R.string.headline_synchronization),
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
                            Snackbar.make(findViewById(android.R.id.content), syncFailedMessage, Snackbar.LENGTH_LONG).show();
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
                Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        }*/
    }

    private void showConflictSnackbar() {
        /*Snackbar snackbar = Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_sync_conflict, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snackbar_open_synclog, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSyncLog();
            }
        });
        snackbar.show();*/
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }

    protected int getRootActivityLayout() {
        return R.layout.activity_root_layout;
    }

    protected abstract int getActivityTitle();
}
