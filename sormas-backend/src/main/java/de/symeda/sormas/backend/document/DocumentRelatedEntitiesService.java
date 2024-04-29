/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.backend.document;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.BaseAdoService;

@Stateless
@LocalBean
public class DocumentRelatedEntitiesService extends BaseAdoService<DocumentRelatedEntities> {

	public DocumentRelatedEntitiesService() {
		super(DocumentRelatedEntities.class);
	}

	public DocumentRelatedEntities getByDocumentAndRelatedEntityUuid(String documentUuid, String relatedEntityUuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DocumentRelatedEntities> cq = cb.createQuery(DocumentRelatedEntities.class);

		Root<DocumentRelatedEntities> root = cq.from(DocumentRelatedEntities.class);
		Join<DocumentRelatedEntities, Document> documentJoin = root.join(DocumentRelatedEntities.DOCUMENT, JoinType.LEFT);

		cq.where(
			cb.and(cb.equal(documentJoin.get(AbstractDomainObject.UUID), documentUuid)),
			cb.equal(root.get(DocumentRelatedEntities.RELATED_ENTITY_UUID), relatedEntityUuid));

		return em.createQuery(cq).getSingleResult();
	}

	public void deleteDocumentRelatedEntity(String documentUuid, String relatedEntityUuid) {
		DocumentRelatedEntities documentRelatedEntity = getByDocumentAndRelatedEntityUuid(documentUuid, relatedEntityUuid);
		deletePermanent(documentRelatedEntity);
	}
}
