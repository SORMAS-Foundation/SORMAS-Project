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

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.document.DocumentReferenceDto;
import de.symeda.sormas.api.document.DocumentRelatedEntitiesDto;
import de.symeda.sormas.api.document.DocumentRelatedEntitiesFacade;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "DocumentRelatedEntitiesFacade")
public class DocumentRelatedEntitiesFacadeEjb implements DocumentRelatedEntitiesFacade {

	@EJB
	private DocumentRelatedEntitiesService documentRelatedEntitiesService;
	@EJB
	private DocumentService documentService;

	public static DocumentRelatedEntitiesDto toDto(DocumentRelatedEntities source) {
		if (source == null) {
			return null;
		}

		DocumentRelatedEntitiesDto target = new DocumentRelatedEntitiesDto();
		DtoHelper.fillDto(target, source);

		target.setDocument(new DocumentReferenceDto(source.getDocument().getUuid(), source.getDocument().getName()));
		target.setRelatedEntityUuid(source.getRelatedEntityUuid());
		target.setRelatedEntityType(source.getRelatedEntityType());

		return target;
	}

	public DocumentRelatedEntities fillOrBuildEntity(DocumentRelatedEntitiesDto source, DocumentRelatedEntities target, boolean checkChangeDate) {
		if (source == null) {
			return null;
		}
		target = DtoHelper.fillOrBuildEntity(source, target, DocumentRelatedEntities::new, checkChangeDate);

		target.setRelatedEntityType(source.getRelatedEntityType());
		target.setRelatedEntityUuid(source.getRelatedEntityUuid());
		target.setDocument(documentService.getByReferenceDto(source.getDocument()));

		return target;
	}

	@Override
	public void delete(String uuid, DeletionDetails deletionDetails) {

	}

	@Override
	public List<ProcessedEntity> delete(List<String> uuids, DeletionDetails deletionDetails) {
		return null;
	}

	@Override
	public void restore(String uuid) {

	}

	@Override
	public List<ProcessedEntity> restore(List<String> uuids) {
		return null;
	}

	@Override
	public boolean isDeleted(String uuid) {
		return false;
	}

	@Override
	public DocumentRelatedEntitiesDto saveDocumentRelatedEntity(DocumentRelatedEntitiesDto dto) {

		DocumentRelatedEntities documentRelatedEntities = new DocumentRelatedEntities().build(dto.getRelatedEntityType(), dto.getRelatedEntityUuid());
		documentRelatedEntities.setDocument(documentRelatedEntities.getDocument());
		documentRelatedEntitiesService.ensurePersisted(documentRelatedEntities);

		return toDto(documentRelatedEntities);
	}

	@LocalBean
	@Stateless
	public static class DocumentRelatedEntitiesFacadeEjbLocal extends de.symeda.sormas.backend.document.DocumentRelatedEntitiesFacadeEjb {

	}

	public static DocumentReferenceDto toReferenceDto(Document entity) {

		if (entity == null) {
			return null;
		}

		return new DocumentReferenceDto(entity.getUuid(), entity.getName());
	}

}
