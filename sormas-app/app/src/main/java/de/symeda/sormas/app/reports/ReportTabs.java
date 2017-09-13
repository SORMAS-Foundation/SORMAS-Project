package de.symeda.sormas.app.reports;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.report.WeeklyReportDto;

/**
 * Created by Mate Strysewske on 08.09.2017.
 */
public enum ReportTabs {
    WEEKLY_REPORT;

    public static ReportTabs fromInt(int x) {
        return ReportTabs.values()[x];
    }

    public String toString() {
        return I18nProperties.getFieldCaption(ReportsActivity.I18N_PREFIX + "." + this.name());
    }
}
