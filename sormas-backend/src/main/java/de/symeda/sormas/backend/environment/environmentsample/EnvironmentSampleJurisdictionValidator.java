/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.environment.environmentsample;

import java.util.List;
import java.util.function.Function;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.apache.commons.lang3.NotImplementedException;

import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;

public final class EnvironmentSampleJurisdictionValidator extends PredicateJurisdictionValidator {

	private final EnvironmentSampleJoins joins;

	private EnvironmentSampleJurisdictionValidator(
		CriteriaBuilder cb,
		User user,
		EnvironmentSampleJoins joins,
		List<PredicateJurisdictionValidator> jurisdictionValidators) {
		super(cb, user, null, jurisdictionValidators);
		this.joins = joins;
	}

	public static EnvironmentSampleJurisdictionValidator of(EnvironmentSampleQueryContext qc, User user) {
		return new EnvironmentSampleJurisdictionValidator(qc.getCriteriaBuilder(), user, qc.getJoins(), null);
	}

	private Predicate getReportedByCurrentUserPredicate() {
		return cb.and(
			cb.isNotNull(joins.getRoot().get(EnvironmentSample.REPORTING_USER)),
			user != null
				? cb.equal(joins.getRoot().get(EnvironmentSample.REPORTING_USER).get(User.ID), user.getId())
				: cb.equal(joins.getRoot().get(EnvironmentSample.REPORTING_USER).get(User.ID), userPath.get(User.ID)));
	}

	@Override
	public Predicate isRootInJurisdictionOrOwned() {
		final Predicate reportedByCurrentUser = getReportedByCurrentUserPredicate();
		return cb.or(reportedByCurrentUser, isRootInJurisdiction());
	}

	@Override
	public Predicate isRootInJurisdictionForRestrictedAccess() {
		final Predicate reportedByCurrentUser = getReportedByCurrentUserPredicate();
		final Predicate restrictedAccess = user != null
			? cb.equal(joins.getEnvironment().get(Environment.RESPONSIBLE_USER).get(User.ID), user.getId())
			: cb.equal(joins.getEnvironment().get(Environment.RESPONSIBLE_USER).get(User.ID), userPath.get(User.ID));
		return cb.or(reportedByCurrentUser, restrictedAccess);
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
		return buildLocationPredicate(Location.REGION, User.REGION, User::getRegion);
	}

	@Override
	protected Predicate whenDistrictLevel() {
		return buildLocationPredicate(Location.DISTRICT, User.DISTRICT, User::getDistrict);
	}

	@Override
	protected Predicate whenCommunityLevel() {
		return buildLocationPredicate(Location.COMMUNITY, User.COMMUNITY, User::getCommunity);
	}

	@Override
	protected Predicate whenFacilityLevel() {
		throw new NotImplementedException("Facility is not supported for environment samples");
	}

	@Override
	protected Predicate whenPointOfEntryLevel() {
		throw new NotImplementedException("Point of entry is not supported for environment samples");
	}

	@Override
	protected Predicate whenLaboratoryLevel() {
		return user != null ? cb.equal(joins.getLaboratory(), user.getLaboratory()) : cb.equal(joins.getLaboratory(), userPath.get(User.LABORATORY));
	}

	private Predicate buildLocationPredicate(
		String locationProperty,
		String userJurisdictionProperty,
		Function<User, Object> userJurisdictionProvider) {
		if (user != null) {
			Object userJurisdiction = userJurisdictionProvider.apply(user);
			return cb.or(
				cb.and(
					cb.isNull(joins.getLocation().get(locationProperty)),
					cb.equal(joins.getEnvironmentJoins().getLocation().get(locationProperty), userJurisdiction)),
				cb.equal(joins.getLocation().get(locationProperty), userJurisdiction));
		}

		Path<?> userJurisdictionPath = userPath.get(userJurisdictionProperty);

		return cb.or(
			cb.and(
				cb.isNull(joins.getLocation().get(locationProperty)),
				cb.equal(joins.getEnvironmentJoins().getLocation().get(locationProperty), userJurisdictionPath)),
			cb.equal(joins.getLocation().get(locationProperty), userJurisdictionPath));
	}
}
