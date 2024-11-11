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

import static de.symeda.sormas.app.backend.config.ConfigProvider.setNewPassword;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseLocalizedActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.ActivityChangePasswordLayoutBinding;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.settings.SettingsActivity;
import de.symeda.sormas.app.util.NavigationHelper;
import de.symeda.sormas.app.util.SoftKeyboardHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("ResourceType")
public class ChangePasswordActivity extends BaseLocalizedActivity implements ActivityCompat.OnRequestPermissionsResultCallback, NotificationContext {

	public static final String TAG = ChangePasswordActivity.class.getSimpleName();

	private boolean isAtLeast8 = false;
	private boolean hasUppercase = false;
	private boolean hasNumber = false;
	private boolean hasLowerCaseCharacter = false;
	private boolean isGood = false;
	private ActivityChangePasswordLayoutBinding binding;
	private ProgressBar preloader;
	private View fragmentFrame;
	private boolean isPasswordGenerated = false;

	@RequiresApi(api = Build.VERSION_CODES.R)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password_layout);

		ChangePasswordViewModel changePasswordViewModel = new ChangePasswordViewModel();

		binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password_layout);
		binding.setData(changePasswordViewModel);

		binding.changePasswordConfirmPassword.setLiveValidationDisabled(true);
		binding.changePasswordCurrentPassword.setLiveValidationDisabled(true);
		binding.changePasswordNewPassword.setLiveValidationDisabled(true);

		preloader = findViewById(R.id.preloader);
		fragmentFrame = findViewById(R.id.fragment_frame);

		showPreloader();

	}

	@Override
	public void onPause() {

		super.onPause();
		SoftKeyboardHelper.hideKeyboard(this, binding.changePasswordCurrentPassword.getWindowToken());
	}

	@Override
	protected void onDestroy() {

		hidePreloader();
		super.onDestroy();
	}

	@Override
	protected void onResume() {

		super.onResume();
	}

	public void backToSettings(View view) {

		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	@SuppressLint("ResourceType")
	public void passwordValidationCheck(View view) {

		String newPassword = binding.changePasswordNewPassword.getValue();

		isAtLeast8 = newPassword.length() >= 8;
		hasUppercase = newPassword.matches("(.*[A-Z].*)");
		hasNumber = newPassword.matches("(.*[0-9].*)");
		hasLowerCaseCharacter = newPassword.matches(".*[a-z].*");
		if (isAtLeast8 && hasNumber && hasUppercase && hasLowerCaseCharacter) {
			isGood = true;
			binding.actionPasswordStrength.setVisibility(view.getVisibility());
			binding.actionPasswordStrength.setText(R.string.message_password_strong);
			binding.actionPasswordStrength.setTextColor(Color.parseColor(getString(R.color.successBackground)));
		} else {
			binding.actionPasswordStrength.setVisibility(view.getVisibility());
			binding.actionPasswordStrength.setText(R.string.message_password_weak);
			NotificationHelper.showNotification(binding, NotificationType.ERROR, R.string.additional_message_passord_weak);
			binding.actionPasswordStrength.setTextColor(Color.parseColor(getString(R.color.errorBackground)));
		}
	}

	@SuppressLint("ResourceType")
	public void generatePassword(View view) {

		if (DataHelper.isNullOrEmpty(ConfigProvider.getServerRestUrl())) {
			NavigationHelper.goToSettings(this);
			return;
		}
		try {
			RetroProvider.connectAsyncHandled(this, true, true, result -> {
				if (Boolean.TRUE.equals(result)) {
					try {
						executeGeneratePasswordCall(UserDtoHelper.generatePassword());
					} catch (Exception e) {
						binding.actionPasswordStrength.setVisibility(view.getVisibility());
						binding.actionPasswordStrength.setText(e.toString());
						binding.actionPasswordStrength.setTextColor(Color.parseColor(getString(R.color.brightYellow)));
					}
					RetroProvider.disconnect();
				}
			});
		} catch (Exception e) {
			binding.actionPasswordStrength.setVisibility(view.getVisibility());
			binding.actionPasswordStrength.setText(e.toString());
			binding.actionPasswordStrength.setTextColor(Color.parseColor(getString(R.color.brightYellow)));
		}
	}

	@Override
	public View getRootView() {

		if (binding != null)
			return binding.getRoot();

		return null;
	}

	private void executeGeneratePasswordCall(Call<String> call) {

		try {
			call.enqueue(new Callback<>() {

				@Override
				public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
					if (response.code() != 200) {
						NotificationHelper.showNotification(binding, NotificationType.ERROR, getString(R.string.message_could_not_generate_password));
					}
					var generatedPassword = response.body();
					binding.changePasswordNewPassword.setValue(generatedPassword);
					binding.changePasswordConfirmPassword.setValue(generatedPassword);
					isPasswordGenerated = true;
					Toast.makeText(getApplicationContext(), getString(R.string.message_password_generated), Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
					NotificationHelper.showNotification(binding, NotificationType.ERROR, R.string.message_could_not_generate_password);
				}
			});
		} catch (Exception e) {
			NotificationHelper.showNotification(binding, NotificationType.ERROR, R.string.message_could_not_generate_password);
		}
	}

	private void executeSaveNewPasswordCall(Call<String> call, Activity activity) {

		try {
			showPreloader();

			call.enqueue(new Callback<String>() {

				@Override
				public void onResponse(Call<String> call, Response<String> response) {
					hidePreloader();

					if (response.code() != 200) {
						NotificationHelper.showNotification(binding, NotificationType.ERROR, R.string.message_could_not_save_password);
					} else {
						String message;
						String title;
						title = getString(R.string.heading_change_password);

						if (isPasswordGenerated) {
							String generatedPassword = binding.changePasswordNewPassword.getValue();
							message = generatedPassword;
							alertDialog(activity, message, title);
						} else {
							message = getString(R.string.message_password_changed);
							alertDialog(activity, message, title);
						}
						NotificationHelper.showNotification(binding, NotificationType.SUCCESS, R.string.message_password_changed);
					}

				}

				@Override
				public void onFailure(Call<String> call, Throwable t) {
					String message = getString(R.string.error_server_connection);
					String title = getString(R.string.heading_change_password);
					alertDialog(activity, message, title);
				}
			});
		} catch (Exception e) {
			Log.d("Call", call.toString());
		}

	}

	public void alertDialog(Activity activity, String message, String title) {

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		LayoutInflater inflater = activity.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_change_password, null);

		TextView passwordTextView = dialogView.findViewById(R.id.passwordTextView);

		if (isPasswordGenerated) {
			passwordTextView.setText(message);
		}

		builder.setTitle(title);

		passwordTextView.setOnClickListener(v -> {
			// Copy password to clipboard
			ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText(getString(R.string.heading_password), message);
			clipboard.setPrimaryClip(clip);
			Toast.makeText(activity, R.string.message_password_copied_to_clipbord, Toast.LENGTH_SHORT).show();
		});

		builder.setView(dialogView);
		builder.setCancelable(true);
		builder.setPositiveButton(activity.getString(R.string.action_ok), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * When clicked on submit
	 **/
	public void savePassword(View view) {

		binding.changePasswordNewPassword.disableErrorState();
		binding.changePasswordCurrentPassword.disableErrorState();
		binding.changePasswordConfirmPassword.disableErrorState();

		if (DataHelper.isNullOrEmpty(ConfigProvider.getServerRestUrl())) {
			NavigationHelper.goToSettings(this);
			return;
		}

		String currentPassword = binding.changePasswordCurrentPassword.getValue();
		String newPassword = binding.changePasswordNewPassword.getValue();
		String confirmPassword = binding.changePasswordConfirmPassword.getValue();
		String configPassword = ConfigProvider.getPassword();

		boolean isValid = true;

		if (currentPassword == null || currentPassword.trim().isEmpty()) {
			binding.changePasswordCurrentPassword.enableErrorState(R.string.error_current_password_empty);
			isValid = false;
		} else if (newPassword == null || newPassword.trim().isEmpty()) {
			binding.changePasswordNewPassword.enableErrorState(R.string.error_new_password_empty);
			isValid = false;
		} else if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
			binding.changePasswordConfirmPassword.enableErrorState(R.string.error_confirm_password_empty);
			isValid = false;
		} else if (!configPassword.equals(currentPassword)) {
			binding.changePasswordCurrentPassword.enableErrorState(R.string.error_current_password_incorrect);
			NotificationHelper.showNotification(binding, NotificationType.ERROR, R.string.error_current_password_incorrect);
			isValid = false;
		} else if (!newPassword.equals(confirmPassword)) {
			binding.changePasswordConfirmPassword.enableErrorState(R.string.error_passwords_do_not_match);
			NotificationHelper.showNotification(binding, NotificationType.ERROR, R.string.error_passwords_do_not_match);
			isValid = false;
		}

		if (isValid) {
			passwordValidationCheck(view);
		}

		if (isValid && isGood) {
			RetroProvider.connectAsyncHandled(this, true, true, result -> {
				if (Boolean.TRUE.equals(result)) {
					try {
						executeSaveNewPasswordCall(UserDtoHelper.saveNewPassword(ConfigProvider.getUser().getUuid(), newPassword), this);
						setNewPassword(newPassword);
					} catch (Exception e) {
						binding.actionPasswordStrength.setVisibility(View.VISIBLE);
						binding.actionPasswordStrength.setText(e.toString());
						binding.actionPasswordStrength.setTextColor(Color.parseColor(getString(R.color.brightYellow)));
					}
					RetroProvider.disconnect();
				}
			});
		}
	}

	public void showPreloader() {

		if (fragmentFrame != null) {
			fragmentFrame.setVisibility(View.GONE);
		}
		if (preloader != null) {
			preloader.setVisibility(View.VISIBLE);
		}
	}

	public void hidePreloader() {

		if (preloader != null) {
			preloader.setVisibility(View.GONE);
		}
		if (fragmentFrame != null) {
			fragmentFrame.setVisibility(View.VISIBLE);
		}
	}
}
