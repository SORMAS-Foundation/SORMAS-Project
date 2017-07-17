package de.symeda.sormas.app.task;

import android.accounts.AuthenticatorException;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import org.joda.time.DateTime;

import java.net.ConnectException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.AbstractSormasActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.task.TaskDao;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.SyncCallback;

/**
 * Created by Stefan Szczesny on 15.11.2016.
 */

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
        AbstractSormasActivity activeActivity = AbstractSormasActivity.getActiveActivity();
        if (activeActivity == null || !activeActivity.isEditing()) {

            if (!RetroProvider.isConnected()) {
                try {
                    RetroProvider.connect(getApplicationContext());
                } catch (AuthenticatorException e) {
                    // do nothing
                } catch (RetroProvider.ApiVersionException e) {
                    // do nothing
                } catch (ConnectException e) {
                    // do nothing
                }
            }

            if (RetroProvider.isConnected()) {
                SynchronizeDataAsync.call(SynchronizeDataAsync.SyncMode.ChangesOnly, this, new SyncCallback() {
                    @Override
                    public void call(boolean syncFailed) {
                        if (syncFailed) {
                            RetroProvider.disconnect();
                        }
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
        // start after the last check
        notificationRangeStart = new Date(notificationRangeStart.getTime()+1);

        Date notificationRangeEnd = new Date();

        TaskDao taskDao = DatabaseHelper.getTaskDao();
        List<Task> taskList = taskDao.queryMyPendingForNotification(notificationRangeStart, notificationRangeEnd);

        CaseDao caseDAO = DatabaseHelper.getCaseDao();
        ContactDao contactDAO = DatabaseHelper.getContactDao();
        PersonDao personDAO = DatabaseHelper.getPersonDao();

        for (Task task:taskList) {
            Intent notificationIntent = new Intent(context, TaskEditActivity.class);
            notificationIntent.putExtra(Task.UUID, task.getUuid());

            Case caze = null;
            Contact contact = null;
            if(task.getCaze() != null) {
                caze = caseDAO.queryForId(task.getCaze().getId());
            }
            else if(task.getContact() != null) {
                contact = contactDAO.queryForId(task.getContact().getId());
            }
            else {
                continue;
            }

            Person person = caze != null ? personDAO.queryForId(caze.getPerson().getId()) : contact != null ? personDAO.queryForId(contact.getPerson().getId()) : null;

            // Just for your information: The issue here was that the second argument of the getActivity call
            // was set to 0, which leads to previous intents to be recycled; passing the task's ID instead
            // makes sure that a new intent with the right task behind it is created
            PendingIntent pi = PendingIntent.getActivity(context, task.getId().intValue(), notificationIntent, 0);
            Resources r = context.getResources();

            StringBuilder content = new StringBuilder();
            if(caze != null) {
                content.append("<b>").append(person.toString())
                        .append(" (").append(DataHelper.getShortUuid(caze.getUuid())).append(")</b><br/>");
            }
            if(contact != null) {
                content.append("<b>").append(person.toString())
                        .append(" (").append(DataHelper.getShortUuid(contact.getUuid())).append(")</b><br/>");
            }

            if (!TextUtils.isEmpty(task.getCreatorComment())) {
                content.append(task.getCreatorComment());
            }

            Notification notification = new NotificationCompat.Builder(context)
                    .setTicker(r.getString(R.string.headline_task_notification))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(task.getTaskType().toString() + " (" + (caze != null ? caze.getDisease().toShortString() : contact != null ? contact.getCaze().getDisease().toShortString() : "") + ")")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(content.toString())))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            int notificationId = task.getId().intValue();
            notificationManager.notify(notificationId, notification);

            break; // @TODO implement notification grouping
        }

        ConfigProvider.setLastNotificationDate(notificationRangeEnd);
    }

    public static void startTaskNotificationAlarm(Context context) {
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, TaskNotificationService.class);
        PendingIntent alarmIntent = PendingIntent.getService(context, 1414, intent, 0);

        // setRepeating() lets you specify a precise custom interval--in this case, 5 minutes.
        Date now = new Date();
        alarmMgr.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                now.getTime(), // TODO start at full XX:X5 minute, not somewhere in between
                1000 * 60 * 2, // TODO sync every 5 minutes - not 2
                alarmIntent);
    }
}