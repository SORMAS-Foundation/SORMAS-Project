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
package de.symeda.sormas.backend.outbreak;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.outbreak.OutbreakCriteria;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.disease.DiseaseConfigurationService;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless
@LocalBean
public class OutbreakService extends AdoServiceWithUserFilterAndJurisdiction<Outbreak> {

	@EJB
	private DiseaseConfigurationService diseaseConfigurationService;

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

		Predicate filter = createUserFilter(cb, cq, from);
		Predicate activeDiseasePredicate = cb.exists(diseaseConfigurationService.existActiveDisease(cq, cb, from, Outbreak.DISEASE));
		filter = CriteriaBuilderHelper.and(cb, filter, buildCriteriaFilter(criteria, cb, from), activeDiseasePredicate);

		if (filter != null) {
			cq.where(filter);
		}

		return em.createQuery(cq).getResultList();
	}

	public List<Outbreak> queryByCriteria(OutbreakCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Outbreak> cq = cb.createQuery(getElementClass());
		Root<Outbreak> outbreak = cq.from(getElementClass());

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case Outbreak.DISEASE:
				case Outbreak.DISTRICT:
				case Outbreak.START_DATE:
					expression = outbreak.get(sortProperty.propertyName);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.asc(outbreak.get(Outbreak.DISEASE)));
		}

		Predicate filter = createUserFilter(cb, cq, outbreak);
		filter = CriteriaBuilderHelper.and(cb, filter, buildCriteriaFilter(criteria, cb, outbreak));
		if (filter != null) {
			cq.where(filter);
		}

		cq.select(outbreak);

		return QueryHelper.getResultList(em, cq, first, max);

	}

	public List<String> queryUuidByCriteria(OutbreakCriteria criteria, User user, String orderProperty, boolean asc) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Outbreak> from = cq.from(getElementClass());

		if (orderProperty != null) {
			cq.orderBy(asc ? cb.asc(from.get(orderProperty)) : cb.desc(from.get(orderProperty)));
		}

		Predicate filter = createUserFilter(cb, cq, from);
		filter = CriteriaBuilderHelper.and(cb, filter, buildCriteriaFilter(criteria, cb, from));
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

		Predicate filter = createUserFilter(cb, cq, from);
		filter = CriteriaBuilderHelper.and(cb, filter, buildCriteriaFilter(criteria, cb, from));
		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(from));

		return em.createQuery(cq).getSingleResult();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Outbreak> from) {
		// no filter by user needed
		return null;
	}

	public Predicate buildCriteriaFilter(OutbreakCriteria criteria, CriteriaBuilder cb, Root<Outbreak> from) {

		Predicate filter = null;
		if (criteria.getChangeDateAfter() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, createChangeDateFilter(cb, from, criteria.getChangeDateAfter()));
		}
		if (CollectionUtils.isNotEmpty(criteria.getDiseases())) {
			filter = CriteriaBuilderHelper.and(cb, filter, from.get(Outbreak.DISEASE).in(criteria.getDiseases()));
		}
		if (criteria.getDistrict() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(from.join(Outbreak.DISTRICT, JoinType.LEFT).get(District.UUID), criteria.getDistrict().getUuid()));
		}
		if (criteria.getRegion() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(
					from.join(Outbreak.DISTRICT, JoinType.LEFT).join(District.REGION, JoinType.LEFT).get(Region.UUID),
					criteria.getRegion().getUuid()));
		}
		if (criteria.getActive() != null) {
			Predicate activeFilter = cb.and(
				cb.lessThanOrEqualTo(from.get(Outbreak.START_DATE), criteria.getActiveUpper()),
				cb.or(cb.isNull(from.get(Outbreak.END_DATE)), cb.greaterThan(from.get(Outbreak.END_DATE), criteria.getActiveLower())));
			if (Boolean.FALSE.equals(criteria.getActive())) {
				activeFilter = cb.not(activeFilter);
			}
			filter = CriteriaBuilderHelper.and(cb, filter, activeFilter);
		}
		if (criteria.getReportedDateFrom() != null || criteria.getReportedDateTo() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.between(from.get(Outbreak.REPORT_DATE), criteria.getReportedDateFrom(), criteria.getReportedDateTo()));
		}

		return filter;
	}

	public Map<Disease, District> getOutbreakDistrictNameByDisease(OutbreakCriteria criteria, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Outbreak> outbreak = cq.from(Outbreak.class);
		Join<Outbreak, District> districtJoin = outbreak.join(Case.DISTRICT, JoinType.LEFT);

		Predicate filter = this.buildCriteriaFilter(criteria, cb, outbreak);
		filter = and(cb, filter, createUserFilter(cb, cq, outbreak));

		if (filter != null)
			cq.where(filter);


		Expression<Number> maxReportDate = cb.max(outbreak.get(Outbreak.REPORT_DATE));
		cq.multiselect(outbreak.get(Outbreak.DISEASE), districtJoin, maxReportDate);
		cq.groupBy(outbreak.get(Outbreak.DISEASE), districtJoin);
		cq.orderBy(cb.desc(maxReportDate));

		List<Object[]> results = em.createQuery(cq).getResultList();

		Map<Disease, District> outbreaksDistrict = new HashMap<>(); //results.stream().collect(Collectors.toMap(e -> (Disease) e[0], e -> (String) e[1]));

		for (Object[] e : results) {
			Disease disease = (Disease) e[0];
			if (!outbreaksDistrict.containsKey(disease)) {
				District district = (District) e[1];
				outbreaksDistrict.put(disease, district);
			}
		}
		return outbreaksDistrict;
	}

	public Map<Disease, Long> getOutbreakDistrictCountByDisease(OutbreakCriteria criteria, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Outbreak> outbreak = cq.from(Outbreak.class);
		cq.multiselect(outbreak.get(Outbreak.DISEASE), cb.countDistinct(outbreak.get(Outbreak.DISTRICT)));
		cq.groupBy(outbreak.get(Outbreak.DISEASE));

		Predicate filter = this.buildCriteriaFilter(criteria, cb, outbreak);
		filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(cb, cq, outbreak));

		if (filter != null)
			cq.where(filter);

		List<Object[]> results = em.createQuery(cq).getResultList();

		return results.stream().collect(Collectors.toMap(e -> (Disease) e[0], e -> (Long) e[1]));
	}

	public Long getOutbreakDistrictCount(OutbreakCriteria criteria, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Outbreak> outbreak = cq.from(getElementClass());

		Join<Outbreak, District> regionJoin = outbreak.join(Outbreak.DISTRICT, JoinType.LEFT);
		cq.groupBy(regionJoin);

		Predicate filter = this.buildCriteriaFilter(criteria, cb, outbreak);
		filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(cb, cq, outbreak));

		if (filter != null)
			cq.where(filter);

		cq.select(cb.count(outbreak));

		return em.createQuery(cq).getResultList().stream().findFirst().orElse(0L);
	}
}
