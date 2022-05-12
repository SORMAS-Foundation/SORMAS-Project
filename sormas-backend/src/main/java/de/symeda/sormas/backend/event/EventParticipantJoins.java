package de.symeda.sormas.backend.event;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseJoins;
import de.symeda.sormas.backend.common.QueryJoins;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonJoins;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.user.User;

public class EventParticipantJoins extends QueryJoins<EventParticipant> {

	private Join<EventParticipant, User> eventParticipantReportingUser;
	private Join<EventParticipant, Person> person;
	private Join<EventParticipant, Region> eventParticipantResponsibleRegion;
	private Join<EventParticipant, District> eventParticipantResponsibleDistrict;
	private Join<EventParticipant, Case> resultingCase;
	private Join<EventParticipant, Event> event;
	private Join<EventParticipant, Sample> samples;

	private CaseJoins caseJoins;
	private PersonJoins personJoins;
	private EventJoins eventJoins;

	public EventParticipantJoins(From<?, EventParticipant> eventParticipant) {
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
		return getPersonJoins().getAddress();
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

	public Join<EventParticipant, Case> getResultingCase() {
		return getOrCreate(resultingCase, EventParticipant.RESULTING_CASE, JoinType.LEFT, this::setResultingCase);
	}

	private void setResultingCase(Join<EventParticipant, Case> resultingCase) {
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

	public Join<Case, PointOfEntry> getCaseAsPointOfEntry() {
		return getCaseJoins().getPointOfEntry();
	}

	public Join<EventParticipant, Event> getEvent() {
		return getOrCreate(event, EventParticipant.EVENT, JoinType.LEFT, this::setEvent);
	}

	private void setEvent(Join<EventParticipant, Event> event) {
		this.event = event;
	}

	public Join<EventParticipant, Sample> getSamples() {
		return getOrCreate(samples, EventParticipant.SAMPLES, JoinType.LEFT, this::setSamples);
	}

	public void setSamples(Join<EventParticipant, Sample> samples) {
		this.samples = samples;
	}

	public Join<Event, Location> getEventAddress() {
		return getEventJoins().getLocation();
	}

	public Join<Location, Region> getEventAddressRegion() {
		return getEventJoins().getLocationJoins().getRegion();
	}

	public Join<Location, District> getEventAddressDistrict() {
		return getEventJoins().getLocationJoins().getDistrict();
	}

	public Join<Location, Community> getEventAddressCommunity() {
		return getEventJoins().getLocationJoins().getCommunity();
	}

	public CaseJoins getCaseJoins() {
		return getOrCreate(caseJoins, () -> new CaseJoins(getResultingCase()), this::setCaseJoins);
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

	public EventJoins getEventJoins() {
		return getOrCreate(eventJoins, () -> new EventJoins(getEvent()), this::setEventJoins);
	}

	private void setEventJoins(EventJoins eventJoins) {
		this.eventJoins = eventJoins;
	}
}
