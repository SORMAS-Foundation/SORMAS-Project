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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity(name = DocumentRelatedEntity.TABLE_NAME)
public class DocumentRelatedEntity extends AbstractDomainObject {

	public static final String TABLE_NAME = "documentrelatedentities";

	public static final String DOCUMENT = "document";
	public static final String RELATED_ENTITY_UUID = "relatedEntityUuid";
	public static final String RELATED_ENTITY_TYPE = "relatedEntityType";

	private Document document;
	private String relatedEntityUuid;
	private DocumentRelatedEntityType relatedEntityType;

	public DocumentRelatedEntity() {
	}

	public DocumentRelatedEntity build(DocumentRelatedEntityType documentRelatedEntityType, String relatedEntityUuid) {
		DocumentRelatedEntity documentRelatedEntity = new DocumentRelatedEntity();
		documentRelatedEntity.setRelatedEntityType(documentRelatedEntityType);
		documentRelatedEntity.setRelatedEntityUuid(relatedEntityUuid);
		return documentRelatedEntity;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	@Column()
	public String getRelatedEntityUuid() {
		return relatedEntityUuid;
	}

	public void setRelatedEntityUuid(String relatedEntityUuid) {
		this.relatedEntityUuid = relatedEntityUuid;
	}

	@Enumerated(EnumType.STRING)
	@Column()
	public DocumentRelatedEntityType getRelatedEntityType() {
		return relatedEntityType;
	}

	public void setRelatedEntityType(DocumentRelatedEntityType relatedEntityType) {
		this.relatedEntityType = relatedEntityType;
	}
}
