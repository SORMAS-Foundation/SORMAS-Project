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

package de.symeda.sormas.backend.contact;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseJoins;
import de.symeda.sormas.backend.clinicalcourse.HealthConditions;
import de.symeda.sormas.backend.common.QueryJoins;
import de.symeda.sormas.backend.epidata.EpiData;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
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
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.visit.Visit;
import de.symeda.sormas.backend.visit.VisitJoins;

public class ContactJoins extends QueryJoins<Contact> {

	private Join<Contact, Person> person;
	private Join<Contact, Case> caze;
	private Join<Contact, Case> resultingCase;

	private Join<Contact, User> contactOfficer;
	private Join<Contact, Region> region;
	private Join<Contact, District> district;
	private Join<Contact, Community> community;
	private Join<Contact, User> reportingUser;
	private Join<Contact, EpiData> epiData;
	private Join<EventParticipant, Event> event;
	private Join<EventParticipant, Event> caseEvent;

	private Join<Contact, Sample> samples;

	private Join<Contact, Visit> visits;
	private Join<Contact, HealthConditions> healthConditions;

	private Join<Contact, District> reportingDistrict;

	private Join<Contact, User> followUpStatusChangeUser;

	private CaseJoins caseJoins;
	private PersonJoins personJoins;
	private SampleJoins sampleJoins;
	private VisitJoins visitJoins;

	public ContactJoins(From<?, Contact> contact) {
		super(contact);
	}

	public Join<Contact, Person> getPerson() {
		return getOrCreate(person, Contact.PERSON, JoinType.LEFT, this::setPerson);
	}

	private void setPerson(Join<Contact, Person> person) {
		this.person = person;
	}

	public Join<Contact, Case> getCaze() {
		return getOrCreate(caze, Contact.CAZE, JoinType.LEFT, this::setCaze);
	}

	private void setCaze(Join<Contact, Case> caze) {
		this.caze = caze;
	}

	public Join<Contact, Case> getResultingCase() {
		return getOrCreate(resultingCase, Contact.RESULTING_CASE, JoinType.LEFT, this::setResultingCase);
	}

	private void setResultingCase(Join<Contact, Case> resultingCase) {
		this.resultingCase = resultingCase;
	}

	public Join<Case, Person> getCasePerson() {
		return getCaseJoins().getPerson();
	}

	public Join<Case, User> getCaseReportingUser() {
		return getCaseJoins().getReportingUser();
	}

	public Join<Case, Region> getCaseResponsibleRegion() {
		return getCaseJoins().getResponsibleRegion();
	}

	public Join<Case, District> getCaseResponsibleDistrict() {
		return getCaseJoins().getResponsibleDistrict();
	}

	public Join<Case, Community> getCaseResponsibleCommunity() {
		return getCaseJoins().getResponsibleCommunity();
	}

	public Join<Case, Region> getCaseRegion() {
		return getCaseJoins().getRegion();
	}

	public Join<Case, District> getCaseDistrict() {
		return getCaseJoins().getDistrict();
	}

	public Join<Case, Community> getCaseCommunity() {
		return getCaseJoins().getCommunity();
	}

	public Join<Case, Facility> getCaseHealthFacility() {
		return getCaseJoins().getFacility();
	}

	public Join<Case, PointOfEntry> getCaseasePointOfEntry() {
		return getCaseJoins().getPointOfEntry();
	}

	public Join<Contact, User> getContactOfficer() {
		return getOrCreate(contactOfficer, Contact.CONTACT_OFFICER, JoinType.LEFT, this::setContactOfficer);
	}

	private void setContactOfficer(Join<Contact, User> contactOfficer) {
		this.contactOfficer = contactOfficer;
	}

	public Join<Person, Location> getAddress() {
		return getPersonJoins().getAddress();
	}

	public Join<Contact, Region> getRegion() {
		return getOrCreate(region, Contact.REGION, JoinType.LEFT, this::setRegion);
	}

	private void setRegion(Join<Contact, Region> region) {
		this.region = region;
	}

	public Join<Contact, District> getDistrict() {
		return getOrCreate(district, Contact.DISTRICT, JoinType.LEFT, this::setDistrict);
	}

	private void setDistrict(Join<Contact, District> district) {
		this.district = district;
	}

	public Join<Contact, Community> getCommunity() {
		return getOrCreate(community, Contact.COMMUNITY, JoinType.LEFT, this::setCommunity);
	}

