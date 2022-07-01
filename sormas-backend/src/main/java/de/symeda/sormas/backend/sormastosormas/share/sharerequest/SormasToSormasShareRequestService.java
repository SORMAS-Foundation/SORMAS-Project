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

package de.symeda.sormas.backend.sormastosormas.share.sharerequest;

import static de.symeda.sormas.backend.ExtendedPostgreSQL94Dialect.ARRAY_CONTAINS_TEXT;
import static de.symeda.sormas.backend.ExtendedPostgreSQL94Dialect.JSON_EXTRACT_PATH_TEXT;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.util.CollectionUtils;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestCriteria;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;

@Stateless
@LocalBean
public class SormasToSormasShareRequestService extends AdoServiceWithUserFilter<SormasToSormasShareRequest> {

	public SormasToSormasShareRequestService() {
		super(SormasToSormasShareRequest.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, SormasToSormasShareRequest> from) {
		return null;
	}

	public Predicate buildCriteriaFilter(ShareRequestCriteria criteria, CriteriaBuilder cb, Root<SormasToSormasShareRequest> root) {
		Predicate filter = null;
		if (criteria.getStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(root.get(SormasToSormasShareRequest.STATUS), criteria.getStatus()));
		}

		if (!CollectionUtils.isEmpty(criteria.getStatusesExcepted())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.not(root.get(SormasToSormasShareRequest.STATUS).in(criteria.getStatusesExcepted())));
		}

		return filter;
	}

	public List<SormasToSormasShareRequest> getShareRequestsForCase(CaseReferenceDto caze) {
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<SormasToSormasShareRequest> cq = cb.createQuery(SormasToSormasShareRequest.class);
		Root<SormasToSormasShareRequest> from = cq.from(SormasToSormasShareRequest.class);

		Subquery<String> casesSubQuery = cq.subquery(String.class);
		Root<SormasToSormasShareRequest> casesRoot = casesSubQuery.from(SormasToSormasShareRequest.class);
		casesSubQuery.where(cb.equal(casesRoot.get(SormasToSormasShareRequest.ID), from.get(SormasToSormasShareRequest.ID)));
		casesSubQuery.select(
			cb.function(
				JSON_EXTRACT_PATH_TEXT,
				String.class,
				cb.function("json_array_elements", String[].class, casesRoot.get(SormasToSormasShareRequest.CASES)),
				cb.literal("uuid")).as(String.class));

		cq.where(
			cb.isTrue(
				cb.function(ARRAY_CONTAINS_TEXT, Boolean.class, cb.function("array", String[].class, casesSubQuery), cb.literal(caze.getUuid()))));

		return em.createQuery(cq).getResultList();
	}
}
