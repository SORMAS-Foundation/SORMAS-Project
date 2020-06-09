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

package de.symeda.sormas.app.settings;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.app.BaseLandingActivity;
import de.symeda.sormas.app.BaseLandingFragment;
import de.symeda.sormas.app.LocaleManager;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationPosition;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.util.AppUpdateController;

public class SettingsActivity extends BaseLandingActivity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);

		MenuItem syncMenuItem = menu.findItem(R.id.action_sync);
		syncMenuItem.setVisible(false);

		return true;
	}

	@Override
	public SettingsFragment getActiveFragment() {
		return (SettingsFragment) super.getActiveFragment();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (ConfigProvider.getUser() == null) {
				onBackPressed(); // Settings don't have a parent -> go back instead of up
				return true;
			}
			return super.onOptionsItemSelected(item);

		case R.id.action_save:
			String serverUrl = getActiveFragment().getServerUrl();
			ConfigProvider.setServerRestUrl(serverUrl);

			onBackPressed(); // Settings don't have a parent -> go back instead of up

			NotificationHelper.showNotification(this, NotificationPosition.BOTTOM, NotificationType.SUCCESS, R.string.message_settings_saved);

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Enum getPageStatus() {
		return null;
	}

	@Override
	public boolean isAccessNeeded() {
		return false;
	}

	@Override
	public BaseLandingFragment buildLandingFragment() {
		return new SettingsFragment();
	}

	/**
	 * Is a sub-activity when the user needs to go back to the LoginActivity
	 * 
	 * @see SettingsActivity#onOptionsItemSelected(MenuItem)
	 */
	@Override
	protected boolean isSubActivity() {
		return ConfigProvider.getUser() == null;
	}

	protected int getActivityTitle() {
		return R.string.main_menu_settings;
	}

	@Override
	// Handles the result of the attempt to install a new app version - should be added to every activity that uses the UpdateAppDialog
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == AppUpdateController.INSTALL_RESULT) {
			switch (resultCode) {
			// Do nothing if the installation was successful
			case Activity.RESULT_OK:
			case Activity.RESULT_CANCELED:
				break;
			// Everything else probably is an error
			default:
				AppUpdateController.getInstance().handleInstallFailure();
				break;
			}
		}
	}

	public void setNewLocale(AppCompatActivity mContext, Language language) {
		LocaleManager.setNewLocale(this, language);
		if (ConfigProvider.getUser() != null) {
			I18nProperties.setUserLanguage(ConfigProvider.getUser().getLanguage());
		}
		Intent intent = mContext.getIntent();
		startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
	}
}
