package de.symeda.sormas.app;

import android.accounts.AuthenticatorException;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.analytics.Tracker;

import java.net.ConnectException;

import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.caze.CasesActivity;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.settings.SettingsActivity;

/**
 * Created by Mate Strysewske on 16.05.2017.
 */
public class LoginActivity extends AppCompatActivity {

    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login_activity_layout);

        SormasApplication application = (SormasApplication) getApplication();
        tracker = application.getDefaultTracker();

        // try to connect
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

        // already logged in?
        if (ConfigProvider.getUser() != null) {
            Intent intent = new Intent(this, EnterPinActivity.class);
            startActivity(intent);
        }
    }

    public void login(View view) {
        EditText usernameField = (EditText) findViewById(R.id.login_user);
        EditText passwordField = (EditText) findViewById(R.id.login_password);
        String username = usernameField.getText().toString();
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
                Snackbar.make(findViewById(R.id.base_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
            } catch (RetroProvider.ApiVersionException e) {
                Snackbar.make(findViewById(R.id.base_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
            } catch (ConnectException e) {
                Snackbar.make(findViewById(R.id.base_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
            }

            if (RetroProvider.isConnected()) {
                Intent intent = new Intent(this, EnterPinActivity.class);
                startActivity(intent);
            }
        }
    }

    public void showSettingsView(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
