package de.symeda.sormas.app.report;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.report.WeeklyReport;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.core.Callback;

/**
 * Created by Orson on 24/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public abstract class BaseEpiWeekCategory implements IReportDate {

    protected WeeklyReport mReport;

    protected boolean mShowReportTable;
    protected boolean mShowPendingReport;
    protected boolean mShowWeeklyReport;
    protected boolean mShowReportNotSubmittedNotification;
    protected boolean mShowNoReportNotification;
    protected boolean mShowNoDataNotification;
    protected boolean mShowAddMissingButton;
    protected boolean mShowConfirmButton;

    @Override
    public String getReportDate() {
        if (mReport != null)
            return DateHelper.formatLocalShortDate(mReport.getReportDateTime());

        return null;
    }

    protected WeeklyReport getPreviousEpiWeek(EpiWeek epiWeek, User user) {
        return DatabaseHelper.getWeeklyReportDao().queryForEpiWeek(DateHelper.getPreviousEpiWeek(epiWeek), user);
    }

    public abstract boolean isMatch(EpiWeek epiWeek);

    public abstract void processReport(EpiWeek epiWeek, User user, Callback.IAction2<String, BaseEpiWeekCategory> callback);

    public boolean showReportTable() {
        return mShowReportTable;
    }

    public boolean showPendingReport() {
        return mShowPendingReport;
    }

    public boolean showWeeklyReport() {
        return mShowWeeklyReport;
    }

    public boolean showShowReportNotSubmittedNotification() {
        return mShowReportNotSubmittedNotification;
    }

    public boolean showNoReportNotification() {
        return mShowNoReportNotification;
    }

    public boolean showNoDataNotification() {
        return mShowNoDataNotification;
    }

    public boolean showAddMissingButton() {
        return mShowAddMissingButton;
    }

    public boolean showConfirmButton() {
        return mShowConfirmButton;
    }

    public WeeklyReport getReport() {
        return mReport;
    }
}
