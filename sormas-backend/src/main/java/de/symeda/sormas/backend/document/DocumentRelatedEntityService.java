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

import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.BaseAdoService;

@Stateless
@LocalBean
public class DocumentRelatedEntityService extends BaseAdoService<DocumentRelatedEntity> {

	public DocumentRelatedEntityService() {
		super(DocumentRelatedEntity.class);
	}

	public DocumentRelatedEntity getByDocumentAndRelatedEntityUuid(
		String documentUuid,
		String relatedEntityUuid,
		DocumentRelatedEntityType relatedEntityType) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DocumentRelatedEntity> cq = cb.createQuery(DocumentRelatedEntity.class);

		Root<DocumentRelatedEntity> root = cq.from(DocumentRelatedEntity.class);
		Join<DocumentRelatedEntity, Document> documentJoin = root.join(DocumentRelatedEntity.DOCUMENT, JoinType.LEFT);

		cq.where(
			cb.and(cb.equal(documentJoin.get(AbstractDomainObject.UUID), documentUuid)),
			cb.equal(root.get(DocumentRelatedEntity.RELATED_ENTITY_UUID), relatedEntityUuid),
			cb.equal(root.get(DocumentRelatedEntity.RELATED_ENTITY_TYPE), relatedEntityType));

		return em.createQuery(cq).getSingleResult();
	}

	public void deleteDocumentRelatedEntity(String documentUuid, String relatedEntityUuid, DocumentRelatedEntityType relatedEntityType) {
		DocumentRelatedEntity documentRelatedEntity = getByDocumentAndRelatedEntityUuid(documentUuid, relatedEntityUuid, relatedEntityType);
		deletePermanent(documentRelatedEntity);
	}
}
