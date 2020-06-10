package de.symeda.sormas.app.core;

import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

public class FeatureConfigurationService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		DatabaseHelper.getFeatureConfigurationDao().deleteExpiredFeatureConfigurations();
		return super.onStartCommand(intent, flags, startId);
	}

	public static void startFeatureConfigurationService(Context context) {
		AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(context, FeatureConfigurationService.class);
		PendingIntent alarmIntent = PendingIntent.getService(context, 1515, intent, 0);

		Date date = DateHelper.getEndOfDay(new Date());
		alarmMgr.setInexactRepeating(
			AlarmManager.RTC_WAKEUP,
			date.getTime(),
			1000 * 60 * 60 * 24, // delete expired configurations every 24 hours
			alarmIntent);
	}
}