	private void setCommunity(Join<Contact, Community> community) {
		this.community = community;
	}

	public Join<Contact, User> getReportingUser() {
		return getOrCreate(reportingUser, Contact.REPORTING_USER, JoinType.LEFT, this::setReportingUser);
	}

	private void setReportingUser(Join<Contact, User> reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Join<Location, Region> getAddressRegion() {
		return getPersonJoins().getAddressJoins().getRegion();
	}

	public Join<Location, District> getAddressDistrict() {
		return getPersonJoins().getAddressJoins().getDistrict();
	}

	public Join<Location, Community> getAddressCommunity() {
		return getPersonJoins().getAddressJoins().getCommunity();
	}

	public Join<Location, Facility> getAddressFacility() {
		return getPersonJoins().getAddressJoins().getFacility();
	}

	public Join<Contact, EpiData> getEpiData() {
		return getOrCreate(epiData, Contact.EPI_DATA, JoinType.LEFT, this::setEpiData);
	}

	private void setEpiData(Join<Contact, EpiData> epiData) {
		this.epiData = epiData;
	}

	public Join<Contact, Visit> getVisits() {
		return getOrCreate(visits, Contact.VISITS, JoinType.LEFT, this::setVisits);
	}

	private void setVisits(Join<Contact, Visit> visits) {
		this.visits = visits;
	}

	public Join<Visit, Symptoms> getVisitSymptoms() {
		return getVisitJoins().getSymptoms();
	}

	public Join<Contact, HealthConditions> getHealthConditions() {
		return healthConditions;
	}

	public void setHealthConditions(Join<Contact, HealthConditions> healthConditions) {
		this.healthConditions = healthConditions;
	}

	public Join<Person, EventParticipant> getEventParticipants() {
		return getPersonJoins().getEventParticipant();
	}

	public Join<Case, EventParticipant> getCaseEventParticipants() {
		return getCaseJoins().getEventParticipants();
	}

	private void setEvent(Join<EventParticipant, Event> event) {
		this.event = event;
	}

	public Join<EventParticipant, Event> getEvent() {
		return getOrCreate(event, EventParticipant.EVENT, JoinType.LEFT, getEventParticipants(), this::setEvent);
	}

	private void setCaseEvent(Join<EventParticipant, Event> caseEvent) {
		this.caseEvent = caseEvent;
	}

	public Join<EventParticipant, Event> getCaseEvent() {
		return getOrCreate(caseEvent, EventParticipant.EVENT, JoinType.LEFT, getCaseEventParticipants(), this::setCaseEvent);
	}

	public Join<Person, Country> getPersonBirthCountry() {
		return getPersonJoins().getBirthCountry();
	}

	public Join<Person, Country> getPersonCitizenship() {
		return getPersonJoins().getCitizenship();
	}

	public Join<Contact, District> getReportingDistrict() {
		return getOrCreate(reportingDistrict, Contact.REPORTING_DISTRICT, JoinType.LEFT, this::setReportingDistrict);
	}

	private void setReportingDistrict(Join<Contact, District> reportingDistrict) {
		this.reportingDistrict = reportingDistrict;
	}

	public Join<Contact, Sample> getSamples() {
		return getOrCreate(samples, Case.SAMPLES, JoinType.LEFT, this::setSamples);
	}

	private void setSamples(Join<Contact, Sample> samples) {
		this.samples = samples;
	}

	public Join<Sample, Facility> getSampleLabs() {
		return getSampleJoins().getLab();
	}

	public Join<Contact, User> getFollowUpStatusChangeUser() {
		return getOrCreate(followUpStatusChangeUser, Contact.FOLLOW_UP_STATUS_CHANGE_USER, JoinType.LEFT, this::setFollowUpStatusChangeUser);
	}

	private void setFollowUpStatusChangeUser(Join<Contact, User> followUpStatusChangeUser) {
		this.followUpStatusChangeUser = followUpStatusChangeUser;
	}

	public CaseJoins getCaseJoins() {
		return getOrCreate(caseJoins, () -> new CaseJoins(getCaze()), this::setCaseJoins);
	}

	private void setCaseJoins(CaseJoins caseJoins) {
		this.caseJoins = caseJoins;
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

	public VisitJoins getVisitJoins() {
		return getOrCreate(visitJoins, () -> new VisitJoins(getVisits(), JoinType.LEFT), this::setVisitJoins);
	}

	private void setVisitJoins(VisitJoins visitJoins) {
		this.visitJoins = visitJoins;
	}
}
