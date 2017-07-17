package de.symeda.sormas.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Date;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.task.TaskNotificationService;

/**
 * Created by Stefan Szczesny on 15.11.2016.
 * Receive the android system status broadcasts.
 */

public class SormasBootstrap extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

                TaskNotificationService.startTaskNotificationAlarm(context);
            }
        }
}
