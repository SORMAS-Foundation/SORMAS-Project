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

import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;

public class CaseCriteriaHelper {

	private CaseCriteriaHelper() {
	}

	public static Predicate createRegionFilterWithFallback(CriteriaBuilder cb, CaseJoins joins, RegionReferenceDto region) {
		return CriteriaBuilderHelper.or(
			cb,
			cb.and(cb.isNotNull(joins.getRegion()), cb.equal(joins.getRegion().get(Region.UUID), region.getUuid())),
			cb.and(cb.isNotNull(joins.getResponsibleRegion()), cb.equal(joins.getResponsibleRegion().get(Region.UUID), region.getUuid())));
	}

	public static Predicate createDistrictFilterWithFallback(CriteriaBuilder cb, CaseJoins joins, DistrictReferenceDto district) {
		return CriteriaBuilderHelper.or(
			cb,
			cb.and(cb.isNotNull(joins.getDistrict()), cb.equal(joins.getDistrict().get(District.UUID), district.getUuid())),
			cb.and(cb.isNotNull(joins.getResponsibleDistrict()), cb.equal(joins.getResponsibleDistrict().get(District.UUID), district.getUuid())));
	}

	public static Predicate createCommunityFilterWithFallback(CriteriaBuilder cb, CaseJoins joins, CommunityReferenceDto community) {
		return CriteriaBuilderHelper.or(
			cb,
			cb.and(cb.isNotNull(joins.getCommunity()), cb.equal(joins.getCommunity().get(Community.UUID), community.getUuid())),
			cb.and(
				cb.isNotNull(joins.getResponsibleCommunity()),
				cb.equal(joins.getResponsibleCommunity().get(Community.UUID), community.getUuid())));
	}
}
