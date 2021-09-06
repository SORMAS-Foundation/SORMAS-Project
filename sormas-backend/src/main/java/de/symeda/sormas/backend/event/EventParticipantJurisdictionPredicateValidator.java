package de.symeda.sormas.backend.event;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleJoins;
import de.symeda.sormas.backend.sample.SampleJurisdictionPredicateValidator;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;
import de.symeda.sormas.utils.EventParticipantJoins;

public class EventParticipantJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

	private final CriteriaQuery<?> cq;
	private EventParticipantJoins<?> joins;
	private User currentUser;

	private EventParticipantJurisdictionPredicateValidator(EventParticipantQueryContext qc, User currentUser) {
		super(qc.getCriteriaBuilder(), null);
		this.joins = (EventParticipantJoins<?>) qc.getJoins();
		this.currentUser = currentUser;
		this.cq = qc.getQuery();
	}

	public static EventParticipantJurisdictionPredicateValidator of(EventParticipantQueryContext qc, User currentUser) {
		return new EventParticipantJurisdictionPredicateValidator(qc, currentUser);
	}

	@Override
	protected Predicate isInJurisdictionOrOwned() {
		final Predicate reportedByCurrentUser = cb.and(
			cb.isNotNull(joins.getRoot().get(EventParticipant.REPORTING_USER)),
			cb.equal(joins.getRoot().get(EventParticipant.REPORTING_USER).get(User.ID), currentUser.getId()));
		return cb.or(reportedByCurrentUser, isInJurisdiction());
	}

	@Override
	protected Predicate isInJurisdiction() {
		return isInJurisdictionByJurisdictionLevel(currentUser.getJurisdictionLevel());
	}

	@Override
	protected Predicate whenNotAllowed() {
		return cb.disjunction();
	}

	@Override
	protected Predicate whenNationalLevel() {
		return cb.conjunction();
	}

	@Override
	protected Predicate whenRegionalLevel() {
		return CriteriaBuilderHelper.or(
			cb,
			cb.equal(joins.getRoot().get(EventParticipant.REGION).get(Region.ID), currentUser.getRegion().getId()),
			cb.equal(joins.getEventAddress().get(Location.REGION).get(Region.ID), currentUser.getRegion().getId()));
	}

	@Override
	protected Predicate whenDistrictLevel() {
		return CriteriaBuilderHelper.or(
			cb,
			cb.equal(joins.getRoot().get(EventParticipant.DISTRICT).get(District.ID), currentUser.getDistrict().getId()),
			cb.equal(joins.getEventAddress().get(Location.DISTRICT).get(District.ID), currentUser.getDistrict().getId()));
	}

	@Override
	protected Predicate whenCommunityLevel() {
		return CriteriaBuilderHelper
			.or(cb, cb.equal(joins.getEventAddress().get(Location.COMMUNITY).get(Community.ID), currentUser.getCommunity().getId()));
	}

	@Override
	protected Predicate whenFacilityLevel() {
		return cb.equal(joins.getAddress().get(Location.FACILITY).get(Facility.ID), currentUser.getHealthFacility().getId());
	}

	@Override
	protected Predicate whenPointOfEntryLevel() {
		return cb.disjunction();
	}

	@Override
	protected Predicate whenLaboratoryLevel() {
		final Subquery<Long> sampleSubQuery = cq.subquery(Long.class);
		final Root<Sample> sampleRoot = sampleSubQuery.from(Sample.class);
		final SampleJoins sampleJoins = new SampleJoins(sampleRoot);
		final Join eventParticipant = sampleJoins.getEventParticipant();
		SampleJurisdictionPredicateValidator sampleJurisdictionPredicateValidator =
			SampleJurisdictionPredicateValidator.withoutAssociations(cb, sampleJoins, currentUser);
		sampleSubQuery.where(cb.and(cb.equal(eventParticipant, joins.getRoot()), sampleJurisdictionPredicateValidator.inJurisdictionOrOwned()));
		sampleSubQuery.select(sampleRoot.get(Sample.ID));
		return cb.exists(sampleSubQuery);
	}
}
