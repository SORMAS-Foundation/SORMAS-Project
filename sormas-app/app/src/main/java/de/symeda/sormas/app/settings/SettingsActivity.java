package de.symeda.sormas.app.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import de.symeda.sormas.app.BaseLandingActivity;
import de.symeda.sormas.app.BaseLandingActivityFragment;
import de.symeda.sormas.app.EnterPinActivity;
import de.symeda.sormas.app.LoginActivity;
import de.symeda.sormas.app.R;

import de.symeda.sormas.app.backend.config.ConfigProvider;

/**
 * Created by Orson on 03/11/2017.
 */

public class SettingsActivity extends BaseLandingActivity {

    //TODO: Create an interface to enforce Form Instantiation
    //TODO: Create an abstract method to set root layout
    //TODO: Create an interface to set Activity Title
    //TODO: Method to access the Form

    private SettingsForm settingsForm;
    //private BaseLandingActivityFragment activeFragment = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setting the fragment_frame
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        settingsForm = new SettingsForm();
        ft.add(R.id.fragment_frame, settingsForm).commit();
    }

    @Override
    protected void initializeActivity(Bundle arguments) {

    }

    @Override
    public BaseLandingActivityFragment getActiveReadFragment() {
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (settingsForm != null)
            settingsForm.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        final Menu _menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_action_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

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
                /*UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), null);
                AlertDialog dialog = userReportDialog.create();
                dialog.show();*/
                return true;

            /*case R.id.option_menu_action_save:
                String serverUrl = settingsForm.getServerUrl();
                ConfigProvider.setServerRestUrl(serverUrl);
                onResume();
                return true;*/
        }
        return super.onOptionsItemSelected(item);
    }

    protected int getActivityTitle() {
        return R.string.main_menu_settings;
    }

    // TODO: openSyncLog
    public void openSyncLog(View view) {
        /*SyncLogDialog syncLogDialog = new SyncLogDialog(this);
        syncLogDialog.show(this);*/
    }

    public void logout(View view) {
        ConfigProvider.clearUsernameAndPassword();
        ConfigProvider.clearPin();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void changePIN(View view) {
        Intent intent = new Intent(this, EnterPinActivity.class);
        intent.putExtra(EnterPinActivity.CALLED_FROM_SETTINGS, true);
        startActivity(intent);
    }
}
