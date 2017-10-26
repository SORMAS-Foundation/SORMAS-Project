package de.symeda.sormas.app;

import android.Manifest;
import android.accounts.AuthenticatorException;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.net.ConnectException;

import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.caze.CasesActivity;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.settings.SettingsActivity;
import de.symeda.sormas.app.util.LocationService;
import de.symeda.sormas.app.util.SyncCallback;

/**
 * Created by Mate Strysewske on 16.05.2017.
 */
public class LoginActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

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

        if (hasGPSTurnedOnAndPermissionGranted()) {
            processLogin();
        }
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

            try {
                RetroProvider.connect(getApplicationContext());
            } catch (AuthenticatorException e) {
                // clearing login data is done below
                Snackbar.make(findViewById(R.id.base_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
            } catch (RetroProvider.ApiVersionException e) {
                Snackbar.make(findViewById(R.id.base_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
            } catch (ConnectException e) {
                Snackbar.make(findViewById(R.id.base_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
            }

            if (!RetroProvider.isConnected()) {
                // we HAVE to be connected now. Otherwise reset the authentication data
                ConfigProvider.clearUsernameAndPassword();
            } else {
                SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.ChangesAndInfrastructure, LoginActivity.this, new SyncCallback() {
                    @Override
                    public void call(boolean syncFailed, String syncFailedMessage) {
                        // logged in?
                        if (ConfigProvider.getUser() != null) {
                            Intent intent = new Intent(LoginActivity.this, CasesActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        }
    }

    public void showSettingsView(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (hasGPSTurnedOnAndPermissionGranted()) {
            processLogin();
        }
    }

    private boolean hasGPSTurnedOnAndPermissionGranted() {

        if (!LocationService.instance().hasGPSAccess()) {
            AlertDialog requestPermissionDialog = buildRequestPermissionDialog();
            requestPermissionDialog.show();
            return false;
        }

        if (!LocationService.instance().hasGPSEnabled()) {
            AlertDialog turnOnGPSDialog = buildTurnOnGPSDialog();
            turnOnGPSDialog.show();
            return false;
        }

        return true;
    }

    private AlertDialog buildRequestPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(R.string.alert_gps_permission);
        builder.setTitle(R.string.alert_title_gps_permission);
        builder.setIcon(R.drawable.ic_perm_device_information_black_24dp);
        AlertDialog dialog = builder.create();
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.action_close_app),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Activity finishActivity = LoginActivity.this;
                        do {
                            finishActivity.finish();
                            finishActivity = finishActivity.getParent();
                        } while (finishActivity != null);
                    }
                }
        );
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.action_allow_gps_access),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 9999);
                    }
                }
        );

        return dialog;
    }

    private AlertDialog buildTurnOnGPSDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(R.string.alert_gps);
        builder.setTitle(R.string.alert_title_gps);
        builder.setIcon(R.drawable.ic_location_on_black_24dp);
        AlertDialog dialog = builder.create();
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.action_close_app),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Activity finishActivity = LoginActivity.this;
                        do {
                            finishActivity.finish();
                            finishActivity = finishActivity.getParent();
                        } while (finishActivity != null);
                    }
                }
        );
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.action_turn_on_gps),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent gpsOptionsIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(gpsOptionsIntent);
                    }
                }
        );

        return dialog;
    }

    private void processLogin() {
        // try to connect -> validates login data
        if (ConfigProvider.getUsername() != null) {
            try {
                RetroProvider.connect(getApplicationContext());
            } catch (AuthenticatorException e) {
                // clear login data if authentication failed
                ConfigProvider.clearUsernameAndPassword();
                Snackbar.make(findViewById(R.id.base_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
            } catch (RetroProvider.ApiVersionException e) {
                Snackbar.make(findViewById(R.id.base_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
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
                            Intent intent = new Intent(LoginActivity.this, CasesActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
            else {
                Intent intent = new Intent(LoginActivity.this, CasesActivity.class);
                startActivity(intent);
            }
        }
    }

}
