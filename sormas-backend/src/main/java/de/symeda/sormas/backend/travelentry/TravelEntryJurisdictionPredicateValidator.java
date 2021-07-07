package de.symeda.sormas.backend.travelentry;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;

public class TravelEntryJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

	private final TravelEntryJoins<?> joins;
	private final User currentUser;
	private final CriteriaQuery<?> cq;

	private TravelEntryJurisdictionPredicateValidator(
		CriteriaQuery<?> cq,
		CriteriaBuilder cb,
		TravelEntryJoins<?> joins,
		User currentUser,
		List<PredicateJurisdictionValidator> associatedJurisdictionValidators) {
		super(cb, associatedJurisdictionValidators);
		this.joins = joins;
		this.currentUser = currentUser;
		this.cq = cq;
	}

	public static TravelEntryJurisdictionPredicateValidator of(TravelEntryQueryContext qc, User currentUser) {
		return new TravelEntryJurisdictionPredicateValidator(
			qc.getQuery(),
			qc.getCriteriaBuilder(),
			(TravelEntryJoins<?>) qc.getJoins(),
			currentUser,
			null);
	}

	@Override
	protected Predicate isInJurisdiction() {
		return isInJurisdictionByJurisdictionLevel(currentUser.getJurisdictionLevel());
	}

	@Override
	protected Predicate isInJurisdictionOrOwned() {
		final Predicate reportedByCurrentUser =
			cb.and(cb.isNotNull(joins.getReportingUser()), cb.equal(joins.getReportingUser().get(User.UUID), currentUser.getUuid()));
		return cb.or(reportedByCurrentUser, isInJurisdiction());
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
		return cb.equal(joins.getResponsibleRegion().get(Region.ID), currentUser.getRegion().getId());
	}

	@Override
	protected Predicate whenDistrictLevel() {
		return cb.equal(joins.getResponsibleDistrict().get(District.ID), currentUser.getDistrict().getId());
	}

	@Override
	protected Predicate whenCommunityLevel() {
		return cb.equal(joins.getResponsibleCommunity().get(Community.ID), currentUser.getCommunity().getId());
	}

	@Override
	protected Predicate whenFacilityLevel() {
		return cb.disjunction();
	}

	@Override
	protected Predicate whenPointOfEntryLevel() {
		return cb.equal(joins.getPointOfEntry().get(PointOfEntry.ID), currentUser.getPointOfEntry().getId());
	}

	@Override
	protected Predicate whenLaboratoryLevel() {
		return cb.disjunction();
	}
}
