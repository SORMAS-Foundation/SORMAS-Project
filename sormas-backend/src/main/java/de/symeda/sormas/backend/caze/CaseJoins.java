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

package de.symeda.sormas.backend.caze;

import java.util.List;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReport;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourse;
import de.symeda.sormas.backend.clinicalcourse.HealthConditions;
import de.symeda.sormas.backend.common.QueryJoins;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.epidata.EpiData;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantJoins;
import de.symeda.sormas.backend.hospitalization.Hospitalization;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonJoins;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleJoins;
import de.symeda.sormas.backend.share.ExternalShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfo;
import de.symeda.sormas.backend.specialcaseaccess.SpecialCaseAccess;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.visit.Visit;

public class CaseJoins extends QueryJoins<Case> {

	private Join<Case, Person> person;
	private Join<Case, Contact> contacts;
	private Join<Case, Region> responsibleRegion;
	private Join<Case, District> responsibleDistrict;
	private Join<Case, Community> responsibleCommunity;
	private Join<Case, Region> region;
	private Join<Case, District> district;
	private Join<Case, Community> community;
	private Join<Case, Facility> facility;
	private Join<Case, PointOfEntry> pointOfEntry;
	private Join<Case, User> surveillanceOfficer;
	private Join<Case, User> reportingUser;
	private Join<Case, Hospitalization> hospitalization;
	private Join<Case, EpiData> epiData;
	private Join<Case, Symptoms> symptoms;
	private Join<Case, ClinicalCourse> clinicalCourse;
	private Join<Case, HealthConditions> healthConditions;
	private Join<Case, EventParticipant> eventParticipants;
	private Join<Case, Sample> samples;
	private Join<Case, SormasToSormasShareInfo> sormasToSormasShareInfo;
	private Join<Case, ExternalShareInfo> externalShareInfo;
	private Join<Case, User> followUpStatusChangeUser;

	private Join<Case, Visit> visit;
	private Join<Case, SurveillanceReport> surveillanceReportJoin;

	private PersonJoins personJoins;
	private SampleJoins sampleJoins;
	private EventParticipantJoins eventParticipantJoins;
	private Join<Case, SpecialCaseAccess> specialCaseAccesses;

	public CaseJoins(From<?, Case> caze) {
		super(caze);
	}

	public Join<Case, Person> getPerson() {
		return getOrCreate(person, Case.PERSON, JoinType.LEFT, this::setPerson);
	}

	private void setPerson(Join<Case, Person> person) {
		this.person = person;
	}

	public Join<Case, Contact> getContacts() {
		return getOrCreate(contacts, Case.CONTACTS, JoinType.LEFT, this::setContacts);
	}

	private void setContacts(Join<Case, Contact> contacts) {
		this.contacts = contacts;
	}

	public Join<Case, Region> getResponsibleRegion() {
		return getOrCreate(responsibleRegion, Case.RESPONSIBLE_REGION, JoinType.LEFT, this::setResponsibleRegion);
	}

	private void setResponsibleRegion(Join<Case, Region> responsibleRegion) {
		this.responsibleRegion = responsibleRegion;
	}

	public Join<Case, District> getResponsibleDistrict() {
		return getOrCreate(responsibleDistrict, Case.RESPONSIBLE_DISTRICT, JoinType.LEFT, this::setResponsibleDistrict);
	}

	private void setResponsibleDistrict(Join<Case, District> responsibleDistrict) {
		this.responsibleDistrict = responsibleDistrict;
	}

	public Join<Case, Community> getResponsibleCommunity() {
		return getOrCreate(responsibleCommunity, Case.RESPONSIBLE_COMMUNITY, JoinType.LEFT, this::setResponsibleCommunity);
	}

