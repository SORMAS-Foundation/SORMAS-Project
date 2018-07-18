package de.symeda.sormas.app.menu;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.caze.edit.CaseNewActivity;
import de.symeda.sormas.app.dashboard.DashboardActivity;
import de.symeda.sormas.app.report.ReportLandingActivity;
import de.symeda.sormas.app.settings.SettingsActivity;
import de.symeda.sormas.app.util.NavigationHelper;

/**
 * Created by Orson on 22/11/2017.
 */
public class MainMenuItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {

    private BaseActivity activity;
    private DrawerLayout drawer;


    public MainMenuItemSelectedListener(BaseActivity activity, DrawerLayout drawer) {
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
        NavigationHelper.goToCases(activity);
    }

    public void showCaseNewView() {
        Intent intent = new Intent(activity, CaseNewActivity.class);
        activity.startActivity(intent);
    }

    public void showContactsView() {
        NavigationHelper.goToContacts(activity);
    }

    public void showEventsView() {
        NavigationHelper.goToEvents(activity);
    }

    public void showSamplesView() {
        NavigationHelper.goToSamples(activity);
    }

    public void showTasksView() {
        NavigationHelper.goToTasks(activity);
    }






    public void showReportsView() {
        Intent intent = new Intent(activity, ReportLandingActivity.class);
        activity.startActivity(intent);
    }

    public void showSettingsView() {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivity(intent);
    }
}
