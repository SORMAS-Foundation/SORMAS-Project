package de.symeda.sormas.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.caze.CasesActivity;
import de.symeda.sormas.app.settings.SettingsActivity;

/**
 * Created by Mate Strysewske on 23.06.2017.
 */
public class EnterPinActivity extends AppCompatActivity {

    public static final String CALLED_FROM_SETTINGS = "calledFromSettings";

    public String lastEnteredPIN;
    public boolean calledFromSettings;
    public boolean confirmedCurrentPIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_enter_pin_activity_layout);

        Bundle params = getIntent().getExtras();
        if (params != null) {
            if (params.containsKey(CALLED_FROM_SETTINGS)) {
                calledFromSettings = params.getBoolean(CALLED_FROM_SETTINGS);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        final TextView headline = (TextView) findViewById(R.id.pin_headline_createOrEnter);
        final TextView hint = (TextView) findViewById(R.id.pin_hint_createOrEnter);

        if (calledFromSettings) {
            // Hide the forgot PIN button
            findViewById(R.id.action_forgotPIN).setVisibility(View.GONE);
        } else {
            // Hide back to settings button
            findViewById(R.id.action_backToSettings).setVisibility(View.GONE);
        }

        // Adjust headline and hint
        String savedPIN = ConfigProvider.getPin();
        if (savedPIN == null) {
            // Hide the forgot PIN button
            findViewById(R.id.action_forgotPIN).setVisibility(View.GONE);
            if (lastEnteredPIN == null) {
                headline.setText(R.string.headline_create_pin);
                hint.setText(R.string.hint_create_pin);
            } else {
                headline.setText(R.string.headline_create_pin);
                hint.setText(R.string.hint_create_pin_again);
            }
        } else {
            if (calledFromSettings) {
                if (confirmedCurrentPIN) {
                    if (lastEnteredPIN == null) {
                        headline.setText(R.string.headline_new_pin);
                        hint.setText(R.string.hint_new_pin);
                    } else {
                        headline.setText(R.string.headline_new_pin);
                        hint.setText(R.string.hint_new_pin_again);
                    }
                } else {
                    headline.setText(R.string.headline_enter_current_pin);
                    hint.setText(R.string.hint_enter_current_pin);
                }
            } else {
                headline.setText(R.string.headline_enter_pin);
                hint.setText(R.string.hint_enter_pin);
            }
        }

        final EditText char1 = (EditText) findViewById(R.id.pin_char1);
        final EditText char2 = (EditText) findViewById(R.id.pin_char2);
        final EditText char3 = (EditText) findViewById(R.id.pin_char3);
        final EditText char4 = (EditText) findViewById(R.id.pin_char4);
        final Button submitButton = (Button) findViewById(R.id.action_submit);

        // Clear the PIN entry fields in case the activity is resumed after an unsuccessful
        // submit attempt or when it has to be entered a second time
        char1.setText("");
        char2.setText("");
        char3.setText("");
        char4.setText("");

        char1.requestFocus();

        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }

        char1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if (!char1.getText().toString().isEmpty()) {
                    char2.requestFocus();
                }
            }
        });

        char2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if (!char2.getText().toString().isEmpty()) {
                    char3.requestFocus();
                }
            }
        });

        char3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if (!char3.getText().toString().isEmpty()) {
                    char4.requestFocus();
                }
            }
        });

        char4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if (!char4.getText().toString().isEmpty()) {
                    submitButton.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(char4.getWindowToken(), 0);
                }
            }
        });
    }

    public void submit(View view) {
        final EditText char1 = (EditText) findViewById(R.id.pin_char1);
        final EditText char2 = (EditText) findViewById(R.id.pin_char2);
        final EditText char3 = (EditText) findViewById(R.id.pin_char3);
        final EditText char4 = (EditText) findViewById(R.id.pin_char4);

        String enteredPIN = char1.getText().toString() + char2.getText().toString() +
                char3.getText().toString() + char4.getText().toString();

        if (enteredPIN.length() != 4) {
            Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_pin_too_short, Snackbar.LENGTH_LONG).show();
            onResume();
            return;
        }

        String savedPIN = ConfigProvider.getPin();

        if (savedPIN == null) {
            if (lastEnteredPIN == null) {
                // Store the entered PIN and restart the activity because the user has to
                // enter it twice
                lastEnteredPIN = enteredPIN;
                onResume();
            } else {
                // Check whether the two entered PINs match - if yes, store it and process the login,
                // otherwise display an error message and restart the activity
                if (lastEnteredPIN.equals(enteredPIN)) {
                    ConfigProvider.setPin(enteredPIN);
                    startMainActivity();
                } else {
                    lastEnteredPIN = null;
                    Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_pin_not_matching, Snackbar.LENGTH_LONG).show();
                    onResume();
                }
            }
        } else {
            if (calledFromSettings) {
                if (confirmedCurrentPIN) {
                    if (lastEnteredPIN == null) {
                        lastEnteredPIN = enteredPIN;
                        onResume();
                    } else {
                        if (lastEnteredPIN.equals(enteredPIN)) {
                            ConfigProvider.setPin(enteredPIN);
                            Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_pin_changed, Snackbar.LENGTH_LONG).show();
                            finish();
                        } else {
                            lastEnteredPIN = null;
                            Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_pin_not_matching, Snackbar.LENGTH_LONG).show();
                            onResume();
                        }
                    }
                } else {
                    if (enteredPIN.equals(savedPIN)) {
                        confirmedCurrentPIN = true;
                        onResume();
                    } else {
                        Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_pin_wrong, Snackbar.LENGTH_LONG).show();
                        onResume();
                    }
                }
            } else {
                // Process the login if the PIN is correct, otherwise display an error message and restart the activity
                if (enteredPIN.equals(savedPIN)) {
                    startMainActivity();
                } else {
                    Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_pin_wrong, Snackbar.LENGTH_LONG).show();
                    onResume();
                }
            }
        }
    }

    public void forgotPIN(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setPositiveButton(view.getContext().getResources().getText(R.string.action_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ConfigProvider.clearUsernameAndPassword();
                        ConfigProvider.clearPin();
                        Intent intent = new Intent(view.getContext(), LoginActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                }
        );
        builder.setNegativeButton(view.getContext().getResources().getText(R.string.action_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );

        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setTitle(view.getContext().getResources().getText(R.string.headline_reset_PIN).toString());
        dialog.setMessage(view.getContext().getResources().getText(R.string.infoText_resetPIN).toString());
        dialog.show();
    }

    public void backToSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }



    private void startMainActivity() {
        Intent intent = new Intent(this, CasesActivity.class);
        startActivity(intent);
    }

}
