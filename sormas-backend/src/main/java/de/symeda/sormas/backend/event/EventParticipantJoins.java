package de.symeda.sormas.backend.event;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.QueryJoins;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.User;

public class EventParticipantJoins<T> extends QueryJoins<T, EventParticipant> {

	private Join<EventParticipant, User> eventParticipantReportingUser;

	private Join<EventParticipant, Person> person;
	private Join<EventParticipant, Region> eventParticipantResponsibleRegion;
	private Join<EventParticipant, District> eventParticipantResponsibleDistrict;

	private Join<Person, Location> address;
	private Join<Location, Region> addressRegion;
	private Join<Location, District> addressDistrict;
	private Join<Location, Community> addressCommunity;
	private Join<Location, Facility> addressFacility;

	private Join<EventParticipant, Case> resultingCase;
	private Join<Case, Person> casePerson;
	private Join<Case, User> caseReportingUser;
	private Join<Case, Region> caseResponsibleRegion;
	private Join<Case, District> caseResponsibleDistrict;
	private Join<Case, Community> caseResponsibleCommunity;
	private Join<Case, Region> caseRegion;
	private Join<Case, District> caseDistrict;
	private Join<Case, Community> caseCommunity;
	private Join<Case, Facility> caseHealthFacility;
	private Join<Case, PointOfEntry> caseAsPointOfEntry;

	private Join<EventParticipant, Event> event;
	private Join<Event, Location> eventAddress;
	private Join<Location, Region> eventAddressRegion;
	private Join<Location, District> eventAddressDistrict;
	private Join<Location, Community> eventAddressCommunity;

	public EventParticipantJoins(From<T, EventParticipant> eventParticipant) {
		super(eventParticipant);
	}

	public Join<EventParticipant, User> getEventParticipantReportingUser() {
		return getOrCreate(eventParticipantReportingUser, EventParticipant.REPORTING_USER, JoinType.LEFT, this::setEventParticipantReportingUser);
	}

	private void setEventParticipantReportingUser(Join<EventParticipant, User> eventParticipantReportingUser) {
		this.eventParticipantReportingUser = eventParticipantReportingUser;
	}

	public Join<EventParticipant, Person> getPerson() {
		return getOrCreate(person, EventParticipant.PERSON, JoinType.LEFT, this::setPerson);
	}

	private void setPerson(Join<EventParticipant, Person> person) {
		this.person = person;
	}

	public Join<EventParticipant, Region> getEventParticipantResponsibleRegion() {
		return getOrCreate(eventParticipantResponsibleRegion, EventParticipant.REGION, JoinType.LEFT, this::setEventParticipantResponsibleRegion);
	}

	private void setEventParticipantResponsibleRegion(Join<EventParticipant, Region> eventParticipantResponsibleRegion) {
		this.eventParticipantResponsibleRegion = eventParticipantResponsibleRegion;
	}

	public Join<EventParticipant, District> getEventParticipantResponsibleDistrict() {
		return getOrCreate(
			eventParticipantResponsibleDistrict,
			EventParticipant.DISTRICT,
			JoinType.LEFT,
			this::setEventParticipantResponsibleDistrict);
	}

	private void setEventParticipantResponsibleDistrict(Join<EventParticipant, District> eventParticipantResponsibleDistrict) {
		this.eventParticipantResponsibleDistrict = eventParticipantResponsibleDistrict;
	}

	public Join<Person, Location> getAddress() {
		return getOrCreate(address, Person.ADDRESS, JoinType.LEFT, getPerson(), this::setAddress);
	}

	private void setAddress(Join<Person, Location> address) {
		this.address = address;
	}

	public Join<Location, Region> getAddressRegion() {
		return getOrCreate(addressRegion, Location.REGION, JoinType.LEFT, getAddress(), this::setAddressRegion);
	}

	private void setAddressRegion(Join<Location, Region> addressRegion) {
		this.addressRegion = addressRegion;
	}

	public Join<Location, District> getAddressDistrict() {
		return getOrCreate(addressDistrict, Location.DISTRICT, JoinType.LEFT, getAddress(), this::setAddressDistrict);
	}

	private void setAddressDistrict(Join<Location, District> addressDistrict) {
		this.addressDistrict = addressDistrict;
	}

	public Join<Location, Community> getAddressCommunity() {
		return getOrCreate(addressCommunity, Location.COMMUNITY, JoinType.LEFT, getAddress(), this::setAddressCommunity);
	}

	private void setAddressCommunity(Join<Location, Community> addressCommunity) {
		this.addressCommunity = addressCommunity;
	}

	public Join<Location, Facility> getAddressFacility() {
		return getOrCreate(addressFacility, Location.FACILITY, JoinType.LEFT, getAddress(), this::setAddressFacility);
	}

	private void setAddressFacility(Join<Location, Facility> addressFacility) {
		this.addressFacility = addressFacility;
	}

	public Join<EventParticipant, Case> getResultingCase() {
		return getOrCreate(resultingCase, EventParticipant.RESULTING_CASE, JoinType.LEFT, this::setResultingCase);
	}

	private void setResultingCase(Join<EventParticipant, Case> resultingCase) {
		this.resultingCase = resultingCase;
	}

	public Join<Case, Person> getCasePerson() {
		return getOrCreate(casePerson, Case.PERSON, JoinType.LEFT, getResultingCase(), this::setCasePerson);
	}

