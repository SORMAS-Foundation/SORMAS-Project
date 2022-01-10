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

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.action.ActionCriteria;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateEntities;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentVariables;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.EventDocumentFacade;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
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
		String templateName,
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

		String body = documentTemplateFacade.generateDocumentTxtFromEntities(DOCUMENT_WORKFLOW, templateName, entities, extraProperties);
		String styledHtml = createStyledHtml(templateName, body);
		if (shouldUploadGeneratedDoc) {
			byte[] documentToSave = styledHtml.getBytes(StandardCharsets.UTF_8);//mandatory UTF_8
			try {
				helper.saveDocument(
					helper.getDocumentFileName(eventReference, templateName),
					null,// default type will be applied: "application/octet-stream" for /*"text/html"*/ it will work as well in the same way.
					documentToSave.length,
					helper.getDocumentRelatedEntityType(eventReference),
					eventReference.getUuid(),
					documentToSave);
			} catch (Exception e) {
				throw new DocumentTemplateException(I18nProperties.getString(Strings.errorProcessingTemplate));
			}
		}
		return styledHtml;
	}

	@Override
	public Map<ReferenceDto, byte[]> getGeneratedDocuments(
		String templateName,
		List<EventReferenceDto> eventReferences,
		Properties extraProperties,
		Boolean shouldUploadGeneratedDoc)
		throws DocumentTemplateException {
		Map<ReferenceDto, byte[]> documents = new HashMap<>(eventReferences.size());

		for (EventReferenceDto referenceDto : eventReferences) {
			String documentContent = getGeneratedDocument(templateName, referenceDto, extraProperties, shouldUploadGeneratedDoc);
			documents.put(referenceDto, documentContent.getBytes(StandardCharsets.UTF_8));
		}

		return documents;
	}

	@Override
	public List<String> getAvailableTemplates() {
		return documentTemplateFacade.getAvailableTemplates(DOCUMENT_WORKFLOW);
	}

	@Override
	public DocumentVariables getDocumentVariables(String templateName) throws DocumentTemplateException {
		return documentTemplateFacade.getDocumentVariables(DOCUMENT_WORKFLOW, templateName);
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
