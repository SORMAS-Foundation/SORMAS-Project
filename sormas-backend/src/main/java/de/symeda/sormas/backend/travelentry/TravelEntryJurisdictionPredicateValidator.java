package de.symeda.sormas.backend.travelentry;

import java.util.Collections;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.caze.CaseJurisdictionPredicateValidator;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
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
			Collections.singletonList(
				CaseJurisdictionPredicateValidator.of(
					new CaseQueryContext<>(qc.getCriteriaBuilder(), qc.getQuery(), ((TravelEntryJoins) qc.getJoins()).getResultingCase()),
					currentUser)));
	}

	@Override
	protected Predicate isInJurisdiction() {
		return isInJurisdictionByJurisdictionLevel(currentUser.getJurisdictionLevel());
	}

	@Override
	protected Predicate isInJurisdictionOrOwned() {
		final Predicate reportedByCurrentUser = cb.and(
			cb.isNotNull(joins.getRoot().get(TravelEntry.REPORTING_USER)),
			cb.equal(joins.getRoot().get(TravelEntry.REPORTING_USER).get(User.ID), currentUser.getId()));
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
		return CriteriaBuilderHelper.or(
			cb,
			cb.equal(joins.getRoot().get(TravelEntry.RESPONSIBLE_REGION).get(Region.ID), currentUser.getRegion().getId()),
			cb.equal(joins.getRoot().get(TravelEntry.POINT_OF_ENTRY_REGION).get(Region.ID), currentUser.getRegion().getId()));
	}

	@Override
	protected Predicate whenDistrictLevel() {
		return CriteriaBuilderHelper.or(
			cb,
			cb.equal(joins.getRoot().get(TravelEntry.RESPONSIBLE_DISTRICT).get(District.ID), currentUser.getDistrict().getId()),
			cb.equal(joins.getRoot().get(TravelEntry.POINT_OF_ENTRY_DISTRICT).get(District.ID), currentUser.getDistrict().getId()));
	}

	@Override
	protected Predicate whenCommunityLevel() {
		return cb.equal(joins.getRoot().get(TravelEntry.RESPONSIBLE_COMMUNITY).get(Community.ID), currentUser.getCommunity().getId());
	}

	@Override
	protected Predicate whenFacilityLevel() {
		return cb.disjunction();
	}

	@Override
	protected Predicate whenPointOfEntryLevel() {
		return cb.equal(joins.getRoot().get(TravelEntry.POINT_OF_ENTRY).get(PointOfEntry.ID), currentUser.getPointOfEntry().getId());
	}

	@Override
	protected Predicate whenLaboratoryLevel() {
		return cb.disjunction();
	}
}
