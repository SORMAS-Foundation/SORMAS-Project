package de.symeda.sormas.app.report;

import java.util.Date;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.core.Callback;

/**
 * Created by Orson on 24/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class OtherEpiWeek extends BaseEpiWeekCategory {
    @Override
    public boolean isMatch(EpiWeek epiWeek) {
        return !DateHelper.getEpiWeek(new Date()).equals(epiWeek) &&
                !DateHelper.getPreviousEpiWeek(new Date()).equals(epiWeek);
    }

    @Override
    public void processReport(EpiWeek epiWeek, User user, Callback.IAction2<String, BaseEpiWeekCategory> callback) {
        mReport = DatabaseHelper.getWeeklyReportDao().queryForEpiWeek(epiWeek, user);

        if (mReport != null) {
            mShowReportTable = false;
            mShowWeeklyReport = true;
        } else {
            if (DateHelper.isEpiWeekAfter(DateHelper.getEpiWeek(new Date()), epiWeek)) {
                mShowNoDataNotification = true;
            } else {
                mShowReportTable = false;
                mShowPendingReport = true;
                mShowReportNotSubmittedNotification = true;
            }
        }




        if (mReport != null) {
            mShowReportTable = false; 
            mShowPendingReport = false; 
            mShowWeeklyReport = true; 
            mShowReportNotSubmittedNotification = false; 
            mShowNoReportNotification = false; 
            mShowNoDataNotification = false; 
            mShowAddMissingButton = false; 
            mShowConfirmButton = false; 
        } else {
            if (DateHelper.isEpiWeekAfter(DateHelper.getEpiWeek(new Date()), epiWeek)) {
                mShowReportTable = false; 
                mShowPendingReport = false; 
                mShowWeeklyReport = false; 
                mShowReportNotSubmittedNotification = false; 
                mShowNoReportNotification = false; 
                mShowNoDataNotification = true; 
                mShowAddMissingButton = false; 
                mShowConfirmButton = false; 
            } else {
                mShowReportTable = true; 
                mShowPendingReport = true; 
                mShowWeeklyReport = false; 
                mShowReportNotSubmittedNotification = true; 
                mShowNoReportNotification = false; 
                mShowNoDataNotification = false; 
                mShowAddMissingButton = false; 
                mShowConfirmButton = false; 
            }
        }

        callback.call(mReport != null? DateHelper.formatShortDate(mReport.getReportDateTime()) : null, this);
    }
}
