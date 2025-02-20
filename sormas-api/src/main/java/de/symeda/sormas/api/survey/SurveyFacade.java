/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2025 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.survey;

import java.util.List;

import javax.ejb.Remote;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.docgeneneration.DocumentTemplateDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface SurveyFacade {

    SurveyDto save(@Valid SurveyDto dto);

	void uploadDocumentTemplate(@NotNull SurveyReferenceDto surveyRef, DocumentTemplateDto uploadedDocumentTemplate, byte[] fileContent)
		throws DocumentTemplateException;

	SurveyDto getByUuid(String uuid);

    long count(SurveyCriteria criteria);

    List<SurveyIndexDto> getIndexList(SurveyCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);

    void deletePermanent(String uuid);

    boolean exists(String uuid);

    SurveyReferenceDto getReferenceByUuid(String uuid);

	Boolean isEditAllowed(String uuid);

	void uploadEmailTemplate(@NotNull SurveyReferenceDto surveyReference, DocumentTemplateDto uploadedDocumentTemplateDto, byte[] fileContent)
		throws DocumentTemplateException;
}
