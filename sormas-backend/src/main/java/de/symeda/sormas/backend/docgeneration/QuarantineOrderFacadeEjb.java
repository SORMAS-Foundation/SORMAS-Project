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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateEntities;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentVariables;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.QuarantineOrderFacade;
import de.symeda.sormas.api.sample.PathogenTestReferenceDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.docgeneration.DocumentTemplateFacadeEjb.DocumentTemplateFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;

@Stateless(name = "QuarantineOrderFacade")
public class QuarantineOrderFacadeEjb implements QuarantineOrderFacade {

	@EJB
	private CaseFacadeEjbLocal caseFacade;

	@EJB
	private ContactFacadeEjbLocal contactFacade;

	@EJB
	private EventParticipantFacadeEjbLocal eventParticipantFacade;

	@EJB
	private SampleFacadeEjbLocal sampleFacadeEjb;

	@EJB
	private PathogenTestFacadeEjbLocal pathogenTestFacade;

	@EJB
	private DocumentTemplateFacadeEjbLocal documentTemplateFacade;

	@EJB
	private DocumentTemplateEntitiesBuilder entitiesBuilder;

	@Override
	public byte[] getGeneratedDocument(
		String templateName,
		DocumentWorkflow workflow,
		ReferenceDto rootEntityReference,
		SampleReferenceDto sampleReference,
		PathogenTestReferenceDto pathogenTestReference,
		Properties extraProperties)
		throws DocumentTemplateException {

		DocumentTemplateEntities entities =
			entitiesBuilder.getQuarantineOrderEntities(workflow, rootEntityReference, sampleReference, pathogenTestReference);

		return documentTemplateFacade.generateDocumentDocxFromEntities(workflow, templateName, entities, extraProperties);
	}

	@Override
	public Map<ReferenceDto, byte[]> getGeneratedDocuments(
		String templateName,
		DocumentWorkflow workflow,
		List<ReferenceDto> rootEntityReferences,
		Properties extraProperties)
		throws DocumentTemplateException {

		Map<ReferenceDto, byte[]> documents = new HashMap<>(rootEntityReferences.size());

		Map<ReferenceDto, DocumentTemplateEntities> quarantineOrderEntities =
			entitiesBuilder.getQuarantineOrderEntities(workflow, rootEntityReferences);
		for (Map.Entry<ReferenceDto, DocumentTemplateEntities> entities : quarantineOrderEntities.entrySet()) {
			byte[] documentContent =
				documentTemplateFacade.generateDocumentDocxFromEntities(workflow, templateName, entities.getValue(), extraProperties);

			documents.put(entities.getKey(), documentContent);
		}

		return documents;
	}

	@Override
	public List<String> getAvailableTemplates(DocumentWorkflow workflow) {
		return documentTemplateFacade.getAvailableTemplates(workflow);
	}

	@Override
	public DocumentVariables getDocumentVariables(DocumentWorkflow documentWorkflow, String templateName) throws DocumentTemplateException {
		return documentTemplateFacade.getDocumentVariables(documentWorkflow, templateName);
	}
}
