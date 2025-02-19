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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.FieldConstraints;

@DependingOnFeatureType(featureType = FeatureType.SURVEYS)
public class SurveyDto extends EntityDto {

	private static final long serialVersionUID = 8680621267053679223L;

	public static final String I18N_PREFIX = "Survey";

	public static final String DISEASE = "disease";
	public static final String SURVEY_NAME = "name";

	public static final String NAME = "name";

	@NotBlank(message = Validations.requiredField)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String name;
	@NotNull(message = Validations.requiredField)
	private Disease disease;
	private DocumentTemplateReferenceDto documentTemplate;
	private DocumentTemplateReferenceDto emailTemplate;

	public static SurveyDto build() {
		SurveyDto survey = new SurveyDto();
		survey.setUuid(DataHelper.createUuid());

		return survey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public DocumentTemplateReferenceDto getDocumentTemplate() {
		return documentTemplate;
	}

	public void setDocumentTemplate(DocumentTemplateReferenceDto documentTemplate) {
		this.documentTemplate = documentTemplate;
	}

	public DocumentTemplateReferenceDto getEmailTemplate() {
		return emailTemplate;
	}

	public void setEmailTemplate(DocumentTemplateReferenceDto emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	public SurveyReferenceDto toReference() {
		return new SurveyReferenceDto(getUuid(), getName());
	}
}
