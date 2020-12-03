/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.backend.sample;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.AbstractDomainObjectJoins;

public class SampleJoins<P> extends AbstractDomainObjectJoins<P, Sample> {

	private Join<Sample, User> reportingUser;
	private Join<Sample, Sample> referredSample;
	private Join<Sample, Facility> lab;
	private Join<Sample, Case> caze;
	private Join<Case, Person> casePerson;
	private Join<Case, User> caseReportingUser;
	private Join<Case, Region> caseRegion;
	private Join<Case, District> caseDistrict;
	private Join<Case, Community> caseCommunity;
	private Join<Case, Facility> caseFacility;
	private Join<Case, PointOfEntry> casePointOfEntry;
	private Join<Sample, Contact> contact;
	private Join<Contact, Person> contactPerson;
	private Join<Contact, User> contactReportingUser;
	private Join<Contact, Region> contactRegion;
	private Join<Contact, District> contactDistrict;
	private Join<Contact, Community> contactCommunity;
	private Join<Contact, Case> contactCase;
	private Join<Case, User> contactCaseReportingUser;
	private Join<Case, Region> contactCaseRegion;
	private Join<Case, District> contactCaseDistrict;
	private Join<Case, Community> contactCaseCommunity;
	private Join<Case, Facility> contactCaseHealthFacility;
	private Join<Case, PointOfEntry> contactCasePointOfEntry;
	private Join<Person, Location> casePersonMainAddress;
	private Join<Location, Region> casePersonMainAddressRegion;
	private Join<Location, District> casePersonMainAddressDistrict;
	private Join<Location, Community> casePersonMainAddressCommunity;
	private Join<Person, Location> contactPersonMainAddress;
	private Join<Location, Region> contactPersonMainAddressRegion;
	private Join<Location, District> contactPersonMainAddressDistrict;
	private Join<Location, Community> contactPersonMainAddressCommunity;
	private Join<Sample, EventParticipant> eventParticipant;
	private Join<EventParticipant, Person> eventParticipantPerson;
	private Join<EventParticipant, Event> event;
	private Join<Event, Location> eventLocation;
	private Join<Location, Region> eventRegion;
	private Join<Location, District> eventDistrict;
	private Join<Location, Community> eventCommunity;
	private Join<Event, User> eventReportingUser;
	private Join<Event, User> eventSurveillanceOfficer;

	public SampleJoins(From<P, Sample> root) {
		super(root);
	}

	public Join<Sample, User> getReportingUser() {
		return getOrCreate(reportingUser, Sample.REPORTING_USER, JoinType.LEFT, this::setReportingUser);
	}

