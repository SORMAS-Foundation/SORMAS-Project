package de.symeda.sormas.app;

import android.app.Application;
import android.util.Log;

import java.util.concurrent.ExecutionException;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.util.SyncInfrastructureTask;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class SormasApplication extends Application {

    @Override
    public void onCreate() {
        DatabaseHelper.init(this);
        ConfigProvider.init();
        try {
            new SyncInfrastructureTask().execute().get();
        } catch (InterruptedException e) {
            Log.e(SurveillanceActivity.class.getName(), e.toString(), e);
        } catch (ExecutionException e) {
            Log.e(SurveillanceActivity.class.getName(), e.toString(), e);
        }
        super.onCreate();
    }
}
