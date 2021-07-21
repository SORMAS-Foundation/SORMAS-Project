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

package de.symeda.sormas.backend.immunization;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;

public class ImmunizationJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

	private final ImmunizationJoins<?> joins;
	private final User currentUser;
	private final CriteriaQuery<?> cq;

	private ImmunizationJurisdictionPredicateValidator(
		CriteriaQuery<?> cq,
		CriteriaBuilder cb,
		ImmunizationJoins<?> joins,
		User currentUser,
		List<PredicateJurisdictionValidator> associatedJurisdictionValidators) {
		super(cb, associatedJurisdictionValidators);
		this.joins = joins;
		this.currentUser = currentUser;
		this.cq = cq;
	}

	public static ImmunizationJurisdictionPredicateValidator of(ImmunizationQueryContext qc, User currentUser) {
		return new ImmunizationJurisdictionPredicateValidator(
			qc.getQuery(),
			qc.getCriteriaBuilder(),
			(ImmunizationJoins<?>) qc.getJoins(),
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
		return cb.disjunction();
	}

	@Override
	protected Predicate whenLaboratoryLevel() {
		return cb.disjunction();
	}
}
