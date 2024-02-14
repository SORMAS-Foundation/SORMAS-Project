package de.symeda.sormas.backend.event;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleJoins;
import de.symeda.sormas.backend.sample.SampleJurisdictionPredicateValidator;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;

public class EventParticipantJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

	private final CriteriaQuery<?> cq;
	private EventParticipantJoins joins;

	private EventParticipantJurisdictionPredicateValidator(EventParticipantQueryContext qc, User user) {
		super(qc.getCriteriaBuilder(), user, null, null);
		this.joins = qc.getJoins();
		this.cq = qc.getQuery();
	}

	private EventParticipantJurisdictionPredicateValidator(EventParticipantQueryContext qc, Path userPath) {
		super(qc.getCriteriaBuilder(), null, userPath, null);
		this.joins = qc.getJoins();
		this.cq = qc.getQuery();
	}

	public static EventParticipantJurisdictionPredicateValidator of(EventParticipantQueryContext qc, User user) {
		return new EventParticipantJurisdictionPredicateValidator(qc, user);
	}

	public static EventParticipantJurisdictionPredicateValidator of(EventParticipantQueryContext qc, Path userPath) {
		return new EventParticipantJurisdictionPredicateValidator(qc, userPath);
	}

	@Override
	public Predicate isRootInJurisdictionOrOwned() {
		final Predicate reportedByCurrentUser = getReportedByCurrentUser();
		return cb.or(reportedByCurrentUser, this.isRootInJurisdiction());
	}

	private Predicate getReportedByCurrentUser() {
		final Predicate reportedByCurrentUser = cb.and(
			cb.isNotNull(joins.getRoot().get(EventParticipant.REPORTING_USER)),
			cb.equal(joins.getRoot().get(EventParticipant.REPORTING_USER).get(User.ID), user.getId()));
		return reportedByCurrentUser;
	}

	@Override
	public Predicate isRootInJurisdictionForRestrictedAccess() {
		final Predicate reportedByCurrentUser = getReportedByCurrentUser();
		final Predicate restrictedAccess = user != null
			? cb.equal(joins.getEvent().get(Event.RESPONSIBLE_USER).get(User.ID), user.getId())
			: cb.equal(joins.getEvent().get(Event.RESPONSIBLE_USER).get(User.ID), userPath.get(User.ID));
		return cb.or(reportedByCurrentUser, restrictedAccess);
	}

	@Override
	protected Predicate getLimitedDiseasePredicate() {
		return CriteriaBuilderHelper.limitedDiseasePredicate(cb, user, joins.getEventJoins().getRoot().get(Event.DISEASE));
	}

	@Override
	public Predicate isRootInJurisdiction() {
		return isInJurisdictionByJurisdictionLevel(user.getJurisdictionLevel());
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
			cb.equal(joins.getRoot().get(EventParticipant.REGION).get(Region.ID), user.getRegion().getId()),
			cb.equal(joins.getEventAddress().get(Location.REGION).get(Region.ID), user.getRegion().getId()));
	}

	@Override
	protected Predicate whenDistrictLevel() {
		return CriteriaBuilderHelper.or(
			cb,
			cb.equal(joins.getRoot().get(EventParticipant.DISTRICT).get(District.ID), user.getDistrict().getId()),
			cb.equal(joins.getEventAddress().get(Location.DISTRICT).get(District.ID), user.getDistrict().getId()));
	}

	@Override
	protected Predicate whenCommunityLevel() {
		return CriteriaBuilderHelper.or(cb, cb.equal(joins.getEventAddress().get(Location.COMMUNITY).get(Community.ID), user.getCommunity().getId()));
	}

	@Override
	protected Predicate whenFacilityLevel() {
		return cb.equal(joins.getAddress().get(Location.FACILITY).get(Facility.ID), user.getHealthFacility().getId());
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
		SampleJurisdictionPredicateValidator sampleJurisdictionPredicateValidator = user != null
			? SampleJurisdictionPredicateValidator.withoutAssociations(cb, sampleJoins, user)
			: SampleJurisdictionPredicateValidator.withoutAssociations(cb, sampleJoins, userPath);

		sampleSubQuery.where(cb.and(cb.equal(eventParticipant, joins.getRoot()), sampleJurisdictionPredicateValidator.inJurisdictionOrOwned()));
		sampleSubQuery.select(sampleRoot.get(Sample.ID));
		return cb.exists(sampleSubQuery);
	}
}
