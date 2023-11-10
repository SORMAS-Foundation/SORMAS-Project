package de.symeda.sormas.backend.travelentry;

import java.util.Collections;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.caze.CaseJurisdictionPredicateValidator;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;

public class TravelEntryJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

	private final TravelEntryJoins joins;

	private TravelEntryJurisdictionPredicateValidator(
		CriteriaQuery<?> cq,
		CriteriaBuilder cb,
		TravelEntryJoins joins,
		User user,
		List<PredicateJurisdictionValidator> associatedJurisdictionValidators) {
		super(cb, user, null, associatedJurisdictionValidators);
		this.joins = joins;
	}

	private TravelEntryJurisdictionPredicateValidator(
		CriteriaQuery<?> cq,
		CriteriaBuilder cb,
		TravelEntryJoins joins,
		Path userPath,
		List<PredicateJurisdictionValidator> associatedJurisdictionValidators) {
		super(cb, null, userPath, associatedJurisdictionValidators);
		this.joins = joins;
	}

	public static TravelEntryJurisdictionPredicateValidator of(TravelEntryQueryContext qc, User user) {
		return new TravelEntryJurisdictionPredicateValidator(
			qc.getQuery(),
			qc.getCriteriaBuilder(),
			qc.getJoins(),
			user,
			Collections.singletonList(
				CaseJurisdictionPredicateValidator
					.of(new CaseQueryContext(qc.getCriteriaBuilder(), qc.getQuery(), ((TravelEntryJoins) qc.getJoins()).getResultingCase()), user)));
	}

	public static TravelEntryJurisdictionPredicateValidator of(TravelEntryQueryContext qc, Path userPath) {
		return new TravelEntryJurisdictionPredicateValidator(
			qc.getQuery(),
			qc.getCriteriaBuilder(),
			qc.getJoins(),
			userPath,
			Collections.singletonList(
				CaseJurisdictionPredicateValidator.of(
					new CaseQueryContext(qc.getCriteriaBuilder(), qc.getQuery(), ((TravelEntryJoins) qc.getJoins()).getResultingCase()),
					userPath)));
	}

	@Override
	public Predicate isRootInJurisdiction() {
		return super.isRootInJurisdiction();
	}

	@Override
	public Predicate isRootInJurisdictionOrOwned() {
		final Predicate reportedByCurrentUser = cb.and(
			cb.isNotNull(joins.getRoot().get(TravelEntry.REPORTING_USER)),
			user != null
				? cb.equal(joins.getRoot().get(TravelEntry.REPORTING_USER).get(User.ID), user.getId())
				: cb.equal(joins.getRoot().get(TravelEntry.REPORTING_USER).get(User.ID), userPath.get(User.ID)));

		return cb.or(reportedByCurrentUser, isRootInJurisdiction());
	}

	@Override
	protected Predicate getLimitedDiseasePredicate() {
		return CriteriaBuilderHelper.limitedDiseasePredicate(cb, user, joins.getRoot().get(TravelEntry.DISEASE));
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
			user != null
				? cb.equal(joins.getRoot().get(TravelEntry.RESPONSIBLE_REGION).get(Region.ID), user.getRegion().getId())
				: cb.equal(joins.getRoot().get(TravelEntry.RESPONSIBLE_REGION).get(Region.ID), userPath.get(User.REGION).get(Region.ID)),
			user != null
				? cb.equal(joins.getRoot().get(TravelEntry.POINT_OF_ENTRY_REGION).get(Region.ID), user.getRegion().getId())
				: cb.equal(joins.getRoot().get(TravelEntry.POINT_OF_ENTRY_REGION).get(Region.ID), userPath.get(User.REGION).get(Region.ID)));
	}

	@Override
	protected Predicate whenDistrictLevel() {
		return CriteriaBuilderHelper.or(
			cb,
			user != null
				? cb.equal(joins.getRoot().get(TravelEntry.RESPONSIBLE_DISTRICT).get(District.ID), user.getDistrict().getId())
				: cb.equal(joins.getRoot().get(TravelEntry.RESPONSIBLE_DISTRICT).get(District.ID), userPath.get(User.DISTRICT).get(District.ID)),
			user != null
				? cb.equal(joins.getRoot().get(TravelEntry.POINT_OF_ENTRY_DISTRICT).get(District.ID), user.getDistrict().getId())
				: cb.equal(joins.getRoot().get(TravelEntry.POINT_OF_ENTRY_DISTRICT).get(District.ID), userPath.get(User.DISTRICT).get(District.ID)));
	}

	@Override
	protected Predicate whenCommunityLevel() {
		return user != null
			? cb.equal(joins.getRoot().get(TravelEntry.RESPONSIBLE_COMMUNITY).get(Community.ID), user.getCommunity().getId())
			: cb.equal(joins.getRoot().get(TravelEntry.RESPONSIBLE_COMMUNITY).get(Community.ID), userPath.get(User.COMMUNITY).get(Community.ID));
	}

	@Override
	protected Predicate whenFacilityLevel() {
		return cb.disjunction();
	}

	@Override
	protected Predicate whenPointOfEntryLevel() {
		return user != null
			? cb.equal(joins.getRoot().get(TravelEntry.POINT_OF_ENTRY).get(PointOfEntry.ID), user.getPointOfEntry().getId())
			: cb.equal(joins.getRoot().get(TravelEntry.POINT_OF_ENTRY).get(PointOfEntry.ID), userPath.get(User.POINT_OF_ENTRY).get(PointOfEntry.ID));
	}

	@Override
	protected Predicate whenLaboratoryLevel() {
		return cb.disjunction();
	}
}
