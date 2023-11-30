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

package de.symeda.sormas.api.docgeneneration;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.Remote;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestReferenceDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.vaccination.VaccinationReferenceDto;

@Remote
public interface QuarantineOrderFacade {

	byte[] getGeneratedDocument(
		String templateName,
		DocumentWorkflow workflow,
		RootEntityType rootEntityType,
		ReferenceDto rootEntityReference,
		SampleReferenceDto sampleReference,
		PathogenTestReferenceDto pathogenTestReference,
		VaccinationReferenceDto vaccinationReference,
		Properties extraProperties,
		Boolean shouldUploadGeneratedDoc)
		throws DocumentTemplateException;

	Map<ReferenceDto, byte[]> getGeneratedDocuments(
		String templateName,
		DocumentWorkflow workflow,
		List<ReferenceDto> rootEntityReferences,
		Properties extraProperties,
		Boolean shouldUploadGeneratedDoc)
		throws DocumentTemplateException;

	Map<ReferenceDto, byte[]> getGeneratedDocumentsForEventParticipants(
			String templateName,
			List<EventParticipantReferenceDto> rootEntityReferences,
			Disease eventDisease,
			Properties extraProperties,
			Boolean shouldUploadGeneratedDoc)
			throws DocumentTemplateException;

	List<String> getAvailableTemplates(DocumentWorkflow workflow);

	DocumentVariables getDocumentVariables(DocumentWorkflow documentWorkflow, String templateName) throws DocumentTemplateException;
}
