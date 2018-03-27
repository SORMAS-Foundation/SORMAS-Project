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

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.core.VibrationHelper;
import de.symeda.sormas.app.util.LocationService;
import de.symeda.sormas.app.util.MemoryDatabaseHelper;
import de.symeda.sormas.app.util.UncaughtExceptionParser;

/**
 * Created by Orson on 03/11/2017.
 */

public class SormasApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static final String PROPERTY_ID = "UA-98128295-1";

    //private AsyncTask loaderTask;
    private Tracker tracker;

    synchronized public Tracker getDefaultTracker() {
        return tracker;
    }

    @Override
    public void onCreate() {
        //updateLocale(this);
        DatabaseHelper.init(this);
        ConfigProvider.init(this);
        LocationService.init(this);

        VibrationHelper.getInstance(this);

        // Make sure the Enter Pin Activity is shown when the app has just started
        ConfigProvider.setAccessGranted(false);

        //TaskNotificationService.startTaskNotificationAlarm(this);

        // Initialize the tracker that is used to send information to Google Analytics
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        tracker = analytics.newTracker(PROPERTY_ID);
        tracker.enableExceptionReporting(true);
        // TODO find a way to automatically disable exception reporting when the app is started in Android Studio

        // Enable the forwarding of uncaught exceptions to Google Analytics
        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        ExceptionReporter reporter = (ExceptionReporter) handler;
        reporter.setExceptionParser(new UncaughtExceptionParser());

        super.onCreate();

        this.registerActivityLifecycleCallbacks(this);

        //TODO: Remove Temporary Database
        MemoryDatabaseHelper.init(this);
    }

    public static void updateLocale(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String lang = prefs.getString("locale_override", "");
        updateLocal(ctx, lang);
    }

    public static void updateLocal(Context ctx, String lang) {
        Configuration cfg = new Configuration();
        if (!TextUtils.isEmpty(lang))
            cfg.locale = new Locale(lang);
        else
            cfg.locale = Locale.getDefault();

        ctx.getResources().updateConfiguration(cfg, null);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        LocationService.instance().requestActiveLocationUpdates(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        LocationService.instance().removeActiveLocationUpdates();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        /*if (loaderTask != null && !loaderTask.isCancelled())
            loaderTask.cancel(true);*/
    }
}
