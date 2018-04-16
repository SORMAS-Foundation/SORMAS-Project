package de.symeda.sormas.app.util;

import android.view.MenuItem;
import android.view.View;

import java.util.Date;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.dialog.MissingWeeklyReportDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.component.dialog.UserReportDialog;

/**
 * Created by Orson on 16/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class MenuOptionsHelper {


    public static boolean handleListModuleOptionsItemSelected(BaseListActivity activity, MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_new:
                EpiWeek lastEpiWeek = DateHelper.getPreviousEpiWeek(new Date());
                User user = ConfigProvider.getUser();
                if (user.hasUserRole(UserRole.INFORMANT)
                        && DatabaseHelper.getWeeklyReportDao().queryForEpiWeek(lastEpiWeek, ConfigProvider.getUser()) == null) {

                    MissingWeeklyReportDialog confirmationDialog = new MissingWeeklyReportDialog(activity);

                    confirmationDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                        @Override
                        public void onOkClick(View v, Object item, View viewRoot) {
                            /*Intent intent = new Intent(CaseListActivity.this, ReportsActivity.class);
                            startActivity(intent);*/
                        }
                    });

                    confirmationDialog.show(null);
                } else {
                    activity.gotoNewView();
                }
                return true;

            case R.id.option_menu_action_sync:
                activity.synchronizeChangedData();
                return true;

            case R.id.option_menu_action_markAllAsRead:
                MarkAllAsReadHelper.markCases(activity, null);
                return true;

            // Report problem button
            case R.id.action_report:
                UserReportDialog userReportDialog = new UserReportDialog(activity, activity.getClass().getSimpleName(), null);
                userReportDialog.show(null);

                return true;
        }

        return false;
    }
}
