package org.technologybord.loaders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Orson on 06/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class StringLoader extends AsyncTaskLoader<List<String>> {

    public static final String TAG = StringLoader.class.getSimpleName();

    private List<String> cachedData;
    public static final String ACTION = "com.loaders.FORCE";

    public StringLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        IntentFilter filter = new IntentFilter(ACTION);
        manager.registerReceiver(broadcastReceiver, filter);

        if (cachedData == null) {
            forceLoad();
        } else {
            super.deliverResult(cachedData);
        }
    }

    @Override
    public List<String> loadInBackground() {
        Log.d(TAG, "Loading new data");

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<String> data = Arrays.asList(getContext().getResources().getStringArray(R.array.items));
        return data;
    }

    @Override
    public void deliverResult(@Nullable List<String> data) {
        cachedData = data;
        super.deliverResult(data);
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
    }

    @Override
    protected void onReset() {
        super.onReset();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    public void loadNewStrings() {
        forceLoad();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            forceLoad();
        }
    };
}
