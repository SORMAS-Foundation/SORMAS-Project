package de.symeda.sormas.app.report.viewmodel;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.report.ReportedCasesStatus;

/**
 * Created by Orson on 25/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class WeeklyReportOverviewViewModel {

    private Facility mHealthFacility;
    private User mUser;
    private int nNumberOfCases;
    private ReportedCasesStatus nReportedCasesStatus;
    private UserRole mUserRole;

    public WeeklyReportOverviewViewModel(Facility healthFacility, User user, UserRole userRole, int numberOfCases, boolean casesConfirmed) {
        this.mHealthFacility = healthFacility;
        this.mUser = user;
        this.mUserRole = userRole;
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

    public String getUserRole() {
        if (mUserRole == null)
            return null;

        return mUserRole.toString();
    }
}
