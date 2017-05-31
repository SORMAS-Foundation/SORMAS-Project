package de.symeda.sormas.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.task.TaskNotificationService;
import de.symeda.sormas.app.util.UncaughtExceptionParser;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class SormasApplication extends Application {

    private static final String PROPERTY_ID = "UA-98128295-1";
    private static Context context;

    private Tracker tracker;

    synchronized public Tracker getDefaultTracker() {
        return tracker;
    }

    @Override
    public void onCreate() {
        DatabaseHelper.init(this);
        ConfigProvider.init(this);

        TaskNotificationService.startTaskNotificationAlarm(this);

        // Initialize the tracker that is used to send information to Google Analytics
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        tracker = analytics.newTracker(PROPERTY_ID);
        tracker.enableExceptionReporting(true);
        // TODO find a way to automatically disable exception reporting when the app is started in Android Studio

        // Enable the forwarding of uncaught exceptions to Google Analytics
        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        ExceptionReporter reporter = (ExceptionReporter) handler;
        reporter.setExceptionParser(new UncaughtExceptionParser());

        context = this;

        super.onCreate();
    }

    public static Context getContext() {
        return context;
    }
}
