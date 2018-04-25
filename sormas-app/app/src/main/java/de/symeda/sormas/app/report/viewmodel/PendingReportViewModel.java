package de.symeda.sormas.app.report.viewmodel;

import de.symeda.sormas.api.Disease;

/**
 * Created by Orson on 25/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
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
