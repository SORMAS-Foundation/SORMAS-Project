package de.symeda.sormas.app.util;

import android.content.Context;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.caze.list.CaseListActivity;
import de.symeda.sormas.app.contact.list.ContactListActivity;
import de.symeda.sormas.app.core.IListNavigationCapsule;
import de.symeda.sormas.app.core.ListNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.event.list.EventListActivity;
import de.symeda.sormas.app.sample.list.SampleListActivity;
import de.symeda.sormas.app.shared.ShipmentStatus;
import de.symeda.sormas.app.task.list.TaskListActivity;

/**
 * Created by Orson on 07/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class LandingNavigationHelper {


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
}
