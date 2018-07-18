package de.symeda.sormas.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.symeda.sormas.app.core.TaskNotificationService;

public class SormasBootstrap extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            TaskNotificationService.startTaskNotificationAlarm(context);
        }
    }
}

