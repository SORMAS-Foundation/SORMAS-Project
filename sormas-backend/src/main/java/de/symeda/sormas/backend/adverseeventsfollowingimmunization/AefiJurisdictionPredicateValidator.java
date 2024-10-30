/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.adverseeventsfollowingimmunization;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.adverseeventsfollowingimmunization.entity.Aefi;
import de.symeda.sormas.backend.adverseeventsfollowingimmunization.entity.AefiJoins;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;

public class AefiJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

	private final AefiJoins joins;

	public AefiJurisdictionPredicateValidator(
		CriteriaBuilder cb,
		AefiJoins joins,
		User user,
		List<PredicateJurisdictionValidator> jurisdictionValidators) {
		super(cb, user, null, jurisdictionValidators);
		this.joins = joins;
	}

	public static AefiJurisdictionPredicateValidator of(AefiQueryContext qc, User user) {
		return new AefiJurisdictionPredicateValidator(qc.getCriteriaBuilder(), qc.getJoins(), user, null);
	}

	@Override
	public Predicate isRootInJurisdiction() {
		return isInJurisdictionByJurisdictionLevel(user.getJurisdictionLevel());
	}

	@Override
	public Predicate isRootInJurisdictionOrOwned() {
		final Path<Object> reportingUserPath = joins.getRoot().get(Aefi.REPORTING_USER);
		final Predicate reportedByCurrentUser = cb.and(cb.isNotNull(reportingUserPath), cb.equal(reportingUserPath.get(User.ID), user.getId()));
		return cb.or(reportedByCurrentUser, this.isRootInJurisdiction());
	}

	@Override
	public Predicate isRootInJurisdictionForRestrictedAccess() {
		return null;
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
		return cb.equal(joins.getRoot().get(Aefi.IMMUNIZATION).get(Immunization.RESPONSIBLE_REGION).get(Region.ID), user.getRegion().getId());
	}

	@Override
	protected Predicate whenDistrictLevel() {
		return cb.equal(joins.getRoot().get(Aefi.IMMUNIZATION).get(Immunization.RESPONSIBLE_DISTRICT).get(District.ID), user.getDistrict().getId());
	}

	@Override
	protected Predicate whenCommunityLevel() {
		return cb
			.equal(joins.getRoot().get(Aefi.IMMUNIZATION).get(Immunization.RESPONSIBLE_COMMUNITY).get(Community.ID), user.getCommunity().getId());
	}

	@Override
	protected Predicate whenFacilityLevel() {
		return cb.equal(joins.getRoot().get(Aefi.IMMUNIZATION).get(Immunization.HEALTH_FACILITY).get(Facility.ID), user.getHealthFacility().getId());
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
