package de.symeda.sormas.backend.docgeneration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentVariables;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.QuarantineOrderFacade;
import de.symeda.sormas.api.docgeneneration.RootEntityName;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.PathogenTestReferenceDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.docgeneration.DocumentTemplateFacadeEjb.DocumentTemplateFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;

@Stateless(name = "QuarantineOrderFacade")
public class QuarantineOrderFacadeEjb implements QuarantineOrderFacade {

	private static final DocumentWorkflow DOCUMENT_WORKFLOW = DocumentWorkflow.QUARANTINE_ORDER;

	@EJB
	private CaseFacadeEjbLocal caseFacade;

	@EJB
	private ContactFacadeEjbLocal contactFacade;

	@EJB
	private UserFacadeEjbLocal userFacade;

	@EJB
	private SampleFacadeEjbLocal sampleFacadeEjb;

	@EJB
	private PathogenTestFacadeEjbLocal pathogenTestFacade;

	@EJB
	private DocumentTemplateFacadeEjbLocal documentTemplateFacade;

	@Override
	public byte[] getGeneratedDocument(
		String templateName,
		ReferenceDto rootEntityReference,
		UserReferenceDto userReference,
		SampleReferenceDto sampleReference,
		PathogenTestReferenceDto pathogenTestReference,
		Properties extraProperties)
		throws IOException {
		String rootEntityUuid = rootEntityReference.getUuid();

		Map<String, Object> entities = new HashMap<>();
		if (rootEntityReference instanceof CaseReferenceDto) {
			entities.put(RootEntityName.ROOT_CASE, caseFacade.getCaseDataByUuid(rootEntityUuid));
		} else if (rootEntityReference instanceof ContactReferenceDto) {
			entities.put(RootEntityName.ROOT_CASE, contactFacade.getContactByUuid(rootEntityUuid));
		} else {
			throw new IllegalArgumentException(I18nProperties.getString(Strings.errorQuarantineOnlyCaseAndContacts));
		}

		if (userReference != null) {
			entities.put(RootEntityName.ROOT_USER, userFacade.getByUuid(userReference.getUuid()));
		}

		if (sampleReference != null) {
			entities.put(RootEntityName.ROOT_SAMPLE, sampleFacadeEjb.getSampleByUuid(sampleReference.getUuid()));
		}

		if (pathogenTestReference != null) {
			entities.put(RootEntityName.ROOT_PATHOGEN_TEST, pathogenTestFacade.getByUuid(pathogenTestReference.getUuid()));
		}

		return documentTemplateFacade.generateDocumentDocxFromEntities(DOCUMENT_WORKFLOW, templateName, entities, extraProperties);
	}

	@Override
	public List<String> getAvailableTemplates() {
		return documentTemplateFacade.getAvailableTemplates(DOCUMENT_WORKFLOW);
	}

	@Override
	public DocumentVariables getDocumentVariables(String templateName) throws IOException {
		return documentTemplateFacade.getDocumentVariables(DOCUMENT_WORKFLOW, templateName);
	}
}
