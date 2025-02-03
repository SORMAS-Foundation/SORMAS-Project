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

package de.symeda.sormas.backend.survey;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.document.Document;

@Entity(name = "surveytokens")
public class SurveyToken extends AbstractDomainObject {

	private static final long serialVersionUID = -2865609720432906419L;

	public static final String TOKEN = "token";
	public static final String SURVEY = "survey";
	public static final String CASE_ASSIGNED_TO = "caseAssignedTo";
	public static final String ASSIGNMENT_DATE = "assignmentDate";

	private String token;
	private Survey survey;
	private Case caseAssignedTo;
	private Date assignmentDate;
	private String recipientEmail;
	private Document generatedDocument;
	private boolean responseReceived;

	@Column(nullable = false, length = FieldConstraints.CHARACTER_LIMIT_SMALL)
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Case getCaseAssignedTo() {
		return caseAssignedTo;
	}

	public void setCaseAssignedTo(Case caseAssignedTo) {
		this.caseAssignedTo = caseAssignedTo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getAssignmentDate() {
		return assignmentDate;
	}

	public void setAssignmentDate(Date assignmentDate) {
		this.assignmentDate = assignmentDate;
	}

	@Column(length = FieldConstraints.CHARACTER_LIMIT_DEFAULT)
	public String getRecipientEmail() {
		return recipientEmail;
	}

	public void setRecipientEmail(String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Document getGeneratedDocument() {
		return generatedDocument;
	}

	public void setGeneratedDocument(Document generatedDocument) {
		this.generatedDocument = generatedDocument;
	}

	@Column
	public boolean isResponseReceived() {
		return responseReceived;
	}

	public void setResponseReceived(boolean responseReceived) {
		this.responseReceived = responseReceived;
	}
}
