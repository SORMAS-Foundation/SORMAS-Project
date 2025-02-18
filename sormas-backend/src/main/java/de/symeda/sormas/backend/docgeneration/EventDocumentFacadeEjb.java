package de.symeda.sormas.backend.docgeneration;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.io.IOUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.action.ActionCriteria;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateEntities;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentVariables;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.EventDocumentFacade;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.document.DocumentDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.backend.action.ActionFacadeEjb.ActionFacadeEjbLocal;
import de.symeda.sormas.backend.docgeneration.DocumentTemplateFacadeEjb.DocumentTemplateFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal;

@Stateless(name = "EventDocumentFacade")
public class EventDocumentFacadeEjb implements EventDocumentFacade {

	private static final DocumentWorkflow DOCUMENT_WORKFLOW = DocumentWorkflow.EVENT_HANDOUT;
	private static final String PLACEHOLDER_TITLE = "___title___";
	private static final String PLACEHOLDER_BODY = "___body___";

	@EJB
	private DocumentTemplateFacadeEjbLocal documentTemplateFacade;

	@EJB
	private ActionFacadeEjbLocal actionFacade;

	@EJB
	private EventParticipantFacadeEjbLocal eventParticipantFacade;

	@EJB
	private DocGenerationHelper helper;

	@Override
	public String getGeneratedDocument(
		DocumentTemplateReferenceDto templateReference,
		EventReferenceDto eventReference,
		Properties extraProperties,
		Boolean shouldUploadGeneratedDoc)
		throws DocumentTemplateException {
		DocumentTemplateEntities entities = new DocumentTemplateEntities();
		entities.addEntity(RootEntityType.ROOT_EVENT, eventReference);

		ActionCriteria actionCriteria = new ActionCriteria().event(eventReference);
		entities.addEntity(RootEntityType.ROOT_EVENT_ACTIONS, actionFacade.getActionList(actionCriteria, null, null));

		entities
			.addEntity(RootEntityType.ROOT_EVENT_PARTICIPANTS, eventParticipantFacade.getAllActiveEventParticipantsByEvent(eventReference.getUuid()));

		String body = documentTemplateFacade.generateDocumentTxtFromEntities(templateReference, entities, extraProperties);
		String styledHtml = createStyledHtml(templateReference.getCaption(), body);
		if (shouldUploadGeneratedDoc) {
			byte[] documentToSave = styledHtml.getBytes(StandardCharsets.UTF_8);//mandatory UTF_8
			helper.saveDocument(
				helper.getDocumentFileName(eventReference, templateReference),
				DocumentDto.MIME_TYPE_DEFAULT,
				eventReference,
				documentToSave);
		}
		return styledHtml;
	}

	@Override
	public Map<ReferenceDto, byte[]> getGeneratedDocuments(
		DocumentTemplateReferenceDto templateReference,
		List<EventReferenceDto> eventReferences,
		Properties extraProperties,
		Boolean shouldUploadGeneratedDoc)
		throws DocumentTemplateException {
		Map<ReferenceDto, byte[]> documents = new HashMap<>(eventReferences.size());

		for (EventReferenceDto referenceDto : eventReferences) {
			String documentContent = getGeneratedDocument(templateReference, referenceDto, extraProperties, shouldUploadGeneratedDoc);
			documents.put(referenceDto, documentContent.getBytes(StandardCharsets.UTF_8));
		}

		return documents;
	}

	@Override
	public List<DocumentTemplateDto> getAvailableTemplates(Disease disease) {
		return documentTemplateFacade.getAvailableTemplates(DOCUMENT_WORKFLOW, disease);
	}

	@Override
	public DocumentVariables getDocumentVariables(DocumentTemplateReferenceDto templateReference) throws DocumentTemplateException {
		return documentTemplateFacade.getDocumentVariables(templateReference);
	}

	private String createStyledHtml(String title, String body) throws DocumentTemplateException {
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(getClass().getResourceAsStream("/docgeneration/sormasStyle.html"), writer, "UTF-8");
			String document = writer.toString();
			return document.replace(PLACEHOLDER_TITLE, title).replace(PLACEHOLDER_BODY, body);
		} catch (IOException e) {
			throw new DocumentTemplateException(I18nProperties.getString(Strings.errorProcessingTemplate));
		}
	}
}
