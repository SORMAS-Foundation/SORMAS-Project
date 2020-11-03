package de.symeda.sormas.backend.docgeneration;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateFacade;
import de.symeda.sormas.api.docgeneneration.QuarantineOrderFacade;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;

@Stateless(name = "QuarantineOrderFacade")
public class QuarantineOrderFacadeEjb implements QuarantineOrderFacade {

	@EJB
	private CaseFacadeEjbLocal caseFacade;

	@EJB
	private ContactFacadeEjbLocal contactFacade;

	@EJB
	DocumentTemplateFacade documentTemplateFacade;

	@Override
	public byte[] getGeneratedDocument(String templateName, ReferenceDto rootEntityReference, Properties extraProperties) throws IOException {
		String rootEntityUuid = rootEntityReference.getUuid();

		EntityDto entityData;
		if (rootEntityReference instanceof CaseReferenceDto) {
			entityData = caseFacade.getCaseDataByUuid(rootEntityUuid);
		} else if (rootEntityReference instanceof ContactReferenceDto) {
			entityData = contactFacade.getContactByUuid(rootEntityUuid);
		} else {
			throw new IllegalArgumentException(I18nProperties.getString(Strings.errorQuarantineOnlyCaseAndContacts));
		}

		return documentTemplateFacade.generateDocument(templateName, entityData, extraProperties);
	}

	@Override
	public List<String> getAvailableTemplates() {
		return documentTemplateFacade.getAvailableTemplates();
	}

	@Override
	public boolean isExistingTemplate(String templateName) {
		return documentTemplateFacade.isExistingTemplate(templateName);
	}

	@Override
	public List<String> getAdditionalVariables(String templateName) throws IOException {
		return documentTemplateFacade.getAdditionalVariables(templateName);
	}

	@Override
	public void writeQuarantineTemplate(String templateName, byte[] document) throws IOException {
		documentTemplateFacade.writeQuarantineTemplate(templateName, document);
	}

	@Override
	public boolean deleteQuarantineTemplate(String templateName) {
		return documentTemplateFacade.deleteQuarantineTemplate(templateName);
	}

	@Override
	public byte[] getTemplate(String templateName) throws IOException {
		return documentTemplateFacade.getTemplate(templateName);
	}
}
