package de.symeda.sormas.backend.travelentry;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObjectJoins;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.user.User;

public class TravelEntryJoins<T> extends AbstractDomainObjectJoins<T, TravelEntry> {

	private Join<TravelEntry, Person> person;
	private Join<TravelEntry, Region> responsibleRegion;
	private Join<TravelEntry, District> responsibleDistrict;
	private Join<TravelEntry, Community> responsibleCommunity;
	private Join<TravelEntry, User> reportingUser;
	private Join<TravelEntry, Region> pointOfEntryRegion;
	private Join<TravelEntry, District> pointOfEntryDistrict;
	private Join<TravelEntry, PointOfEntry> pointOfEntry;
	private Join<TravelEntry, Case> resultingCase;

	public TravelEntryJoins(From<T, TravelEntry> root) {
		super(root);
	}

	public Join<TravelEntry, Person> getPerson() {
		return getOrCreate(person, TravelEntry.PERSON, JoinType.LEFT, this::setPerson);
	}

	private void setPerson(Join<TravelEntry, Person> person) {
		this.person = person;
	}

	public Join<TravelEntry, Region> getResponsibleRegion() {
		return getOrCreate(responsibleRegion, TravelEntry.RESPONSIBLE_REGION, JoinType.LEFT, this::setResponsibleRegion);
	}

	private void setResponsibleRegion(Join<TravelEntry, Region> responsibleRegion) {
		this.responsibleRegion = responsibleRegion;
	}

	public Join<TravelEntry, District> getResponsibleDistrict() {
		return getOrCreate(responsibleDistrict, TravelEntry.RESPONSIBLE_DISTRICT, JoinType.LEFT, this::setResponsibleDistrict);
	}

	private void setResponsibleDistrict(Join<TravelEntry, District> responsibleDistrict) {
		this.responsibleDistrict = responsibleDistrict;
	}

	public Join<TravelEntry, Community> getResponsibleCommunity() {
		return getOrCreate(responsibleCommunity, TravelEntry.RESPONSIBLE_COMMUNITY, JoinType.LEFT, this::setResponsibleCommunity);
	}

	private void setResponsibleCommunity(Join<TravelEntry, Community> responsibleCommunity) {
		this.responsibleCommunity = responsibleCommunity;
	}

	public Join<TravelEntry, User> getReportingUser() {
		return getOrCreate(reportingUser, TravelEntry.REPORTING_USER, JoinType.LEFT, this::setReportingUser);
	}

	private void setReportingUser(Join<TravelEntry, User> reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Join<TravelEntry, Region> getPointOfEntryRegion() {
		return getOrCreate(pointOfEntryRegion, TravelEntry.POINT_OF_ENTRY_REGION, JoinType.LEFT, this::setPointOfEntryRegion);
	}

	public void setPointOfEntryRegion(Join<TravelEntry, Region> pointOfEntryRegion) {
		this.pointOfEntryRegion = pointOfEntryRegion;
	}

	public Join<TravelEntry, District> getPointOfEntryDistrict() {
		return getOrCreate(pointOfEntryDistrict, TravelEntry.POINT_OF_ENTRY_DISTRICT, JoinType.LEFT, this::setPointOfEntryDistrict);
	}

	private void setPointOfEntryDistrict(Join<TravelEntry, District> pointOfEntryDistrict) {
		this.pointOfEntryDistrict = pointOfEntryDistrict;
	}

	public Join<TravelEntry, PointOfEntry> getPointOfEntry() {
		return getOrCreate(pointOfEntry, TravelEntry.POINT_OF_ENTRY, JoinType.LEFT, this::setPointOfEntry);
	}

	private void setPointOfEntry(Join<TravelEntry, PointOfEntry> pointOfEntry) {
		this.pointOfEntry = pointOfEntry;
	}

	public Join<TravelEntry, Case> getResultingCase() {
		return getOrCreate(resultingCase, TravelEntry.RESULTING_CASE, JoinType.LEFT, this::setResultingCase);
	}

	private void setResultingCase(Join<TravelEntry, Case> resultingCase) {
		this.resultingCase = resultingCase;
	}
}
