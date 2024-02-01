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

package de.symeda.sormas.backend.campaign.data;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;

public class CampaignFormDataJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

	private final CampaignFormDataJoins joins;

	private CampaignFormDataJurisdictionPredicateValidator(CriteriaQuery<?> cq, CriteriaBuilder cb, CampaignFormDataJoins joins, User user) {
		super(cb, user, null, null);
		this.joins = joins;
	}

	public static CampaignFormDataJurisdictionPredicateValidator of(CampaignFormDataQueryContext qc, User user) {
		return new CampaignFormDataJurisdictionPredicateValidator(qc.getQuery(), qc.getCriteriaBuilder(), qc.getJoins(), user);
	}

	@Override
	public Predicate isRootInJurisdictionOrOwned() {
		final Predicate reportedByCurrentUser = cb.and(
			cb.isNotNull(joins.getRoot().get(CampaignFormData.CREATING_USER)),
			cb.equal(joins.getRoot().get(CampaignFormData.CREATING_USER).get(User.ID), user.getId()));
		return cb.or(reportedByCurrentUser, isRootInJurisdiction());
	}

	@Override
	public Predicate isRootInJurisdictionForRestrictedAccess() {
		return cb.disjunction();
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
		return cb.equal(joins.getRoot().get(CampaignFormData.REGION).get(Region.ID), user.getRegion().getId());
	}

	@Override
	protected Predicate whenDistrictLevel() {
		return cb.equal(joins.getRoot().get(CampaignFormData.DISTRICT).get(District.ID), user.getDistrict().getId());
	}

	@Override
	protected Predicate whenCommunityLevel() {
		return cb.equal(joins.getRoot().get(CampaignFormData.COMMUNITY).get(Community.ID), user.getCommunity().getId());
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
