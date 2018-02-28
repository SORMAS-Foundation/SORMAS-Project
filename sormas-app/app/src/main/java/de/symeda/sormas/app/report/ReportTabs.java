package de.symeda.sormas.app.report;

import de.symeda.sormas.api.I18nProperties;

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
