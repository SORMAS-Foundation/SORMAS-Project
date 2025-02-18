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

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class SurveyTokenIndexDto extends PseudonymizableIndexDto {

	private static final long serialVersionUID = 4358173798026207265L;

	private final String surveyUuid;
	private final String surveyName;
	private final String token;
	private final CaseReferenceDto caseAssignedTo;
	private final Date assignmentDate;
	private final String recipientEmail;
	private final Boolean responseReceived;
	private final String generatedDocumentUuid;
	private final String generatedDocumentName;
	private final String generatedDocumentMimeType;

	public SurveyTokenIndexDto(
            String uuid,
            String surveyUuid,
            String surveyName,
            String token,
            CaseReferenceDto caseAssignedTo,
            Date assignmentDate,
            String recipientEmail, Boolean responseReceived, String generatedDocumentUuid, String generatedDocumentName, String generatedDocumentMimeType) {
		super(uuid);
		this.surveyUuid = surveyUuid;
		this.surveyName = surveyName;
		this.token = token;
		this.caseAssignedTo = caseAssignedTo;
		this.assignmentDate = assignmentDate;
		this.recipientEmail = recipientEmail;
        this.responseReceived = responseReceived;
        this.generatedDocumentUuid = generatedDocumentUuid;
        this.generatedDocumentName = generatedDocumentName;
        this.generatedDocumentMimeType = generatedDocumentMimeType;
    }

	public String getSurveyUuid() {
		return surveyUuid;
	}

	public String getSurveyName() {
		return surveyName;
	}

	public String getToken() {
		return token;
	}

	public CaseReferenceDto getCaseAssignedTo() {
		return caseAssignedTo;
	}

	public Date getAssignmentDate() {
		return assignmentDate;
	}

	public String getRecipientEmail() {
		return recipientEmail;
	}

	public Boolean getResponseReceived() {
		return responseReceived;
	}

	public SurveyTokenReferenceDto toReference() {
		return new SurveyTokenReferenceDto(getUuid(), getSurveyName());
	}

	public String getGeneratedDocumentUuid() {
		return generatedDocumentUuid;
	}

	public String getGeneratedDocumentName() {
		return generatedDocumentName;
	}

	public String getGeneratedDocumentMimeType() {
		return generatedDocumentMimeType;
	}
}
