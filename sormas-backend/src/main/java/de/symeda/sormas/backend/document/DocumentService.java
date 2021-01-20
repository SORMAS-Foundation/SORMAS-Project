/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.backend.document;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;

@Stateless
@LocalBean
public class DocumentService extends AdoServiceWithUserFilter<Document> {

	public DocumentService() {
		super(Document.class);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Document> from) {
		return null;
	}

	public void markAsDeleted(Document deleteme) {
		deleteme.setDeleted(true);
		em.persist(deleteme);
		em.flush();
	}

	public List<Document> getRelatedToEntity(DocumentRelatedEntityType type, String uuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Document> cq = cb.createQuery(getElementClass());
		Root<Document> from = cq.from(getElementClass());
		from.fetch(Document.UPLOADING_USER);

		Predicate filter = cb.and(
			cb.isFalse(from.get(Document.DELETED)),
			cb.equal(from.get(Document.RELATED_ENTITY_TYPE), type),
			cb.equal(from.get(Document.RELATED_ENTITY_UUID), uuid));

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(Document.CHANGE_DATE)));
		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}

	public String isExisting(DocumentRelatedEntityType type, String uuid, String name) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Document> from = cq.from(getElementClass());

		Predicate filter = cb.and(
			cb.isFalse(from.get(Document.DELETED)),
			cb.equal(from.get(Document.RELATED_ENTITY_TYPE), type),
			cb.equal(from.get(Document.RELATED_ENTITY_UUID), uuid),
			cb.equal(from.get(Document.NAME), name));

		cq.where(filter);
		cq.select(from.get(Document.UUID));

		try {
			return em.createQuery(cq).getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	public List<Document> getDocumentsMarkedForDeletion() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Document> cq = cb.createQuery(getElementClass());
		Root<Document> from = cq.from(getElementClass());

		Predicate filter = cb.isTrue(from.get(Document.DELETED));

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(Document.CHANGE_DATE)));
		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}
}
