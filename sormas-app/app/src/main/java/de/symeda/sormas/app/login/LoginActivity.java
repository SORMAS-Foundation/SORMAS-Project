package de.symeda.sormas.app.login;

import android.accounts.AuthenticatorException;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.net.ConnectException;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.caze.landing.CasesLandingActivity;
import de.symeda.sormas.app.component.OnHideInputErrorListener;
import de.symeda.sormas.app.component.OnShowInputErrorListener;
import de.symeda.sormas.app.contact.landing.ContactsLandingActivity;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.ActivityLoginLayoutBinding;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.settings.SettingsActivity;
import de.symeda.sormas.app.util.AppUpdateController;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.LocationService;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.util.SyncCallback;

//import android.support.design.widget.Snackbar;

/**
 * Created by Orson on 29/10/2017.
 */
public class LoginActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, OnShowInputErrorListener, OnHideInputErrorListener, INotificationContext {
    private ActivityLoginLayoutBinding binding;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);

        if (!ConfigProvider.ensureDeviceEncryption(LoginActivity.this)) {
            return;
        }

        //TODO: Orson Remove this later
        processLogout();

        loginViewModel = new LoginViewModel();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_layout);

        //TODO: Orson Remove this later
        loginViewModel.setUserName("SanaObas");
        loginViewModel.setPassword("BZWhXQfXAMG2");

        binding.setData(loginViewModel);
        binding.setShowNotificationCallback(this);
        binding.setHideNotificationCallback(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (LocationService.instance().validateGpsAccessAndEnabled(this)) {
            processLogin(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (LocationService.instance().validateGpsAccessAndEnabled(this)) {
            processLogin(false);
        }
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

    public void login(View view) {
        //Hide notification
        //NotificationHelper.hideNotification(binding);
        binding.txtUserName.disableErrorState((INotificationContext)this);
        binding.txtPassword.disableErrorState((INotificationContext)this);

        String errorMessage = null;
        String userName = binding.txtUserName.getValue().trim();
        String password = binding.txtPassword.getValue();

        if (userName.isEmpty()) {
            binding.txtUserName.enableErrorState((INotificationContext)this, R.string.notification_empty_username);
        } else if (password.isEmpty()) {
            binding.txtPassword.enableErrorState((INotificationContext)this, R.string.notification_empty_password);
        } else {
            ConfigProvider.setUsernameAndPassword(userName, password);

            try {
                RetroProvider.connect(getApplicationContext());
            } catch (AuthenticatorException e) {
                // clearing login data is done below
                errorMessage = e.getMessage();
            } catch (RetroProvider.ApiVersionException e) {
                if (e.getAppUrl() != null) {
                    AppUpdateController.getInstance().updateApp(this, e.getAppUrl(), e.getVersion(), false,
                            new Callback() {
                                @Override
                                public void call() {
                                    closeApp();
                                }
                            });
                    return;
                } else {
                    errorMessage = e.getMessage();
                }
            } catch (ConnectException e) {
                errorMessage = e.getMessage();
            }

            if (errorMessage != null && !errorMessage.isEmpty())
                NotificationHelper.showNotification(binding, NotificationType.ERROR, errorMessage);

            if (!RetroProvider.isConnected()) {
                // we HAVE to be connected now. Otherwise reset the authentication data
                ConfigProvider.clearUsernameAndPassword();
            } else {
                SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.ChangesAndInfrastructure, LoginActivity.this, new SyncCallback() {
                    @Override
                    public void call(boolean syncFailed, String syncFailedMessage) {
                        // logged in?
                        if (ConfigProvider.getUser() != null) {
                            openLandingActivity();
                        }
                    }
                });
            }
        }

        /*Intent intent = new Intent(LoginActivity.this, EnterPinActivity.class);
        startActivity(intent);*/
    }

    public void showSettingsView(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void processLogin(boolean ignoreApiVersionConflict) {
        // try to connect -> validates login data
        if (ConfigProvider.getUsername() != null) {
            try {
                RetroProvider.connect(getApplicationContext());
            } catch (AuthenticatorException e) {
                // clear login data if authentication failed
                ConfigProvider.clearUsernameAndPassword();
                Snackbar.make(findViewById(R.id.base_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
            } catch (RetroProvider.ApiVersionException e) {
                if (!ignoreApiVersionConflict) {
                    if (e.getAppUrl() != null) {
                        AppUpdateController.getInstance().updateApp(this, e.getAppUrl(), e.getVersion(), ConfigProvider.getUser() != null,
                                new Callback() {
                                    @Override
                                    public void call() {
                                        if (ConfigProvider.getUser() != null) {
                                            processLogin(true);
                                        } else {
                                            closeApp();
                                        }
                                    }
                                }
                        );
                        return;
                    } else {
                        Snackbar.make(findViewById(R.id.base_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                }
            } catch (ConnectException e) {
                Snackbar.make(findViewById(R.id.base_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        }

        if (ConfigProvider.getUsername() != null) {
            // valid login
            if (ConfigProvider.getUser() == null) {
                // no user yet? sync...
                SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.ChangesAndInfrastructure, LoginActivity.this, new SyncCallback() {
                    @Override
                    public void call(boolean syncFailed, String syncFailedMessage) {
                        if (ConfigProvider.getUser() != null) {
                            openLandingActivity();
                        }
                    }
                });
            } else {
                openLandingActivity();
            }
        }
    }

    private void openLandingActivity() {
        Intent intent;
        if (ConfigProvider.getUser().hasUserRole(UserRole.CONTACT_OFFICER)) {
            intent = new Intent(LoginActivity.this, ContactsLandingActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, CasesLandingActivity.class);
        }
        startActivity(intent);
    }

    private void closeApp() {
        Activity finishActivity = this;
        do {
            finishActivity.finish();
            finishActivity = finishActivity.getParent();
        } while (finishActivity != null);
    }

    @Override
    public void onShowInputErrorShowing(View v, String message, boolean errorState) {
        NotificationHelper.showNotification(binding, NotificationType.ERROR, message);
    }

    @Override
    public void onInputErrorHiding(View v, boolean errorState) {
        NotificationHelper.hideNotification(binding);
    }

    //TODO: Orson Remove this later
    private void processLogout() {
        ConfigProvider.clearUsernameAndPassword();
        ConfigProvider.clearPin();
        ConfigProvider.setAccessGranted(false);
    }

    @Override
    public View getRootView() {
        if (binding != null)
            return binding.getRoot();

        return null;
    }
}
