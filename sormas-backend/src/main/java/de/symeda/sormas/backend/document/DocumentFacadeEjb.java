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

import static java.util.Arrays.asList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;

import de.symeda.sormas.api.document.DocumentCriteria;
import de.symeda.sormas.api.document.DocumentDto;
import de.symeda.sormas.api.document.DocumentFacade;
import de.symeda.sormas.api.document.DocumentReferenceDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.FileContentsDoNotMatchExtensionException;
import de.symeda.sormas.api.utils.FileExtensionNotAllowedException;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.RightsAllowed;

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
@RightsAllowed(UserRight._DOCUMENT_VIEW)
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
	private ContactService contactService;
	@EJB
	private EventService eventService;
	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	@Override
	public DocumentDto getDocumentByUuid(String uuid) {
		return convertToDto(documentService.getByUuid(uuid), Pseudonymizer.getDefault(userService));
	}

	@Override
	@RightsAllowed(UserRight._DOCUMENT_UPLOAD)
	public DocumentDto saveDocument(@Valid DocumentDto dto, byte[] content) throws IOException {
		Document existingDocument = dto.getUuid() == null ? null : documentService.getByUuid(dto.getUuid());
		if (existingDocument != null) {
			throw new EntityExistsException("Tried to save a document that already exists: " + dto.getUuid());
		}

		String fileExtension = getFileExtension(dto.getName());
		checkFileExtension(fileExtension);
		checkFileContents(dto.getName(), content, fileExtension);

		Document document = fillOrBuildEntity(dto, existingDocument, true);
		String storageReference = documentStorageService.save(document, content);
		try {
			document.setStorageReference(storageReference);

			documentService.persist(document);
			documentService.doFlush();

			return convertToDto(document, Pseudonymizer.getDefault(userService));
		} catch (Throwable t) {
			try {
				documentStorageService.delete(storageReference);
			} catch (Throwable t2) {
				t.addSuppressed(t2);
			}
			throw t;
		}
	}

	private String getFileExtension(String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index > 0) {
			return fileName.substring(index);
		} else {
			throw new FileExtensionNotAllowedException(String.format("File name (%s) is not properly formatted", fileName));
		}
	}

	private void checkFileExtension(String fileExtension) {
		String[] allowedFileExtensions = configFacade.getAllowedFileExtensions();
		boolean fileTypeAllowed = asList(allowedFileExtensions).contains(fileExtension);

		if (!fileTypeAllowed) {
			throw new FileExtensionNotAllowedException(String.format("File extension %s not allowed", fileExtension));
		}
	}

	private void checkFileContents(String fileName, byte[] content, String fileExtension) throws IOException {
		try {
			getMimeTypeFromFileContents(fileName, content).getExtensions()
				.stream()
				.filter(fileExtension::equals)
				.findAny()
				.orElseThrow(() -> new FileContentsDoNotMatchExtensionException("File extension and file contents are not the same"));
		} catch (MimeTypeException e) {
			throw new FileExtensionNotAllowedException("Could not read file extension within file");
		}
	}

	private static MimeType getMimeTypeFromFileContents(String fileName, byte[] content) throws IOException, MimeTypeException {
		InputStream stream = new ByteArrayInputStream(content);
		TikaConfig tikaConfig = TikaConfig.getDefaultConfig();
		Metadata metaData = new Metadata();
		metaData.set("resourceName", fileName);
		MediaType detect = tikaConfig.getDetector().detect(stream, metaData);

		return tikaConfig.getMimeRepository().forName(detect.toString());
	}

	@Override
	@RightsAllowed(UserRight._DOCUMENT_DELETE)
	public void deleteDocument(String uuid) {
		// Only mark as delete here; actual deletion will be done in document storage cleanup via cron job
		documentService.markAsDeleted(documentService.getByUuid(uuid));
	}

	@Override
	public List<DocumentDto> getDocumentsRelatedToEntity(DocumentRelatedEntityType type, String uuid) {
		Pseudonymizer<DocumentDto> pseudonymizer = Pseudonymizer.getDefault(userService);
		return documentService.getRelatedToEntity(type, uuid).stream().map(d -> convertToDto(d, pseudonymizer)).collect(Collectors.toList());
	}

	@Override
	public List<DocumentReferenceDto> getReferencesRelatedToEntity(DocumentRelatedEntityType type, String uuid, Set<String> fileExtensions) {
		return documentService.getReferencesRelatedToEntity(type, uuid, fileExtensions);
	}

	@Override
	public Map<String, List<DocumentDto>> getDocumentsRelatedToEntities(DocumentCriteria criteria, List<SortProperty> sortProperties) {
		Pseudonymizer<DocumentDto> pseudonymizer = Pseudonymizer.getDefault(userService);
		List<Document> allDocuments =
			documentService.getRelatedToEntities(criteria.getDocumentRelatedEntityType(), criteria.getEntityUuids(), sortProperties);
		return allDocuments.stream().map(d -> convertToDto(d, pseudonymizer)).collect(Collectors.groupingBy(DocumentDto::getRelatedEntityUuid));
	}

	@Override
	public String isExistingDocument(DocumentRelatedEntityType type, String uuid, String name) {
		return documentService.isExisting(type, uuid, name);
	}

	@Override
	public byte[] getContent(String uuid) throws IOException {
		Document document = documentService.getByUuid(uuid);
		return documentStorageService.read(document.getStorageReference());
	}

	@Override
	@RightsAllowed(UserRight._SYSTEM)
	public void cleanupDeletedDocuments() {
		List<Document> deleted = documentService.getDocumentsMarkedForDeletion();
		for (Document document : deleted) {
			documentStorageService.delete(document.getStorageReference());
			documentService.deletePermanent(document);
		}
	}

	public Document fillOrBuildEntity(DocumentDto source, Document target, boolean checkChangeDate) {
		if (source == null) {
			return null;
		}

		target = DtoHelper.fillOrBuildEntity(source, target, Document::new, checkChangeDate);

		target.setUploadingUser(userService.getByReferenceDto(source.getUploadingUser()));
		target.setName(source.getName());
		target.setMimeType(source.getMimeType());
		target.setSize(source.getSize());
		target.setRelatedEntityUuid(source.getRelatedEntityUuid());
		target.setRelatedEntityType(source.getRelatedEntityType());

		return target;
	}

	public DocumentDto convertToDto(Document source, Pseudonymizer<DocumentDto> pseudonymizer) {
		DocumentDto documentDto = toDto(source);

		pseudonymizeDto(source, documentDto, pseudonymizer);

		return documentDto;
	}

	private void pseudonymizeDto(Document document, DocumentDto dto, Pseudonymizer<DocumentDto> pseudonymizer) {
		if (dto != null) {
			boolean inJurisdiction = isInJurisdiction(dto);
			pseudonymizer.pseudonymizeDto(
				DocumentDto.class,
				dto,
				inJurisdiction,
				e -> pseudonymizer.pseudonymizeUser(document.getUploadingUser(), userService.getCurrentUser(), dto::setUploadingUser, dto));
		}
	}

	private boolean isInJurisdiction(DocumentDto dto) {
		switch (dto.getRelatedEntityType()) {
		case CASE:
			Case caze = caseService.getByUuid(dto.getRelatedEntityUuid());
			return caseService.inJurisdictionOrOwned(caze);
		case CONTACT:
			Contact contact = contactService.getByUuid(dto.getRelatedEntityUuid());
			return contactService.inJurisdictionOrOwned(contact);
		case EVENT:
			Event event = eventService.getByUuid(dto.getRelatedEntityUuid());
			return eventService.inJurisdictionOrOwned(event);
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
