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
import de.symeda.sormas.backend.immunization.ImmunizationJoins;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.vaccination.Vaccination;

public class AefiJoins extends QueryJoins<Aefi> {

	private Join<Aefi, Immunization> immunization;
	private Join<Aefi, Location> address;
	private Join<Aefi, Vaccination> primarySuspectVaccination;
	private Join<Aefi, AdverseEvents> adverseEvents;
	private Join<Aefi, Person> person;
	private Join<Aefi, User> reportingUser;
	private Join<Aefi, Region> responsibleRegion;
	private Join<Aefi, District> responsibleDistrict;
	private Join<Aefi, Community> responsibleCommunity;
	private Join<Aefi, Country> country;
	private Join<Aefi, Facility> healthFacility;
	private Join<Aefi, Facility> reporterInstitution;

	private ImmunizationJoins immunizationJoins;

	public AefiJoins(From<?, Aefi> root) {
		super(root);
	}

	public Join<Aefi, Immunization> getImmunization() {
		return getOrCreate(immunization, Aefi.IMMUNIZATION, JoinType.LEFT, this::setImmunization);
	}

	public void setImmunization(Join<Aefi, Immunization> immunization) {
		this.immunization = immunization;
	}

	public ImmunizationJoins getImmunizationJoins() {
		return getOrCreate(immunizationJoins, () -> new ImmunizationJoins(getImmunization()), this::setImmunizationJoins);
	}

	public void setImmunizationJoins(ImmunizationJoins immunizationJoins) {
		this.immunizationJoins = immunizationJoins;
	}

	public Join<Aefi, Location> getAddress() {
		return getOrCreate(address, Aefi.ADDRESS, JoinType.LEFT, this::setAddress);
	}

	public void setAddress(Join<Aefi, Location> address) {
		this.address = address;
	}

	public Join<Aefi, Vaccination> getPrimarySuspectVaccination() {
		return getOrCreate(primarySuspectVaccination, Aefi.PRIMARY_SUSPECT_VACCINE, JoinType.LEFT, this::setPrimarySuspectVaccination);
	}

	public void setPrimarySuspectVaccination(Join<Aefi, Vaccination> primarySuspectVaccination) {
		this.primarySuspectVaccination = primarySuspectVaccination;
	}

	public Join<Aefi, AdverseEvents> getAdverseEvents() {
		return getOrCreate(adverseEvents, Aefi.ADVERSE_EVENTS, JoinType.LEFT, this::setAdverseEvents);
	}

	public void setAdverseEvents(Join<Aefi, AdverseEvents> adverseEvents) {
		this.adverseEvents = adverseEvents;
	}

	public Join<Aefi, Person> getPerson() {
		return getOrCreate(person, Aefi.PERSON, JoinType.LEFT, this::setPerson);
	}

	public void setPerson(Join<Aefi, Person> person) {
		this.person = person;
	}

	public Join<Aefi, User> getReportingUser() {
		return getOrCreate(reportingUser, Aefi.REPORTING_USER, JoinType.LEFT, this::setReportingUser);
	}

	public void setReportingUser(Join<Aefi, User> reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Join<Aefi, Region> getResponsibleRegion() {
		return getOrCreate(responsibleRegion, Aefi.RESPONSIBLE_REGION, JoinType.LEFT, this::setResponsibleRegion);
	}

	public void setResponsibleRegion(Join<Aefi, Region> responsibleRegion) {
		this.responsibleRegion = responsibleRegion;
	}

	public Join<Aefi, District> getResponsibleDistrict() {
		return getOrCreate(responsibleDistrict, Aefi.RESPONSIBLE_DISTRICT, JoinType.LEFT, this::setResponsibleDistrict);
	}

	public void setResponsibleDistrict(Join<Aefi, District> responsibleDistrict) {
		this.responsibleDistrict = responsibleDistrict;
	}

	public Join<Aefi, Community> getResponsibleCommunity() {
		return getOrCreate(responsibleCommunity, Aefi.RESPONSIBLE_COMMUNITY, JoinType.LEFT, this::setResponsibleCommunity);
	}

	public void setResponsibleCommunity(Join<Aefi, Community> responsibleCommunity) {
		this.responsibleCommunity = responsibleCommunity;
	}

	public Join<Aefi, Country> getCountry() {
		return getOrCreate(country, Aefi.COUNTRY, JoinType.LEFT, this::setCountry);
	}

	public void setCountry(Join<Aefi, Country> country) {
		this.country = country;
	}

	public Join<Aefi, Facility> getHealthFacility() {
		return getOrCreate(healthFacility, Aefi.HEALTH_FACILITY, JoinType.LEFT, this::setHealthFacility);
	}

	public void setHealthFacility(Join<Aefi, Facility> healthFacility) {
		this.healthFacility = healthFacility;
	}

	public Join<Aefi, Facility> getReporterInstitution() {
		return getOrCreate(reporterInstitution, Aefi.REPORTING_OFFICER_FACILITY, JoinType.LEFT, this::setReporterInstitution);
	}

	public void setReporterInstitution(Join<Aefi, Facility> reporterInstitution) {
		this.reporterInstitution = reporterInstitution;
	}
}
