/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.caze;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
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
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleJoins;
import de.symeda.sormas.backend.sample.SampleJurisdictionPredicateValidator;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;

public class CaseJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

	private final CaseJoins joins;

	private final CriteriaQuery<?> cq;

	private CaseJurisdictionPredicateValidator(
		CriteriaQuery<?> cq,
		CriteriaBuilder cb,
		CaseJoins joins,
		User user,
		List<PredicateJurisdictionValidator> associatedJurisdictionValidators) {
		super(cb, user, null, associatedJurisdictionValidators);
		this.joins = joins;
		this.cq = cq;
	}

	private CaseJurisdictionPredicateValidator(
		CriteriaQuery<?> cq,
		CriteriaBuilder cb,
		CaseJoins joins,
		Path userPath,
		List<PredicateJurisdictionValidator> associatedJurisdictionValidators) {
		super(cb, null, userPath, associatedJurisdictionValidators);
		this.joins = joins;
		this.cq = cq;
	}

	public static CaseJurisdictionPredicateValidator of(CaseQueryContext qc, User user) {
		return new CaseJurisdictionPredicateValidator(qc.getQuery(), qc.getCriteriaBuilder(), qc.getJoins(), user, null);
	}

	public static CaseJurisdictionPredicateValidator of(CaseQueryContext qc, Path userPath) {
		return new CaseJurisdictionPredicateValidator(qc.getQuery(), qc.getCriteriaBuilder(), qc.getJoins(), userPath, null);
	}

	@Override
	public Predicate isRootInJurisdiction() {
		return super.isRootInJurisdiction();
	}

	@Override
	public Predicate isRootInJurisdictionOrOwned() {
		final Predicate reportedByCurrentUser = cb.and(
			cb.isNotNull(joins.getRoot().get(Case.REPORTING_USER)),
			user != null
				? cb.equal(joins.getRoot().get(Case.REPORTING_USER).get(User.ID), user.getId())
				: cb.equal(joins.getRoot().get(Case.REPORTING_USER).get(User.ID), userPath.get(User.ID)));

		return cb.or(reportedByCurrentUser, this.isRootInJurisdiction());
	}

	@Override
	protected Predicate getLimitedDiseasePredicate() {
		return CriteriaBuilderHelper.limitedDiseasePredicate(cb, user, joins.getRoot().get(Case.DISEASE));
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
				? cb.equal(joins.getRoot().get(Case.RESPONSIBLE_REGION).get(Region.ID), user.getRegion().getId())
				: cb.equal(joins.getRoot().get(Case.RESPONSIBLE_REGION).get(Region.ID), userPath.get(User.REGION).get(Region.ID)),
			user != null
				? cb.equal(joins.getRoot().get(Case.REGION).get(Region.ID), user.getRegion().getId())
				: cb.equal(joins.getRoot().get(Case.REGION).get(Region.ID), userPath.get(User.REGION).get(Region.ID)));
	}

	@Override
	protected Predicate whenDistrictLevel() {
		return CriteriaBuilderHelper.or(
			cb,
			user != null
				? cb.equal(joins.getRoot().get(Case.RESPONSIBLE_DISTRICT).get(District.ID), user.getDistrict().getId())
				: cb.equal(joins.getRoot().get(Case.RESPONSIBLE_DISTRICT).get(District.ID), userPath.get(User.DISTRICT).get(District.ID)),
			user != null
				? cb.equal(joins.getRoot().get(Case.DISTRICT).get(District.ID), user.getDistrict().getId())
				: cb.equal(joins.getRoot().get(Case.DISTRICT).get(District.ID), userPath.get(User.DISTRICT).get(District.ID)));
	}

	@Override
	protected Predicate whenCommunityLevel() {
		return CriteriaBuilderHelper.or(
			cb,
			user != null
				? cb.equal(joins.getRoot().get(Case.RESPONSIBLE_COMMUNITY).get(Community.ID), user.getCommunity().getId())
				: cb.equal(joins.getRoot().get(Case.RESPONSIBLE_COMMUNITY).get(Community.ID), userPath.get(User.COMMUNITY).get(Community.ID)),
			user != null
				? cb.equal(joins.getRoot().get(Case.COMMUNITY).get(Community.ID), user.getCommunity().getId())
				: cb.equal(joins.getRoot().get(Case.COMMUNITY).get(Community.ID), userPath.get(User.COMMUNITY).get(Community.ID)));
	}

	@Override
	protected Predicate whenFacilityLevel() {
		return user != null
			? cb.equal(joins.getRoot().get(Case.HEALTH_FACILITY).get(Facility.ID), user.getHealthFacility().getId())
			: cb.equal(joins.getRoot().get(Case.HEALTH_FACILITY).get(Facility.ID), userPath.get(User.HEALTH_FACILITY).get(Facility.ID));
	}

	@Override
	protected Predicate whenPointOfEntryLevel() {
		return user != null
			? cb.equal(joins.getRoot().get(Case.POINT_OF_ENTRY).get(PointOfEntry.ID), user.getPointOfEntry().getId())
			: cb.equal(joins.getRoot().get(Case.POINT_OF_ENTRY).get(PointOfEntry.ID), userPath.get(User.POINT_OF_ENTRY).get(PointOfEntry.ID));
	}

	@Override
	protected Predicate whenLaboratoryLevel() {

		final Subquery<Long> sampleSubquery = cq.subquery(Long.class);
		final Root<Sample> sampleRoot = sampleSubquery.from(Sample.class);
		final SampleJoins sampleJoins = new SampleJoins(sampleRoot);
		final Join caseJoin = sampleJoins.getCaze();
		SampleJurisdictionPredicateValidator sampleJurisdictionPredicateValidator = user != null
			? SampleJurisdictionPredicateValidator.withoutAssociations(cb, sampleJoins, user)
			: SampleJurisdictionPredicateValidator.withoutAssociations(cb, sampleJoins, userPath);

		sampleSubquery.where(cb.and(cb.equal(caseJoin, joins.getRoot()), sampleJurisdictionPredicateValidator.inJurisdictionOrOwned()));
		sampleSubquery.select(sampleRoot.get(Sample.ID));
		return cb.exists(sampleSubquery);
	}
}
