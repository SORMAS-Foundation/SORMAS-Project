package de.symeda.sormas.app.menu;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import de.symeda.sormas.app.AbstractSormasActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.caze.edit.CaseNewActivity;
import de.symeda.sormas.app.caze.landing.CasesLandingActivity;
import de.symeda.sormas.app.contact.landing.ContactsLandingActivity;
import de.symeda.sormas.app.dashboard.DashboardActivity;
import de.symeda.sormas.app.event.landing.EventsLandingActivity;
import de.symeda.sormas.app.report.ReportsLandingActivity;
import de.symeda.sormas.app.sample.landing.SampleLandingActivity;
import de.symeda.sormas.app.settings.SettingsActivity;
import de.symeda.sormas.app.task.landing.TasksLandingActivity;

/**
 * Created by Orson on 22/11/2017.
 */
public class MainMenuItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {

    private AbstractSormasActivity activity;
    private DrawerLayout drawer;


    public MainMenuItemSelectedListener(AbstractSormasActivity activity, DrawerLayout drawer) {
        this.activity = activity;
        this.drawer = drawer;
        //this.activity = activity;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_item_dashboard) {
            showDashboardView();
        } else if (id == R.id.menu_item_tasks) {
            showTasksView();
        } else if (id == R.id.menu_item_cases) {
            showCasesView();
        } else if (id == R.id.menu_item_contacts) {
            showContactsView();
        } else if (id == R.id.menu_item_events) {
            showEventsView();
        } else if (id == R.id.menu_item_samples) {
            showSamplesView();
        } else if (id == R.id.menu_item_reports) {
           showReportsView();
        }

        // necessary to prevent the drawer from staying open when the same entry is selected
        drawer.closeDrawers();

        return true;
    }


    public void showDashboardView() {
        Intent intent = new Intent(activity, DashboardActivity.class);
        activity.startActivity(intent);
    }

    public void showCasesView() {
        Intent intent = new Intent(activity, CasesLandingActivity.class);
        activity.startActivity(intent);
    }

    public void showCaseNewView() {
        Intent intent = new Intent(activity, CaseNewActivity.class);
        activity.startActivity(intent);
    }

    public void showContactsView() {
        Intent intent = new Intent(activity, ContactsLandingActivity.class);
        activity.startActivity(intent);
    }

    public void showTasksView() {
        Intent intent = new Intent(activity, TasksLandingActivity.class);
        activity.startActivity(intent);
    }

    public void showEventsView() {
        Intent intent = new Intent(activity, EventsLandingActivity.class);
        activity.startActivity(intent);
    }

    public void showSamplesView() {
        Intent intent = new Intent(activity, SampleLandingActivity.class);
        activity.startActivity(intent);
    }

    public void showReportsView() {
        Intent intent = new Intent(activity, ReportsLandingActivity.class);
        activity.startActivity(intent);
    }

    public void showSettingsView() {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivity(intent);
    }
}
