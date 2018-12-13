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
package de.symeda.sormas.backend.outbreak;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.outbreak.OutbreakCriteria;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class OutbreakService extends AbstractAdoService<Outbreak> {
	
	public OutbreakService() {
		super(Outbreak.class);
	}
	
	public List<Outbreak> queryByCriteria(OutbreakCriteria criteria, User user, String orderProperty, boolean asc) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Outbreak> cq = cb.createQuery(getElementClass());
		Root<Outbreak> from = cq.from(getElementClass());
		
		if (orderProperty != null) {
			cq.orderBy(asc ? cb.asc(from.get(orderProperty)) : cb.desc(from.get(orderProperty)));
		}

		Predicate filter = createUserFilter(cb, cq, from, user);
		filter = and(cb, filter, buildCriteriaFilter(criteria, cb, from));
		if (filter != null) {
			cq.where(filter);
		}
		
		return em.createQuery(cq).getResultList();
	}

	public List<String> queryUuidByCriteria(OutbreakCriteria criteria, User user, String orderProperty, boolean asc) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Outbreak> from = cq.from(getElementClass());
		
		if (orderProperty != null) {
			cq.orderBy(asc ? cb.asc(from.get(orderProperty)) : cb.desc(from.get(orderProperty)));
		}

		Predicate filter = createUserFilter(cb, cq, from, user);
		filter = and(cb, filter, buildCriteriaFilter(criteria, cb, from));
		if (filter != null) {
			cq.where(filter);
		}
		
		cq.select(from.get(Outbreak.UUID));
		return em.createQuery(cq).getResultList();
	}

	public Long countByCriteria(OutbreakCriteria criteria, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Outbreak> from = cq.from(getElementClass());
		
		Predicate filter = createUserFilter(cb, cq, from, user);
		filter = and(cb, filter, buildCriteriaFilter(criteria, cb, from));
		if (filter != null) {
			cq.where(filter);
		}
		
		cq.select(cb.count(from));

		return em.createQuery(cq).getSingleResult();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Outbreak, Outbreak> from, User user) {
		// no filter by user needed
		return null;
	}
	
	public Predicate buildCriteriaFilter(OutbreakCriteria criteria, CriteriaBuilder cb, Root<Outbreak> from) {
		Predicate filter = null;
		if (criteria.getChangeDateAfter() != null) {
			filter = and(cb, filter, createChangeDateFilter(cb, from, criteria.getChangeDateAfter()));
		}
		if (criteria.getDisease() != null) {
			filter = and(cb, filter, cb.equal(from.get(Outbreak.DISEASE), criteria.getDisease()));
		}
		if (criteria.getDistrict() != null) {
			filter = and(cb, filter, cb.equal(from.join(Outbreak.DISTRICT, JoinType.LEFT).get(District.UUID), criteria.getDistrict().getUuid()));
		}
		if (criteria.getRegion() != null) {
			filter = and(cb, filter, cb.equal(from.join(Outbreak.DISTRICT, JoinType.LEFT).join(District.REGION, JoinType.LEFT).get(Region.UUID), criteria.getRegion().getUuid()));
		}
		if (criteria.getActive() != null) {
			Date now = new Date();
			Predicate activeFilter = cb.and(
					cb.lessThanOrEqualTo(from.get(Outbreak.START_DATE), now),
					cb.or(cb.isNull(from.get(Outbreak.END_DATE)), cb.greaterThanOrEqualTo(from.get(Outbreak.END_DATE), now)));
			if (Boolean.FALSE.equals(criteria.getActive())) {
				activeFilter = cb.not(activeFilter);
			}
			filter = and(cb, filter, activeFilter);
		}
		return filter;
	}
}
