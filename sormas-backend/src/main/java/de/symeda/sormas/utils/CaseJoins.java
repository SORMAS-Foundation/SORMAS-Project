/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.utils;

import java.util.List;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourse;
import de.symeda.sormas.backend.clinicalcourse.HealthConditions;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.epidata.EpiData;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.hospitalization.Hospitalization;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.AbstractDomainObjectJoins;

public class CaseJoins<T extends AbstractDomainObject> extends AbstractDomainObjectJoins<T, Case> {

	private Join<Case, Person> person;
	private Join<Case, Region> region;
	private Join<Case, District> district;
	private Join<Case, Community> community;
	private Join<Case, Facility> facility;
	private Join<Case, PointOfEntry> pointOfEntry;
	private Join<Case, User> surveillanceOfficer;
	private Join<Person, Location> address;
	private Join<Case, User> reportingUser;
	private Join<Person, Location> personMainAddress;
	private Join<Location, Region> personMainAddressRegion;
	private Join<Location, District> personMainAddressDistrict;
	private Join<Location, Community> personMainAddressCommunity;
	private Join<Location, Facility> personMainAddressFacility;
	private Join<Case, Hospitalization> hospitalization;
	private Join<Case, EpiData> epiData;
	private Join<Case, Symptoms> symptoms;
	private Join<Case, ClinicalCourse> clinicalCourse;
	private Join<ClinicalCourse, HealthConditions> healthConditions;
	private Join<Case, EventParticipant> eventParticipants;
	private Join<Person, List<Location>> personAddresses;

	public CaseJoins(From<T, Case> caze) {
		super(caze);
	}

	public Join<Case, Person> getPerson() {
		return getOrCreate(person, Case.PERSON, JoinType.LEFT, this::setPerson);
	}

	private void setPerson(Join<Case, Person> person) {
		this.person = person;
	}

	public Join<Case, Region> getRegion() {
		return getOrCreate(region, Case.REGION, JoinType.LEFT, this::setRegion);
	}

	private void setRegion(Join<Case, Region> region) {
		this.region = region;
	}

	public Join<Case, District> getDistrict() {
		return getOrCreate(district, Case.DISTRICT, JoinType.LEFT, this::setDistrict);
	}

	public Join<Case, Community> getCommunity() {
		return getOrCreate(community, Case.COMMUNITY, JoinType.LEFT, this::setCommunity);
	}

	private void setCommunity(Join<Case, Community> community) {
		this.community = community;
	}

	private void setDistrict(Join<Case, District> district) {
		this.district = district;
	}

	public Join<Case, Facility> getFacility() {
		return getOrCreate(facility, Case.HEALTH_FACILITY, JoinType.LEFT, this::setFacility);
	}

	private void setFacility(Join<Case, Facility> facility) {
		this.facility = facility;
	}

	public Join<Case, PointOfEntry> getPointOfEntry() {
		return getOrCreate(pointOfEntry, Case.POINT_OF_ENTRY, JoinType.LEFT, this::setPointOfEntry);
	}

	private void setPointOfEntry(Join<Case, PointOfEntry> pointOfEntry) {
		this.pointOfEntry = pointOfEntry;
	}

	public Join<Case, User> getSurveillanceOfficer() {
		return getOrCreate(surveillanceOfficer, Case.SURVEILLANCE_OFFICER, JoinType.LEFT, this::setSurveillanceOfficer);
	}

	private void setSurveillanceOfficer(Join<Case, User> surveillanceOfficer) {
		this.surveillanceOfficer = surveillanceOfficer;
	}

	public Join<Person, Location> getAddress() {
		return getOrCreate(address, Person.MAIN_ADDRESS, JoinType.LEFT, getPerson(), this::setAddress);
	}

	private void setAddress(Join<Person, Location> address) {
		this.address = address;
	}

	public Join<Case, User> getReportingUser() {
		return getOrCreate(reportingUser, Case.REPORTING_USER, JoinType.LEFT, this::setReportingUser);
	}

