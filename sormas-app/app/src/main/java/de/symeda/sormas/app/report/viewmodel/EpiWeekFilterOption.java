package de.symeda.sormas.app.report.viewmodel;

public enum EpiWeekFilterOption {

    LAST_WEEK("Last Week"),
    THIS_WEEK("This Week"),
    SPECIFY_WEEK("Specify");

    private String displayName;
    EpiWeekFilterOption(String name){displayName = name;}

    public String toString() {
        return displayName;
    }
}
