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
import de.symeda.sormas.app.core.IListNavigationCapsule;
import de.symeda.sormas.app.core.ListNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.event.list.EventListActivity;
import de.symeda.sormas.app.sample.list.SampleListActivity;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.shared.ShipmentStatus;
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

    public static void goToCases(Context context) {
        InvestigationStatus status = InvestigationStatus.PENDING;
        IListNavigationCapsule dataCapsule = new ListNavigationCapsule(context, status, SearchBy.BY_FILTER_STATUS);
        CaseListActivity.goToActivity(context, dataCapsule);
    }

    public static void goToContacts(Context context) {
        FollowUpStatus status = FollowUpStatus.NO_FOLLOW_UP;
        IListNavigationCapsule dataCapsule = new ListNavigationCapsule(context, status, SearchBy.BY_FILTER_STATUS);
        ContactListActivity.goToActivity(context, dataCapsule);
    }

    public static void goToEvents(Context context) {
        EventStatus status = EventStatus.POSSIBLE;
        IListNavigationCapsule dataCapsule = new ListNavigationCapsule(context, status, SearchBy.BY_FILTER_STATUS);
        EventListActivity.goToActivity(context, dataCapsule);
    }

    public static void goToSamples(Context context) {
        ShipmentStatus status = ShipmentStatus.NOT_SHIPPED;
        IListNavigationCapsule dataCapsule = new ListNavigationCapsule(context, status, SearchBy.BY_FILTER_STATUS);
        SampleListActivity.goToActivity(context, dataCapsule);
    }

    public static void goToTasks(Context context) {
        TaskStatus status = TaskStatus.PENDING;
        IListNavigationCapsule dataCapsule = new ListNavigationCapsule(context, status, SearchBy.BY_FILTER_STATUS);
        TaskListActivity.goToActivity(context, dataCapsule);
    }

    public static void goToNewCase(Context context) {
        CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(context).setPersonUuid(null);
        CaseNewActivity.goToActivity(context, dataCapsule);
    }
}
