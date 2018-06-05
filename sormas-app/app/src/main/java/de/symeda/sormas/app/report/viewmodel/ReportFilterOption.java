package de.symeda.sormas.app.report.viewmodel;

/**
 * Created by Orson on 24/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public enum ReportFilterOption {

    LAST_WEEK("Last Week"),
    THIS_WEEK("This Week"),
    SPECIFY_WEEK("Specify");

    private String displayName;
    ReportFilterOption(String name){displayName = name;}

    public String toString() {
        return displayName;
    }
}
