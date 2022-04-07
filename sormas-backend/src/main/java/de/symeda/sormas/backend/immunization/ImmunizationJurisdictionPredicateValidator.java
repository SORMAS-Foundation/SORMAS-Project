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
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.immunization.joins.ImmunizationJoins;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;

public class ImmunizationJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

	private final ImmunizationJoins joins;

	private ImmunizationJurisdictionPredicateValidator(
		CriteriaQuery<?> cq,
		CriteriaBuilder cb,
		ImmunizationJoins joins,
		User user,
		List<PredicateJurisdictionValidator> associatedJurisdictionValidators) {

		super(cb, user, null, associatedJurisdictionValidators);
		this.joins = joins;
	}

	public static ImmunizationJurisdictionPredicateValidator of(ImmunizationQueryContext qc, User user) {
		return new ImmunizationJurisdictionPredicateValidator(qc.getQuery(), qc.getCriteriaBuilder(), qc.getJoins(), user, null);
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
