/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.docgeneration;

import static de.symeda.sormas.api.docgeneneration.RootEntityName.ROOT_CASE;
import static de.symeda.sormas.api.docgeneneration.RootEntityName.ROOT_CONTACT;
import static de.symeda.sormas.api.docgeneneration.RootEntityName.ROOT_EVENT_PARTICIPANT;
import static de.symeda.sormas.api.docgeneneration.RootEntityName.ROOT_PERSON;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentVariables;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.QuarantineOrderFacade;
import de.symeda.sormas.api.docgeneneration.RootEntityName;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.PathogenTestReferenceDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.docgeneration.DocumentTemplateFacadeEjb.DocumentTemplateFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;

@Stateless(name = "QuarantineOrderFacade")
public class QuarantineOrderFacadeEjb implements QuarantineOrderFacade {

	@EJB
	private CaseFacadeEjbLocal caseFacade;

	@EJB
	private ContactFacadeEjbLocal contactFacade;

	@EJB
	private UserFacadeEjbLocal userFacade;

	@EJB
	private EventParticipantFacadeEjbLocal eventParticipantFacade;

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
		throws DocumentTemplateException {
		String rootEntityUuid = rootEntityReference.getUuid();

		Map<String, Object> entities = new HashMap<>();
		if (rootEntityReference instanceof CaseReferenceDto) {
			CaseDataDto caseDataDto = caseFacade.getCaseDataByUuid(rootEntityUuid);
			entities.put(ROOT_CASE, caseDataDto);
			if (caseDataDto != null) {
				entities.put(ROOT_PERSON, caseDataDto.getPerson());
			}
		} else if (rootEntityReference instanceof ContactReferenceDto) {
			ContactDto contactDto = contactFacade.getContactByUuid(rootEntityUuid);
			entities.put(ROOT_CONTACT, contactDto);
			if (contactDto != null) {
				entities.put(ROOT_PERSON, contactDto.getPerson());
			}
		} else if (rootEntityReference instanceof EventParticipantReferenceDto) {
			EventParticipantDto eventParticipantDto = eventParticipantFacade.getByUuid(rootEntityUuid);
			entities.put(ROOT_EVENT_PARTICIPANT, eventParticipantDto);
			if (eventParticipantDto != null) {
				entities.put(ROOT_PERSON, eventParticipantDto.getPerson());
			}
		} else {
			throw new DocumentTemplateException(I18nProperties.getString(Strings.errorQuarantineOnlyCaseAndContacts));
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

		return documentTemplateFacade
			.generateDocumentDocxFromEntities(getDocumentWorkflow(rootEntityReference), templateName, entities, extraProperties);
	}

	@Override
	public List<String> getAvailableTemplates(ReferenceDto referenceDto) throws DocumentTemplateException {
		return documentTemplateFacade.getAvailableTemplates(getDocumentWorkflow(referenceDto));
	}

	@Override
	public DocumentVariables getDocumentVariables(ReferenceDto referenceDto, String templateName) throws DocumentTemplateException {
		DocumentWorkflow documentWorkflow = getDocumentWorkflow(referenceDto);
		return documentTemplateFacade.getDocumentVariables(documentWorkflow, templateName);
	}

	private DocumentWorkflow getDocumentWorkflow(ReferenceDto rootEntityReference) throws DocumentTemplateException {
		if (CaseReferenceDto.class.isAssignableFrom(rootEntityReference.getClass())) {
			return DocumentWorkflow.QUARANTINE_ORDER_CASE;
		} else if (ContactReferenceDto.class.isAssignableFrom(rootEntityReference.getClass())) {
			return DocumentWorkflow.QUARANTINE_ORDER_CONTACT;
		} else if (EventParticipantReferenceDto.class.isAssignableFrom(rootEntityReference.getClass())) {
			return DocumentWorkflow.QUARANTINE_ORDER_EVENT_PARTICIPANT;
		} else {
			throw new DocumentTemplateException(I18nProperties.getString(Strings.errorQuarantineOnlyCaseAndContacts));
		}
	}
}
