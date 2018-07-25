package de.symeda.sormas.app.report;

import java.util.Date;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.core.Callback;

/**
 * Created by Orson on 24/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class CurrentEpiWeek extends BaseEpiWeekCategory {

    @Override
    public boolean isMatch(EpiWeek epiWeek) {
        return DateHelper.getEpiWeek(new Date()).equals(epiWeek);
    }

    @Override
    public void processReport(EpiWeek epiWeek, User user, Callback.IAction2<String, BaseEpiWeekCategory> callback) {
        mReport = getPreviousEpiWeek(epiWeek, user);

        if (mReport != null) {
            mShowReportTable = true; 
            mShowPendingReport = true; 
            mShowWeeklyReport = false; 
            mShowReportNotSubmittedNotification = true; 
            mShowNoReportNotification = false; 
            mShowNoDataNotification = false; 
            mShowAddMissingButton = false; 
            mShowConfirmButton = false; 
        } else {
            mShowReportTable = false; 
            mShowPendingReport = false; 
            mShowWeeklyReport = false;  
            mShowReportNotSubmittedNotification = true; 
            mShowNoReportNotification = true; 
            mShowNoDataNotification = false; 
            mShowAddMissingButton = false; 
            mShowConfirmButton = false; 
        }

        callback.call(mReport != null? DateHelper.formatLocalShortDate(mReport.getReportDateTime()) : null, this);
    }
}
