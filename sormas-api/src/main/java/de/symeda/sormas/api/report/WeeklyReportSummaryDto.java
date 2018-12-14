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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.report;

import java.io.Serializable;

import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public class WeeklyReportSummaryDto implements Serializable {

	private static final long serialVersionUID = -8776688104892901821L;

	public static final String I18N_PREFIX = "WeeklyReportSummary";
	
	public static final String REGION = "region";
	public static final String OFFICER = "officer";
	public static final String DISTRICT = "district";
	public static final String OFFICERS = "officers";
	public static final String INFORMANTS = "informants";
	public static final String OFFICER_REPORTS = "officerReports";
	public static final String OFFICER_ZERO_REPORTS = "officerZeroReports";
	public static final String OFFICER_MISSING_REPORTS = "officerMissingReports";
	public static final String INFORMANT_REPORTS = "informantReports";
	public static final String INFORMANT_ZERO_REPORTS = "informantZeroReports";
	public static final String INFORMANT_MISSING_REPORTS = "informantMissingReports";
	
	private RegionReferenceDto region;
	private UserReferenceDto officer;
	private DistrictReferenceDto district;
	private int officers;
	private int informants;
	private int officerReports;
	private int officerZeroReports;
	private int officerMissingReports;
	private int informantReports;
	private int informantZeroReports;
	private int informantMissingReports;

	public RegionReferenceDto getRegion() {
		return region;
	}
	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}
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
	public int getOfficerReports() {
		return officerReports;
	}
	public void setOfficerReports(int officerReports) {
		this.officerReports = officerReports;
	}
	public int getOfficerZeroReports() {
		return officerZeroReports;
	}
	public void setOfficerZeroReports(int officerZeroReports) {
		this.officerZeroReports = officerZeroReports;
	}
	public int getOfficerMissingReports() {
		return officerMissingReports;
	}
	public void setOfficerMissingReports(int officerMissingReports) {
		this.officerMissingReports = officerMissingReports;
	}
	public int getInformantReports() {
		return informantReports;
	}
	public void setInformantReports(int informantReports) {
		this.informantReports = informantReports;
	}
	public int getInformantZeroReports() {
		return informantZeroReports;
	}
	public void setInformantZeroReports(int informantZeroReports) {
		this.informantZeroReports = informantZeroReports;
	}
	public int getInformantMissingReports() {
		return informantMissingReports;
	}
	public void setInformantMissingReports(int informantMissingReports) {
		this.informantMissingReports = informantMissingReports;
	}
	
}
