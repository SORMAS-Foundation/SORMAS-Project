package de.symeda.sormas.app.settings;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import de.symeda.sormas.app.AbstractEditTabActivity;
import de.symeda.sormas.app.EnterPinActivity;
import de.symeda.sormas.app.LoginActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.SyncLogDialog;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.util.Callback;


public class SettingsActivity extends AbstractEditTabActivity {

    private SettingsForm settingsForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.sormas_default_activity_layout);

        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.main_menu_settings));

        // setting the fragment_frame
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        settingsForm = new SettingsForm();
        ft.add(R.id.fragment_frame, settingsForm).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        settingsForm.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_action_bar, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_options).getSubMenu().setGroupVisible(R.id.group_action_help,false);
        menu.setGroupVisible(R.id.group_action_add,false);
        menu.setGroupVisible(R.id.group_action_save,true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Settings don't have a parent -> go back instead of up
            case android.R.id.home:
                onBackPressed();
                return true;

            // Report problem button
            case R.id.action_report:
                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), null);
                AlertDialog dialog = userReportDialog.create();
                dialog.show();
                return true;

            case R.id.action_save:
                String serverUrl = settingsForm.getServerUrl();
                ConfigProvider.setServerRestUrl(serverUrl);
                onResume();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openSyncLog(View view) {
        SyncLogDialog syncLogDialog = new SyncLogDialog(this);
        syncLogDialog.show(this);
    }

    public void logout(View view) {
        final ProgressDialog progressDialog = ProgressDialog.show(this, getString(R.string.headline_logout),
                getString(R.string.hint_logout), true);

        new AsyncLogoutTask(new LogoutCallback() {
            @Override
            public void call(boolean hasUnmodifiedEntities) {
                progressDialog.dismiss();

                if (hasUnmodifiedEntities) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                    builder.setCancelable(true);
                    builder.setMessage(R.string.alert_unsynchronized_changes);
                    builder.setTitle(R.string.alert_title_unsynchronized_changes);
                    builder.setIcon(R.drawable.ic_perm_device_information_black_24dp);
                    AlertDialog dialog = builder.create();

                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.action_cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    processLogout();
                                }
                            }
                    );
                    dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.action_logout_anyway),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }
                    );

                    dialog.show();
                } else {
                    processLogout();
                }
            }
        }).execute();
    }

    public void changePIN(View view) {
        Intent intent = new Intent(this, EnterPinActivity.class);
        intent.putExtra(EnterPinActivity.CALLED_FROM_SETTINGS, true);
        startActivity(intent);
    }

    private void processLogout() {
        ConfigProvider.clearUsernameAndPassword();
        ConfigProvider.clearPin();
        ConfigProvider.setAccessGranted(false);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private class AsyncLogoutTask extends AsyncTask<Void, Void, Boolean> {
        private LogoutCallback callback;

        public AsyncLogoutTask(LogoutCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return DatabaseHelper.getCaseDao().isAnyADOModified() || DatabaseHelper.getContactDao().isAnyADOModified() ||
                    DatabaseHelper.getEventDao().isAnyADOModified() || DatabaseHelper.getEventParticipantDao().isAnyADOModified() ||
                    DatabaseHelper.getSampleDao().isAnyADOModified() || DatabaseHelper.getSampleTestDao().isAnyADOModified() ||
                    DatabaseHelper.getTaskDao().isAnyADOModified() || DatabaseHelper.getVisitDao().isAnyADOModified();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            callback.call(result);
        }
    }

    private interface LogoutCallback {
        void call(boolean hasUnmodifiedEntities);
    }

}
