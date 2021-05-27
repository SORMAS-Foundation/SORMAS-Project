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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.api.utils.jurisdiction.JurisdictionValidator;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.utils.CaseJoins;

public class CaseJurisdictionPredicateValidator extends JurisdictionValidator<Predicate> {

	private final CriteriaBuilder cb;
	private final CaseJoins<?> joins;
	private final User currentUser;

	public static CaseJurisdictionPredicateValidator of(CriteriaBuilder cb, CaseJoins<?> joins, User currentUser) {
		return new CaseJurisdictionPredicateValidator(cb, joins, currentUser);
	}

	private CaseJurisdictionPredicateValidator(CriteriaBuilder cb, CaseJoins<?> joins, User currentUser) {
		this.cb = cb;
		this.joins = joins;
		this.currentUser = currentUser;
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
			cb.equal(joins.getRegion().get(Region.ID), currentUser.getRegion().getId()),
			cb.equal(joins.getResponsibleRegion().get(Region.ID), currentUser.getRegion().getId()));
	}

	@Override
	protected Predicate whenDistrictLevel() {
		return CriteriaBuilderHelper.or(
			cb,
			cb.equal(joins.getDistrict().get(District.ID), currentUser.getDistrict().getId()),
			cb.equal(joins.getResponsibleDistrict().get(District.ID), currentUser.getDistrict().getId()));
	}

	@Override
	protected Predicate whenCommunityLevel() {
		return CriteriaBuilderHelper.or(
			cb,
			cb.equal(joins.getCommunity().get(Community.ID), currentUser.getCommunity().getId()),
			cb.equal(joins.getResponsibleCommunity().get(Community.ID), currentUser.getCommunity().getId()));
	}

	@Override
	protected Predicate whenFacilityLevel() {
		return cb.equal(joins.getFacility().get(Facility.ID), currentUser.getHealthFacility().getId());
	}

	@Override
	protected Predicate whenPointOfEntryLevel() {
		return cb.equal(joins.getPointOfEntry().get(PointOfEntry.ID), currentUser.getPointOfEntry().getId());
	}
}
