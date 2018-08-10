package de.symeda.sormas.app.report.viewmodel;

import de.symeda.sormas.api.Disease;

public class PendingReportViewModel {

    private Disease mDisease;
    private int nNumberOfCases;

    public PendingReportViewModel(Disease disease, int numberOfCases) {
        this.mDisease = disease;
        this.nNumberOfCases = numberOfCases;
    }

    public Disease getDisease() {
        return mDisease;
    }

    public int getNumberOfCases() {
        return nNumberOfCases;
    }
}
