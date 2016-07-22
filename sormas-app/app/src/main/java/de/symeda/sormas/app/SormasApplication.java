package de.symeda.sormas.app;

import android.app.Application;

import de.symeda.sormas.app.backend.common.DatabaseHelper;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class SormasApplication extends Application {

    @Override
    public void onCreate() {
        DatabaseHelper.init(this);
        super.onCreate();
    }
}
