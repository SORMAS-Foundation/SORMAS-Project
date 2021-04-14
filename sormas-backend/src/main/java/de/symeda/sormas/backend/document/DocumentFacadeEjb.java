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
package de.symeda.sormas.backend.document;

import java.io.IOException;
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
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseJurisdictionChecker;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactJurisdictionChecker;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventJurisdictionChecker;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;

/**
 * Manages documents and their storage.
 *
 * <p>
 * Storage of the document content itself is delegated to {@link DocumentStorageService},
 * which generates a <i>storage reference</i> that is stored alongside document metadata in the
 * database ({@link Document} entity).
 * <p>
 * Deletion is a two-phase process (see {@link Document#isDeleted()} for details).
 *
 * @see DocumentStorageService
 * @see Document#isDeleted()
 */
@Stateless(name = "DocumentFacade")
public class DocumentFacadeEjb implements DocumentFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private UserService userService;
	@EJB
	private DocumentService documentService;
	@EJB
	private DocumentStorageService documentStorageService;
	@EJB
	private CaseService caseService;
	@EJB
	private CaseJurisdictionChecker caseJurisdictionChecker;
	@EJB
	private ContactService contactService;
	@EJB
	private ContactJurisdictionChecker contactJurisdictionChecker;
	@EJB
	private EventService eventService;
	@EJB
	private EventJurisdictionChecker eventJurisdictionChecker;

	@Override
	public DocumentDto getDocumentByUuid(String uuid) {
		return convertToDto(documentService.getByUuid(uuid), Pseudonymizer.getDefault(userService::hasRight));
	}

	@Override
	public DocumentDto saveDocument(DocumentDto dto, byte[] content) throws IOException {
		Document existingDocument = dto.getUuid() == null ? null : documentService.getByUuid(dto.getUuid());
		if (existingDocument != null) {
			throw new EntityExistsException("Tried to save a document that already exists: " + dto.getUuid());
		}

		Document document = fromDto(dto, true);

		String storageReference = documentStorageService.save(document, content);
		try {
			document.setStorageReference(storageReference);

			documentService.persist(document);
			documentService.doFlush();

			return convertToDto(document, Pseudonymizer.getDefault(userService::hasRight));
		} catch (Throwable t) {
			try {
				documentStorageService.delete(storageReference);
			} catch (Throwable t2) {
				t.addSuppressed(t2);
			}
			throw t;
		}
	}

	@Override
	public void deleteDocument(String uuid) {
		// Only mark as delete here; actual deletion will be done in document storage cleanup via cron job
		documentService.markAsDeleted(documentService.getByUuid(uuid));
	}

	@Override
	public List<DocumentDto> getDocumentsRelatedToEntity(DocumentRelatedEntityType type, String uuid) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return documentService.getRelatedToEntity(type, uuid).stream().map(d -> convertToDto(d, pseudonymizer)).collect(Collectors.toList());
	}

	@Override
	public String isExistingDocument(DocumentRelatedEntityType type, String uuid, String name) {
		return documentService.isExisting(type, uuid, name);
	}

	@Override
	public byte[] read(String uuid) throws IOException {
		Document document = documentService.getByUuid(uuid);
		return documentStorageService.read(document.getStorageReference());
	}

	@Override
	public void cleanupDeletedDocuments() {
		List<Document> deleted = documentService.getDocumentsMarkedForDeletion();
		for (Document document : deleted) {
			documentStorageService.delete(document.getStorageReference());
			documentService.delete(document);
		}
	}

	public Document fromDto(DocumentDto source, boolean checkChangeDate) {
		Document target = DtoHelper.fillOrBuildEntity(source, documentService.getByUuid(source.getUuid()), Document::new, checkChangeDate);

		target.setUploadingUser(userService.getByReferenceDto(source.getUploadingUser()));
		target.setName(source.getName());
		target.setMimeType(source.getMimeType());
		target.setSize(source.getSize());
		target.setRelatedEntityUuid(source.getRelatedEntityUuid());
		target.setRelatedEntityType(source.getRelatedEntityType());

		return target;
	}

	public DocumentDto convertToDto(Document source, Pseudonymizer pseudonymizer) {
		DocumentDto documentDto = toDto(source);

		pseudonymizeDto(source, documentDto, pseudonymizer);

		return documentDto;
	}

	private void pseudonymizeDto(Document document, DocumentDto dto, Pseudonymizer pseudonymizer) {
		if (dto != null) {
			boolean inJurisdiction = isInJurisdiction(dto);
			pseudonymizer.pseudonymizeDto(
					DocumentDto.class,
					dto,
					inJurisdiction,
					(e) -> pseudonymizer.pseudonymizeUser(document.getUploadingUser(), userService.getCurrentUser(), dto::setUploadingUser));
		}
	}

	private boolean isInJurisdiction(DocumentDto dto) {
		switch (dto.getRelatedEntityType()) {
			case CASE:
				Case caze = caseService.getByUuid(dto.getRelatedEntityUuid());
				return caseJurisdictionChecker.isInJurisdictionOrOwned(caze);
			case CONTACT:
				Contact contact = contactService.getByUuid(dto.getRelatedEntityUuid());
				return contactJurisdictionChecker.isInJurisdictionOrOwned(contact);
			case EVENT:
				Event event = eventService.getByUuid(dto.getRelatedEntityUuid());
				return eventJurisdictionChecker.isInJurisdictionOrOwned(event);
		}
		return true;
	}

	public static DocumentDto toDto(Document source) {
		if (source == null) {
			return null;
		}
		DocumentDto target = new DocumentDto();
		DtoHelper.fillDto(target, source);

		target.setUploadingUser(UserFacadeEjb.toReferenceDto(source.getUploadingUser()));
		target.setName(source.getName());
		target.setMimeType(source.getMimeType());
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
