package de.symeda.sormas.app.login;


import static de.symeda.sormas.app.backend.config.ConfigProvider.setNewPassword;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
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

    private boolean isAtLeast8 = false, hasUppercase = false, hasNumber = false, hasSymbol = false, isGood = false;

    private ActivityChangePasswordLayoutBinding binding;
    private ProgressBar preloader;
    private View fragmentFrame;

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
    public void registrationDataCheck(View view) {
        String newPassword = binding.changePasswordNewPassword.getValue();

        isAtLeast8 = newPassword.length() >= 8;
        hasUppercase = newPassword.matches("(.*[A-Z].*)");
        hasNumber = newPassword.matches("(.*[0-9].*)");
        hasSymbol = newPassword.matches("^(?=.*[_@#%&?;,.()]).*$");
        if (isAtLeast8 && hasNumber && hasUppercase && hasSymbol) {
            isGood = true;
            binding.actionPasswordStrength.setVisibility(view.getVisibility());
            binding.actionPasswordStrength.setText(R.string.message_password_strong);
            binding.actionPasswordStrength.setTextColor(Color.parseColor(getString(R.color.successBackground)));
        } else {
            binding.actionPasswordStrength.setVisibility(view.getVisibility());
            binding.actionPasswordStrength.setText(R.string.message_password_weak);
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
                        NotificationHelper.showNotification(binding, NotificationType.ERROR, R.string.message_could_not_generate_password);
                    }
                    var generatedPassword = response.body();
                    binding.changePasswordNewPassword.setValue(generatedPassword);
                    binding.changePasswordConfirmPassword.setValue(generatedPassword);
                    Toast.makeText(getApplicationContext(), "Password Generated", Toast.LENGTH_SHORT).show();
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

    private void executeSaveNewPasswordCall(Call<String> call) {
        try {
            showPreloader();

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.code() != 200) {
                        NotificationHelper.showNotification(binding, NotificationType.ERROR, R.string.message_could_not_save_password);
                        hidePreloader();

                    } else {
                        Toast.makeText(getApplicationContext(), "Password Saved", Toast.LENGTH_LONG).show();
                        NotificationHelper.showNotification(binding, NotificationType.SUCCESS, R.string.message_password_changed);
                        finish();
                        hidePreloader();
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.d("Call", call.toString());
        }

    }

    /**
     * When clicked on submit
     **/
    public void savePassword(View view) {
        // Disable error states
        binding.changePasswordNewPassword.disableErrorState();
        binding.changePasswordCurrentPassword.disableErrorState();
        binding.changePasswordConfirmPassword.disableErrorState();

        // Check if server URL is configured
        if (DataHelper.isNullOrEmpty(ConfigProvider.getServerRestUrl())) {
            NavigationHelper.goToSettings(this);
            return;
        }

        // Retrieve password values
        String currentPassword = binding.changePasswordCurrentPassword.getValue();
        String newPassword = binding.changePasswordNewPassword.getValue();
        String confirmPassword = binding.changePasswordConfirmPassword.getValue();
        String configPassword = ConfigProvider.getPassword();

        // Validate input fields
        boolean isValid = true;
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            binding.incorrectCurrentPassword.setVisibility(View.VISIBLE);
            binding.incorrectCurrentPassword.setText("Current password cannot be empty.");
            isValid = false;
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            binding.incorrectNewPassword.setVisibility(View.VISIBLE);
            binding.incorrectNewPassword.setText("New password cannot be empty.");
            isValid = false;
        }
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            binding.incorrectConfirmPassword.setVisibility(View.VISIBLE);
            binding.incorrectConfirmPassword.setText("Confirm password cannot be empty.");
            isValid = false;
        }
        if (!configPassword.equals(currentPassword)) {
            binding.incorrectCurrentPassword.setVisibility(View.VISIBLE);
            binding.incorrectCurrentPassword.setText("Current password is incorrect.");
            isValid = false;
        }
        if (!newPassword.equals(confirmPassword)) {
            binding.incorrectConfirmPassword.setVisibility(View.VISIBLE);
            binding.incorrectConfirmPassword.setText("Passwords do not match.");
            isValid = false;
        }

        // If all validations pass, proceed with the password change
        System.out.println("isValid: " + isValid);
        System.out.println("isGood: " + isGood);
        System.out.println("currentPassword: " + currentPassword);
        System.out.println("newPassword: " + newPassword);
        System.out.println("confirmPassword: " + confirmPassword);
        System.out.println("configPassword: " + configPassword);
        // Call registrationDataCheck if necessary
        if (isValid) {
            registrationDataCheck(view);
        }
        
        if (isValid && isGood) {
            RetroProvider.connectAsyncHandled(this, true, true, result -> {
                if (Boolean.TRUE.equals(result)) {
                    try {
                        executeSaveNewPasswordCall(UserDtoHelper.saveNewPassword(ConfigProvider.getUser().getUuid(), newPassword, currentPassword));
                        // Cache the new password
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
