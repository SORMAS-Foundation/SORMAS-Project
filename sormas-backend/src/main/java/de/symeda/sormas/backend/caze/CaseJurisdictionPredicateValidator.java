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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleJoins;
import de.symeda.sormas.backend.sample.SampleJurisdictionPredicateValidator;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;
import de.symeda.sormas.utils.CaseJoins;

public class CaseJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

	private final CaseJoins<?> joins;
	private final User currentUser;
	private final CriteriaQuery<?> cq;

	private CaseJurisdictionPredicateValidator(
		CriteriaQuery<?> cq,
		CriteriaBuilder cb,
		CaseJoins<?> joins,
		User currentUser,
		List<PredicateJurisdictionValidator> associatedJurisdictionValidators) {
		super(cb, associatedJurisdictionValidators);
		this.joins = joins;
		this.currentUser = currentUser;
		this.cq = cq;
	}

	public static CaseJurisdictionPredicateValidator of(CaseQueryContext qc, User currentUser) {
		return new CaseJurisdictionPredicateValidator(qc.getQuery(), qc.getCriteriaBuilder(), (CaseJoins<?>) qc.getJoins(), currentUser, null);
	}

	@Override
	protected Predicate isInJurisdictionOrOwned() {
		final Predicate reportedByCurrentUser =
			cb.and(cb.isNotNull(joins.getReportingUser()), cb.equal(joins.getReportingUser().get(User.UUID), currentUser.getUuid()));
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
			cb.equal(joins.getResponsibleRegion().get(Region.ID), currentUser.getRegion().getId()),
			cb.equal(joins.getRegion().get(Region.ID), currentUser.getRegion().getId()));
	}

	@Override
	protected Predicate whenDistrictLevel() {
		return CriteriaBuilderHelper.or(
			cb,
			cb.equal(joins.getResponsibleDistrict().get(District.ID), currentUser.getDistrict().getId()),
			cb.equal(joins.getDistrict().get(District.ID), currentUser.getDistrict().getId()));
	}

	@Override
	protected Predicate whenCommunityLevel() {
		return CriteriaBuilderHelper.or(
			cb,
			cb.equal(joins.getResponsibleCommunity().get(Community.ID), currentUser.getCommunity().getId()),
			cb.equal(joins.getCommunity().get(Community.ID), currentUser.getCommunity().getId()));
	}

	@Override
	protected Predicate whenFacilityLevel() {
		return cb.equal(joins.getFacility().get(Facility.ID), currentUser.getHealthFacility().getId());
	}

	@Override
	protected Predicate whenPointOfEntryLevel() {
		return cb.equal(joins.getPointOfEntry().get(PointOfEntry.ID), currentUser.getPointOfEntry().getId());
	}

	@Override
	protected Predicate whenLaboratoryLevel() {

		final Subquery<Long> sampleSubquery = cq.subquery(Long.class);
		final Root<Sample> sampleRoot = sampleSubquery.from(Sample.class);
		final SampleJoins sampleJoins = new SampleJoins(sampleRoot);
		final Join caseJoin = sampleJoins.getCaze();
		SampleJurisdictionPredicateValidator sampleJurisdictionPredicateValidator =
			SampleJurisdictionPredicateValidator.withoutAssociations(cb, sampleJoins, currentUser);
		sampleSubquery.where(cb.and(cb.equal(caseJoin, joins.getRoot()), sampleJurisdictionPredicateValidator.inJurisdictionOrOwned()));
		sampleSubquery.select(sampleRoot.get(Sample.ID));
		return cb.exists(sampleSubquery);
	}
}
