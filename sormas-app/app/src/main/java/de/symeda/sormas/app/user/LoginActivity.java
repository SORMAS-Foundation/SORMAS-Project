package de.symeda.sormas.app.user;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.util.Base64;
import android.widget.EditText;

import com.google.android.gms.analytics.Tracker;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.caze.CaseEditActivity;
import de.symeda.sormas.app.caze.CasesActivity;
import de.symeda.sormas.app.util.ErrorReportingHelper;

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

        String username = ConfigProvider.getUsername();
        if (username != null) {
            User user = DatabaseHelper.getUserDao().getByUsername(username);
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
            try {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);
                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);
                KeyPairGeneratorSpec credentialsSpec = new KeyPairGeneratorSpec.Builder(this)
                        .setAlias("Credentials")
                        .setSubject(new X500Principal("CN=SORMAS, O=symeda, C=Germany"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                generator.initialize(credentialsSpec);
                generator.generateKeyPair();

                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry("Credentials", null);
                RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

                Cipher input = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
                input.init(Cipher.ENCRYPT_MODE, publicKey);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, input);
                cipherOutputStream.write(password.getBytes("UTF-8"));
                cipherOutputStream.close();
                byte[] vals = outputStream.toByteArray();

                ConfigProvider.setUsername(username);
                ConfigProvider.setPassword(Base64.encodeToString(vals, Base64.DEFAULT));

                User user = DatabaseHelper.getUserDao().getByUsername(username);
                ConfigProvider.setUser(user);

                Intent intent = new Intent(this, CasesActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(getClass().getName(), "Error while trying to write credentials to key store", e);
                Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_login_failed, Snackbar.LENGTH_LONG).show();
                ErrorReportingHelper.sendCaughtException(tracker, e, null, true);
            }
        }
    }

}
