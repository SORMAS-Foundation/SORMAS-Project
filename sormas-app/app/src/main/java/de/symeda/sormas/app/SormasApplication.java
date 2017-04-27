package de.symeda.sormas.app;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.concurrent.ExecutionException;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.caze.CasesActivity;
import de.symeda.sormas.app.caze.SyncCasesTask;
import de.symeda.sormas.app.person.SyncPersonsTask;
import de.symeda.sormas.app.task.SyncTasksTask;
import de.symeda.sormas.app.task.TaskNotificationService;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.SyncInfrastructureTask;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class SormasApplication extends Application {

    private static final String PROPERTY_ID = "UA-98128295-1";

    private Tracker tracker;

    synchronized public Tracker getDefaultTracker() {
        return tracker;
    }

    @Override
    public void onCreate() {
        DatabaseHelper.init(this);
        ConfigProvider.init();

        SyncInfrastructureTask.syncInfrastructure(new Callback() {
            @Override
            public void call() {
                // this also syncs cases which syncs persons
                SyncTasksTask.syncTasks((Callback)null, SormasApplication.this);
            }
        });

        TaskNotificationService.startTaskNotificationAlarm(this);

        // Initialize the tracker that is used to send information to Google Analytics
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        tracker = analytics.newTracker(PROPERTY_ID);
        tracker.enableExceptionReporting(true);

        // Enable the forwarding of uncaught exceptions to Google Analytics
        Thread.UncaughtExceptionHandler handler = new ExceptionReporter(
                tracker,
                Thread.getDefaultUncaughtExceptionHandler(),
                getApplicationContext()
        );
        Thread.setDefaultUncaughtExceptionHandler(handler);

        super.onCreate();
    }
}