	private void setReportingUser(Join<Sample, User> reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Join<Sample, Sample> getReferredSample() {
		return getOrCreate(referredSample, Sample.REFERRED_TO, JoinType.LEFT, this::setReferredSample);
	}

	private void setReferredSample(Join<Sample, Sample> referredSample) {
		this.referredSample = referredSample;
	}

	public Join<Sample, Facility> getLab() {
		return getOrCreate(lab, Sample.LAB, JoinType.LEFT, this::setLab);
	}

	private void setLab(Join<Sample, Facility> lab) {
		this.lab = lab;
	}

	public Join<Sample, Case> getCaze() {
		return getOrCreate(caze, Sample.ASSOCIATED_CASE, JoinType.LEFT, this::setCaze);
	}

	private void setCaze(Join<Sample, Case> caze) {
		this.caze = caze;
	}

	public Join<Case, Person> getCasePerson() {
		return getOrCreate(casePerson, Case.PERSON, JoinType.LEFT, getCaze(), this::setCasePerson);
	}

	private void setCasePerson(Join<Case, Person> casePerson) {
		this.casePerson = casePerson;
	}

	public Join<Case, User> getCaseReportingUser() {
		return getOrCreate(caseReportingUser, Case.REPORTING_USER, JoinType.LEFT, getCaze(), this::setCaseReportingUser);
	}

	private void setCaseReportingUser(Join<Case, User> caseReportingUser) {
		this.caseReportingUser = caseReportingUser;
	}

	public Join<Case, Region> getCaseRegion() {
		return getOrCreate(caseRegion, Case.REGION, JoinType.LEFT, getCaze(), this::setCaseRegion);
	}

	private void setCaseRegion(Join<Case, Region> caseRegion) {
		this.caseRegion = caseRegion;
	}

	public Join<Case, District> getCaseDistrict() {
		return getOrCreate(caseDistrict, Case.DISTRICT, JoinType.LEFT, getCaze(), this::setCaseDistrict);
	}

	private void setCaseDistrict(Join<Case, District> caseDistrict) {
		this.caseDistrict = caseDistrict;
	}

	public Join<Case, Community> getCaseCommunity() {
		return getOrCreate(caseCommunity, Case.COMMUNITY, JoinType.LEFT, getCaze(), this::setCaseCommunity);
	}

	private void setCaseCommunity(Join<Case, Community> caseCommunity) {
		this.caseCommunity = caseCommunity;
	}

	public Join<Case, Facility> getCaseFacility() {
		return getOrCreate(caseFacility, Case.HEALTH_FACILITY, JoinType.LEFT, getCaze(), this::setCaseFacility);
	}

	private void setCaseFacility(Join<Case, Facility> caseFacility) {
		this.caseFacility = caseFacility;
	}

	public Join<Case, PointOfEntry> getCasePointOfEntry() {
		return getOrCreate(casePointOfEntry, Case.POINT_OF_ENTRY, JoinType.LEFT, getCaze(), this::setCasePointOfEntry);
	}

	private void setCasePointOfEntry(Join<Case, PointOfEntry> casePointOfEntry) {
		this.casePointOfEntry = casePointOfEntry;
	}

	public Join<Sample, Contact> getContact() {
		return getOrCreate(contact, Sample.ASSOCIATED_CONTACT, JoinType.LEFT, this::setContact);
	}

	private void setContact(Join<Sample, Contact> contact) {
		this.contact = contact;
	}

	public Join<Sample, EventParticipant> getEventParticipant() {
		return getOrCreate(eventParticipant, Sample.ASSOCIATED_EVENT_PARTICIPANT, JoinType.LEFT, this::setEventParticipant);
	}

	private void setEventParticipant(Join<Sample, EventParticipant> eventParticipant) {
		this.eventParticipant = eventParticipant;
	}

	public Join<EventParticipant, Person> getEventParticipantPerson() {
		return getOrCreate(eventParticipantPerson, EventParticipant.PERSON, JoinType.LEFT, getEventParticipant(), this::setEventParticipantPerson);
	}

	public void setEventParticipantPerson(Join<EventParticipant, Person> eventParticipantPerson) {
		this.eventParticipantPerson = eventParticipantPerson;
	}

	public Join<EventParticipant, Event> getEvent() {
		return getOrCreate(event, EventParticipant.EVENT, JoinType.LEFT, getEventParticipant(), this::setEvent);
	}

	public void setEvent(Join<EventParticipant, Event> event) {
		this.event = event;
	}

	public Join<Event, Location> getEventLocation() {
		return getOrCreate(eventLocation, Event.EVENT_LOCATION, JoinType.LEFT, getEvent(), this::setEventLocation);
	}

	public void setEventLocation(Join<Event, Location> eventLocation) {
		this.eventLocation = eventLocation;
	}

	public Join<Location, Region> getEventRegion() {
		return getOrCreate(eventRegion, Location.REGION, JoinType.LEFT, getEventLocation(), this::setEventRegion);
	}

	public void setEventRegion(Join<Location, Region> eventRegion) {
		this.eventRegion = eventRegion;
	}

	public Join<Location, District> getEventDistrict() {
		return getOrCreate(eventDistrict, Location.DISTRICT, JoinType.LEFT, getEventLocation(), this::setEventDistrict);
	}

	public Join<Location, Community> getEventCommunity() {
		return getOrCreate(eventCommunity, Location.COMMUNITY, JoinType.LEFT, getEventLocation(), this::setEventCommunity);
	}

	public void setEventCommunity(Join<Location, Community> eventCommunity) {
		this.eventCommunity = eventCommunity;
	}

	public void setEventDistrict(Join<Location, District> eventDistrict) {
		this.eventDistrict = eventDistrict;
	}

	public Join<Event, User> getEventReportingUser() {
		return getOrCreate(eventReportingUser, Event.REPORTING_USER, JoinType.LEFT, getEvent(), this::setEventReportingUser);
	}

	private void setEventReportingUser(Join<Event, User> eventReportingUser) {
		this.eventReportingUser = eventReportingUser;
	}

	public Join<Event, User> getEventSurveillanceOfficer() {
		return getOrCreate(eventSurveillanceOfficer, Event.SURVEILLANCE_OFFICER, JoinType.LEFT, getEvent(), this::setEventSurveillanceOfficer);
	}

	private void setEventSurveillanceOfficer(Join<Event, User> eventSurveillanceOfficer) {
		this.eventSurveillanceOfficer = eventSurveillanceOfficer;
	}

	public Join<Contact, Person> getContactPerson() {
		return getOrCreate(contactPerson, Contact.PERSON, JoinType.LEFT, getContact(), this::setContactPerson);
	}

	private void setContactPerson(Join<Contact, Person> contactPerson) {
		this.contactPerson = contactPerson;
	}

	public Join<Contact, User> getContactReportingUser() {
		return getOrCreate(contactReportingUser, Contact.REPORTING_USER, JoinType.LEFT, getContact(), this::setContactReportingUser);
	}

	private void setContactReportingUser(Join<Contact, User> contactReportingUser) {
		this.contactReportingUser = contactReportingUser;
	}

	public Join<Contact, Region> getContactRegion() {
		return getOrCreate(contactRegion, Contact.REGION, JoinType.LEFT, getContact(), this::setContactRegion);
	}

	private void setContactRegion(Join<Contact, Region> contactRegion) {
		this.contactRegion = contactRegion;
	}

	public Join<Contact, District> getContactDistrict() {
		return getOrCreate(contactDistrict, Contact.DISTRICT, JoinType.LEFT, getContact(), this::setContactDistrict);
	}

	private void setContactDistrict(Join<Contact, District> contactDistrict) {
		this.contactDistrict = contactDistrict;
	}

	public Join<Contact, Community> getContactCommunity() {
		return getOrCreate(contactCommunity, Contact.COMMUNITY, JoinType.LEFT, getContact(), this::setContactCommunity);
	}

	private void setContactCommunity(Join<Contact, Community> contactCommunity) {
		this.contactCommunity = contactCommunity;
	}

	public Join<Contact, Case> getContactCase() {
		return getOrCreate(contactCase, Contact.CAZE, JoinType.LEFT, getContact(), this::setContactCase);
	}

	private void setContactCase(Join<Contact, Case> contactCase) {
		this.contactCase = contactCase;
	}

	public Join<Case, User> getContactCaseReportingUser() {
		return getOrCreate(contactCaseReportingUser, Case.REPORTING_USER, JoinType.LEFT, getContactCase(), this::setContactCaseReportingUser);
	}

	private void setContactCaseReportingUser(Join<Case, User> contactCaseReportingUser) {
		this.contactCaseReportingUser = contactCaseReportingUser;
	}

	public Join<Case, Region> getContactCaseRegion() {
		return getOrCreate(contactCaseRegion, Case.REGION, JoinType.LEFT, getContactCase(), this::setContactCaseRegion);
	}

	private void setContactCaseRegion(Join<Case, Region> contactCaseRegion) {
		this.contactCaseRegion = contactCaseRegion;
	}

	public Join<Case, District> getContactCaseDistrict() {
		return getOrCreate(contactCaseDistrict, Case.DISTRICT, JoinType.LEFT, getContactCase(), this::setContactCaseDistrict);
	}

	private void setContactCaseDistrict(Join<Case, District> contactCaseDistrict) {
		this.contactCaseDistrict = contactCaseDistrict;
	}

	public Join<Case, Community> getContactCaseCommunity() {
		return getOrCreate(contactCaseCommunity, Case.COMMUNITY, JoinType.LEFT, getContactCase(), this::setContactCaseCommunity);
	}

	private void setContactCaseCommunity(Join<Case, Community> contactCaseCommunity) {
		this.contactCaseCommunity = contactCaseCommunity;
	}

	public Join<Case, Facility> getContactCaseHealthFacility() {
		return getOrCreate(contactCaseHealthFacility, Case.HEALTH_FACILITY, JoinType.LEFT, getContactCase(), this::setContactCaseHealthFacility);
	}

	private void setContactCaseHealthFacility(Join<Case, Facility> contactCaseHealthFacility) {
		this.contactCaseHealthFacility = contactCaseHealthFacility;
	}

	public Join<Case, PointOfEntry> getContactCasePointOfEntry() {
		return getOrCreate(contactCasePointOfEntry, Case.POINT_OF_ENTRY, JoinType.LEFT, getContactCase(), this::setContactCasePointOfEntry);
	}

	private void setContactCasePointOfEntry(Join<Case, PointOfEntry> contactCasePointOfEntry) {
		this.contactCasePointOfEntry = contactCasePointOfEntry;
	}

	public Join<Person, Location> getCasePersonMainAddress() {
		return getOrCreate(casePersonMainAddress, Person.MAIN_ADDRESS, JoinType.LEFT, getCasePerson(), this::setCasePersonMainAddress);
	}

	private void setCasePersonMainAddress(Join<Person, Location> casePersonMainAddress) {
		this.casePersonMainAddress = casePersonMainAddress;
	}

	public Join<Location, Region> getCasePersonMainAddressRegion() {
		return getOrCreate(
			casePersonMainAddressRegion,
			Location.REGION,
			JoinType.LEFT,
			getCasePersonMainAddress(),
			this::setCasePersonMainAddressRegion);
	}

	private void setCasePersonMainAddressRegion(Join<Location, Region> casePersonMainAddressRegion) {
		this.casePersonMainAddressRegion = casePersonMainAddressRegion;
	}

	public Join<Location, District> getCasePersonMainAddressDistrict() {
		return getOrCreate(
			casePersonMainAddressDistrict,
			Location.DISTRICT,
			JoinType.LEFT,
			getCasePersonMainAddress(),
			this::setCasePersonMainAddressDistrict);
	}

	private void setCasePersonMainAddressDistrict(Join<Location, District> casePersonMainAddressDistrict) {
		this.casePersonMainAddressDistrict = casePersonMainAddressDistrict;
	}

	public Join<Location, Community> getCasePersonMainAddressCommunity() {

		return getOrCreate(
			casePersonMainAddressCommunity,
			Location.COMMUNITY,
			JoinType.LEFT,
			getCasePersonMainAddress(),
			this::setCasePersonMainAddressCommunity);
	}

	private void setCasePersonMainAddressCommunity(Join<Location, Community> casePersonMainAddressCommunity) {
		this.casePersonMainAddressCommunity = casePersonMainAddressCommunity;
	}

	public Join<Person, Location> getContactPersonMainAddress() {
		return getOrCreate(contactPersonMainAddress, Person.MAIN_ADDRESS, JoinType.LEFT, getContactPerson(), this::setContactPersonMainAddress);
	}

	public void setContactPersonMainAddress(Join<Person, Location> contactPersonMainAddress) {
		this.contactPersonMainAddress = contactPersonMainAddress;
	}

	public Join<Location, Region> getContactPersonMainAddressRegion() {
		return getOrCreate(
			contactPersonMainAddressRegion,
			Location.REGION,
			JoinType.LEFT,
			getContactPersonMainAddress(),
			this::setContactPersonMainAddressRegion);
	}

	public void setContactPersonMainAddressRegion(Join<Location, Region> contactPersonMainAddressRegion) {
		this.contactPersonMainAddressRegion = contactPersonMainAddressRegion;
	}

	public Join<Location, District> getContactPersonMainAddressDistrict() {
		return getOrCreate(
			contactPersonMainAddressDistrict,
			Location.DISTRICT,
			JoinType.LEFT,
			getContactPersonMainAddress(),
			this::setContactPersonMainAddressDistrict);
	}

	public void setContactPersonMainAddressDistrict(Join<Location, District> contactPersonMainAddressDistrict) {
		this.contactPersonMainAddressDistrict = contactPersonMainAddressDistrict;
	}

	public Join<Location, Community> getContactPersonMainAddressCommunity() {
		return getOrCreate(
			contactPersonMainAddressCommunity,
			Location.COMMUNITY,
			JoinType.LEFT,
			getContactPersonMainAddress(),
			this::setContactPersonMainAddressCommunity);
	}

	public void setContactPersonMainAddressCommunity(Join<Location, Community> contactPersonMainAddressCommunity) {
		this.contactPersonMainAddressCommunity = contactPersonMainAddressCommunity;
	}
}
