package de.symeda.sormas.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.caze.CasesActivity;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.settings.SettingsActivity;
import de.symeda.sormas.app.util.SyncCallback;

/**
 * Created by Mate Strysewske on 23.06.2017.
 */
public class EnterPinActivity extends AppCompatActivity {

    public static final String CALLED_FROM_SETTINGS = "calledFromSettings";

    private String lastEnteredPIN;
    private boolean calledFromSettings;
    private boolean confirmedCurrentPIN;
    private boolean triedAgain;
    private EditText[] pinFields;

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

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        // sync...
        SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.ChangesAndInfrastructure, EnterPinActivity.this, null);
    }

    @Override
    public void onResume() {
        super.onResume();

        final TextView headline = (TextView) findViewById(R.id.pin_headline_createOrEnter);
        final TextView hint = (TextView) findViewById(R.id.pin_hint_createOrEnter);

        // Hide back to settings button
        findViewById(R.id.action_backToSettings).setVisibility(calledFromSettings ? View.VISIBLE : View.GONE);
        // Hide the forgot PIN button?
        findViewById(R.id.action_forgotPIN).setVisibility(!calledFromSettings && triedAgain ? View.VISIBLE : View.GONE);

        // Adjust headline and hint
        String savedPIN = ConfigProvider.getPin();
        if (savedPIN == null) {
            // Hide the forgot PIN button
            findViewById(R.id.action_forgotPIN).setVisibility(View.GONE);
            if (lastEnteredPIN == null) {
                headline.setText(R.string.headline_create_pin);
                hint.setText(R.string.hint_create_pin);
            } else {
                headline.setText(R.string.headline_confirm_pin);
                hint.setText(R.string.hint_create_pin_again);
            }
        } else {
            if (calledFromSettings) {
                if (confirmedCurrentPIN) {
                    if (lastEnteredPIN == null) {
                        headline.setText(R.string.headline_new_pin);
                        hint.setText(R.string.hint_new_pin);
                    } else {
                        headline.setText(R.string.headline_confirm_pin);
                        hint.setText(R.string.hint_new_pin_again);
                    }
                } else {
                    headline.setText(R.string.headline_enter_pin);
                    hint.setText(R.string.hint_enter_current_pin);
                }
            } else {
                headline.setText(R.string.headline_enter_pin);
                hint.setText(R.string.hint_enter_pin);
            }
        }

       pinFields = new EditText[] {
                (EditText) findViewById(R.id.pin_char1),
                (EditText) findViewById(R.id.pin_char2),
                (EditText) findViewById(R.id.pin_char3),
                (EditText) findViewById(R.id.pin_char4)};

        // Clear the PIN entry fields in case the activity is resumed after an unsuccessful
        // submit attempt or when it has to be entered a second time
        for (int i = 0; i< pinFields.length; i++) {
            pinFields[i].setText("");
        }
    }

    public void submit(View view) {

        String enteredPIN = "";
        for (int i = 0; i< pinFields.length; i++) {
            enteredPIN += pinFields[i].getText().toString();
        }

        if (!validateNumber(enteredPIN, true)) {
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
                    Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_pin_correct_loading, Snackbar.LENGTH_LONG).show();
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
                        triedAgain = true;
                        onResume();
                    }
                }
            } else {
                // Process the login if the PIN is correct, otherwise display an error message and restart the activity
                if (enteredPIN.equals(savedPIN)) {
                    Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_pin_correct_loading, Snackbar.LENGTH_LONG).show();
                    startMainActivity();
                } else {
                    Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_pin_wrong, Snackbar.LENGTH_LONG).show();
                    triedAgain = true;
                    onResume();
                }
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // using this, because onKeyDown does not receiver ENTER key event

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            // support hardware keyboard inputs

            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_0:
                    enterNumber("0");
                    break;
                case KeyEvent.KEYCODE_1:
                    enterNumber("1");
                    break;
                case KeyEvent.KEYCODE_2:
                    enterNumber("2");
                    break;
                case KeyEvent.KEYCODE_3:
                    enterNumber("3");
                    break;
                case KeyEvent.KEYCODE_4:
                    enterNumber("4");
                    break;
                case KeyEvent.KEYCODE_5:
                    enterNumber("5");
                    break;
                case KeyEvent.KEYCODE_6:
                    enterNumber("6");
                    break;
                case KeyEvent.KEYCODE_7:
                    enterNumber("7");
                    break;
                case KeyEvent.KEYCODE_8:
                    enterNumber("8");
                    break;
                case KeyEvent.KEYCODE_9:
                    enterNumber("9");
                    break;
                case KeyEvent.KEYCODE_ENTER:
                    submit(null);
                    break;
            }
        }

        return super.dispatchKeyEvent(event);
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

    public void enterNumber(View view)
    {
        enterNumber(((Button)view).getText());
    }

    public void enterNumber(CharSequence number) {

        if (number.length() != 1 || !Character.isDigit(number.charAt(0))) {
            throw new IllegalArgumentException(number + " is not a single number");
        }

        for (int i = 0; i< pinFields.length; i++) {
            if (pinFields[i].length() == 0) {
                pinFields[i].setText(number);
                break;
            }
        }

    }

    public void deleteNumber(View view) {

        for (int i = pinFields.length-1; i>=0; i--) {
            if (pinFields[i].length() > 0) {
                pinFields[i].setText("");
                break;
            }
        }
    }

    public void backToSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }



    private void startMainActivity() {
        Intent intent = new Intent(this, CasesActivity.class);
        startActivity(intent);
    }

    private boolean validateNumber(String number, boolean showSnackbar) {

        if (number.length() != 4) {
            if (showSnackbar) {
                Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_pin_too_short, Snackbar.LENGTH_LONG).show();
            }
            return false;
        }

        boolean consecutiveNumbers = Pattern.matches("(0123|1234|2345|3456|4567|5678|6789|9876|8765|7654|6543|5432|4321|3210)", number);
        if (consecutiveNumbers) {
            if (showSnackbar) {
                Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_pin_no_consecutive, Snackbar.LENGTH_LONG).show();
            }
            return false;
        }

        boolean sameNumbers = Pattern.matches("\\\\d*?(\\\\d)\\\\1{2,}\\\\d*", number);
        if (sameNumbers) {
            if (showSnackbar) {
                Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_pin_no_same, Snackbar.LENGTH_LONG).show();
            }
            return false;
        }

        return true;
    }
}
