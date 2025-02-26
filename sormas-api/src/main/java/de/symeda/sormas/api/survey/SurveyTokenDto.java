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

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.document.DocumentReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.FieldConstraints;

@DependingOnFeatureType(featureType = FeatureType.SURVEYS)
public class SurveyTokenDto extends EntityDto {

	private static final long serialVersionUID = -4598785341989656729L;

	public static final String I18N_PREFIX = "SurveyToken";
	public static final String SURVEY = "survey";
	public static final String TOKEN = "token";
	public static final String CASE_ASSIGNED_TO = "caseAssignedTo";
	public static final String ASSIGNMENT_DATE = "assignmentDate";
	public static final String RECIPIENT_EMAIL = "recipientEmail";
	public static final String GENERATED_DOCUMENT = "generatedDocument";
	public static final String RESPONSE_RECEIVED = "responseReceived";

	@NotNull(message = Validations.requiredField)
	private SurveyReferenceDto survey;
	@NotBlank(message = Validations.requiredField)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String token;
	private CaseReferenceDto caseAssignedTo;
	private Date assignmentDate;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String recipientEmail;
	private DocumentReferenceDto generatedDocument;
	private boolean responseReceived;

	public static SurveyTokenDto build() {
		SurveyTokenDto token = new SurveyTokenDto();
		token.setUuid(DataHelper.createUuid());

		return token;
	}

	public static SurveyTokenDto build(SurveyDto survey) {
		SurveyTokenDto token = new SurveyTokenDto();
		token.setUuid(DataHelper.createUuid());
		token.setSurvey(new SurveyReferenceDto(survey.getUuid(), survey.getName()));

		return token;
	}

	public SurveyReferenceDto getSurvey() {
		return survey;
	}

	public void setSurvey(SurveyReferenceDto survey) {
		this.survey = survey;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public CaseReferenceDto getCaseAssignedTo() {
		return caseAssignedTo;
	}

	public void setCaseAssignedTo(CaseReferenceDto caseAssignedTo) {
		this.caseAssignedTo = caseAssignedTo;
	}

	public Date getAssignmentDate() {
		return assignmentDate;
	}

	public void setAssignmentDate(Date assignmentDate) {
		this.assignmentDate = assignmentDate;
	}

	public String getRecipientEmail() {
		return recipientEmail;
	}

	public void setRecipientEmail(String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}

	public DocumentReferenceDto getGeneratedDocument() {
		return generatedDocument;
	}

	public void setGeneratedDocument(DocumentReferenceDto generatedDocument) {
		this.generatedDocument = generatedDocument;
	}

	public boolean isResponseReceived() {
		return responseReceived;
	}

	public void setResponseReceived(boolean responseReceived) {
		this.responseReceived = responseReceived;
	}
}
