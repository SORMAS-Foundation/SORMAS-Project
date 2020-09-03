/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.login;

import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.ActivityEnterPinLayoutBinding;
import de.symeda.sormas.app.util.NavigationHelper;

public class EnterPinActivity extends AppCompatActivity implements NotificationContext {

	public static final String CALLED_FROM_SETTINGS = "calledFromSettings";

	private boolean calledFromSettings;
	private String lastEnteredPIN;
	private boolean confirmedCurrentPIN;
	private boolean triedAgain;
	private EditText[] pinFields;
	private ProgressDialog progressDialog = null;

	private ActivityEnterPinLayoutBinding binding;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = DataBindingUtil.setContentView(this, R.layout.activity_enter_pin_layout);

		Bundle params = getIntent().getExtras();
		if (params != null) {
			if (params.containsKey(CALLED_FROM_SETTINGS)) {
				calledFromSettings = params.getBoolean(CALLED_FROM_SETTINGS);
			}
		}

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// sync will be done by other activities anyway...
		//progressDialog = SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.Changes, EnterPinActivity.this, null);
	}

	@Override
	protected void onDestroy() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		super.onDestroy();
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
				headline.setText(R.string.heading_create_pin);
				hint.setText(R.string.hint_create_pin);
			} else {
				headline.setText(R.string.heading_confirm_pin);
				hint.setText(R.string.hint_create_pin_again);
			}
		} else {
			if (calledFromSettings) {
				if (confirmedCurrentPIN) {
					if (lastEnteredPIN == null) {
						headline.setText(R.string.heading_create_new_pin);
						hint.setText(R.string.hint_new_pin);
					} else {
						headline.setText(R.string.heading_confirm_pin);
						hint.setText(R.string.hint_new_pin_again);
					}
				} else {
					headline.setText(R.string.heading_enter_pin);
					hint.setText(R.string.hint_enter_current_pin);
				}
			} else {
				headline.setText(R.string.heading_enter_pin);
				hint.setText(R.string.hint_enter_authentication_pin);
			}
		}

		pinFields = new EditText[] {
			(EditText) findViewById(R.id.pin_char1),
			(EditText) findViewById(R.id.pin_char2),
			(EditText) findViewById(R.id.pin_char3),
			(EditText) findViewById(R.id.pin_char4) };

		// Clear the PIN entry fields in case the activity is resumed after an unsuccessful
		// submit attempt or when it has to be entered a second time
		for (int i = 0; i < pinFields.length; i++) {
			pinFields[i].setText("");
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

	public void enterNumber(View view) {
		enterNumber(((Button) view).getText());
	}

	public void enterNumber(CharSequence number) {
		if (number.length() != 1 || !Character.isDigit(number.charAt(0))) {
			throw new IllegalArgumentException(number + " is not a single number");
		}

		for (int i = 0; i < pinFields.length; i++) {
			if (pinFields[i].length() == 0) {
				pinFields[i].setText(number);
				break;
			}
		}
	}

	public void deleteNumber(View view) {
		for (int i = pinFields.length - 1; i >= 0; i--) {
			if (pinFields[i].length() > 0) {
				pinFields[i].setText("");
				break;
			}
		}
	}

	private boolean validateNumber(String number, boolean showSnackbar) {

		if (number.length() != 4) {
			if (showSnackbar) {
				NotificationHelper.showNotification(binding, NotificationType.ERROR, R.string.message_pin_too_short);
			}
			return false;
		}

		boolean consecutiveNumbers = Pattern.matches("(0123|1234|2345|3456|4567|5678|6789|9876|8765|7654|6543|5432|4321|3210)", number);
		if (consecutiveNumbers) {
			if (showSnackbar) {
				NotificationHelper.showNotification(binding, NotificationType.ERROR, R.string.message_pin_no_consecutive);
			}
			return false;
		}

		boolean sameNumbers = Pattern.matches("\\d*?(\\d)\\1{2,}\\d*", number);
		if (sameNumbers) {
			if (showSnackbar) {
				NotificationHelper.showNotification(binding, NotificationType.ERROR, R.string.message_pin_no_same);
			}
			return false;
		}

		return true;
	}

	public void submit(View view) {
		String enteredPIN = "";
		for (int i = 0; i < pinFields.length; i++) {
			enteredPIN += pinFields[i].getText().toString();
		}

		String savedPIN = ConfigProvider.getPin();

		if (savedPIN == null) {
			if (lastEnteredPIN == null) {
				// validate the entered pin
				if (!validateNumber(enteredPIN, true)) {
					onResume();
					return;
				}
				// Store the entered PIN and restart the activity because the user has to enter it twice
				lastEnteredPIN = enteredPIN;
				onResume();
			} else {
				// Check whether the two entered PINs match - if yes, store it and process the login,
				// otherwise display an error message and restart the activity
				if (lastEnteredPIN.equals(enteredPIN)) {
					ConfigProvider.setPin(enteredPIN);
					ConfigProvider.setAccessGranted(true);
					NotificationHelper.showNotification(binding, NotificationType.SUCCESS, R.string.message_pin_correct_loading);
					finish();
				} else {
					lastEnteredPIN = null;
					NotificationHelper.showNotification(binding, NotificationType.ERROR, R.string.message_pin_not_matching);
					onResume();
				}
			}
		} else {
			if (calledFromSettings) {
				if (confirmedCurrentPIN) {
					if (lastEnteredPIN == null) {
						// validate the entered pin
						if (!validateNumber(enteredPIN, true)) {
							onResume();
							return;
						}
						// Store the entered PIN and restart the activity because the user has to enter it twice
						lastEnteredPIN = enteredPIN;
						onResume();
					} else {
						if (lastEnteredPIN.equals(enteredPIN)) {
							ConfigProvider.setPin(enteredPIN);
							NotificationHelper.showNotification(binding, NotificationType.INFO, R.string.message_pin_changed);
							finish();
						} else {
							lastEnteredPIN = null;
							NotificationHelper.showNotification(binding, NotificationType.ERROR, R.string.message_pin_not_matching);
							onResume();
						}
					}
				} else {
					if (enteredPIN.equals(savedPIN)) {
						confirmedCurrentPIN = true;
						onResume();
					} else {
						NotificationHelper.showNotification(binding, NotificationType.ERROR, R.string.message_pin_wrong);
						triedAgain = true;
						onResume();
					}
				}
			} else {
				// Process the login if the PIN is correct, otherwise display an error message and restart the activity
				if (enteredPIN.equals(savedPIN)) {
					ConfigProvider.setAccessGranted(true);
					NotificationHelper.showNotification(binding, NotificationType.SUCCESS, R.string.message_pin_correct_loading);
					finish();
				} else {
					NotificationHelper.showNotification(binding, NotificationType.ERROR, R.string.message_pin_wrong);
					triedAgain = true;
					onResume();
				}
			}
		}
	}

	public void backToSettings(View view) {
		NavigationHelper.goToSettings(view.getContext());
	}

	public void forgotPIN(final View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
		builder.setPositiveButton(view.getContext().getResources().getText(R.string.action_ok), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				ConfigProvider.clearUserLogin();
				ConfigProvider.clearPin();
				Intent intent = new Intent(view.getContext(), LoginActivity.class);
				startActivity(intent);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton(view.getContext().getResources().getText(R.string.action_cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		AlertDialog dialog = builder.create();
		dialog.setCancelable(true);
		dialog.setTitle(view.getContext().getResources().getText(R.string.heading_reset_PIN).toString());
		dialog.setMessage(view.getContext().getResources().getText(R.string.info_reset_pin).toString());
		dialog.show();
	}

	@Override
	public View getRootView() {
		if (binding != null)
			return binding.getRoot();

		return null;
	}
}
