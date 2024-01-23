package de.symeda.sormas.backend.immunization;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.api.person.PersonAssociation;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.person.PersonJurisdictionPredicateValidator;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;

public final class DirectoryImmunizationJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

	private final DirectoryImmunizationJoins joins;
	private final DirectoryImmunizationQueryContext queryContext;

	private DirectoryImmunizationJurisdictionPredicateValidator(
		CriteriaBuilder cb,
		DirectoryImmunizationJoins joins,
		User user,
		List<PredicateJurisdictionValidator> associatedJurisdictionValidators,
		DirectoryImmunizationQueryContext queryContext) {
		super(cb, user, null, associatedJurisdictionValidators);
		this.joins = joins;
		this.queryContext = queryContext;
	}

	public static DirectoryImmunizationJurisdictionPredicateValidator of(DirectoryImmunizationQueryContext qc, User user) {
		return new DirectoryImmunizationJurisdictionPredicateValidator(qc.getCriteriaBuilder(), qc.getJoins(), user, null, qc);
	}

	@Override
	public Predicate isRootInJurisdiction() {
		return isInJurisdictionByJurisdictionLevel(user.getJurisdictionLevel());
	}

	@Override
	public Predicate isRootInJurisdictionOrOwned() {
		final Predicate reportedByCurrentUser = getReportedByCurrentUser();
		return cb.or(reportedByCurrentUser, this.isRootInJurisdiction());
	}

	private Predicate getReportedByCurrentUser() {
		final Path<Object> reportingUserPath = joins.getRoot().get(Immunization.REPORTING_USER);
		final Predicate reportedByCurrentUser = cb.and(cb.isNotNull(reportingUserPath), cb.equal(reportingUserPath.get(User.ID), user.getId()));
		return reportedByCurrentUser;
	}

	@Override
	public Predicate isRootInJurisdictionForRestrictedAccess() {
		//for restricted access the immunization is only accessible when the associated person is accessible
		Predicate isRootInJurisdiction = isRootInJurisdictionOrOwned();

		final CriteriaQuery<?> cq = queryContext.getQuery();

		Subquery<Boolean> personSubquery = cq.subquery(Boolean.class);
		final Root<Immunization> from = personSubquery.from(Immunization.class);
		ImmunizationJoins immunizationJoins = new ImmunizationJoins(from);
		final Predicate isPersonInJurisdiction = PersonJurisdictionPredicateValidator
				.of(
						cq,
						cb,
						immunizationJoins.getPersonJoins(),
						user,
						new HashSet<>(Arrays.asList(PersonAssociation.CASE, PersonAssociation.CONTACT, PersonAssociation.EVENT_PARTICIPANT)))
				.isRootInJurisdictionForRestrictedAccess();
		personSubquery.select(from.get(AbstractDomainObject.ID));
		personSubquery.where(isPersonInJurisdiction, cb.equal(from.get(AbstractDomainObject.ID), joins.getRoot().get(AbstractDomainObject.ID)));

		return and(isRootInJurisdiction, cb.exists(personSubquery));
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
		return cb.equal(joins.getRoot().get(Immunization.HEALTH_FACILITY).get(Facility.ID), user.getHealthFacility().getId());
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
