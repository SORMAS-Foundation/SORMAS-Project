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

import org.apache.commons.lang3.NotImplementedException;

import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.document.DocumentReferenceDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityFacade;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "DocumentRelatedEntitiesFacade")
public class DocumentRelatedEntityFacadeEjb implements DocumentRelatedEntityFacade {

	@EJB
	private DocumentRelatedEntityService documentRelatedEntityService;
	@EJB
	private DocumentService documentService;

	public static DocumentRelatedEntityDto toDto(DocumentRelatedEntity source) {
		if (source == null) {
			return null;
		}

		DocumentRelatedEntityDto target = new DocumentRelatedEntityDto();
		DtoHelper.fillDto(target, source);

		target.setDocument(DocumentFacadeEjb.toReferenceDto(source.getDocument()));
		target.setRelatedEntityUuid(source.getRelatedEntityUuid());
		target.setRelatedEntityType(source.getRelatedEntityType());

		return target;
	}

	public DocumentRelatedEntity fillOrBuildEntity(DocumentRelatedEntityDto source, DocumentRelatedEntity target, boolean checkChangeDate) {
		if (source == null) {
			return null;
		}
		target = DtoHelper.fillOrBuildEntity(source, target, DocumentRelatedEntity::new, checkChangeDate);

		target.setRelatedEntityType(source.getRelatedEntityType());
		target.setRelatedEntityUuid(source.getRelatedEntityUuid());
		target.setDocument(documentService.getByReferenceDto(source.getDocument()));

		return target;
	}

	@Override
	public void delete(String uuid, DeletionDetails deletionDetails) {
		throw new NotImplementedException();
	}

	@Override
	public List<ProcessedEntity> delete(List<String> uuids, DeletionDetails deletionDetails) {
		throw new NotImplementedException();
	}

	@Override
	public void restore(String uuid) {
		throw new NotImplementedException();
	}

	@Override
	public List<ProcessedEntity> restore(List<String> uuids) {
		throw new NotImplementedException();
	}

	@Override
	public boolean isDeleted(String uuid) {
		return false;
	}

	@Override
	public DocumentRelatedEntityDto saveDocumentRelatedEntity(DocumentRelatedEntityDto dto) {

		DocumentRelatedEntity documentRelatedEntity = new DocumentRelatedEntity().build(dto.getRelatedEntityType(), dto.getRelatedEntityUuid());
		documentRelatedEntity.setDocument(documentRelatedEntity.getDocument());
		documentRelatedEntityService.ensurePersisted(documentRelatedEntity);

		return toDto(documentRelatedEntity);
	}

	@LocalBean
	@Stateless
	public static class DocumentRelatedEntityFacadeEjbLocal extends DocumentRelatedEntityFacadeEjb {

	}

	public static DocumentReferenceDto toReferenceDto(Document entity) {

		if (entity == null) {
			return null;
		}

		return new DocumentReferenceDto(entity.getUuid(), entity.getName());
	}

}
