package de.symeda.sormas.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Orson on 21/11/2017.
 */

public class SormasBootstrap extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            //TaskNotificationService.startTaskNotificationAlarm(context);
        }
    }
}

