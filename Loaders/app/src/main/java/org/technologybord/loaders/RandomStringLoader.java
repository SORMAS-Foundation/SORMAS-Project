package org.technologybord.loaders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Orson on 05/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class RandomStringLoader extends AsyncTaskLoader<ArrayList<String>>  {

    private Receiver mReceiver;

    public static final String STRING_LOADER_RELOAD = "RandomStringLoader.RELOAD";

    public RandomStringLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        mReceiver = new Receiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(STRING_LOADER_RELOAD);
        getContext().registerReceiver(mReceiver, filter);

        forceLoad();
        super.onStartLoading();
    }

    @Nullable
    @Override
    public ArrayList<String> loadInBackground() {
        if (isReset())
            return null;


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<String> array = new ArrayList<String>();
        char[] chars = "ioiwpdwewpewioe90ew09398oij32o3200=29j-932=932092oike[09j09j0m93j0ek93o40943090309340j3m=-9jr[3m[rm[-9rei09rev[ejrejreka]".toCharArray();

        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            String randomString = "";
            for (int j = 0; j < 20; j++) {
                randomString = randomString + chars[random.nextInt(chars.length)];
            }
            randomString += "\n";
            array.add(randomString);
        }



        return array;
    }

    @Override
    public void deliverResult(@Nullable ArrayList<String> data) {
        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onReset() {
        getContext().unregisterReceiver(mReceiver);
        super.onReset();
    }

    public class Receiver extends BroadcastReceiver {

        private RandomStringLoader loader;

        public Receiver(RandomStringLoader loader) {
            this.loader = loader;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            this.loader.onContentChanged();
        }
    }
}
