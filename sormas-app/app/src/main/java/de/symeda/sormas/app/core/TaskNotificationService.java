/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.core;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventDao;
import de.symeda.sormas.app.backend.report.WeeklyReport;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.task.TaskDao;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.report.ReportActivity;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.task.edit.TaskEditActivity;

public class TaskNotificationService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // don't sync, when user is currently editing data
        BaseActivity activeActivity = BaseActivity.getActiveActivity();
        if (activeActivity == null || !activeActivity.isEditing()) {
            // only when we do have a user and there is currently no other connection
            if (ConfigProvider.getUser() != null && !RetroProvider.isConnectedOrConnecting()) {

                RetroProvider.connectAsync(getApplicationContext(), false,
                        (result, versionCompatible) -> {

                            if (result.getResultStatus().isSuccess()) {
                                SynchronizeDataAsync.call(SynchronizeDataAsync.SyncMode.Changes, this,
                                        (syncFailed, syncFailedMessage) -> {
                                            RetroProvider.disconnect();
                                        });
                            }
                        });
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public static void doTaskNotification(Context context) {
        Date notificationRangeStart = ConfigProvider.getLastNotificationDate();
        if (notificationRangeStart == null) {
            notificationRangeStart = new DateTime().minusDays(1).toDate();
        }
        Date notificationRangeEnd = new Date();

        TaskDao taskDao = DatabaseHelper.getTaskDao();
        List<Task> taskList = taskDao.queryMyPendingForNotification(notificationRangeStart, notificationRangeEnd);

        CaseDao caseDAO = DatabaseHelper.getCaseDao();
        ContactDao contactDAO = DatabaseHelper.getContactDao();
        EventDao eventDAO = DatabaseHelper.getEventDao();

        for (Task task : taskList) {
            Case caze = null;
            Contact contact = null;
            Event event = null;
            StringBuilder content = new StringBuilder();
            switch (task.getTaskContext()) {
                case CASE:
                    if (task.getCaze() != null) {
                        caze = caseDAO.queryForId(task.getCaze().getId());
                        content.append("<b>").append(caze.toString()).append("</b><br/>");
                    }
                    break;
                case CONTACT:
                    if (task.getContact() != null) {
                        contact = contactDAO.queryForId(task.getContact().getId());
                        content.append("<b>").append(contact.toString()).append("</b><br/>");
                    }
                    break;
                case EVENT:
                    if (task.getEvent() != null) {
                        event = eventDAO.queryForId(task.getEvent().getId());
                        content.append("<b>").append(event.toString()).append("</b><br/>");
                    }
                    break;
                case GENERAL:
                    break;
                default:
                    continue;
            }

            Intent notificationIntent = new Intent(context, TaskEditActivity.class);
            notificationIntent.putExtras(TaskEditActivity.buildBundle(task.getUuid()).get());
            // Just for your information: The issue here was that the second argument of the getActivity call
            // was set to 0, which leads to previous intents to be recycled; passing the task's ID instead
            // makes sure that a new intent with the right task behind it is created
            PendingIntent pi = PendingIntent.getActivity(context, task.getId().intValue(), notificationIntent, 0);
            Resources r = context.getResources();

            if (!TextUtils.isEmpty(task.getCreatorComment())) {
                content.append(task.getCreatorComment());
            }

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NotificationHelper.NOTIFICATION_CHANNEL_TASKS_ID)
                    .setTicker(r.getString(R.string.heading_task_notification))
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setContentTitle(task.getTaskType().toString() + (caze != null ? " (" + caze.getDisease().toShortString() + ")" : contact != null ? " (" + contact.getDisease().toShortString() + ")" : ""))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(content.toString())))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setContentIntent(pi);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            int notificationId = task.getId().intValue();
            notificationManager.notify(notificationId, notificationBuilder.build());

            break; // @TODO implement notification grouping
        }

        doWeeklyReportNotification(context, notificationRangeStart, notificationRangeEnd);

        ConfigProvider.setLastNotificationDate(notificationRangeEnd);
    }

    private static void doWeeklyReportNotification(Context context, Date notificationRangeStart, Date notificationRangeEnd) {
        if (ConfigProvider.hasUserRight(UserRight.WEEKLYREPORT_CREATE)) {
            // notify at 6:00
            Date notificationPoint = DateHelper.addSeconds(DateHelper.getStartOfDay(new Date()), 60 * 60 * 6);
            if (DateHelper.isBetween(notificationPoint, notificationRangeStart, notificationRangeEnd)) {
                WeeklyReport weeklyReport = DatabaseHelper.getWeeklyReportDao().queryByEpiWeekAndUser(DateHelper.getPreviousEpiWeek(notificationPoint), ConfigProvider.getUser());
                if (weeklyReport == null) {

                    int notificationId = (int) notificationPoint.getTime();
                    Intent notificationIntent = new Intent(context, ReportActivity.class);
                    PendingIntent pi = PendingIntent.getActivity(context, notificationId, notificationIntent, 0);

                    String title = context.getResources().getString(R.string.action_submit_report);

                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NotificationHelper.NOTIFICATION_CHANNEL_TASKS_ID)
                            .setTicker(title)
                            .setSmallIcon(R.mipmap.ic_launcher_foreground)
                            .setContentTitle(title)
                            .setContentText(context.getResources().getString(R.string.hint_weekly_report_confirmation_required))
                            .setContentIntent(pi)
                            .setAutoCancel(true)
                            .setContentIntent(pi);

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(notificationId, notificationBuilder.build());
                }
            }
        }
    }

    public static void startTaskNotificationAlarm(Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, TaskNotificationService.class);
        PendingIntent alarmIntent = PendingIntent.getService(context, 1414, intent, 0);

        // setRepeating() lets you specify a precise custom interval--in this case, 5 minutes.
        Date now = new Date();
        alarmMgr.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                now.getTime(),
                1000 * 60 * 10, // sync every 10 minutes
                alarmIntent);
    }
}