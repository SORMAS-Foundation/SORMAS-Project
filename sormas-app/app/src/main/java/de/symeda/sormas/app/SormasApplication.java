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

package de.symeda.sormas.app;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.core.FeatureConfigurationService;
import de.symeda.sormas.app.core.TaskNotificationService;
import de.symeda.sormas.app.core.VibrationHelper;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.util.LocationService;

public class SormasApplication extends Application implements Application.ActivityLifecycleCallbacks {

	private FirebaseAnalytics firebaseAnalytics;

	@Override
	protected void attachBaseContext(Context base) {
		DatabaseHelper.init(base);
		ConfigProvider.init(base);
		super.attachBaseContext(LocaleManager.setLocale(base));
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		LocaleManager.setLocale(this);
	}

	@Override
	public void onCreate() {
		LocationService.init(this);
		VibrationHelper.getInstance(this);
		LocaleManager.initializeI18nProperties();

		// Make sure the Enter Pin Activity is shown when the app has just started
		ConfigProvider.setAccessGranted(false);

		firebaseAnalytics = FirebaseAnalytics.getInstance(this);
		FirebaseRemoteConfig.getInstance().fetch();
		NotificationHelper.createNotificationChannels(this);

		TaskNotificationService.startTaskNotificationAlarm(this);
		FeatureConfigurationService.startFeatureConfigurationService(this);

		super.onCreate();

		this.registerActivityLifecycleCallbacks(this);
	}

	@Override
	public void onActivityCreated(Activity activity, Bundle bundle) {

	}

	@Override
	public void onActivityStarted(Activity activity) {

	}

	@Override
	public void onActivityResumed(Activity activity) {
		LocationService.instance().requestActiveLocationUpdates(activity);
	}

	@Override
	public void onActivityPaused(Activity activity) {
		LocationService.instance().removeActiveLocationUpdates();
	}

	@Override
	public void onActivityStopped(Activity activity) {

	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

	}

	@Override
	public void onActivityDestroyed(Activity activity) {

	}

	public void closeApp(Activity callingActivity) {
		Activity finishActivity = callingActivity;
		do {
			finishActivity.finish();
			finishActivity = finishActivity.getParent();
		}
		while (finishActivity != null);
	}

	public FirebaseAnalytics getFirebaseAnalytics() {
		return firebaseAnalytics;
	}
}
