/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.adverseeventsfollowingimmunization.entity;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.common.QueryJoins;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.vaccination.Vaccination;

public class AefiInvestigationJoins extends QueryJoins<AefiInvestigation> {

	private Join<AefiInvestigation, Aefi> aefi;

	private AefiJoins aefiJoins;
	private Join<AefiInvestigation, Location> address;
	private Join<AefiInvestigation, Vaccination> primarySuspectVaccination;
	private Join<AefiInvestigation, User> reportingUser;
	private Join<AefiInvestigation, Region> responsibleRegion;
	private Join<AefiInvestigation, District> responsibleDistrict;
	private Join<AefiInvestigation, Community> responsibleCommunity;
	private Join<AefiInvestigation, Country> country;
	private Join<AefiInvestigation, Facility> vaccinationFacility;
	private Join<AefiInvestigation, Facility> reportingOfficerFacility;

	public AefiInvestigationJoins(From<?, AefiInvestigation> root) {
		super(root);
	}

	public Join<AefiInvestigation, Aefi> getAefi() {
		return getOrCreate(aefi, AefiInvestigation.AEFI_REPORT, JoinType.LEFT, this::setAefi);
	}

	public void setAefi(Join<AefiInvestigation, Aefi> aefi) {
		this.aefi = aefi;
	}

	public AefiJoins getAefiJoins() {
		return getOrCreate(aefiJoins, () -> new AefiJoins(getAefi()), this::setAefiJoins);
	}

	public void setAefiJoins(AefiJoins aefiJoins) {
		this.aefiJoins = aefiJoins;
	}

	public Join<AefiInvestigation, Location> getAddress() {
		return getOrCreate(address, AefiInvestigation.ADDRESS, JoinType.LEFT, this::setAddress);
	}

	public void setAddress(Join<AefiInvestigation, Location> address) {
		this.address = address;
	}

	public Join<AefiInvestigation, Vaccination> getPrimarySuspectVaccination() {
		return getOrCreate(primarySuspectVaccination, AefiInvestigation.PRIMARY_SUSPECT_VACCINE, JoinType.LEFT, this::setPrimarySuspectVaccination);
	}

	public void setPrimarySuspectVaccination(Join<AefiInvestigation, Vaccination> primarySuspectVaccination) {
		this.primarySuspectVaccination = primarySuspectVaccination;
	}

	public Join<AefiInvestigation, User> getReportingUser() {
		return getOrCreate(reportingUser, AefiInvestigation.REPORTING_USER, JoinType.LEFT, this::setReportingUser);
	}

	public void setReportingUser(Join<AefiInvestigation, User> reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Join<AefiInvestigation, Region> getResponsibleRegion() {
		return getOrCreate(responsibleRegion, AefiInvestigation.RESPONSIBLE_REGION, JoinType.LEFT, this::setResponsibleRegion);
	}

	public void setResponsibleRegion(Join<AefiInvestigation, Region> responsibleRegion) {
		this.responsibleRegion = responsibleRegion;
	}

	public Join<AefiInvestigation, District> getResponsibleDistrict() {
		return getOrCreate(responsibleDistrict, AefiInvestigation.RESPONSIBLE_DISTRICT, JoinType.LEFT, this::setResponsibleDistrict);
	}

	public void setResponsibleDistrict(Join<AefiInvestigation, District> responsibleDistrict) {
		this.responsibleDistrict = responsibleDistrict;
	}

	public Join<AefiInvestigation, Community> getResponsibleCommunity() {
		return getOrCreate(responsibleCommunity, AefiInvestigation.RESPONSIBLE_COMMUNITY, JoinType.LEFT, this::setResponsibleCommunity);
	}

	public void setResponsibleCommunity(Join<AefiInvestigation, Community> responsibleCommunity) {
		this.responsibleCommunity = responsibleCommunity;
	}

	public Join<AefiInvestigation, Country> getCountry() {
		return getOrCreate(country, AefiInvestigation.COUNTRY, JoinType.LEFT, this::setCountry);
	}

	public void setCountry(Join<AefiInvestigation, Country> country) {
		this.country = country;
	}

	public Join<AefiInvestigation, Facility> getVaccinationFacility() {
		return getOrCreate(vaccinationFacility, AefiInvestigation.VACCINATION_FACILITY, JoinType.LEFT, this::setVaccinationFacility);
	}

	public void setVaccinationFacility(Join<AefiInvestigation, Facility> vaccinationFacility) {
		this.vaccinationFacility = vaccinationFacility;
	}

	public Join<AefiInvestigation, Facility> getReportingOfficerFacility() {
		return getOrCreate(reportingOfficerFacility, AefiInvestigation.REPORTING_OFFICER_FACILITY, JoinType.LEFT, this::setReportingOfficerFacility);
	}

	public void setReportingOfficerFacility(Join<AefiInvestigation, Facility> reportingOfficerFacility) {
		this.reportingOfficerFacility = reportingOfficerFacility;
	}
}
