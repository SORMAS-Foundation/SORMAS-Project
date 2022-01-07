package de.symeda.sormas.backend.docgeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.action.ActionReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.document.DocumentDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.document.DocumentFacadeEjb;
import de.symeda.sormas.backend.user.UserService;

public class DocGenerationHelper {

	private UserService userService;

	private DocumentFacadeEjb.DocumentFacadeEjbLocal documentFacade;

	public DocGenerationHelper(UserService userService, DocumentFacadeEjb.DocumentFacadeEjbLocal documentFacade) {
		this.userService = userService;
		this.documentFacade = documentFacade;
	}

	protected DocumentRelatedEntityType getDocumentRelatedEntityType(ReferenceDto rootEntityReference) {
		if (rootEntityReference instanceof CaseReferenceDto) {
			return DocumentRelatedEntityType.CASE;
		} else if (rootEntityReference instanceof ContactReferenceDto) {
			return DocumentRelatedEntityType.CONTACT;
		} else if (rootEntityReference instanceof ActionReferenceDto) {
			return DocumentRelatedEntityType.ACTION;
		} else if (rootEntityReference instanceof EventReferenceDto) {
			return DocumentRelatedEntityType.EVENT;
		} else {
			return DocumentRelatedEntityType.TRAVEL_ENTRY;
		}
	}

	protected String getDocumentFileName(ReferenceDto rootEntityReference, String templateFileName) {
		List<DocumentDto> docs =
			documentFacade.getDocumentsRelatedToEntity(getDocumentRelatedEntityType(rootEntityReference), rootEntityReference.getUuid());
		return generateNewFileName(
			docs.stream().map(DocumentDto::getName).collect(Collectors.toList()),
			DataHelper.getShortUuid(rootEntityReference),
			'-' + templateFileName);
	}

	private String generateNewFileName(List<String> docs, String shortUuid, String templateFileName) {
		int i = 1;
		String forCompare = shortUuid + templateFileName;
		while (new ArrayList<>(docs).contains(forCompare)) {
			forCompare = generateFileName(shortUuid, templateFileName, i);
			i++;
		}
		return forCompare;
	}

	private static String generateFileName(String shortUuid, String templateFileName, int i) {
		shortUuid += "(" + i + ")";
		return shortUuid + templateFileName;
	}

	protected void saveDocument(
		String fileName,
		String mimeType,
		int length,
		DocumentRelatedEntityType relatedEntityType,
		String relatedEntityUuid,
		byte[] bytes)
		throws Exception {
		DocumentDto document = DocumentDto.build();
		document.setUploadingUser(userService.getCurrentUser().toReference());
		document.setName(fileName);
		document.setMimeType(mimeType);
		document.setSize(length);
		document.setRelatedEntityType(relatedEntityType);
		document.setRelatedEntityUuid(relatedEntityUuid);
		documentFacade.saveDocument(document, bytes);
	}

}
