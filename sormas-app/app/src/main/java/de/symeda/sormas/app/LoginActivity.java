package de.symeda.sormas.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import de.symeda.sormas.app.settings.SettingsActivity;
import de.symeda.sormas.app.util.ChangeLabelColorOnEditTextFocus;
import de.symeda.sormas.app.core.CompositeOnFocusChangeListener;
import de.symeda.sormas.app.component.ShowKeyboardOnEditTextClickHandler;
import de.symeda.sormas.app.component.ShowKeyboardOnEditTextFocus;

//import android.support.design.widget.Snackbar;

/**
 * Created by Orson on 29/10/2017.
 */
public class LoginActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private TextView lblUserName;
    private TextView lblPassword;
    private EditText txtUserName;
    private EditText txtPassword;
    private TextView btnChangeSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);

        CompositeOnFocusChangeListener txtUserNameListeners = new CompositeOnFocusChangeListener();
        CompositeOnFocusChangeListener txtPasswordListeners = new CompositeOnFocusChangeListener();
        ShowKeyboardOnEditTextFocus showKeyboardOnEditTextFocus = new ShowKeyboardOnEditTextFocus(LoginActivity.this);
        ShowKeyboardOnEditTextClickHandler showKeyboardOnEditTextClickHandler = new ShowKeyboardOnEditTextClickHandler(this);

        lblUserName = (TextView)findViewById(R.id.lblUserName);
        lblPassword = (TextView)findViewById(R.id.lblPassword);
        txtUserName = (EditText)findViewById(R.id.txtUserName);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        btnChangeSettings = (TextView)findViewById(R.id.btnChangeSettings);

        txtUserNameListeners.registerListener(showKeyboardOnEditTextFocus);
        txtUserNameListeners.registerListener(new ChangeLabelColorOnEditTextFocus(this, lblUserName));
        txtUserName.setOnFocusChangeListener(txtUserNameListeners);
        txtUserName.setOnClickListener(showKeyboardOnEditTextClickHandler);

        txtPasswordListeners.registerListener(showKeyboardOnEditTextFocus);
        txtPasswordListeners.registerListener(new ChangeLabelColorOnEditTextFocus(this, lblPassword));
        txtPassword.setOnFocusChangeListener(txtPasswordListeners);
        txtPassword.setOnClickListener(showKeyboardOnEditTextClickHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*if (hasGPSTurnedOnAndPermissionGranted()) {
            processLogin();
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        /*if (hasGPSTurnedOnAndPermissionGranted()) {
            processLogin();
        }*/
    }

    // TODO: login
    public void login(View view) {
        Intent intent = new Intent(LoginActivity.this, EnterPinActivity.class);
        startActivity(intent);
    }


    public void showSettingsView(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}
