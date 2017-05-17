package de.symeda.sormas.app.user;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.analytics.Tracker;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.caze.CaseEditActivity;
import de.symeda.sormas.app.caze.CasesActivity;

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

        // If device encryption is not active, show a non-cancelable alert that blocks app usage
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (dpm.getStorageEncryptionStatus() == DevicePolicyManager.ENCRYPTION_STATUS_INACTIVE ||
                dpm.getStorageEncryptionStatus() == DevicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage(R.string.alert_encryption);
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String prefUsername = sharedPref.getString("username", null);
        if (prefUsername != null) {
            User user = DatabaseHelper.getUserDao().getByUsername(prefUsername);
            ConfigProvider.setUser(user);
            Intent intent = new Intent(this, CasesActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("username", username);
            editor.apply();
            User user = DatabaseHelper.getUserDao().getByUsername(username);
            ConfigProvider.setUser(user);
            Intent intent = new Intent(this, CasesActivity.class);
            startActivity(intent);
        }

        // TODO use key store to encrypt an authentication token received by the server and decrypt it before every request
//        try {
//            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
//            keyStore.load(null);
//
//            if (keyStore.containsAlias("Username")) {
//                Intent intent = new Intent(this, CasesActivity.class);
//                startActivity(intent);
//            } else {
//                KeyPairGeneratorSpec usernameSpec = new KeyPairGeneratorSpec.Builder(this)
//                        .setAlias("Username")
//                        .setSerialNumber(BigInteger.ONE)
//                        .build();
//                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
//                generator.initialize(usernameSpec);
//
//                KeyPair keyPair = generator.generateKeyPair();
//
//                Intent intent = new Intent(this, CasesActivity.class);
//                startActivity(intent);
//            }
//        } catch (Exception e) {
//
//        }
    }

}
