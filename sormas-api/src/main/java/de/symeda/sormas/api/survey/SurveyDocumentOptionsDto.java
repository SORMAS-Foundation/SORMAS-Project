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

import java.io.Serializable;
import java.util.Properties;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.docgeneneration.RootEntityType;

@AuditedClass
public class SurveyDocumentOptionsDto implements Serializable {

	private static final long serialVersionUID = 2850464241974703991L;

	public static final String I18N_PREFIX = "SurveyDocumentOptions";

	public static final String SURVEY = "survey";
	public static final String RECIPIENT_EMAIL = "recipientEmail";

	private final RootEntityType rootEntityType;
	private final ReferenceDto rootEntityReference;
	private SurveyReferenceDto survey;
	private Properties templateProperties;
	private String recipientEmail;

	public RootEntityType getRootEntityType() {
		return rootEntityType;
	}

	public ReferenceDto getRootEntityReference() {
		return rootEntityReference;
	}

	public SurveyDocumentOptionsDto(RootEntityType rootEntityType, ReferenceDto rootEntityReference) {
		this.rootEntityType = rootEntityType;
		this.rootEntityReference = rootEntityReference;
	}

	public SurveyReferenceDto getSurvey() {
		return survey;
	}

	public void setSurvey(SurveyReferenceDto survey) {
		this.survey = survey;
	}

	public Properties getTemplateProperties() {
		return templateProperties;
	}

	public void setTemplateProperties(Properties templateProperties) {
		this.templateProperties = templateProperties;
	}

	public String getRecipientEmail() {
		return recipientEmail;
	}

	public void setRecipientEmail(String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}
}
