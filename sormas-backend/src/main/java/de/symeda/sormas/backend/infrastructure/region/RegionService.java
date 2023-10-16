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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.infrastructure.region;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionCriteria;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureAdoService;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb.CountryFacadeEjbLocal;

@Stateless
@LocalBean
public class RegionService extends AbstractInfrastructureAdoService<Region, RegionCriteria> {

	@EJB
	private CountryFacadeEjbLocal countryFacade;

	public RegionService() {
		super(Region.class);
	}

	public List<Region> getByName(String name, boolean includeArchivedEntities) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Region> cq = cb.createQuery(getElementClass());
		Root<Region> from = cq.from(getElementClass());

		Predicate filter = CriteriaBuilderHelper.unaccentedIlikePrecise(cb, from.get(Region.NAME), name.trim());
		if (!includeArchivedEntities) {
			filter = cb.and(filter, createBasicFilter(cb, from));
		}

		cq.where(filter);

		return em.createQuery(cq).getResultList();
	}

	public List<Region> getByExternalId(String externalId, boolean includeArchivedEntities) {
		return getByExternalId(externalId, Region.EXTERNAL_ID, includeArchivedEntities);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Region> from) {
		// no filter by user needed
		return null;
	}

	@Override
	public Predicate buildCriteriaFilter(RegionCriteria criteria, CriteriaBuilder cb, Root<Region> from) {

		Predicate filter = null;
		if (criteria.getNameEpidLike() != null) {
			String[] textFilters = criteria.getNameEpidLike().split("\\s+");
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(Region.NAME), textFilter),
					CriteriaBuilderHelper.ilike(cb, from.get(Region.EPID_CODE), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}
		if (criteria.getRelevanceStatus() != null) {
			if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter =
					CriteriaBuilderHelper.and(cb, filter, cb.or(cb.equal(from.get(Region.ARCHIVED), false), cb.isNull(from.get(Region.ARCHIVED))));
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Region.ARCHIVED), true));
			}
		}

		CountryReferenceDto country = criteria.getCountry();
		if (country != null) {
			CountryReferenceDto serverCountry = countryFacade.getServerCountry();

			Path<Object> countryUuid = from.join(Region.COUNTRY, JoinType.LEFT).get(Country.UUID);
			Predicate countryFilter = cb.equal(countryUuid, country.getUuid());

			if (country.equals(serverCountry)) {
				filter = CriteriaBuilderHelper.and(cb, filter, CriteriaBuilderHelper.or(cb, countryFilter, countryUuid.isNull()));
			} else {
				filter = CriteriaBuilderHelper.and(cb, filter, countryFilter);
			}
		}

		return filter;
	}
}
