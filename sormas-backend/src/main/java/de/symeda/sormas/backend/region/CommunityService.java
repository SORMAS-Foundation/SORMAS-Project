/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.region;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.region.CommunityCriteria;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.AbstractInfrastructureAdoService;

@Stateless
@LocalBean
public class CommunityService extends AbstractInfrastructureAdoService<Community> {
	
	public CommunityService() {
		super(Community.class);
	}

	public List<Community> getByName(String name, District district, boolean includeArchivedEntities) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Community> cq = cb.createQuery(getElementClass());
		Root<Community> from = cq.from(getElementClass());
		
		Predicate filter = cb.or(
				cb.equal(cb.trim(from.get(Community.NAME)), name.trim()),
				cb.equal(cb.lower(cb.trim(from.get(Community.NAME))), name.trim().toLowerCase())
				);
		if (!includeArchivedEntities) {
			filter = cb.and(filter, createBasicFilter(cb, from));
		}
		if (district != null) {
			filter = cb.and(filter, cb.equal(from.get(Community.DISTRICT), district));
		}
		
		cq.where(filter);

		return em.createQuery(cq).getResultList();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Community, Community> from) {
		// no filter by user needed
		return null;
	}

	public Predicate buildCriteriaFilter(CommunityCriteria criteria, CriteriaBuilder cb, Root<Community> from) {
		Join<Community, District> district = from.join(Community.DISTRICT, JoinType.LEFT);
		Join<District, Region> region = district.join(District.REGION, JoinType.LEFT);
		Predicate filter = null;
		if (criteria.getRegion() != null) {
			filter = and(cb, filter, cb.equal(region.get(Region.UUID), criteria.getRegion().getUuid()));
		}
		if (criteria.getDistrict() != null) {
			filter = and(cb, filter, cb.equal(district.get(District.UUID), criteria.getDistrict().getUuid()));
		}
		if (criteria.getNameLike() != null) {
			String[] textFilters = criteria.getNameLike().split("\\s+");
			for (int i=0; i<textFilters.length; i++)
			{
				String textFilter = "%" + textFilters[i].toLowerCase() + "%";
				if (!DataHelper.isNullOrEmpty(textFilter)) {
					Predicate likeFilters = cb.or(
							cb.like(cb.lower(from.get(District.NAME)), textFilter));
					filter = and(cb, filter, likeFilters);
				}
			}
		}
		if (criteria.getRelevanceStatus() != null) {
			if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = and(cb, filter, cb.or(
						cb.equal(from.get(Community.ARCHIVED), false),
						cb.isNull(from.get(Community.ARCHIVED))));
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = and(cb, filter, cb.equal(from.get(Community.ARCHIVED), true));
			}
		}
		return filter;
	}
}
