package de.symeda.sormas.app.settings;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.symeda.sormas.app.BaseLandingActivity;
import de.symeda.sormas.app.BaseLandingFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationPosition;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.util.AppUpdateController;

public class SettingsActivity extends BaseLandingActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        MenuItem syncMenuItem = menu.findItem(R.id.action_sync);
        syncMenuItem.setVisible(false);

        return true;
    }

    @Override
    public SettingsFragment getActiveFragment() {
        return (SettingsFragment)super.getActiveFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (ConfigProvider.getUser() == null) {
                    // Settings don't have a parent -> go back instead of up
                    onBackPressed();
                    return true;
                }
                return super.onOptionsItemSelected(item);

            case R.id.action_save:
                String serverUrl = getActiveFragment().getServerUrl();
                ConfigProvider.setServerRestUrl(serverUrl);
                onResume();

                NotificationHelper.showNotification(this, NotificationPosition.BOTTOM, NotificationType.SUCCESS, R.string.notification_saved_settings);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Enum getPageStatus() {
        return null;
    }

    @Override
    public boolean isAccessNeeded() {
        return false;
    }

    @Override
    public BaseLandingFragment buildLandingFragment() {
        return new SettingsFragment();
    }

    /**
     * Is a sub-activity when the user needs to go back to the LoginActivity
     * @see SettingsActivity#onOptionsItemSelected(MenuItem)
     */
    @Override
    protected boolean isSubActivitiy() {
        return ConfigProvider.getUser() == null;
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
