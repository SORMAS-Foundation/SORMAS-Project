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

/**
 * Created by Orson on 28/12/2017.
 */

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

    public static void gotoDashboard(Context context) {

    }

    public static void gotoCase(Context context) {
        InvestigationStatus status = InvestigationStatus.PENDING; // statusFilters[menuItem.getKey()];
        IListNavigationCapsule dataCapsule = new ListNavigationCapsule(context, status, SearchBy.BY_FILTER_STATUS);
        CaseListActivity.goToActivity(context, dataCapsule);
    }

    public static void gotoContact(Context context) {
        FollowUpStatus status = FollowUpStatus.NO_FOLLOW_UP; // statusFilters[menuItem.getKey()];
        IListNavigationCapsule dataCapsule = new ListNavigationCapsule(context, status, SearchBy.BY_FILTER_STATUS);
        ContactListActivity.goToActivity(context, dataCapsule);
    }

    public static void gotoEvent(Context context) {
        EventStatus status = EventStatus.POSSIBLE; // statusFilters[menuItem.getKey()];
        IListNavigationCapsule dataCapsule = new ListNavigationCapsule(context, status, SearchBy.BY_FILTER_STATUS);
        EventListActivity.goToActivity(context, dataCapsule);
    }

    public static void gotoSample(Context context) {
        ShipmentStatus status = ShipmentStatus.NOT_SHIPPED;// statusFilters[menuItem.getKey()];
        IListNavigationCapsule dataCapsule = new ListNavigationCapsule(context, status, SearchBy.BY_FILTER_STATUS);
        SampleListActivity.goToActivity(context, dataCapsule);
    }

    public static void gotoTask(Context context) {
        TaskStatus status = TaskStatus.PENDING;// statusFilters[menuItem.getKey()];
        IListNavigationCapsule dataCapsule = new ListNavigationCapsule(context, status, SearchBy.BY_FILTER_STATUS);
        TaskListActivity.goToActivity(context, dataCapsule);
    }

    public static void gotoReport(Context context) {

    }

    public static void gotoNewCase(Context context) {
        CaseFormNavigationCapsule dataCapsule = (CaseFormNavigationCapsule)new CaseFormNavigationCapsule(context,
                null).setEditPageStatus(InvestigationStatus.PENDING).setPersonUuid(null);
        CaseNewActivity.goToActivity(context, dataCapsule);
    }
}
