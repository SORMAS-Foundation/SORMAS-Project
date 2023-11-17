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

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateEntities;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentVariables;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.QuarantineOrderFacade;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.document.DocumentDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
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
		String templateName,
		DocumentWorkflow workflow,
		RootEntityType rootEntityType,
		ReferenceDto rootEntityReference,
		SampleReferenceDto sampleReference,
		PathogenTestReferenceDto pathogenTestReference,
		VaccinationReferenceDto vaccinationReference,
		Properties extraProperties,
		Boolean shouldUploadGeneratedDoc)
		throws DocumentTemplateException {

		DocumentTemplateEntities entities =
			entitiesBuilder.getQuarantineOrderEntities(rootEntityType, rootEntityReference, sampleReference, pathogenTestReference, vaccinationReference);
		byte[] documentToSave = documentTemplateFacade.generateDocumentDocxFromEntities(workflow, templateName, entities, extraProperties);
		if (shouldUploadGeneratedDoc) {
			uploadDocument(templateName, rootEntityReference, documentToSave);
		}
		return documentToSave;
	}

	private void uploadDocument(String templateName, ReferenceDto rootEntityReference, byte[] documentToSave) throws DocumentTemplateException {
		try {
			if (isFileSizeLimitExceeded(documentToSave.length)) {
				return;
			}

			helper.saveDocument(
				helper.getDocumentFileName(rootEntityReference, templateName),
				DocumentDto.MIME_TYPE_DEFAULT,
				documentToSave.length,
				helper.getDocumentRelatedEntityType(rootEntityReference),
				rootEntityReference.getUuid(),
				documentToSave);
		} catch (Exception e) {
			throw new DocumentTemplateException(I18nProperties.getString(Strings.errorProcessingTemplate));
		}
	}

	private boolean isFileSizeLimitExceeded(int length) {
		long fileSizeLimitMb = configFacade.getDocumentUploadSizeLimitMb();
		fileSizeLimitMb = fileSizeLimitMb * 1_000_000;
		return length > fileSizeLimitMb;
	}

	@Override
	public Map<ReferenceDto, byte[]> getGeneratedDocuments(
		String templateName,
		DocumentWorkflow workflow,
		List<ReferenceDto> rootEntityReferences,
		Properties extraProperties,
		Boolean shouldUploadGeneratedDoc)
		throws DocumentTemplateException {

		Map<ReferenceDto, DocumentTemplateEntities> quarantineOrderEntities =
			entitiesBuilder.getQuarantineOrderEntities(workflow, rootEntityReferences);

		return getGeneratedDocuments(templateName, workflow, quarantineOrderEntities, extraProperties, shouldUploadGeneratedDoc);
	}

	@Override
	public Map<ReferenceDto, byte[]> getGeneratedDocumentsForEventParticipants(
		String templateName,
		List<EventParticipantReferenceDto> rootEntityReferences,
		Disease eventDisease,
		Properties extraProperties,
		Boolean shouldUploadGeneratedDoc)
		throws DocumentTemplateException {

		Map<ReferenceDto, DocumentTemplateEntities> quarantineOrderEntities =
			entitiesBuilder.getEventParticipantQuarantineOrderEntities(rootEntityReferences, eventDisease);

		return getGeneratedDocuments(templateName, DocumentWorkflow.QUARANTINE_ORDER_EVENT_PARTICIPANT, quarantineOrderEntities, extraProperties, shouldUploadGeneratedDoc);
	}

	private Map<ReferenceDto, byte[]> getGeneratedDocuments(
		String templateName,
		DocumentWorkflow workflow,
		Map<ReferenceDto, DocumentTemplateEntities> quarantineOrderEntities,
		Properties extraProperties,
		Boolean shouldUploadGeneratedDoc)
		throws DocumentTemplateException {

		Map<ReferenceDto, byte[]> documents = new HashMap<>(quarantineOrderEntities.size());

		for (Map.Entry<ReferenceDto, DocumentTemplateEntities> entities : quarantineOrderEntities.entrySet()) {
			byte[] documentContent = documentTemplateFacade.generateDocumentDocxFromEntities(
				workflow,
				templateName,
				entities.getValue(),
				extraProperties);
			if (shouldUploadGeneratedDoc) {
				uploadDocument(templateName, entities.getKey(), documentContent);
			}
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
