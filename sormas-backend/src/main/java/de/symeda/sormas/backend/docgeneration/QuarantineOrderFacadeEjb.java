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
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateCriteria;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateEntities;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentVariables;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.QuarantineOrderFacade;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.document.DocumentDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestReferenceDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.vaccination.VaccinationReferenceDto;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.docgeneration.DocumentTemplateFacadeEjb.DocumentTemplateFacadeEjbLocal;

@Stateless(name = "QuarantineOrderFacade")
public class QuarantineOrderFacadeEjb implements QuarantineOrderFacade {

	@EJB
	private DocumentTemplateFacadeEjbLocal documentTemplateFacade;

	@EJB
	private DocumentTemplateEntitiesBuilder entitiesBuilder;

	@EJB
	private DocGenerationHelper helper;

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	@Override
	public byte[] getGeneratedDocument(
		DocumentTemplateReferenceDto templateReference,
		RootEntityType rootEntityType,
		ReferenceDto rootEntityReference,
		SampleReferenceDto sampleReference,
		PathogenTestReferenceDto pathogenTestReference,
		VaccinationReferenceDto vaccinationReference,
		Properties extraProperties,
		Boolean shouldUploadGeneratedDoc)
		throws DocumentTemplateException {

		DocumentTemplateEntities entities = entitiesBuilder
			.getQuarantineOrderEntities(rootEntityType, rootEntityReference, sampleReference, pathogenTestReference, vaccinationReference);
		byte[] documentToSave = documentTemplateFacade.generateDocumentDocxFromEntities(templateReference, entities, extraProperties);
		if (shouldUploadGeneratedDoc) {
			uploadDocument(helper.getDocumentFileName(rootEntityReference, templateReference), rootEntityReference, documentToSave);
		}
		return documentToSave;
	}

	public void uploadDocument(String fileName, ReferenceDto rootEntityReference, byte[] documentToSave) throws DocumentTemplateException {
		helper.saveDocument(fileName, DocumentDto.MIME_TYPE_DEFAULT, rootEntityReference, documentToSave);

	}

	@Override
	public Map<ReferenceDto, byte[]> getGeneratedDocuments(
		DocumentTemplateReferenceDto templateReference,
		List<ReferenceDto> rootEntityReferences,
		Properties extraProperties,
		Boolean shouldUploadGeneratedDoc)
		throws DocumentTemplateException {

		DocumentTemplateDto template = documentTemplateFacade.getByUuid(templateReference.getUuid());

		Map<ReferenceDto, DocumentTemplateEntities> quarantineOrderEntities =
			entitiesBuilder.getQuarantineOrderEntities(template.getWorkflow(), rootEntityReferences);

		return getGeneratedDocuments(templateReference, quarantineOrderEntities, extraProperties, shouldUploadGeneratedDoc);
	}

	@Override
	public Map<ReferenceDto, byte[]> getGeneratedDocumentsForEventParticipants(
		DocumentTemplateReferenceDto templateReference,
		List<EventParticipantReferenceDto> rootEntityReferences,
		Disease eventDisease,
		Properties extraProperties,
		Boolean shouldUploadGeneratedDoc)
		throws DocumentTemplateException {

		Map<ReferenceDto, DocumentTemplateEntities> quarantineOrderEntities =
			entitiesBuilder.getEventParticipantQuarantineOrderEntities(rootEntityReferences, eventDisease);

		return getGeneratedDocuments(templateReference, quarantineOrderEntities, extraProperties, shouldUploadGeneratedDoc);
	}

	private Map<ReferenceDto, byte[]> getGeneratedDocuments(
		DocumentTemplateReferenceDto templateReference,
		Map<ReferenceDto, DocumentTemplateEntities> quarantineOrderEntities,
		Properties extraProperties,
		Boolean shouldUploadGeneratedDoc)
		throws DocumentTemplateException {

		Map<ReferenceDto, byte[]> documents = new HashMap<>(quarantineOrderEntities.size());

		for (Map.Entry<ReferenceDto, DocumentTemplateEntities> entities : quarantineOrderEntities.entrySet()) {
			byte[] documentContent = documentTemplateFacade.generateDocumentDocxFromEntities(templateReference, entities.getValue(), extraProperties);
			if (shouldUploadGeneratedDoc) {
				uploadDocument(helper.getDocumentFileName(entities.getKey(), templateReference), entities.getKey(), documentContent);
			}
			documents.put(entities.getKey(), documentContent);
		}

		return documents;
	}

	@Override
	public List<DocumentTemplateDto> getAvailableTemplates(DocumentWorkflow workflow, Disease disease) {
		return documentTemplateFacade.getAvailableTemplates(new DocumentTemplateCriteria(workflow, disease, null));
	}

	@Override
	public DocumentVariables getDocumentVariables(DocumentTemplateReferenceDto templateReference) throws DocumentTemplateException {
		return documentTemplateFacade.getDocumentVariables(templateReference);
	}

	@LocalBean
	@Stateless
	public static class QuarantineOrderFacadeEjbLocal extends QuarantineOrderFacadeEjb {
	}
}
