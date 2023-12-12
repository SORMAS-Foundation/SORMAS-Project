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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.document.DocumentDto;
import de.symeda.sormas.api.document.DocumentReferenceDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless
@LocalBean
public class DocumentService extends AdoServiceWithUserFilterAndJurisdiction<Document> {

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
		return getRelatedToEntity(type, uuid, null);
	}

	public List<Document> getRelatedToEntities(DocumentRelatedEntityType type, List<String> uuids, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Document> cq = cb.createQuery(getElementClass());
		Root<Document> from = cq.from(getElementClass());
		from.fetch(Document.UPLOADING_USER);

		Predicate filter = cb.and(
			cb.isFalse(from.get(Document.DELETED)),
			cb.equal(from.get(Document.RELATED_ENTITY_TYPE), type),
			cb.in(from.get(Document.RELATED_ENTITY_UUID)).value(uuids));

		cq.where(filter);

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<Order>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case DocumentDto.UUID:
				case DocumentDto.NAME:
				case DocumentDto.CONTENT_TYPE:
				case DocumentDto.SIZE:
					expression = from.get(sortProperty.propertyName);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(from.get(Document.CHANGE_DATE)));
		}
		cq.distinct(true);
		return em.createQuery(cq).getResultList();
	}

	public List<Document> getRelatedToEntity(DocumentRelatedEntityType type, String uuid, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Document> cq = cb.createQuery(getElementClass());
		Root<Document> from = cq.from(getElementClass());
		from.fetch(Document.UPLOADING_USER);

        cq.where(buildRelatedEntityFilter(type, uuid, cb, from));

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<Order>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case DocumentDto.UUID:
				case DocumentDto.NAME:
				case DocumentDto.CONTENT_TYPE:
				case DocumentDto.SIZE:
					expression = from.get(sortProperty.propertyName);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(from.get(Document.CHANGE_DATE)));
		}
		cq.distinct(true);
		return em.createQuery(cq).getResultList();
	}

    public List<DocumentReferenceDto> getReferencesRelatedToEntity(DocumentRelatedEntityType type, String uuid, Set<String> fileExtensions) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DocumentReferenceDto> cq = cb.createQuery(DocumentReferenceDto.class);
        Root<Document> from = cq.from(getElementClass());

        cq.multiselect(from.get(Document.UUID), from.get(Document.NAME));

        cq.where(CriteriaBuilderHelper.and(cb, buildRelatedEntityFilter(type, uuid, cb, from), buildExtensionFilter(fileExtensions, cb, from)));

        cq.orderBy(cb.asc(cb.lower(from.get(DocumentDto.NAME))));

        return em.createQuery(cq).getResultList();
    }

    private static Predicate buildRelatedEntityFilter(DocumentRelatedEntityType type, String uuid, CriteriaBuilder cb, Root<Document> from) {
        return cb.and(
                cb.isFalse(from.get(Document.DELETED)),
                cb.equal(from.get(Document.RELATED_ENTITY_TYPE), type),
                cb.equal(from.get(Document.RELATED_ENTITY_UUID), uuid));
    }

    private static Predicate buildExtensionFilter(Set<String> fileExtensions, CriteriaBuilder cb, Root<Document> from) {
        if (fileExtensions == null) {
            return null;
        }

        Predicate[] predicates =
                fileExtensions.stream().map(extension -> cb.like(from.get(Document.NAME), "%" + extension)).toArray(Predicate[]::new);

        return cb.or(predicates);
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

		return QueryHelper.getSingleResult(em, cq);
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
