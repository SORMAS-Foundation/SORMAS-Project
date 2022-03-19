package de.symeda.sormas.backend.immunization;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.immunization.joins.DirectoryImmunizationJoins;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;

public final class DirectoryImmunizationJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

	private final DirectoryImmunizationJoins<?> joins;

	private DirectoryImmunizationJurisdictionPredicateValidator(
		CriteriaBuilder cb,
		DirectoryImmunizationJoins<?> joins,
		User user,
		List<PredicateJurisdictionValidator> associatedJurisdictionValidators) {
		super(cb, user, null, associatedJurisdictionValidators);
		this.joins = joins;
	}

	public static DirectoryImmunizationJurisdictionPredicateValidator of(DirectoryImmunizationQueryContext qc, User user) {
		return new DirectoryImmunizationJurisdictionPredicateValidator(
			qc.getCriteriaBuilder(),
			(DirectoryImmunizationJoins<?>) qc.getJoins(),
			user,
			null);
	}

	@Override
	protected Predicate isInJurisdiction() {
		return isInJurisdictionByJurisdictionLevel(user.getJurisdictionLevel());
	}

	@Override
	protected Predicate isInJurisdictionOrOwned() {
		final Path<Object> reportingUserPath = joins.getRoot().get(Immunization.REPORTING_USER);
		final Predicate reportedByCurrentUser = cb.and(cb.isNotNull(reportingUserPath), cb.equal(reportingUserPath.get(User.ID), user.getId()));
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
		return cb.equal(joins.getRoot().get(Immunization.RESPONSIBLE_REGION).get(Region.ID), user.getRegion().getId());
	}

	@Override
	protected Predicate whenDistrictLevel() {
		return cb.equal(joins.getRoot().get(Immunization.RESPONSIBLE_DISTRICT).get(District.ID), user.getDistrict().getId());
	}

	@Override
	protected Predicate whenCommunityLevel() {
		return cb.equal(joins.getRoot().get(Immunization.RESPONSIBLE_COMMUNITY).get(Community.ID), user.getCommunity().getId());
	}

	@Override
	protected Predicate whenFacilityLevel() {
		return cb.disjunction();
	}

	@Override
	protected Predicate whenPointOfEntryLevel() {
		return cb.disjunction();
	}

	@Override
	protected Predicate whenLaboratoryLevel() {
		return cb.disjunction();
	}
}