	private void setResponsibleCommunity(Join<Case, Community> responsibleCommunity) {
		this.responsibleCommunity = responsibleCommunity;
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

	private void setDistrict(Join<Case, District> district) {
		this.district = district;
	}

	public Join<Case, Community> getCommunity() {
		return getOrCreate(community, Case.COMMUNITY, JoinType.LEFT, this::setCommunity);
	}

	private void setCommunity(Join<Case, Community> community) {
		this.community = community;
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

	public Join<Case, User> getReportingUser() {
		return getOrCreate(reportingUser, Case.REPORTING_USER, JoinType.LEFT, this::setReportingUser);
	}

	private void setReportingUser(Join<Case, User> reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Join<Person, Location> getPersonAddress() {
		return getPersonJoins().getAddress();
	}

	public Join<Location, Region> getPersonAddressRegion() {
		return getPersonJoins().getAddressJoins().getRegion();
	}

	public Join<Location, District> getPersonAddressDistrict() {
		return getPersonJoins().getAddressJoins().getDistrict();
	}

	public Join<Location, Community> getPersonAddressCommunity() {
		return getPersonJoins().getAddressJoins().getCommunity();
	}

	public Join<Location, Facility> getPersonAddressFacility() {
		return getPersonJoins().getAddressJoins().getFacility();
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

	public Join<Case, HealthConditions> getHealthConditions() {
		return getOrCreate(healthConditions, Case.HEALTH_CONDITIONS, JoinType.LEFT, this::setHealthConditions);
	}

	private void setHealthConditions(Join<Case, HealthConditions> healthConditions) {
		this.healthConditions = healthConditions;
	}

	public Join<Case, EventParticipant> getEventParticipants() {
		return getOrCreate(eventParticipants, Case.EVENT_PARTICIPANTS, JoinType.LEFT, this::setEventParticipants);
	}

	private void setEventParticipants(Join<Case, EventParticipant> eventParticipants) {
		this.eventParticipants = eventParticipants;
	}

	public Join<Person, List<Location>> getPersonAddresses() {
		return getPersonJoins().getAddresses();
	}

	public Join<Case, Sample> getSamples() {
		return getOrCreate(samples, Case.SAMPLES, JoinType.LEFT, this::setSamples);
	}

	private void setSamples(Join<Case, Sample> samples) {
		this.samples = samples;
	}

	public Join<Sample, Facility> getSampleLabs() {
		return getSampleJoins().getLab();
	}

	public Join<Person, Country> getPersonBirthCountry() {
		return getPersonJoins().getBirthCountry();
	}

	public Join<Person, Country> getPersonCitizenship() {
		return getPersonJoins().getCitizenship();
	}

	public Join<Case, SormasToSormasShareInfo> getSormasToSormasShareInfo() {
		return getOrCreate(sormasToSormasShareInfo, Case.SORMAS_TO_SORMAS_SHARES, JoinType.LEFT, this::setSormasToSormasShareInfo);
	}

	private void setSormasToSormasShareInfo(Join<Case, SormasToSormasShareInfo> sormasToSormasShareInfo) {
		this.sormasToSormasShareInfo = sormasToSormasShareInfo;
	}

	public Join<Case, ExternalShareInfo> getExternalShareInfo() {
		return getOrCreate(externalShareInfo, Case.EXTERNAL_SHARES, JoinType.LEFT, this::setExternalShareInfo);
	}

	private void setExternalShareInfo(Join<Case, ExternalShareInfo> externalShareInfo) {
		this.externalShareInfo = externalShareInfo;
	}

	public Join<Case, User> getFollowUpStatusChangeUser() {
		return getOrCreate(followUpStatusChangeUser, Case.FOLLOW_UP_STATUS_CHANGE_USER, JoinType.LEFT, this::setFollowUpStatusChangeUser);
	}

	private void setFollowUpStatusChangeUser(Join<Case, User> followUpStatusChangeUser) {
		this.followUpStatusChangeUser = followUpStatusChangeUser;
	}

	public PersonJoins getPersonJoins() {
		return getOrCreate(personJoins, () -> new PersonJoins(getPerson()), this::setPersonJoins);
	}

	private void setPersonJoins(PersonJoins personJoins) {
		this.personJoins = personJoins;
	}

	public SampleJoins getSampleJoins() {
		return getOrCreate(sampleJoins, () -> new SampleJoins(getSamples()), this::setSampleJoins);
	}

	private void setSampleJoins(SampleJoins sampleJoins) {
		this.sampleJoins = sampleJoins;
	}

	public EventParticipantJoins getEventParticipantJoins() {
		return getOrCreate(eventParticipantJoins, () -> new EventParticipantJoins(getEventParticipants()), this::setEventParticipantJoins);
	}

	private void setEventParticipantJoins(EventParticipantJoins eventParticipantJoins) {
		this.eventParticipantJoins = eventParticipantJoins;
	}

	public Join<Case, Visit> getVisit() {
		return getOrCreate(visit, Case.VISITS, JoinType.LEFT, this::setVisit);
	}

	private void setVisit(Join<Case, Visit> visit) {
		this.visit = visit;
	}

	public Join<Case, SurveillanceReport> getSurveillanceReportJoin() {
		return getOrCreate(surveillanceReportJoin, Case.SURVEILLANCE_REPORTS, JoinType.LEFT, this::setSurveillanceReportJoin);
	}

	private void setSurveillanceReportJoin(Join<Case, SurveillanceReport> surveillanceReportJoin) {
		this.surveillanceReportJoin = surveillanceReportJoin;
	}

    public Join<Case, SpecialCaseAccess> getSpecialCaseAccesses() {
		return getOrCreate(specialCaseAccesses, Case.SPECIAL_CASE_ACCESSES, JoinType.LEFT, this::setSpecialCaseAccesses);
    }

	private void setSpecialCaseAccesses(Join<Case, SpecialCaseAccess> specialCaseAccesses) {
		this.specialCaseAccesses = specialCaseAccesses;
	}
}
