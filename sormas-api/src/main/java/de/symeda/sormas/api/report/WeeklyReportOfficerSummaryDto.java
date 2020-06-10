/*******************************************************************************
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.report;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public class WeeklyReportOfficerSummaryDto implements Serializable {

	private static final long serialVersionUID = -4256242450263049614L;

	public static final String I18N_PREFIX = "WeeklyReportOfficerSummary";

	public static final String OFFICER = "officer";
	public static final String DISTRICT = "district";
	public static final String OFFICER_REPORT_DATE = "officerReportDate";
	public static final String TOTAL_CASE_COUNT = "totalCaseCount";
	public static final String INFORMANTS = "informants";
	public static final String INFORMANT_REPORTS = "informantReports";
	public static final String INFORMANT_CASE_REPORTS = "informantCaseReports";
	public static final String INFORMANT_ZERO_REPORTS = "informantZeroReports";
	public static final String INFORMANT_MISSING_REPORTS = "informantMissingReports";
	public static final String INFORMANT_REPORT_PERCENTAGE = "informantReportPercentage";

	private UserReferenceDto officer;
	private DistrictReferenceDto district;
	private Date officerReportDate;
	private int totalCaseCount;
	private int informants;
	private int informantCaseReports;
	private int informantZeroReports;

	public UserReferenceDto getOfficer() {
		return officer;
	}

	public void setOfficer(UserReferenceDto officer) {
		this.officer = officer;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public Date getOfficerReportDate() {
		return officerReportDate;
	}

	public void setOfficerReportDate(Date officerReportDate) {
		this.officerReportDate = officerReportDate;
	}

	public int getTotalCaseCount() {
		return totalCaseCount;
	}

	public void setTotalCaseCount(int totalCaseCount) {
		this.totalCaseCount = totalCaseCount;
	}

	public int getInformants() {
		return informants;
	}

	public void setInformants(int informants) {
		this.informants = informants;
	}

	public int getInformantCaseReports() {
		return informantCaseReports;
	}

	public void setInformantCaseReports(int informantCaseReports) {
		this.informantCaseReports = informantCaseReports;
	}

	public int getInformantZeroReports() {
		return informantZeroReports;
	}

	public void setInformantZeroReports(int informantZeroReports) {
		this.informantZeroReports = informantZeroReports;
	}

	public int getInformantMissingReports() {
		return informants - getInformantReports();
	}

	public int getInformantReports() {
		return informantCaseReports + informantZeroReports;
	}

	public int getInformantReportPercentage() {

		if (informants > 0) {
			return 100 * getInformantReports() / informants;
		}
		return 0;
	}
}
