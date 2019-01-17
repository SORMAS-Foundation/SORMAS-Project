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
import java.util.Optional;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.region.RegionCriteria;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.InfrastructureDataImporter;

@Stateless
@LocalBean
public class RegionService extends AbstractAdoService<Region> {
	
	public RegionService() {
		super(Region.class);
	}
	
	public List<Region> getAllWithoutEpidCode() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Region> cq = cb.createQuery(getElementClass());
		Root<Region> from = cq.from(getElementClass());
		cq.where(cb.isNull(from.get(Region.EPID_CODE)));
		cq.orderBy(cb.asc(from.get(AbstractDomainObject.ID)));

		return em.createQuery(cq).getResultList();
	}

	public List<Region> getAllWithoutPopulationData() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Region> cq = cb.createQuery(getElementClass());
		Root<Region> from = cq.from(getElementClass());
		cq.where(cb.isNull(from.get(Region.POPULATION)));
		cq.orderBy(cb.asc(from.get(AbstractDomainObject.ID)));

		return em.createQuery(cq).getResultList();
	}

	public List<Region> getByName(String name) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Region> cq = cb.createQuery(getElementClass());
		Root<Region> from = cq.from(getElementClass());
		
		cq.where(cb.equal(from.get(Region.NAME), name));

		return em.createQuery(cq).getResultList();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Region, Region> from, User user) {
		// no fitler by user needed
		return null;
	}
	
	public Predicate buildCriteriaFilter(RegionCriteria criteria, CriteriaBuilder cb, Root<Region> from) {
		Predicate filter = null;
		if (criteria.getNameEpidLike() != null) {
			for (int i=0; i<criteria.getNameEpidLike().length; i++)
			{
				String likeFilterText = "%" + criteria.getNameEpidLike()[i].toLowerCase() + "%";
				if (!DataHelper.isNullOrEmpty(likeFilterText)) {
					Predicate likeFilters = cb.or(
							cb.like(cb.lower(from.get(Region.NAME)), likeFilterText),
							cb.like(cb.lower(from.get(Region.EPID_CODE)), likeFilterText));
					filter = and(cb, filter, likeFilters);
				}
			}
		}
		return filter;
	}
	
	public void importRegions(String countryName, List<Region> regions) {
		
		InfrastructureDataImporter.importRegions(countryName, 
			(regionName, epidCode, population, growthRate) -> {
				
				Optional<Region> regionResult = regions.stream()
						.filter(r -> r.getName().equals(regionName))
						.findFirst();
				
				Region region;
				if (regionResult.isPresent()) {
					region = regionResult.get();
				} else {
					region = new Region();
					regions.add(region);
					region.setName(regionName);
				}
				
				region.setEpidCode(epidCode);
				region.setPopulation(population);
				region.setGrowthRate(growthRate);
	
				persist(region);
			});
	}
}
