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

import de.symeda.sormas.api.region.RegionReferenceDto;

public class WeeklyReportRegionSummaryDto implements Serializable {

	private static final long serialVersionUID = -8776688104892901821L;

	public static final String I18N_PREFIX = "WeeklyReportRegionSummary";

	public static final String REGION = "region";
	public static final String OFFICERS = "officers";
	public static final String OFFICER_REPORTS = "officerReports";
	public static final String OFFICER_CASE_REPORTS = "officerCaseReports";
	public static final String OFFICER_ZERO_REPORTS = "officerZeroReports";
	public static final String OFFICER_MISSING_REPORTS = "officerMissingReports";
	public static final String OFFICER_REPORT_PERCENTAGE = "officerReportPercentage";
	public static final String INFORMANTS = "informants";
	public static final String INFORMANT_REPORTS = "informantReports";
	public static final String INFORMANT_CASE_REPORTS = "informantCaseReports";
	public static final String INFORMANT_ZERO_REPORTS = "informantZeroReports";
	public static final String INFORMANT_MISSING_REPORTS = "informantMissingReports";
	public static final String INFORMANT_REPORT_PERCENTAGE = "informantReportPercentage";

	private RegionReferenceDto region;
	private int officers;
	private int informants;
	private int officerCaseReports;
	private int officerZeroReports;
	private int informantCaseReports;
	private int informantZeroReports;

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public int getOfficers() {
		return officers;
	}

	public void setOfficers(int officers) {
		this.officers = officers;
	}

	public int getInformants() {
		return informants;
	}

	public void setInformants(int informants) {
		this.informants = informants;
	}

	public int getOfficerCaseReports() {
		return officerCaseReports;
	}

	public void setOfficerCaseReports(int officerCaseReports) {
		this.officerCaseReports = officerCaseReports;
	}

	public int getOfficerZeroReports() {
		return officerZeroReports;
	}

	public void setOfficerZeroReports(int officerZeroReports) {
		this.officerZeroReports = officerZeroReports;
	}

	public int getOfficerMissingReports() {
		return officers - getOfficerReports();
	}

	public int getOfficerReports() {
		return officerCaseReports + officerZeroReports;
	}

	public int getOfficerReportPercentage() {
		if (officers > 0) {
			return 100 * getOfficerReports() / officers;
		}
		return 0;
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
