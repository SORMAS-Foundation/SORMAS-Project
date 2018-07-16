package de.symeda.sormas.app.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.symeda.sormas.app.BaseLandingActivity;
import de.symeda.sormas.app.BaseLandingFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.dialog.UserReportDialog;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationPosition;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.util.AppUpdateController;

/**
 * Created by Orson on 03/11/2017.
 */

public class SettingsActivity extends BaseLandingActivity {

    //TODO: Create an interface to enforce Form Instantiation
    //TODO: Create an abstract method to set root layout
    //TODO: Create an interface to set Activity Title
    //TODO: Method to access the Form

    //TODO: Fix issues when you change address to api (works but behave strangely)

    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initializeActivity(Bundle arguments) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_action_menu, menu);
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
                if (ConfigProvider.getUser() == null) {
                    onBackPressed();
                    return true;
                }

                return super.onOptionsItemSelected(item);

            // Report problem button
            case R.id.action_report:
                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), null);
                userReportDialog.show(new Callback.IAction<AlertDialog>() {
                    @Override
                    public void call(AlertDialog result) {

                    }
                });
                return true;

            case R.id.action_save:
                String serverUrl = settingsFragment.getServerUrl();
                ConfigProvider.setServerRestUrl(serverUrl);
                onResume();

                NotificationHelper.showNotification(this, NotificationPosition.BOTTOM, NotificationType.SUCCESS, R.string.notification_saved_settings);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public BaseLandingFragment getActiveLandingFragment() {
        if (settingsFragment == null)
            settingsFragment = new SettingsFragment();

        return settingsFragment;
    }

    @Override
    protected boolean isSubActivitiy() {
        return false;
    }

    protected int getActivityTitle() {
        return R.string.main_menu_settings;
    }

    @Override
    // Handles the result of the attempt to install a new app version - should be added to every activity that uses the UpdateAppDialog
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == AppUpdateController.INSTALL_RESULT) {
            switch (resultCode) {
                // Do nothing if the installation was successful
                case Activity.RESULT_OK:
                case Activity.RESULT_CANCELED:
                    break;
                // Everything else probably is an error
                default:
                    AppUpdateController.getInstance().handleInstallFailure();
                    break;
            }
        }
    }
}
