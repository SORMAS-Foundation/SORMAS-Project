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

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseLocalizedActivity;
import de.symeda.sormas.app.LocaleManager;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.dialog.SynchronizationDialog;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.ActivityLoginLayoutBinding;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.settings.SettingsActivity;
import de.symeda.sormas.app.util.AppUpdateController;
import de.symeda.sormas.app.util.NavigationHelper;
import de.symeda.sormas.app.util.SoftKeyboardHelper;
import de.symeda.sormas.app.util.SormasProperties;

public class LoginActivity extends BaseLocalizedActivity implements ActivityCompat.OnRequestPermissionsResultCallback, NotificationContext {

	private ActivityLoginLayoutBinding binding;

	private SynchronizationDialog synchronizationDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_layout);

		if (!ConfigProvider.ensureDeviceEncryption(LoginActivity.this)) {
			return;
		}

		LoginViewModel loginViewModel = new LoginViewModel();
		binding = DataBindingUtil.setContentView(this, R.layout.activity_login_layout);
		binding.setData(loginViewModel);

		binding.loginUsername.setLiveValidationDisabled(true);
		binding.loginPassword.setLiveValidationDisabled(true);

		boolean hasDefaultUser =
			!DataHelper.isNullOrEmpty(SormasProperties.getUserNameDefault()) && !DataHelper.isNullOrEmpty(SormasProperties.getUserPasswordDefault());
		binding.btnLoginDefaultUser.setVisibility(hasDefaultUser ? View.VISIBLE : View.GONE);
	}

	@Override
	protected void onResume() {
		super.onResume();

		checkLoginAndDoUpdateAndInitialSync();

		if (ConfigProvider.getUser() != null) {
			binding.signInLayout.setVisibility(View.GONE);
		} else {
			binding.signInLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		SoftKeyboardHelper.hideKeyboard(this, binding.loginPassword.getWindowToken());
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		checkLoginAndDoUpdateAndInitialSync();
	}

	/**
	 * Handles the result of the attempt to install a new app version.
	 * Has to be added to every activity that uses the UpdateAppDialog
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == AppUpdateController.INSTALL_RESULT) {
			switch (resultCode) {
			// Do nothing if the installation was successful
			case Activity.RESULT_OK:
			case Activity.RESULT_CANCELED:
			case Activity.RESULT_FIRST_USER:
				finishAndRemoveTask();
				break;
			// Everything else probably is an error
			default:
				AppUpdateController.getInstance().handleInstallFailure();
				break;
			}
		}
	}

	@Override
	protected void onDestroy() {
		if (synchronizationDialog != null && synchronizationDialog.isShowing()) {
			synchronizationDialog.dismiss();
		}

		super.onDestroy();
	}

	public void showSettingsView(View view) {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	public void loginDefaultUser(View view) {

		binding.loginUsername.setValue(SormasProperties.getUserNameDefault());
		binding.loginPassword.setValue(SormasProperties.getUserPasswordDefault());

		login(view);
	}

	/**
	 * Called by onClick
	 */
	public void login(View view) {
		//Hide notification
		//NotificationHelper.hideNotification(binding);
		binding.loginUsername.disableErrorState();
		binding.loginPassword.disableErrorState();

		String userName = binding.loginUsername.getValue().trim();
		String password = binding.loginPassword.getValue();

		if (userName.isEmpty()) {
			binding.loginUsername.enableErrorState(R.string.message_empty_username);
		} else if (password.isEmpty()) {
			binding.loginPassword.enableErrorState(R.string.message_empty_password);
		} else {
			ConfigProvider.setUsernameAndPassword(userName, password);

			RetroProvider.connectAsyncHandled(this, true, true, result -> {
				if (Boolean.TRUE.equals(result)) {
					RetroProvider.disconnect();
					checkLoginAndDoUpdateAndInitialSync();
				} else {
					// if we could not connect to the server, the user can't sign in - no matter the reason
					ConfigProvider.clearUserLogin();
				}
			});
		}
	}

	private void checkLoginAndDoUpdateAndInitialSync() {

		if (DataHelper.isNullOrEmpty(ConfigProvider.getServerRestUrl())) {
			NavigationHelper.goToSettings(this);
			return;
		}

		if (ConfigProvider.getPassword() == null)
			return;

		if (synchronizationDialog == null || !synchronizationDialog.isShowing()) {
			synchronizationDialog = new SynchronizationDialog(this);
			synchronizationDialog.create();
		}

		RetroProvider.connectAsyncHandled(this, true, true, result -> {
			if (Boolean.TRUE.equals(result)) {

				boolean needsSync = ConfigProvider.getUser() == null || DatabaseHelper.getCaseDao().isEmpty();

				if (needsSync) {
					SynchronizeDataAsync.call(
						SynchronizeDataAsync.SyncMode.Changes,
						getApplicationContext(),
						synchronizationDialog.getSyncCallbacks(),
						(syncFailed, syncFailedMessage) -> {

							RetroProvider.disconnect();

							if (syncFailed) {
								NotificationHelper.showNotification(LoginActivity.this, NotificationType.ERROR, syncFailedMessage);
							}

							if (synchronizationDialog != null && synchronizationDialog.isShowing()) {
								synchronizationDialog.dismiss();
								synchronizationDialog = null;
							}

							if (ConfigProvider.getUser() != null) {
								initializeFirebase();
								if (ConfigProvider.getUser().getLanguage() != null) {
									setNewLocale(this, ConfigProvider.getUser().getLanguage());
								}
								openLandingActivity();
							} else {
								binding.signInLayout.setVisibility(View.VISIBLE);
							}
						});
				} else {

					RetroProvider.disconnect();

					if (synchronizationDialog != null && synchronizationDialog.isShowing()) {
						synchronizationDialog.dismiss();
						synchronizationDialog = null;
					}

					initializeFirebase();
					if (ConfigProvider.getUser().getLanguage() != null) {
						setNewLocale(this, ConfigProvider.getUser().getLanguage());
					}
					openLandingActivity();
				}
			} else {
				if (synchronizationDialog != null && synchronizationDialog.isShowing()) {
					synchronizationDialog.dismiss();
					synchronizationDialog = null;
				}

				if (ConfigProvider.getUser() != null) {
					initializeFirebase();
					if (ConfigProvider.getUser().getLanguage() != null) {
						setNewLocale(this, ConfigProvider.getUser().getLanguage());
					}
					openLandingActivity();
				} else {
					binding.signInLayout.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private void initializeFirebase() {
		((SormasApplication) getApplication()).getFirebaseAnalytics().setUserId(ConfigProvider.getUser().getUuid());
		FirebaseCrashlytics.getInstance().setUserId(ConfigProvider.getUser().getUuid());
	}

	private void openLandingActivity() {

		User user = ConfigProvider.getUser();

		boolean caseSurveillanceActive = !DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.CASE_SURVEILANCE);
		boolean campaignsActive = !DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.CAMPAIGNS);

		if (caseSurveillanceActive) {
			if (ConfigProvider.hasUserRight(UserRight.CASE_VIEW)
				&& (ConfigProvider.hasUserRight(UserRight.CASE_RESPONSIBLE)
					|| user.hasJurisdictionLevel(JurisdictionLevel.HEALTH_FACILITY, JurisdictionLevel.COMMUNITY, JurisdictionLevel.POINT_OF_ENTRY))) {
				NavigationHelper.goToCases(LoginActivity.this);
			} else if (ConfigProvider.hasUserRight(UserRight.CONTACT_VIEW) && ConfigProvider.hasUserRight(UserRight.CONTACT_RESPONSIBLE)) {
				NavigationHelper.goToContacts(LoginActivity.this);
			} else if (ConfigProvider.hasUserRight(UserRight.CASE_VIEW)) {
				NavigationHelper.goToCases(LoginActivity.this);
			} else if (ConfigProvider.hasUserRight(UserRight.ENVIRONMENT_VIEW)) {
				NavigationHelper.goToEnvironments(LoginActivity.this);
			} else if (ConfigProvider.hasUserRight(UserRight.EVENT_VIEW)) {
				NavigationHelper.goToEvents(LoginActivity.this);
			} else {
				NavigationHelper.goToSettings(LoginActivity.this);
			}
		} else if (campaignsActive && ConfigProvider.hasUserRight(UserRight.CAMPAIGN_FORM_DATA_VIEW)) {
			NavigationHelper.goToCampaigns(LoginActivity.this);
		} else {
			NavigationHelper.goToSettings(LoginActivity.this);
		}
	}

	@Override
	public View getRootView() {
		if (binding != null)
			return binding.getRoot();

		return null;
	}

	private void setNewLocale(AppCompatActivity mContext, Language language) {
		LocaleManager.setNewLocale(this, language);
		I18nProperties.setUserLanguage(ConfigProvider.getUser().getLanguage());
		Intent intent = mContext.getIntent();
		startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
	}
}
