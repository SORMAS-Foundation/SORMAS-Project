package de.symeda.sormas.backend.docgeneration;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.io.IOUtils;

import de.symeda.sormas.api.action.ActionCriteria;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentVariables;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.EventDocumentFacade;
import de.symeda.sormas.api.docgeneneration.RootEntityName;
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
	DocumentTemplateFacadeEjbLocal documentTemplateFacade;

	@EJB
	ActionFacadeEjbLocal actionFacade;

	@EJB
	EventParticipantFacadeEjbLocal eventParticipantFacade;

	@Override
	public String getGeneratedDocument(String templateName, EventReferenceDto eventReference, Properties extraProperties)
		throws DocumentTemplateException {
		Map<String, Object> entities = new HashMap<>();
		entities.put(RootEntityName.ROOT_EVENT, eventReference);

		ActionCriteria actionCriteria = new ActionCriteria().event(eventReference);
		entities.put(RootEntityName.ROOT_EVENT_ACTIONS, actionFacade.getActionList(actionCriteria, null, null));

		entities.put(RootEntityName.ROOT_EVENT_PARTICIPANTS, eventParticipantFacade.getAllActiveEventParticipantsByEvent(eventReference.getUuid()));

		String body = documentTemplateFacade.generateDocumentTxtFromEntities(DOCUMENT_WORKFLOW, templateName, entities, extraProperties);
		return createStyledHtml(templateName, body);
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
