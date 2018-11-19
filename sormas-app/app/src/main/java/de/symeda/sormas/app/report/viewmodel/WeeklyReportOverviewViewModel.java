/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
