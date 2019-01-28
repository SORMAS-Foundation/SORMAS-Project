/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.Locale;

import de.symeda.sormas.api.i18n.I18nProperties;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.core.TaskNotificationService;
import de.symeda.sormas.app.core.VibrationHelper;
import de.symeda.sormas.app.util.LocationService;
import de.symeda.sormas.app.util.UncaughtExceptionParser;

public class SormasApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static final String PROPERTY_ID = "UA-98128295-1";

    private Tracker tracker;

    synchronized public Tracker getDefaultTracker() {
        return tracker;
    }

    @Override
    protected void attachBaseContext(Context base) {
        DatabaseHelper.init(base);
        ConfigProvider.init(base);
        super.attachBaseContext(buildLanguageContext(base));
    }

    private static Context buildLanguageContext(Context context) {
        Locale locale = new Locale(ConfigProvider.getLocale());
        Locale.setDefault(locale);
        I18nProperties.setLocale(locale.toString());

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        return context.createConfigurationContext(configuration);
    }

    @Override
    public void onCreate() {
        LocationService.init(this);
        VibrationHelper.getInstance(this);

        // Make sure the Enter Pin Activity is shown when the app has just started
        ConfigProvider.setAccessGranted(false);

        TaskNotificationService.startTaskNotificationAlarm(this);

        // Initialize the tracker that is used to send information to Google Analytics
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        tracker = analytics.newTracker(PROPERTY_ID);
        tracker.enableExceptionReporting(true);

        // Enable the forwarding of uncaught exceptions to Google Analytics
        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        ExceptionReporter reporter = (ExceptionReporter) handler;
        reporter.setExceptionParser(new UncaughtExceptionParser());

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
        } while (finishActivity != null);
    }

}