	private void setCasePerson(Join<Case, Person> casePerson) {
		this.casePerson = casePerson;
	}

	public Join<Case, User> getCaseReportingUser() {
		return getOrCreate(caseReportingUser, Case.REPORTING_USER, JoinType.LEFT, getResultingCase(), this::setCaseReportingUser);
	}

	private void setCaseReportingUser(Join<Case, User> caseReportingUser) {
		this.caseReportingUser = caseReportingUser;
	}

	public Join<Case, Region> getCaseResponsibleRegion() {
		return getOrCreate(caseResponsibleRegion, Case.RESPONSIBLE_REGION, JoinType.LEFT, getResultingCase(), this::setCaseResponsibleRegion);
	}

	private void setCaseResponsibleRegion(Join<Case, Region> caseResponsibleRegion) {
		this.caseResponsibleRegion = caseResponsibleRegion;
	}

	public Join<Case, District> getCaseResponsibleDistrict() {
		return getOrCreate(caseResponsibleDistrict, Case.RESPONSIBLE_DISTRICT, JoinType.LEFT, getResultingCase(), this::setCaseResponsibleDistrict);
	}

	private void setCaseResponsibleDistrict(Join<Case, District> caseResponsibleDistrict) {
		this.caseResponsibleDistrict = caseResponsibleDistrict;
	}

	public Join<Case, Community> getCaseResponsibleCommunity() {
		return getOrCreate(
			caseResponsibleCommunity,
			Case.RESPONSIBLE_COMMUNITY,
			JoinType.LEFT,
			getResultingCase(),
			this::setCaseResponsibleCommunity);
	}

	private void setCaseResponsibleCommunity(Join<Case, Community> caseResponsibleCommunity) {
		this.caseResponsibleCommunity = caseResponsibleCommunity;
	}

	public Join<Case, Region> getCaseRegion() {
		return getOrCreate(caseRegion, Case.REGION, JoinType.LEFT, getResultingCase(), this::setCaseRegion);
	}

	private void setCaseRegion(Join<Case, Region> caseRegion) {
		this.caseRegion = caseRegion;
	}

	public Join<Case, District> getCaseDistrict() {
		return getOrCreate(caseDistrict, Case.DISTRICT, JoinType.LEFT, getResultingCase(), this::setCaseDistrict);
	}

	private void setCaseDistrict(Join<Case, District> caseDistrict) {
		this.caseDistrict = caseDistrict;
	}

	public Join<Case, Community> getCaseCommunity() {
		return getOrCreate(caseCommunity, Case.COMMUNITY, JoinType.LEFT, getResultingCase(), this::setCaseCommunity);
	}

	private void setCaseCommunity(Join<Case, Community> caseCommunity) {
		this.caseCommunity = caseCommunity;
	}

	public Join<Case, Facility> getCaseHealthFacility() {
		return getOrCreate(caseHealthFacility, Case.HEALTH_FACILITY, JoinType.LEFT, getResultingCase(), this::setCaseHealthFacility);
	}

	private void setCaseHealthFacility(Join<Case, Facility> caseHealthFacility) {
		this.caseHealthFacility = caseHealthFacility;
	}

	public Join<Case, PointOfEntry> getCaseAsPointOfEntry() {
		return getOrCreate(caseAsPointOfEntry, Case.POINT_OF_ENTRY, JoinType.LEFT, getResultingCase(), this::setCaseAsPointOfEntry);
	}

	private void setCaseAsPointOfEntry(Join<Case, PointOfEntry> caseAsPointOfEntry) {
		this.caseAsPointOfEntry = caseAsPointOfEntry;
	}

	public Join<EventParticipant, Event> getEvent() {
		return getOrCreate(event, EventParticipant.EVENT, JoinType.LEFT, this::setEvent);
	}

	private void setEvent(Join<EventParticipant, Event> event) {
		this.event = event;
	}

	public Join<Event, Location> getEventAddress() {
		return getOrCreate(eventAddress, Event.EVENT_LOCATION, JoinType.LEFT, getEvent(), this::setEventAddress);
	}

	private void setEventAddress(Join<Event, Location> eventAddress) {
		this.eventAddress = eventAddress;
	}

	public Join<Location, Region> getEventAddressRegion() {
		return getOrCreate(eventAddressRegion, Location.REGION, JoinType.LEFT, getEventAddress(), this::setEventAddressRegion);
	}

	private void setEventAddressRegion(Join<Location, Region> eventAddressRegion) {
		this.eventAddressRegion = eventAddressRegion;
	}

	public Join<Location, District> getEventAddressDistrict() {
		return getOrCreate(eventAddressDistrict, Location.DISTRICT, JoinType.LEFT, getEventAddress(), this::setEventAddressDistrict);
	}

	private void setEventAddressDistrict(Join<Location, District> eventAddressDistrict) {
		this.eventAddressDistrict = eventAddressDistrict;
	}

	public Join<Location, Community> getEventAddressCommunity() {
		return getOrCreate(eventAddressCommunity, Location.COMMUNITY, JoinType.LEFT, getEventAddress(), this::setEventAddressCommunity);
	}

	private void setEventAddressCommunity(Join<Location, Community> eventAddressCommunity) {
		this.eventAddressCommunity = eventAddressCommunity;
	}
}
