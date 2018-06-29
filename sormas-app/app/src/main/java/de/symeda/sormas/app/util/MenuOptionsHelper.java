package de.symeda.sormas.app.util;

import android.view.MenuItem;

import de.symeda.sormas.app.AbstractSormasActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReportActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.dialog.UserReportDialog;

/**
 * Created by Orson on 16/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class MenuOptionsHelper {

    public static boolean handleListModuleOptionsItemSelected(BaseReportActivity activity, MenuItem item) {
        switch(item.getItemId()) {
            case R.id.option_menu_action_sync:
                activity.synchronizeChangedData();
                return true;

            case R.id.option_menu_action_markAllAsRead:
                MarkAllAsReadHelper.markCasesAsRead(activity, null);
                return true;

            // Report problem button
            case R.id.action_report:
                showUserReportDialog(activity);

                return true;
        }

        return false;
    }

    public static boolean handleListModuleOptionsItemSelected(BaseListActivity activity, MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_new:
                activity.goToNewView();
                return true;

            case R.id.option_menu_action_sync:
                activity.synchronizeChangedData();
                return true;

            case R.id.option_menu_action_markAllAsRead:
                MarkAllAsReadHelper.markCasesAsRead(activity, null);
                return true;

            // Report problem button
            case R.id.action_report:
                showUserReportDialog(activity);

                return true;
        }

        return false;
    }

    public static boolean handleReadModuleOptionsItemSelected(BaseReadActivity activity, MenuItem item) {
        //TODO: Implement the help action

        switch(item.getItemId()) {
            case android.R.id.home:
                NavigationHelper.navigateUpFrom(activity);
                return true;

            case R.id.action_edit:
                activity.gotoEditView();
                return true;

            // Report problem button
            case R.id.action_report:
                showUserReportDialog(activity);

                return true;
        }

        return false;
    }

    public static boolean handleEditModuleOptionsItemSelected(BaseEditActivity activity, MenuItem item) {
        //TODO: Implement the help action

        switch(item.getItemId()) {
            case android.R.id.home:
                NavigationHelper.navigateUpFrom(activity);
                return true;

            case R.id.action_new:
                activity.goToNewView();
                return true;

            case R.id.action_save:
                activity.saveData();
                return true;

            // Report problem button
            case R.id.action_report:
                showUserReportDialog(activity);

                return true;
        }

        /*if (callback.call(item.getItemId()))
            return true;*/

        return false;
    }

    private static void showUserReportDialog(AbstractSormasActivity activity) {
        UserReportDialog userReportDialog = new UserReportDialog(activity, activity.getClass().getSimpleName(), null);
        userReportDialog.show(null);
    }
}
