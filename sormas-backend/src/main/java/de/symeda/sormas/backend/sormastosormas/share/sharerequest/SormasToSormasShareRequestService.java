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
import java.util.UUID;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.sample.Sample;
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

	public List<String> getAllNonRefferencedSormasToSormasShareRequest() {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<String> cq = cb.createQuery(String.class);
		final Root<SormasToSormasShareRequest> root = cq.from(getElementClass());

		final Subquery<String> caseSubquery = createSubquery(cb, cq, root, Case.class, Case.SORMAS_TO_SORMAS_ORIGIN_INFO);
		final Subquery<String> contactSubquery = createSubquery(cb, cq, root, Contact.class, Contact.SORMAS_TO_SORMAS_ORIGIN_INFO);
		final Subquery<String> eventSubquery = createSubquery(cb, cq, root, Event.class, Event.SORMAS_TO_SORMAS_ORIGIN_INFO);
		final Subquery<String> eventParticipantSubquery =
			createSubquery(cb, cq, root, EventParticipant.class, EventParticipant.SORMAS_TO_SORMAS_ORIGIN_INFO);
		final Subquery<String> immunizationSubquery = createSubquery(cb, cq, root, Immunization.class, Immunization.SORMAS_TO_SORMAS_ORIGIN_INFO);

		final Subquery<String> sampleSubquery = createSampleSubquery(cb, cq, root);

		cq.where(
			cb.and(
				cb.not(cb.exists(caseSubquery)),
				cb.not(cb.exists(contactSubquery)),
				cb.not(cb.exists(eventSubquery)),
				cb.not(cb.exists(eventParticipantSubquery)),
				cb.not(cb.exists(immunizationSubquery)),
				cb.not(cb.exists(sampleSubquery))));

		cq.select(root.get(SormasToSormasShareRequest.UUID));
		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}

	private Subquery<String> createSampleSubquery(CriteriaBuilder cb, CriteriaQuery<String> cq, Root<SormasToSormasShareRequest> root) {

		final Subquery<String> sampleSubquery = cq.subquery(String.class);
		final Root<Sample> sampleRoot = sampleSubquery.from(Sample.class);
		sampleSubquery.where(cb.equal(sampleRoot.get(Sample.SORMAS_TO_SORMAS_ORIGIN_INFO), root.get(SormasToSormasShareRequest.ORIGIN_INFO)));
		sampleSubquery.select(sampleRoot.get(AbstractDomainObject.UUID));
		return sampleSubquery;
	}

	private Subquery<String> createSubquery(
		CriteriaBuilder cb,
		CriteriaQuery<String> cq,
		Root<SormasToSormasShareRequest> root,
		Class<? extends CoreAdo> subqueryClass,
		String s2SShareRequestField) {

		final Subquery<String> subquery = cq.subquery(String.class);
		final Root<? extends CoreAdo> from = subquery.from(subqueryClass);
		subquery.where(cb.equal(from.get(s2SShareRequestField), root.get(SormasToSormasShareRequest.ORIGIN_INFO)));
		subquery.select(from.get(AbstractDomainObject.UUID));
		return subquery;
	}

}
