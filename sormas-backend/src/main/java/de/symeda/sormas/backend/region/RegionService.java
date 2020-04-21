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
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.region.RegionCriteria;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.AbstractInfrastructureAdoService;

@Stateless
@LocalBean
public class RegionService extends AbstractInfrastructureAdoService<Region> {

	public RegionService() {
		super(Region.class);
	}

	public List<Region> getByName(String name, boolean includeArchivedEntities) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Region> cq = cb.createQuery(getElementClass());
		Root<Region> from = cq.from(getElementClass());

		Predicate filter = cb.or(
				cb.equal(cb.trim(from.get(Region.NAME)), name.trim()),
				cb.equal(cb.lower(cb.trim(from.get(Region.NAME))), name.trim().toLowerCase()));
		if (!includeArchivedEntities) {
			filter = cb.and(filter, createBasicFilter(cb, from));
		}
		
		cq.where(filter);

		return em.createQuery(cq).getResultList();
	}

	public List<Long> getIdsByReferenceDtos(List<RegionReferenceDto> references) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Region> from = cq.from(getElementClass());

		cq.where(from.get(Region.UUID).in(references.stream().map(RegionReferenceDto::getUuid).collect(Collectors.toList())));
		cq.select(from.get(Region.ID));
		return em.createQuery(cq).getResultList();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Region, Region> from) {
		// no filter by user needed
		return null;
	}

	public Predicate buildCriteriaFilter(RegionCriteria criteria, CriteriaBuilder cb, Root<Region> from) {
		Predicate filter = null;
		if (criteria.getNameEpidLike() != null) {
			String[] textFilters = criteria.getNameEpidLike().split("\\s+");
			for (int i=0; i<textFilters.length; i++)
			{
				String textFilter = "%" + textFilters[i].toLowerCase() + "%";
				if (!DataHelper.isNullOrEmpty(textFilter)) {
					Predicate likeFilters = cb.or(
							cb.like(cb.lower(from.get(Region.NAME)), textFilter),
							cb.like(cb.lower(from.get(Region.EPID_CODE)), textFilter));
					filter = and(cb, filter, likeFilters);
				}
			}
		}
		if (criteria.getRelevanceStatus() != null) {
			if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = and(cb, filter, cb.or(
						cb.equal(from.get(Region.ARCHIVED), false),
						cb.isNull(from.get(Region.ARCHIVED))));
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = and(cb, filter, cb.equal(from.get(Region.ARCHIVED), true));
			}
		}
		return filter;
	}
}
