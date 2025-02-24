package de.symeda.sormas.backend.docgeneration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.action.ActionReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateReferenceDto;
import de.symeda.sormas.api.document.DocumentDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.document.DocumentFacadeEjb;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class DocGenerationHelper {

	@EJB
	private UserService userService;
	@EJB
	private DocumentFacadeEjb.DocumentFacadeEjbLocal documentFacade;
	@EJB
	private ConfigFacadeEjbLocal configFacade;

	public DocumentRelatedEntityType getDocumentRelatedEntityType(ReferenceDto rootEntityReference) {
		if (rootEntityReference instanceof CaseReferenceDto) {
			return DocumentRelatedEntityType.CASE;
		} else if (rootEntityReference instanceof ContactReferenceDto) {
			return DocumentRelatedEntityType.CONTACT;
		} else if (rootEntityReference instanceof ActionReferenceDto) {
			return DocumentRelatedEntityType.ACTION;
		} else if (rootEntityReference instanceof EventReferenceDto) {
			return DocumentRelatedEntityType.EVENT;
		} else if (rootEntityReference instanceof EventParticipantReferenceDto) {
			return DocumentRelatedEntityType.TRAVEL_ENTRY;
		} else {
			throw new IllegalArgumentException("Root entity reference type not supported: " + rootEntityReference.getClass());
		}
	}

	public String getDocumentFileName(ReferenceDto rootEntityReference, DocumentTemplateReferenceDto templateReference) {
		List<DocumentDto> docs =
			documentFacade.getDocumentsRelatedToEntity(getDocumentRelatedEntityType(rootEntityReference), rootEntityReference.getUuid());
		return generateNewFileName(
			docs.stream().map(DocumentDto::getName).collect(Collectors.toList()),
			DataHelper.getShortUuid(rootEntityReference),
			'-' + templateReference.getCaption());
	}

	private String generateNewFileName(List<String> docs, String shortUuid, String templateFileName) {
		int i = 1;
		String newFileName = shortUuid + templateFileName;
		ArrayList<String> docsArray = new ArrayList<>(docs);
		while (docsArray.contains(newFileName)) {
			newFileName = generateFileName(shortUuid, templateFileName, i);
			i++;
		}
		return newFileName;
	}

	private String generateFileName(String shortUuid, String templateFileName, int index) {
		shortUuid += "(" + index + ")";
		return shortUuid + templateFileName;
	}

	public DocumentDto saveDocument(String fileName, String mimeType, ReferenceDto rootEntityReference, byte[] bytes)
		throws DocumentTemplateException {
		if (isFileSizeLimitExceeded(bytes.length)) {
			throw new DocumentTemplateException(I18nProperties.getString(Strings.errorUploadGeneratedDocumentExceedsFileSizeLimit));
		}

		DocumentDto document = DocumentDto.build();
		document.setUploadingUser(userService.getCurrentUser().toReference());
		document.setName(fileName);
		document.setMimeType(mimeType);
		document.setSize(bytes.length);

		DocumentRelatedEntityType relatedEntityType = getDocumentRelatedEntityType(rootEntityReference);
		DocumentRelatedEntityDto documentRelatedEntities = DocumentRelatedEntityDto.build(relatedEntityType, rootEntityReference.getUuid());

		try {
			return documentFacade.saveDocument(document, bytes, Collections.singletonList(documentRelatedEntities));
		} catch (IOException e) {
			throw new DocumentTemplateException(I18nProperties.getString(Strings.errorUploadGeneratedDocument));
		}
	}

	private boolean isFileSizeLimitExceeded(int length) {
		long fileSizeLimitMb = configFacade.getDocumentUploadSizeLimitMb();
		long fileSizeLimitBytes = fileSizeLimitMb * 1_000_000;
		return length > fileSizeLimitBytes;
	}

}
