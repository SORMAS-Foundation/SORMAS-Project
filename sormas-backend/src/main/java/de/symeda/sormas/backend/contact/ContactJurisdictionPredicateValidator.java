/*
 *  SORMAS® - Surveillance Outbreak Response Management & Analysis System
 *  Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.symeda.sormas.backend.contact;

import java.util.Collections;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;
import de.symeda.sormas.backend.caze.CaseJurisdictionPredicateValidator;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.utils.CaseJoins;

public class ContactJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

	private final ContactJoins<?> joins;
	private final User currentUser;

	private ContactJurisdictionPredicateValidator(CriteriaBuilder cb, ContactJoins<?> joins, User currentUser) {
		super(cb, Collections.singletonList(CaseJurisdictionPredicateValidator.of(cb, new CaseJoins<>(joins.getCaze()), currentUser)));
		this.joins = joins;
		this.currentUser = currentUser;
	}

	public static ContactJurisdictionPredicateValidator of(CriteriaBuilder cb, ContactJoins<?> joins, User currentUser) {
		return new ContactJurisdictionPredicateValidator(cb, joins, currentUser);
	}

	@Override
	protected Predicate isInJurisdictionOrOwned() {
		final Predicate reportedByCurrentUser =
			cb.and(cb.isNotNull(joins.getReportingUser()), cb.equal(joins.getReportingUser().get(User.UUID), currentUser.getUuid()));

		return cb.or(reportedByCurrentUser, inJurisdiction());
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
		return cb.equal(joins.getRegion().get(Region.ID), currentUser.getRegion().getId());
	}

	@Override
	protected Predicate whenDistrictLevel() {
		return cb.equal(joins.getDistrict().get(District.ID), currentUser.getDistrict().getId());
	}

	@Override
	protected Predicate whenCommunityLevel() {
		return cb.equal(joins.getCommunity().get(Community.ID), currentUser.getCommunity().getId());
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
		return cb.equal(joins.getSampleLabs().get(Facility.ID), currentUser.getLaboratory().getId());
	}
}
