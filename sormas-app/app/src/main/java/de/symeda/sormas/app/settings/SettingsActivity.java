package de.symeda.sormas.app.settings;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.app.AbstractEditTabActivity;
import de.symeda.sormas.app.EnterPinActivity;
import de.symeda.sormas.app.LoginActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.AbstractRootTabActivity;
import de.symeda.sormas.app.component.SyncLogDialog;
import de.symeda.sormas.app.component.UserReportDialog;


public class SettingsActivity extends AbstractEditTabActivity {

    private SettingsForm settingsForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.sormas_root_activity_layout);

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
        ConfigProvider.clearUsernameAndPassword();
        ConfigProvider.clearPin();
        ConfigProvider.setAccessGranted(false);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void changePIN(View view) {
        Intent intent = new Intent(this, EnterPinActivity.class);
        intent.putExtra(EnterPinActivity.CALLED_FROM_SETTINGS, true);
        startActivity(intent);
    }

}
