package de.symeda.sormas.app;

import android.accounts.AuthenticatorException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.net.ConnectException;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.caze.CasesActivity;
import de.symeda.sormas.app.contact.ContactsActivity;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.settings.SettingsActivity;
import de.symeda.sormas.app.util.AppUpdateController;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.LocationService;
import de.symeda.sormas.app.util.SyncCallback;

/**
 * Created by Mate Strysewske on 16.05.2017.
 */
public class LoginActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_login_activity_layout);

        if (!ConfigProvider.ensureDeviceEncryption(LoginActivity.this)) {
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LocationService.instance().validateGpsAccessAndEnabled(this)) {
            processLogin(true);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (LocationService.instance().validateGpsAccessAndEnabled(this)) {
            processLogin(true);
        }
    }

    private void openStartActivity() {
        Intent intent;
        if (ConfigProvider.getUser().hasUserRole(UserRole.CONTACT_OFFICER)) {
            intent = new Intent(LoginActivity.this, ContactsActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, CasesActivity.class);
        }
        startActivity(intent);
    }

    public void login(View view) {
        EditText usernameField = (EditText) findViewById(R.id.login_user);
        EditText passwordField = (EditText) findViewById(R.id.login_password);
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString();

        if (username.isEmpty()) {
            Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_empty_username, Snackbar.LENGTH_LONG).show();
        } else if(password.isEmpty()) {
            Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_empty_password, Snackbar.LENGTH_LONG).show();
        } else {
            ConfigProvider.setUsernameAndPassword(username, password);
            processLogin(true);
        }
    }

    private void processLogin(boolean checkLoginAndVersion) {
        // try to connect -> validates login and version
        if (checkLoginAndVersion && ConfigProvider.getUsername() != null) {
            boolean connected = false;
            try {
                RetroProvider.connect(getApplicationContext());
                connected = true;
                RetroProvider.matchAppAndApiVersions();
            } catch (AuthenticatorException e) {
                // clear login data if authentication failed
                ConfigProvider.clearUsernameAndPassword();
                Snackbar.make(findViewById(R.id.base_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
            } catch (RetroProvider.ApiVersionException e) {
                if (e.getAppUrl() != null && !e.getAppUrl().isEmpty()) {
                    AppUpdateController.getInstance().updateApp(this, e.getAppUrl(), e.getVersion(), connected || ConfigProvider.getUser() != null,
                            new Callback() {
                                @Override
                                public void call() {
                                    if (ConfigProvider.getUser() != null || RetroProvider.isConnected()) {
                                        processLogin(false);
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
            } catch (ConnectException e) {
                Snackbar.make(findViewById(R.id.base_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        }

        if (ConfigProvider.getUsername() != null) {
            // valid login
            if (ConfigProvider.getUser() == null) {
                // no user yet? sync...
                progressDialog = SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.Changes, LoginActivity.this, new SyncCallback() {
                    @Override
                    public void call(boolean syncFailed, String syncFailedMessage) {
                        if (ConfigProvider.getUser() != null) {
                            openStartActivity();
                        }
                    }
                });
            } else {
                openStartActivity();
            }
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

    private void closeApp() {
        Activity finishActivity = this;
        do {
            finishActivity.finish();
            finishActivity = finishActivity.getParent();
        } while (finishActivity != null);
    }

}
