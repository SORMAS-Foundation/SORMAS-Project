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

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.symeda.sormas.api.document.DocumentDto;
import de.symeda.sormas.api.document.DocumentFacade;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "DocumentFacade")
public class DocumentFacadeEjb implements DocumentFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private UserService userService;
	@EJB
	private DocumentService documentService;

	@Override
	public DocumentDto getDocumentByUuid(String uuid) {
		return toDto(documentService.getByUuid(uuid));
	}

	@Override
	public DocumentDto saveDocument(DocumentDto dto) {
		Document existingDocument = dto.getUuid() == null ? documentService.getByUuid(dto.getUuid()) : null;
		if (existingDocument != null) {
			// TODO: add exception message
			throw new EntityExistsException();
		}

		Document document = fromDto(dto);
		documentService.persist(document);
		documentService.doFlush();

		return toDto(document);
	}

	@Override
	public void deleteDocument(String uuid) {
		documentService.delete(documentService.getByUuid(uuid));
	}

	@Override
	public List<DocumentDto> getDocumentsRelatedToEntity(DocumentRelatedEntityType type, String uuid) {
		return documentService.getRelatedToEntity(type, uuid).stream().map(DocumentFacadeEjb::toDto).collect(Collectors.toList());
	}

	@Override
	public String isExistingDocument(DocumentRelatedEntityType type, String uuid, String name) {
		return documentService.isExisting(type, uuid, name);
	}

	public Document fromDto(DocumentDto source) {
		Document target = documentService.getByUuid(source.getUuid());
		if (target == null) {
			target = new Document();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

		target.setUploadingUser(userService.getByReferenceDto(source.getUploadingUser()));
		target.setName(source.getName());
		target.setContentType(source.getContentType());
		target.setSize(source.getSize());
		target.setRelatedEntityUuid(source.getRelatedEntityUuid());
		target.setRelatedEntityType(source.getRelatedEntityType());

		return target;
	}

	// XXX: pseudonimize uploadingUser? Under which conditions?
	public static DocumentDto toDto(Document source) {
		if (source == null) {
			return null;
		}
		DocumentDto target = new DocumentDto();
		DtoHelper.fillDto(target, source);

		target.setUploadingUser(UserFacadeEjb.toReferenceDto(source.getUploadingUser()));
		target.setName(source.getName());
		target.setContentType(source.getContentType());
		target.setSize(source.getSize());
		target.setRelatedEntityUuid(source.getRelatedEntityUuid());
		target.setRelatedEntityType(source.getRelatedEntityType());

		return target;
	}

	@LocalBean
	@Stateless
	public static class DocumentFacadeEjbLocal extends DocumentFacadeEjb {
	}
}
