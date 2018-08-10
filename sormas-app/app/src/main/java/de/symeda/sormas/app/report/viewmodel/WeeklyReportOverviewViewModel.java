package de.symeda.sormas.app.report.viewmodel;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.report.ReportedCasesStatus;

public class WeeklyReportOverviewViewModel {

    private Facility mHealthFacility;
    private User mUser;
    private int nNumberOfCases;
    private ReportedCasesStatus nReportedCasesStatus;

    public WeeklyReportOverviewViewModel(Facility healthFacility, User user, int numberOfCases, boolean casesConfirmed) {
        this.mHealthFacility = healthFacility;
        this.mUser = user;
        this.nNumberOfCases = numberOfCases;
        this.nReportedCasesStatus = casesConfirmed? ReportedCasesStatus.CONFIRMED : ReportedCasesStatus.UNCONFIRMED;
    }

    public Facility getHealthFacility() {
        return mHealthFacility;
    }

    public User getUser() {
        return mUser;
    }

    public int getNumberOfCases() {
        return nNumberOfCases;
    }

    public ReportedCasesStatus getReportedCaseStatus() {
        return nReportedCasesStatus;
    }
}