	private void setReportingUser(Join<Case, User> reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Join<Person, Location> getPersonMainAddress() {
		return getOrCreate(personMainAddress, Person.MAIN_ADDRESS, JoinType.LEFT, getPerson(), this::setPersonMainAddress);
	}

	private void setPersonMainAddress(Join<Person, Location> personMainAddress) {
		this.personMainAddress = personMainAddress;
	}

	public Join<Location, Region> getPersonMainAddressRegion() {
		return getOrCreate(personMainAddressRegion, Location.REGION, JoinType.LEFT, getPersonMainAddress(), this::setPersonMainAddressRegion);
	}

	private void setPersonMainAddressRegion(Join<Location, Region> personMainAddressRegion) {
		this.personMainAddressRegion = personMainAddressRegion;
	}

	public Join<Location, District> getPersonMainAddressDistrict() {
		return getOrCreate(personMainAddressDistrict, Location.DISTRICT, JoinType.LEFT, getPersonMainAddress(), this::setPersonMainAddressDistrict);
	}

	private void setPersonMainAddressDistrict(Join<Location, District> personMainAddressDistrict) {
		this.personMainAddressDistrict = personMainAddressDistrict;
	}

	public Join<Location, Community> getPersonMainAddressCommunity() {
		return getOrCreate(
			personMainAddressCommunity,
			Location.COMMUNITY,
			JoinType.LEFT,
			getPersonMainAddress(),
			this::setPersonMainAddressCommunity);
	}

	private void setPersonMainAddressCommunity(Join<Location, Community> personMainAddressCommunity) {
		this.personMainAddressCommunity = personMainAddressCommunity;
	}

	public Join<Location, Facility> getPersonMainAddressFacility() {
		return getOrCreate(personMainAddressFacility, Location.FACILITY, JoinType.LEFT, getAddress(), this::setPersonMainAddressFacility);
	}

	private void setPersonMainAddressFacility(Join<Location, Facility> personMainAddressFacility) {
		this.personMainAddressFacility = personMainAddressFacility;
	}

	public Join<Case, Hospitalization> getHospitalization() {
		return getOrCreate(hospitalization, Case.HOSPITALIZATION, JoinType.LEFT, this::setHospitalization);
	}

	private void setHospitalization(Join<Case, Hospitalization> hospitalization) {
		this.hospitalization = hospitalization;
	}

	public Join<Case, EpiData> getEpiData() {
		return getOrCreate(epiData, Case.EPI_DATA, JoinType.LEFT, this::setEpiData);
	}

	private void setEpiData(Join<Case, EpiData> epiData) {
		this.epiData = epiData;
	}

	public Join<Case, Symptoms> getSymptoms() {
		return getOrCreate(symptoms, Case.SYMPTOMS, JoinType.LEFT, this::setSymptoms);
	}

	private void setSymptoms(Join<Case, Symptoms> symptoms) {
		this.symptoms = symptoms;
	}

	public Join<Case, ClinicalCourse> getClinicalCourse() {
		return getOrCreate(clinicalCourse, Case.CLINICAL_COURSE, JoinType.LEFT, this::setClinicalCourse);
	}

	private void setClinicalCourse(Join<Case, ClinicalCourse> clinicalCourse) {
		this.clinicalCourse = clinicalCourse;
	}

	public Join<ClinicalCourse, HealthConditions> getHealthConditions() {
		return getOrCreate(healthConditions, ClinicalCourse.HEALTH_CONDITIONS, JoinType.LEFT, getClinicalCourse(), this::setHealthConditions);
	}

	private void setHealthConditions(Join<ClinicalCourse, HealthConditions> healthConditions) {
		this.healthConditions = healthConditions;
	}

	private void setEventParticipants(Join<Case, EventParticipant> eventParticipants) {
		this.eventParticipants = eventParticipants;
	}

	public Join<Case, EventParticipant> getEventParticipants() {
		return getOrCreate(eventParticipants, Case.EVENT_PARTICIPANTS, JoinType.LEFT, this::setEventParticipants);
	}

	public Join<Person, List<Location>> getPersonAddresses() {
		return getOrCreate(personAddresses, Person.ADDRESSES, JoinType.LEFT, getPerson(), this::setPersonAddresses);
	}

	private void setPersonAddresses(Join<Person, List<Location>> personAddresses) {
		this.personAddresses = personAddresses;
	}
}
