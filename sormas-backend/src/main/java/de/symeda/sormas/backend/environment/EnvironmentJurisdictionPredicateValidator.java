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

package de.symeda.sormas.backend.environment;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;

public final class EnvironmentJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

	private final EnvironmentJoins joins;

	private EnvironmentJurisdictionPredicateValidator(CriteriaBuilder cb, User user, Path<User> userPath, EnvironmentJoins joins) {
		super(cb, user, userPath, null);
		this.joins = joins;
	}

	public static EnvironmentJurisdictionPredicateValidator of(EnvironmentQueryContext queryContext, User user) {
		return new EnvironmentJurisdictionPredicateValidator(queryContext.getCriteriaBuilder(), user, null, queryContext.getJoins());
	}

	private EnvironmentJurisdictionPredicateValidator(EnvironmentQueryContext qc, Path userPath) {
		super(qc.getCriteriaBuilder(), null, userPath, null);
		this.joins = qc.getJoins();
	}

	public static EnvironmentJurisdictionPredicateValidator of(EnvironmentQueryContext qc, Path userPath) {
		return new EnvironmentJurisdictionPredicateValidator(qc, userPath);
	}

	@Override
	public Predicate isRootInJurisdictionOrOwned() {

		final Predicate reportedByCurrentUser = cb.and(
			cb.isNotNull(joins.getRoot().get(Environment.REPORTING_USER)),
			user != null
				? cb.equal(joins.getRoot().get(Environment.REPORTING_USER).get(User.ID), user.getId())
				: cb.equal(joins.getRoot().get(Environment.REPORTING_USER).get(User.ID), userPath.get(User.ID)));

		return cb.or(reportedByCurrentUser, isRootInJurisdiction());
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
		return user != null
			? cb.equal(joins.getLocation().get(Location.REGION).get(Region.ID), user.getRegion().getId())
			: cb.equal(joins.getLocation().get(Location.REGION).get(Region.ID), userPath.get(User.REGION).get(Region.ID));
	}

	@Override
	protected Predicate whenDistrictLevel() {
		return user != null
			? cb.equal(joins.getLocation().get(Location.DISTRICT).get(District.ID), user.getDistrict().getId())
			: cb.equal(joins.getLocation().get(Location.DISTRICT).get(District.ID), userPath.get(User.DISTRICT).get(District.ID));
	}

	@Override
	protected Predicate whenCommunityLevel() {
		return user != null
			? cb.equal(joins.getLocation().get(Location.COMMUNITY).get(Community.ID), user.getCommunity().getId())
			: cb.equal(joins.getLocation().get(Location.COMMUNITY).get(Community.ID), userPath.get(User.COMMUNITY).get(Community.ID));
	}

	@Override
	protected Predicate whenFacilityLevel() {
		return user != null
			? cb.equal(joins.getLocation().get(Location.FACILITY).get(Facility.ID), user.getHealthFacility().getId())
			: cb.equal(joins.getLocation().get(Location.FACILITY).get(Facility.ID), userPath.get(User.HEALTH_FACILITY).get(Facility.ID));
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
