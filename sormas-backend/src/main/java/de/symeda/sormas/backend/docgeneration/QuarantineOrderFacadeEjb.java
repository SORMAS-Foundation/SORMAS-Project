package de.symeda.sormas.backend.docgeneration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateFacade;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.QuarantineOrderFacade;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;

@Stateless(name = "QuarantineOrderFacade")
public class QuarantineOrderFacadeEjb implements QuarantineOrderFacade {

	private static final DocumentWorkflow DOCUMENT_WORKFLOW = DocumentWorkflow.QUARANTINE_ORDER;
	private static final String ROOT_ENTITY_NAME = DOCUMENT_WORKFLOW.getRootEntityNames().get(0);

	@EJB
	private CaseFacadeEjbLocal caseFacade;

	@EJB
	private ContactFacadeEjbLocal contactFacade;

	@EJB
	DocumentTemplateFacade documentTemplateFacade;

	@Override
	public byte[] getGeneratedDocument(String templateName, ReferenceDto rootEntityReference, Properties extraProperties) throws IOException {
		String rootEntityUuid = rootEntityReference.getUuid();

		Map<String, EntityDto> entities = new HashMap<>();
		if (rootEntityReference instanceof CaseReferenceDto) {
			entities.put(ROOT_ENTITY_NAME, caseFacade.getCaseDataByUuid(rootEntityUuid));
		} else if (rootEntityReference instanceof ContactReferenceDto) {
			entities.put(ROOT_ENTITY_NAME, contactFacade.getContactByUuid(rootEntityUuid));
		} else {
			throw new IllegalArgumentException(I18nProperties.getString(Strings.errorQuarantineOnlyCaseAndContacts));
		}

		return documentTemplateFacade.generateDocumentFromEntities(DOCUMENT_WORKFLOW, templateName, entities, extraProperties);
	}

	@Override
	public List<String> getAvailableTemplates() {
		return documentTemplateFacade.getAvailableTemplates(DOCUMENT_WORKFLOW);
	}

	@Override
	public List<String> getAdditionalVariables(String templateName) throws IOException {
		return documentTemplateFacade.getAdditionalVariables(DOCUMENT_WORKFLOW, templateName);
	}
}
