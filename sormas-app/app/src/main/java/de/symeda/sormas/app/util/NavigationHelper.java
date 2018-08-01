package de.symeda.sormas.app.util;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.caze.edit.CaseNewActivity;
import de.symeda.sormas.app.caze.list.CaseListActivity;
import de.symeda.sormas.app.contact.list.ContactListActivity;
import de.symeda.sormas.app.dashboard.DashboardActivity;
import de.symeda.sormas.app.event.list.EventListActivity;
import de.symeda.sormas.app.report.ReportLandingActivity;
import de.symeda.sormas.app.sample.ShipmentStatus;
import de.symeda.sormas.app.sample.list.SampleListActivity;
import de.symeda.sormas.app.task.list.TaskListActivity;

public class NavigationHelper {

    public static void navigateUpFrom(AppCompatActivity activity) {
        Intent upIntent = NavUtils.getParentActivityIntent(activity);
        if (NavUtils.shouldUpRecreateTask(activity, upIntent)) {
            TaskStackBuilder.create(activity)
                    .addNextIntentWithParentStack(upIntent)
                    .startActivities();
        } else {
            NavUtils.navigateUpTo(activity, upIntent);
        }
    }

    public static void goToDashboard(Context context) {
        Intent intent = new Intent(context, DashboardActivity.class);
        context.startActivity(intent);
    }

    public static void goToCases(Context context) {
        CaseListActivity.startActivity(context, InvestigationStatus.PENDING);
    }

    public static void goToContacts(Context context) {
        ContactListActivity.startActivity(context, FollowUpStatus.NO_FOLLOW_UP);
    }

    public static void goToEvents(Context context) {
        EventListActivity.startActivity(context, EventStatus.POSSIBLE);
    }

    public static void goToSamples(Context context) {
        SampleListActivity.startActivity(context, ShipmentStatus.NOT_SHIPPED);
    }

    public static void goToTasks(Context context) {
        TaskListActivity.startActivity(context, TaskStatus.PENDING);
    }

    public static void goToReports(Context context) {
        Intent intent = new Intent(context, ReportLandingActivity.class);
        context.startActivity(intent);
    }

    public static void goToNewCase(Context context) {
        CaseNewActivity.startActivity(context);
    }
}
