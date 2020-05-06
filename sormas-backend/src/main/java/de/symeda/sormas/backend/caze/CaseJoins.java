package de.symeda.sormas.backend.caze;

import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.AbstractDomainObjectJoins;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

class CaseJoins extends AbstractDomainObjectJoins<Case> {
	private Join<Case, Person> person;
	private Join<Case, Region> region;
	private Join<Case, District> district;
	private Join<Case, Facility> facility;
	private Join<Case, PointOfEntry> pointOfEntry;
	private Join<Case, User> surveillanceOfficer;
	private Join<Person, Location> address;
	private Join<Case, User> reportingUser;

	public CaseJoins(Root<Case> caze) {
		super(caze);
	}

	public Join<Case, Person> getPerson() {
		return getOrCreate(person, Case.PERSON, JoinType.LEFT);
	}

	public Join<Case, Region> getRegion() {
		return getOrCreate(region, Case.REGION, JoinType.LEFT);
	}

	public Join<Case, District> getDistrict() {
		return getOrCreate(district, Case.DISTRICT, JoinType.LEFT);
	}

	public Join<Case, Facility> getFacility() {
		return getOrCreate(facility, Case.HEALTH_FACILITY, JoinType.LEFT);
	}

	public Join<Case, PointOfEntry> getPointOfEntry() {
		return getOrCreate(pointOfEntry, Case.POINT_OF_ENTRY, JoinType.LEFT);
	}

	public Join<Case, User> getSurveillanceOfficer() {
		return getOrCreate(surveillanceOfficer, Case.SURVEILLANCE_OFFICER, JoinType.LEFT);
	}

	public Join<Person, Location> getAddress() {
		return getOrCreate(address, Person.ADDRESS, JoinType.LEFT, getPerson());
	}

	public Join<Case, User> getReportingUser() {
		return getOrCreate(reportingUser, Case.REPORTING_USER, JoinType.LEFT);
	}
}
