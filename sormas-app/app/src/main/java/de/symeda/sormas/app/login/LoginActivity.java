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

package de.symeda.sormas.app.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.databinding.ActivityLoginLayoutBinding;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.settings.SettingsActivity;
import de.symeda.sormas.app.util.AppUpdateController;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.LocationService;
import de.symeda.sormas.app.util.NavigationHelper;
import de.symeda.sormas.app.util.SoftKeyboardHelper;
import de.symeda.sormas.app.util.SormasProperties;
import de.symeda.sormas.app.util.SyncCallback;

public class LoginActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, NotificationContext {

    private ActivityLoginLayoutBinding binding;

    private ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);

        if (!ConfigProvider.ensureDeviceEncryption(LoginActivity.this)) {
            return;
        }

        LoginViewModel loginViewModel = new LoginViewModel();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_layout);
        binding.setData(loginViewModel);

        binding.userUserName.setLiveValidationDisabled(true);
        binding.userPassword.setLiveValidationDisabled(true);

        boolean hasDefaultUser = !DataHelper.isNullOrEmpty(SormasProperties.getUserNameDefault())
                && !DataHelper.isNullOrEmpty(SormasProperties.getUserPasswordDefault());
        binding.btnLoginDefaultUser.setVisibility(hasDefaultUser ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (LocationService.instance().validateGpsAccessAndEnabled(this)) {
            processLogin(true);
        }

        if (ConfigProvider.getUser() != null) {
            binding.signInLayout.setVisibility(View.GONE);
        } else {
            binding.signInLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        SoftKeyboardHelper.hideKeyboard(this, binding.userPassword.getWindowToken());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (LocationService.instance().validateGpsAccessAndEnabled(this)) {
            processLogin(true);
        }
    }

    /**
     * Handles the result of the attempt to install a new app version.
     * Has to be added to every activity that uses the UpdateAppDialog
     */
    @Override
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

    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        super.onDestroy();
    }

    public void showSettingsView(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void loginDefaultUser(View view) {

        ConfigProvider.setUsernameAndPassword(SormasProperties.getUserNameDefault(), SormasProperties.getUserPasswordDefault());
        processLogin(true);
    }

    /**
     * Called by onClick
     */
    public void login(View view) {
        //Hide notification
        //NotificationHelper.hideNotification(binding);
        binding.userUserName.disableErrorState();
        binding.userPassword.disableErrorState();

        String userName = binding.userUserName.getValue().trim();
        String password = binding.userPassword.getValue();

        if (userName.isEmpty()) {
            binding.userUserName.enableErrorState(R.string.message_empty_username);
        } else if (password.isEmpty()) {
            binding.userPassword.enableErrorState(R.string.message_empty_password);
        } else {
            ConfigProvider.setUsernameAndPassword(userName, password);
            processLogin(true);
        }
    }

    private void processLogin(boolean checkLoginAndVersion) {

        if (progressDialog == null || !progressDialog.isShowing()) {
            boolean isInitialSync = DatabaseHelper.getFacilityDao().isEmpty();
            progressDialog = ProgressDialog.show(this, getString(R.string.heading_synchronization),
                    getString(isInitialSync ? R.string.info_initial_synchronization : R.string.info_synchronizing), true);
        }

        // try to connect -> validates login and version
        if (checkLoginAndVersion && ConfigProvider.getUsername() != null) {
            RetroProvider.connectAsyncHandled(this, true, true,
                    new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean result) {
                            if (ConfigProvider.getUser() != null || RetroProvider.isConnected()) {
                                processLogin(false);
                            } else {
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                    progressDialog = null;
                                }
                            }
                        }
                    });
            return;
        }

        boolean hideProgressDialog = true;
        if (ConfigProvider.getUsername() != null) {
            // valid login
            if (ConfigProvider.getUser() == null
                    || DatabaseHelper.getCaseDao().isEmpty()
                    || ConfigProvider.isRepullNeeded()) {
                // no user or data yet? sync...
                SynchronizeDataAsync.call(SynchronizeDataAsync.SyncMode.Changes, LoginActivity.this, new SyncCallback() {
                    @Override
                    public void call(boolean syncFailed, String syncFailedMessage) {
                        if (ConfigProvider.getUser() != null) {
                            openLandingActivity();
                        }
                    }
                });
                hideProgressDialog = false;
            } else {
                openLandingActivity();
            }
        }

        if (hideProgressDialog && progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void openLandingActivity() {

        if (ConfigProvider.getUser().hasUserRole(UserRole.CONTACT_OFFICER)) {
            NavigationHelper.goToContacts(LoginActivity.this);
        } else {
            NavigationHelper.goToCases(LoginActivity.this);
        }
    }

    @Override
    public View getRootView() {
        if (binding != null)
            return binding.getRoot();

        return null;
    }

}
