package de.symeda.sormas.app.task;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.task.TaskDao;
import de.symeda.sormas.app.caze.CasesActivity;
import de.symeda.sormas.app.caze.CasesListFragment;

/**
 * Created by Stefan Szczesny on 15.11.2016.
 */

public class TaskNotificationService extends Service {

    private int NOTIFICATION_ID = 0;
    private CaseDao caseDAO;
    private PersonDao personDAO;

    @Override
    public void onCreate() {
        super.onCreate();

        caseDAO = DatabaseHelper.getCaseDao();
        personDAO = DatabaseHelper.getPersonDao();
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

        new SyncTasksTask() {
            @Override
            protected void onPostExecute(Void aVoid) {
                doTaskNotification();
            }
        }
        .execute();

        return super.onStartCommand(intent, flags, startId);
    }

    private void doTaskNotification() {
        Date notificationRangeStart = ConfigProvider.getLastNotificationDate();
        if (notificationRangeStart == null) {
            notificationRangeStart = new DateTime().minusDays(1).toDate();
        }
        // start after the last check
        notificationRangeStart = new Date(notificationRangeStart.getTime()+1);

        Date notificationRangeEnd = new Date();

        TaskDao taskDao = DatabaseHelper.getTaskDao();
        List<Task> taskList = taskDao.queryPendingForNotification(notificationRangeStart, notificationRangeEnd);

        for (Task task:taskList) {
            Intent notificationIntent = new Intent(this, TaskEditActivity.class);
            notificationIntent.putExtra(Task.UUID, task.getUuid());

            Case caze = caseDAO.queryForId(task.getCaze().getId());
            Person person = personDAO.queryForId(caze.getPerson().getId());

            PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            Resources r = getResources();
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(r.getString(R.string.headline_task_notification))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(task.getTaskType().toString())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(
                            task.getCreatorComment()
                            +"<br><br>"
                            +"<b>"
                            + person.toString() + " (" + DataHelper.getShortUuid(caze.getUuid()) +")"
                            +"</b>")))

                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notification);

            break; // @TODO implement notification grouping
        }

        ConfigProvider.setLastNotificationDate(notificationRangeEnd);
    }

    public static void startTaskNotificationAlarm(Context context) {
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, TaskNotificationService.class);
        PendingIntent alarmIntent = PendingIntent.getService(context, 0, intent, 0);

        // setRepeating() lets you specify a precise custom interval--in this case, 5 minutes.
        Date now = new Date();
        alarmMgr.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                now.getTime(), // TODO start at full XX:X5 minute
                1000 * 20,//60 * 5,
                alarmIntent);
    }
}
