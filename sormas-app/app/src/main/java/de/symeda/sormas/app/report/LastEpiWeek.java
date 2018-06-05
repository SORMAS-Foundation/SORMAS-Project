package de.symeda.sormas.app.report;

import java.util.Date;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.core.Callback;

/**
 * Created by Orson on 24/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class LastEpiWeek extends BaseEpiWeekCategory {
    @Override
    public boolean isMatch(EpiWeek epiWeek) {
        return DateHelper.getPreviousEpiWeek(new Date()).equals(epiWeek);
    }

    @Override
    public void processReport(EpiWeek epiWeek, User user, Callback.IAction2<String, BaseEpiWeekCategory> callback) {
        mReport = DatabaseHelper.getWeeklyReportDao().queryForEpiWeek(epiWeek, user);

        if (mReport != null) {
            mShowReportTable = true; 
            mShowPendingReport = false; 
            mShowWeeklyReport = true; 
            mShowReportNotSubmittedNotification = false; 
            mShowNoReportNotification = false; 
            mShowNoDataNotification = false; 
            mShowAddMissingButton = false; 
            mShowConfirmButton = false; 
        } else {
            boolean hasUserRight = ConfigProvider.getUser().hasUserRight(UserRight.WEEKLYREPORT_CREATE);

            mShowReportTable = true; 
            mShowPendingReport = true; 
            mShowWeeklyReport = false; 
            mShowReportNotSubmittedNotification = true; 
            mShowNoReportNotification = false; 
            mShowNoDataNotification = false; 
            mShowAddMissingButton = true && hasUserRight; 
            mShowConfirmButton = true && hasUserRight; 
        }

        callback.call(mReport != null? DateHelper.formatShortDate(mReport.getReportDateTime()) : null, this);
    }
}
